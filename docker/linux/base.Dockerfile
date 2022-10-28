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
FROM $BASE_OS_IMAGE AS updated_javet_buildenv

# Build android instead of linux
ARG ANDROID=false
# Reduce output of noisy commands
ARG LOG_VERBOSE=false
# CPU archetectures to build for
# e.g. 'x86_64,arm,arm64,ia32'
ARG TARGETS='x86_64'
# v8 version
ARG JAVET_V8_VERSION='10.6.194.14'
# NodeJS version
ARG JAVET_NODEJS_VERSION='18.10.0'

# Make args accessable inside scripts
ENV ANDROID=${ANDROID}
ENV LOG_VERBOSE=${LOG_VERBOSE}
ENV TARGETS=${TARGETS}
ENV JAVET_V8_VERSION=${JAVET_V8_VERSION}
ENV JAVET_NODEJS_VERSION=${JAVET_NODEJS_VERSION}

WORKDIR /
# Update debian-based linux
ENV DEBIAN_FRONTEND=noninteractive
ENV TAR_OPTIONS=--no-same-owner
RUN mkdir /custom-commands
ENV PATH=/custom-commands:$PATH
COPY ./docker/linux/utils /custom-commands
RUN chmod +x -R /custom-commands
RUN maybe-verbose.sh apt-get update
RUN maybe-verbose.sh apt-get install --upgrade -qq -y --no-install-recommends \
	git curl zip unzip wget
RUN maybe-verbose.sh apt-get upgrade -y
RUN [ "${LOG_VERBOSE}" = "false" ] \
	&& git config --global advice.detachedHead false

# Stage adding dependencies for that both v8 and Node.js use
FROM updated_javet_buildenv AS make_javet_buildenv
RUN maybe-verbose.sh apt-get install --upgrade -qq -y --no-install-recommends \
	software-properties-common patchelf \
	maven sudo execstack cmake build-essential \
	python3 python3-pip python3-distutils python3-testresources
RUN pip3 install coloredlogs
# Install CMake
RUN wget -q https://github.com/Kitware/CMake/releases/download/v3.21.4/cmake-3.21.4-linux-$(arch).sh \
	&& chmod 755 cmake-3.21.4-linux-$(arch).sh \
	&& mkdir -p /usr/lib/cmake \
	&& ./cmake-3.21.4-linux-$(arch).sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /bin/cmake \
	&& rm cmake-3.21.4-linux-$(arch).sh

FROM make_javet_buildenv AS v8env_javet_buildenv
# Prepare V8
COPY ./docker/linux/helpers/* /custom-commands
RUN chmod +x -R /custom-commands && mkdir /google-temp
WORKDIR /google-temp
COPY ./docker/linux/helpers/v8/repo-tools.sh /v8-repo-tools.sh
RUN chmod +x /v8-repo-tools.sh  \
	&& /v8-repo-tools.sh fetch_depot_tools "depot_tools"
RUN /v8-repo-tools.sh fetch_v8_source "v8-temp" "v8"

FROM v8env_javet_buildenv AS v8_javet_buildenv
# Seprate `gclient sync` for caching for android builds
WORKDIR /
RUN [ "${ANDROID}" = "true" ] \
	&& echo 'target_os = ["android"]' >> .gclient
RUN /v8-repo-tools.sh run_final_sync \
	&& echo "V8 preparation is completed."
ENV PATH=/google/depot_tools:$PATH

# Build V8
WORKDIR /google/v8
COPY ./scripts/python/patch_v8_build.py .
RUN python3 tools/dev/v8gen.py $(arch-build-alias).release -- \
	v8_monolithic=true v8_use_external_startup_data=false \
	is_component_build=false v8_enable_i18n_support=false \
	v8_enable_pointer_compression=false v8_static_library=true \
	symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
RUN maybe-verbose.sh ninja -C out.gn/$(arch-build-alias).release v8_monolith \
	|| python3 patch_v8_build.py -p ./
RUN maybe-verbose.sh ninja -C out.gn/$(arch-build-alias).release v8_monolith
RUN rm patch_v8_build.py
RUN echo V8 build is completed.

FROM make_javet_buildenv AS nodejs_javet_buildenv
# Prepare Node.js
WORKDIR /
RUN git clone https://github.com/nodejs/node.git
WORKDIR /node
RUN git checkout "v$JAVET_NODEJS_VERSION" \
	&& echo 'Node.js preparation is completed.'

# Build Node.js
WORKDIR /node
COPY ./scripts/python/patch_node_build.py .
RUN python3 patch_node_build.py -p ./
RUN ./configure --enable-static --without-intl
RUN python3 patch_node_build.py -p ./
RUN rm patch_node_build.py
RUN maybe-verbose.sh make -j4 \
	&& echo 'Node.js build is completed.'


FROM updated_javet_buildenv AS android_true_java_javet_buildenv
# Prepare Android NDK
WORKDIR /
RUN wget https://dl.google.com/android/repository/android-ndk-r23b-linux.zip
RUN unzip android-ndk-r23b-linux.zip
RUN rm android-ndk-r23b-linux.zip
WORKDIR /google/v8/third_party/android_sdk/public/cmdline-tools/latest/bin
RUN yes | ./sdkmanager --licenses
RUN ./sdkmanager "build-tools;30.0.2" "platforms;android-30"
ENV ANDROID_SDK_ROOT=/google/v8/third_party/android_sdk

FROM updated_javet_buildenv AS android_false_java_javet_buildenv
# Prepare Javet Build Environment
RUN mkdir -p /google/v8 && mkdir -p /node
COPY --from=v8_javet_buildenv /google/v8 /google/v8
COPY --from=nodejs_javet_buildenv /node /node
RUN maybe-verbose.sh apt-get update \
	&& maybe-verbose.sh apt-get install --upgrade -qq -y --no-install-recommends \
	openjdk-8-jdk \
	&& maybe-verbose.sh apt-get upgrade -y
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-$(arch)

FROM android_${ANDROID}_java_javet_buildenv AS end_javet_buildenv
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENV SDKMAN_HOME="/root/.sdkman"
ENV GRADLE_HOME="${SDKMAN_HOME}/candidates/gradle/current"
RUN curl -s https://get.sdkman.io?rcconfig=false | bash
RUN source ${SDKMAN_HOME}/bin/sdkman-init.sh \
	&& sdk install gradle 7.2
ENV PATH=$GRADLE_HOME/bin:$PATH

# Shrink
RUN rm -rf ${SDKMAN_HOME}/archives/* \
	&& rm -rf ${SDKMAN_HOME}/tmp/* \
	&& apt-get clean -y \
	&& rm -rf /var/lib/apt/lists/*
WORKDIR /

# Pre-cache Dependencies
RUN mkdir Javet
COPY . /Javet
RUN cd /Javet \
	&& maybe-verbose.sh gradle --warn dependencies \
	&& rm -rf /Javet \
	# Completed
	&& echo Javet build base image is completed.
WORKDIR /
