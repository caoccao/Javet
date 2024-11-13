# Copyright (c) 2021-2024. caoccao.com Sam Cao
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

# Usage: docker build -t sjtucaocao/javet:4.1.0 -f docker/linux-x86_64/base_all_in_one.Dockerfile .

FROM ubuntu:20.04
WORKDIR /

ARG JAVET_NODE_VERSION=22.11.0
ARG JAVET_V8_VERSION=13.1.201.8

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install --upgrade -qq -y --no-install-recommends git curl wget build-essential software-properties-common patchelf maven sudo zip unzip execstack cmake file keyboard-configuration
RUN apt-get install --upgrade -qq -y --no-install-recommends python3 python python3-pip python3-distutils python3-testresources
RUN apt-get upgrade -y
RUN pip3 install coloredlogs

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v3.25.1/cmake-3.25.1-linux-x86_64.sh
RUN chmod 755 cmake-3.25.1-linux-x86_64.sh
RUN mkdir -p /usr/lib/cmake
RUN ./cmake-3.25.1-linux-x86_64.sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /bin/cmake
RUN rm cmake-3.25.1-linux-x86_64.sh

# Prepare V8
RUN mkdir -p /google/depot_tools && \
    git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git /google/depot_tools && \
    cd /google/depot_tools && \
    git checkout remotes/origin/main && \
    export PATH=/google/depot_tools:$PATH && \
    cd /google && \
    fetch v8 && \
    cd /google/v8 && \
    git checkout ${JAVET_V8_VERSION} && \
    sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh && \
    ./build/install-build-deps.sh && \
    sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh && \
    cd /google && \
    gclient sync && \
    echo V8 preparation is completed.
ENV PATH=/google/depot_tools:$PATH

# Build V8
WORKDIR /google/v8
RUN python3 tools/dev/v8gen.py x64.release -- v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
COPY ./scripts/python/patch_v8_build.py .
RUN ninja -C out.gn/x64.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/x64.release v8_monolith
RUN rm patch_v8_build.py
RUN echo V8 build is completed.

# Prepare Node.js v18
WORKDIR /
RUN git clone https://github.com/nodejs/node.git
WORKDIR /node
RUN git checkout v${JAVET_NODE_VERSION}
RUN echo Node.js preparation is completed.

# Build Node.js
WORKDIR /node
COPY ./scripts/python/patch_node_build.py .
RUN python3 patch_node_build.py -p ./
RUN ./configure --enable-static --without-intl
RUN python3 patch_node_build.py -p ./
RUN rm patch_node_build.py
RUN make -j4
RUN echo Node.js build is completed.

# Prepare Javet Build Environment
RUN apt-get install --upgrade -qq -y --no-install-recommends openjdk-8-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENV SDKMAN_HOME="/root/.sdkman"
ENV GRADLE_HOME="${SDKMAN_HOME}/candidates/gradle/current"
RUN curl -s https://get.sdkman.io | bash
RUN source ${SDKMAN_HOME}/bin/sdkman-init.sh && sdk install gradle 7.2
ENV PATH=$GRADLE_HOME/bin:$PATH

# Shrink
RUN rm -rf ${SDKMAN_HOME}/archives/*
RUN rm -rf ${SDKMAN_HOME}/tmp/*
RUN apt-get clean -y
RUN rm -rf /var/lib/apt/lists/*
WORKDIR /

# Pre-cache Dependencies
RUN mkdir Javet
WORKDIR /Javet
COPY . .
RUN gradle dependencies
WORKDIR /
RUN rm -rf /Javet

# Completed
RUN echo Javet build base image is completed.
