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

# Usage: docker build -t amithgeorge/javet-linux-dev:base-jvm-latest -f docker/linux-x86_64/base-jvm.Dockerfile .

FROM ubuntu:20.04
WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
# files need to be cleaned/deleted in the same RUN layer that adds them, else there is no actual size reduction benefit
# the files remain in the image, just the layer marks the file as deleted and is not visible inside the OS
RUN apt-get update --yes \
	&& apt-get install --upgrade -qq --yes --no-install-recommends \
	build-essential cmake curl execstack git maven openjdk-8-jdk \
	patchelf python3 python python3-pip python3-distutils python3-testresources \
	software-properties-common sudo unzip wget zip \
	&& apt-get upgrade --yes \
	&& pip3 install --no-cache-dir coloredlogs \
	&& apt-get clean --yes

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v3.21.4/cmake-3.21.4-linux-x86_64.sh \
	&& chmod 755 cmake-3.21.4-linux-x86_64.sh \
	&& mkdir -p /usr/lib/cmake \
	&& ./cmake-3.21.4-linux-x86_64.sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake \
	&& ln -sf /usr/lib/cmake/bin/cmake /bin/cmake \
	&& rm cmake-3.21.4-linux-x86_64.sh

# Prepare Javet Build Environment
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV SDKMAN_HOME="/root/.sdkman"
ENV GRADLE_HOME="${SDKMAN_HOME}/candidates/gradle/current"
ENV PATH=$GRADLE_HOME/bin:$PATH

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
# these two commands need to be on separate lines, else the symlink created above is not visible to the commands run below.
# if the two RUN commmands are merged, we get the error "source: not found"
RUN curl -s https://get.sdkman.io | bash \
	&& source ${SDKMAN_HOME}/bin/sdkman-init.sh \
	&& sdk install gradle 7.2 \
	&& rm -rf ${SDKMAN_HOME}/archives/* \
	&& rm -rf ${SDKMAN_HOME}/tmp/*
