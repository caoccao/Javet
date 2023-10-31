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

# Usage: docker build -t sjtucaocao/javet-arm64:3.0.1 -f docker/linux-arm64/base_all_in_one.Dockerfile .

ARG JAVET_NODE_VERSION=18.15.0
ARG JAVET_V8_VERSION=11.9.169.6

FROM ubuntu:20.04
WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install --upgrade -qq -y --no-install-recommends git curl wget build-essential software-properties-common patchelf maven sudo zip unzip execstack cmake pkg-config
RUN apt-get install --upgrade -qq -y --no-install-recommends python3 python python3-pip python3-distutils python3-testresources
RUN apt-get upgrade -y
RUN pip3 install coloredlogs

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v3.25.1/cmake-3.25.1-linux-aarch64.sh
RUN chmod 755 cmake-3.25.1-linux-aarch64.sh
RUN mkdir -p /usr/lib/cmake
RUN ./cmake-3.25.1-linux-aarch64.sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /bin/cmake
RUN rm cmake-3.25.1-linux-aarch64.sh

# Install LLVM
RUN wget https://apt.llvm.org/llvm.sh
RUN chmod +x llvm.sh
RUN ./llvm.sh 15 all

# Prepare V8
RUN mkdir google
WORKDIR /google
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
WORKDIR /google/depot_tools
RUN git checkout remotes/origin/main
ENV PATH=/google/depot_tools:$PATH
WORKDIR /google
RUN fetch v8
WORKDIR /google/v8
RUN git checkout ${JAVET_V8_VERSION}
RUN sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
RUN ./build/install-build-deps.sh
RUN sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
WORKDIR /google
RUN gclient sync
RUN echo V8 preparation is completed.

# Build V8
WORKDIR /google/v8
RUN sed -i -e "s/target_cpu=\"x64\" v8_target_cpu=\"arm64/target_cpu=\"arm64\" v8_target_cpu=\"arm64/" infra/mb/mb_config.pyl
RUN python3 tools/dev/v8gen.py arm64.release -- 'target_cpu="arm64"' 'v8_target_cpu="arm64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false 'clang_base_path="/usr/lib/llvm-15"' clang_use_chrome_plugins=false blink_gc_plugin=false
COPY ./scripts/python/patch_v8_build.py .
RUN ninja -C out.gn/arm64.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/arm64.release v8_monolith
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
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
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
