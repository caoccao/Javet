# Copyright (c) 2021-2025. caoccao.com Sam Cao
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

# Usage: docker build -t sjtucaocao/javet-linux-x86_64:5.0.2 -f docker/linux-x86_64/build.Dockerfile .

# Multi-stage Dockerfile for building Javet on Linux x86_64
# Based on .github/workflows/linux_x86_64_build.yml

# Build arguments
ARG JAVET_NODE_VERSION=24.11.1
ARG JAVET_V8_VERSION=14.3.127.14
ARG JAVET_VERSION=5.0.2

###########################################
# Stage 1: Base with common dependencies
###########################################
FROM ubuntu:latest AS base

ARG JAVET_NODE_VERSION
ARG JAVET_V8_VERSION
ARG JAVET_VERSION

ENV JAVET_NODE_VERSION=${JAVET_NODE_VERSION}
ENV JAVET_V8_VERSION=${JAVET_V8_VERSION}
ENV JAVET_VERSION=${JAVET_VERSION}
ENV ROOT=/home/runner/work/Javet
ENV DEBIAN_FRONTEND=noninteractive

# Install common dependencies including Python
RUN apt-get update -y && \
    apt-get install -y \
    execstack \
    binutils \
    build-essential \
    libc++-dev \
    libc++abi-dev \
    wget \
    git \
    curl \
    unzip \
    software-properties-common \
    lsb-release \
    gnupg \
    python3 \
    python3-dev \
    python3-apt \
    sudo \
    file \
    && rm -rf /var/lib/apt/lists/*

# Setup Deno
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH="/root/.deno/bin:${PATH}"

# Setup JDK 8
RUN apt-get update -y && \
    apt-get install -y openjdk-8-jdk && \
    rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Setup CMake 3.23.x
RUN wget https://github.com/Kitware/CMake/releases/download/v3.23.5/cmake-3.23.5-linux-x86_64.tar.gz && \
    tar -xzf cmake-3.23.5-linux-x86_64.tar.gz -C /opt && \
    rm cmake-3.23.5-linux-x86_64.tar.gz
ENV PATH="/opt/cmake-3.23.5-linux-x86_64/bin:${PATH}"

WORKDIR ${ROOT}

###########################################
# Stage 2: Build V8 (both i18n and non-i18n)
###########################################
FROM base AS build-v8

# Copy Javet source code
COPY . ${ROOT}/Javet/

# Setup depot_tools
RUN mkdir -p ${ROOT}/google && \
    cd ${ROOT}/google && \
    git clone --depth=10 --branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git && \
    cd depot_tools && \
    git checkout remotes/origin/main

ENV PATH="${ROOT}/google/depot_tools:${PATH}"

# Fetch V8
RUN cd ${ROOT}/google && fetch v8

# Checkout V8 version
RUN cd ${ROOT}/google/v8 && git checkout ${JAVET_V8_VERSION}

# Install V8 build dependencies (non-interactive)
RUN cd ${ROOT}/google/v8 && ./build/install-build-deps.sh --no-prompt --no-chromeos-fonts

# Sync V8 dependencies
RUN cd ${ROOT}/google && gclient sync -D

# Apply V8 patches
RUN cd ${ROOT}/google/v8 && \
    sed -i '/#include "src\/libplatform\//a #include <cstdlib>' src/libplatform/default-thread-isolated-allocator.cc && \
    sed -i '/bool KernelHasPkruFix()/a const char* env = std::getenv("JAVET_DISABLE_PKU"); if (env && std::strlen(env) > 0) { return false; }' src/libplatform/default-thread-isolated-allocator.cc

# Build V8 non-i18n
RUN cd ${ROOT}/google/v8 && \
    mkdir -p out.gn.non-i18n/x64.release && \
    cp ${ROOT}/Javet/scripts/v8/gn/linux-x86_64-non-i18n-args.gn out.gn.non-i18n/x64.release/args.gn && \
    gn gen out.gn.non-i18n/x64.release && \
    ninja -C out.gn.non-i18n/x64.release v8_monolith

# Build V8 i18n
RUN cd ${ROOT}/google/v8 && \
    mkdir -p out.gn.i18n/x64.release && \
    cp ${ROOT}/Javet/scripts/v8/gn/linux-x86_64-i18n-args.gn out.gn.i18n/x64.release/args.gn && \
    gn gen out.gn.i18n/x64.release && \
    ninja -C out.gn.i18n/x64.release v8_monolith

# Copy i18n data
RUN mkdir -p ${ROOT}/Javet/icu-v8 && \
    cp ${ROOT}/google/v8/third_party/icu/common/*.dat ${ROOT}/Javet/icu-v8/

# Build Javet JNI for V8 non-i18n
RUN cd ${ROOT}/Javet/cpp && \
    CC=${ROOT}/google/v8/third_party/llvm-build/Release+Asserts/bin/clang \
    CXX=${ROOT}/google/v8/third_party/llvm-build/Release+Asserts/bin/clang \
    sh ./build-linux-x86_64.sh -DV8_DIR=${ROOT}/google/v8

# Build Javet JNI for V8 i18n
RUN cd ${ROOT}/Javet/cpp && \
    CC=${ROOT}/google/v8/third_party/llvm-build/Release+Asserts/bin/clang \
    CXX=${ROOT}/google/v8/third_party/llvm-build/Release+Asserts/bin/clang \
    sh ./build-linux-x86_64.sh -DV8_DIR=${ROOT}/google/v8 -DENABLE_I18N=1

###########################################
# Stage 3: Build Node.js (both i18n and non-i18n)
###########################################
FROM base AS build-node

# Copy Javet source code
COPY . ${ROOT}/Javet/

# Install LLVM 21
RUN wget https://apt.llvm.org/llvm.sh && \
    chmod +x llvm.sh && \
    ./llvm.sh 21 && \
    rm llvm.sh

ENV PATH="/usr/lib/llvm-21/bin:${PATH}"

# Clone Node.js
RUN cd ${ROOT} && \
    git clone https://github.com/nodejs/node.git && \
    cd node && \
    git checkout v${JAVET_NODE_VERSION} && \
    sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' deps/v8/src/execution/isolate.h && \
    sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' deps/v8/src/heap/local-heap.h

# Build Node.js non-i18n
RUN cd ${ROOT}/node && \
    deno --allow-all ${ROOT}/Javet/scripts/deno/patch_node_build.ts -p ./ && \
    ./configure --enable-static --without-intl && \
    deno --allow-all ${ROOT}/Javet/scripts/deno/patch_node_build.ts -p ./ && \
    make -j$(nproc) && \
    mv out out.non-i18n

# Build Node.js i18n
RUN cd ${ROOT}/node && \
    deno --allow-all ${ROOT}/Javet/scripts/deno/patch_node_build.ts -p ./ && \
    ./configure --enable-static --with-intl=full-icu && \
    deno --allow-all ${ROOT}/Javet/scripts/deno/patch_node_build.ts -p ./ && \
    make -j$(nproc) && \
    mv out out.i18n

# Copy i18n data
RUN mkdir -p ${ROOT}/Javet/icu-node && \
    cp ${ROOT}/node/deps/icu-tmp/*.dat ${ROOT}/Javet/icu-node/

# Build Javet JNI for Node non-i18n
RUN cd ${ROOT}/Javet/cpp && \
    sh ./build-linux-x86_64.sh -DNODE_DIR=${ROOT}/node && \
    cp ${ROOT}/Javet/src/main/resources/*.so ${ROOT}/Javet/artifacts-node-non-i18n/ || mkdir -p ${ROOT}/Javet/artifacts-node-non-i18n && cp ${ROOT}/Javet/src/main/resources/*.so ${ROOT}/Javet/artifacts-node-non-i18n/

# Build Javet JNI for Node i18n
RUN cd ${ROOT}/Javet/cpp && \
    sh ./build-linux-x86_64.sh -DNODE_DIR=${ROOT}/node -DENABLE_I18N=1 && \
    cp ${ROOT}/Javet/src/main/resources/*.so ${ROOT}/Javet/artifacts-node-i18n/ || mkdir -p ${ROOT}/Javet/artifacts-node-i18n && cp ${ROOT}/Javet/src/main/resources/*.so ${ROOT}/Javet/artifacts-node-i18n/

###########################################
# Stage 4: Build final JAR
###########################################
FROM base AS build-jar

# Copy Javet source code
COPY . ${ROOT}/Javet/

# Copy V8 artifacts from build-v8 stage
COPY --from=build-v8 ${ROOT}/Javet/src/main/resources/*.so ${ROOT}/Javet/src/main/resources/
COPY --from=build-v8 ${ROOT}/Javet/icu-v8 ${ROOT}/Javet/icu-v8

# Copy Node artifacts from build-node stage
COPY --from=build-node ${ROOT}/Javet/artifacts-node-non-i18n/*.so ${ROOT}/Javet/src/main/resources/
COPY --from=build-node ${ROOT}/Javet/artifacts-node-i18n/*.so ${ROOT}/Javet/src/main/resources/
COPY --from=build-node ${ROOT}/Javet/icu-node ${ROOT}/Javet/icu-node

# Setup i18n locations
RUN mkdir -p ${ROOT}/node/deps/icu-tmp && \
    mv ${ROOT}/Javet/icu-node/*.dat ${ROOT}/node/deps/icu-tmp/ && \
    mkdir -p ${ROOT}/google/v8/third_party/icu/common && \
    mv ${ROOT}/Javet/icu-v8/*.dat ${ROOT}/google/v8/third_party/icu/common/

# Install Gradle
RUN wget https://services.gradle.org/distributions/gradle-8.10.2-bin.zip && \
    unzip gradle-8.10.2-bin.zip -d /opt && \
    rm gradle-8.10.2-bin.zip
ENV PATH="/opt/gradle-8.10.2/bin:${PATH}"

# Build JAR with tests
RUN cd ${ROOT}/Javet && \
    touch src/main/resources/libjavet-v8-*-x86_64.v* && \
    gradle build test --rerun-tasks && \
    touch src/main/resources/libjavet-node-*-x86_64.v* && \
    gradle build test --rerun-tasks && \
    touch src/main/resources/libjavet-v8-*-x86_64-i18n.v* && \
    gradle build test --rerun-tasks && \
    touch src/main/resources/libjavet-node-*-x86_64-i18n.v* && \
    gradle build test --rerun-tasks && \
    gradle build generatePomFileForGeneratePomPublication

# Output directory
WORKDIR ${ROOT}/Javet/build/libs

# Default command
CMD ["bash"]
