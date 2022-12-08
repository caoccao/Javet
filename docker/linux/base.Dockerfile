# Copyright (c) 2021 caoccao.com Sam Cao
# All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# x86_64 Usage: docker build --platform=linux/amd64 -t sjtucaocao/javet:2.0.0 \
#	--build-args TARGETS='x86_64,arm64' \
#	--jobs=$(($( getconf _NPROCESSORS_ONLN ) - 1)) -f docker/linux/base.Dockerfile .

# Only run with other debian-based images of similar version for now
# e.g. navikey/raspbian-bullseye:2022-05-08
ARG BASE_OS_IMAGE=ubuntu:20.04

# Build android instead of linux
ARG ANDROID=noandroid

# --------- Start of Builds --------------
# Initial stage adds all parameters and basic
# dependencies that are good to have for most
# stages until cleanup
FROM $BASE_OS_IMAGE AS minimal_javet_buildenv

# Build android instead of linux
ARG ANDROID=noandroid
# Reduce output of noisy commands
ARG LOG_VERBOSE=false
# v8 version
ARG JAVET_V8_VERSION=10.8.168.20
# NodeJS version
ARG JAVET_NODEJS_VERSION=18.12.1
# OpenJDK version number
ARG OPEN_JDK_VERSION=8
# Version of GCC compiler to use, also determines cross-compilation library versions
ARG GCC_MAJOR_VERSION=9
# CPU archetectures to build for
# e.g. 'x86_64,amd64,arm,arm64,ia32,x86'
ARG TARGETS='x86_64'

# Make args accessable inside scripts
ENV ANDROID=${ANDROID}
ENV LOG_VERBOSE=${LOG_VERBOSE}
ENV TARGETS=${TARGETS}
ENV JAVET_V8_VERSION=${JAVET_V8_VERSION}
ENV JAVET_NODEJS_VERSION=${JAVET_NODEJS_VERSION}
ENV OPEN_JDK_VERSION=${OPEN_JDK_VERSION}
ENV GCC_MAJOR_VERSION=${GCC_MAJOR_VERSION}

ENV JAVA_HOME=/usr/lib/jvm/java-${OPEN_JDK_VERSION}-openjdk-amd64
ENV SDKMAN_HOME="/root/.sdkman"
ENV GRADLE_HOME="${SDKMAN_HOME}/candidates/gradle/current"

ENV PATH=$GRADLE_HOME/bin:$PATH

WORKDIR /
# Update debian-based linux
ENV DEBIAN_FRONTEND=noninteractive
ENV TAR_OPTIONS=--no-same-owner
RUN apt-get update --yes 1>/dev/null \
	&& apt-get install --upgrade -qq --yes --no-install-recommends \
	software-properties-common curl zip unzip wget maven sudo \
	openjdk-${OPEN_JDK_VERSION}-jdk \
	python3 python3-pip cmake python3-distutils python3-testresources 1>/dev/null \
	&& add-apt-repository ppa:git-core/ppa \
	&& apt-get update --yes 1>/dev/null \
	&& apt-get install --upgrade -qq --yes --no-install-recommends \
	git 1>/dev/null \
	&& apt-get upgrade --yes 1>/dev/null \
	&& apt-get clean --yes \
	&& git --version
# Install CMake
RUN wget -q https://github.com/Kitware/CMake/releases/download/v3.21.4/cmake-3.21.4-linux-$(arch).sh \
	&& chmod 755 cmake-3.21.4-linux-$(arch).sh \
	&& mkdir -p /usr/lib/cmake \
	&& ./cmake-3.21.4-linux-$(arch).sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /bin/cmake \
	&& rm cmake-3.21.4-linux-$(arch).sh

FROM minimal_javet_buildenv AS make_javet_buildenv
ENV PATH=/cmds/utils:$PATH
COPY ./docker/linux/helpers /cmds/
RUN chmod +x -R /cmds \
	&& maybe-verbose.sh apt-get update --yes \
	&& maybe-verbose.sh apt-get install --upgrade -qq --yes --no-install-recommends \
	software-properties-common patchelf file gcc-$GCC_MAJOR_VERSION \
	execstack ninja-build build-essential g++-$GCC_MAJOR_VERSION \
	&& pip3 install --no-cache-dir coloredlogs \
	&& /cmds/platform-deps.sh install_deps "${TARGETS}" \
	&& maybe-verbose.sh apt-get upgrade --yes \
	&& apt-get clean --yes \
	&& mkdir -p /node \
	&& [ "${LOG_VERBOSE}" = "false" ] && git config --global advice.detachedHead false

# Prepare V8
FROM make_javet_buildenv AS v8env_javet_buildenv
RUN mkdir /google-temp \
	&& cd /google-temp \
	&& /cmds/v8/repo-tools.sh fetch_depot_tools \
	&& /cmds/v8/repo-tools.sh fetch_v8_source
WORKDIR /google-temp

# Seprate `gclient sync` for caching for android builds
FROM v8env_javet_buildenv AS v8_javet_buildenv
WORKDIR /
RUN /cmds/v8/repo-tools.sh run_final_sync "${ANDROID}" \
	&& echo "V8 preparation is completed."
ENV PATH=/google/depot_tools:$PATH


# Build V8
WORKDIR /google/v8
COPY ./scripts/python/patch_v8_build.py .
# Only using the android ENV to ensure this layer is invalidated if
# doing and android build
RUN /cmds/v8/plat_builds.sh run_platform_builds "${ANDROID}" \
	&& echo V8 build is completed.

FROM make_javet_buildenv AS nodejs_javet_buildenv
# Prepare Node.js
WORKDIR /
RUN git clone -q --depth=1 \
	--branch=v${JAVET_NODEJS_VERSION} https://github.com/nodejs/node.git /node

# Build Node.js
WORKDIR /node
ENV PATH=/rpi-newer-crosstools/bin:$PATH
COPY ./scripts/python/patch_node_build.py .
RUN /cmds/nodejs/plat_builds.sh run_platform_builds \
	&& echo 'Node.js build is completed.'

FROM minimal_javet_buildenv AS java_javet_buildenv
COPY --from=v8_javet_buildenv /google/v8 /google/v8
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN curl -s https://get.sdkman.io?rcconfig=false | bash 1>/dev/null \
	&& source ${SDKMAN_HOME}/bin/sdkman-init.sh 1>/dev/null \
	&& sdk install gradle 7.2 1>/dev/null

FROM java_javet_buildenv AS android_java_javet_buildenv
# Prepare Android NDK
WORKDIR /
RUN ls /google/v8/third_party/ \
	&& wget -q https://dl.google.com/android/repository/android-ndk-r23b-linux.zip \
	&& unzip -qq android-ndk-r23b-linux.zip \
	&& rm android-ndk-r23b-linux.zip
WORKDIR /google/v8/third_party/android_sdk/public/cmdline-tools/latest/bin
RUN yes | ./sdkmanager --licenses 1>/dev/null \
	&& ./sdkmanager "build-tools;30.0.2" "platforms;android-30" 1>/dev/null
ENV ANDROID_SDK_ROOT=/google/v8/third_party/android_sdk

FROM java_javet_buildenv AS noandroid_java_javet_buildenv
# Prepare Javet Build Environment
COPY --from=nodejs_javet_buildenv /node /node

FROM ${ANDROID}_java_javet_buildenv AS full_javet_buildenv
# Shrink
RUN rm -rf ${SDKMAN_HOME}/archives/* \
	&& rm -rf ${SDKMAN_HOME}/tmp/* \
	&& apt-get clean -y \
	&& rm -rf /var/lib/apt/lists/*
WORKDIR /

FROM full_javet_buildenv AS gradle_javet_buildenv
# Pre-cache Dependencies
RUN mkdir Javet
WORKDIR /Javet
COPY . .
RUN gradle --warn dependencies 1>/dev/null \
	&& rm -rf /Javet \
	# Completed
	&& echo Javet build base image is completed.
