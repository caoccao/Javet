# Copyright (c) 2021-2026. caoccao.com Sam Cao
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

# Usage: docker build -t sjtucaocao/javet-arm64:6.0.4 -f docker/linux-arm64/base_all_in_one.Dockerfile .

ARG JAVET_NODE_VERSION=26.13.1
ARG JAVET_V8_VERSION=15.8.62.52
ARG TEMPORAL_VERSION=0.2.3

FROM ubuntu:24.04

ARG JAVET_NODE_VERSION
ARG JAVET_V8_VERSION
ARG TEMPORAL_VERSION

WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install --upgrade -qq -y --no-install-recommends build-essential ca-certificates curl execstack file git gnupg lsb-release maven patchelf pkg-config python3 python3-pip software-properties-common sudo unzip wget xz-utils zip
RUN apt-get upgrade -y

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v4.2.3/cmake-4.2.3-linux-aarch64.tar.gz
RUN tar -xzf cmake-4.2.3-linux-aarch64.tar.gz -C /opt
RUN rm cmake-4.2.3-linux-aarch64.tar.gz
ENV PATH=/opt/cmake-4.2.3-linux-aarch64/bin:$PATH

# Install Deno
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH=/root/.deno/bin:$PATH

# Install Rust
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH=/root/.cargo/bin:$PATH

# Install LLVM
# V8 only ships the clang toolchain for x86_64 Linux hosts, so V8, Node.js and Javet are built with this LLVM.
RUN wget https://apt.llvm.org/llvm.sh
RUN chmod +x llvm.sh
RUN ./llvm.sh 24 all
RUN rm llvm.sh
ENV CC=/usr/lib/llvm-24/bin/clang
ENV CXX=/usr/lib/llvm-24/bin/clang++

# Prepare V8
WORKDIR /google
ENV DEPOT_TOOLS_UPDATE=0
ENV PATH=/google/depot_tools:$PATH
COPY ./scripts/shell/fetch_v8_source.sh .
RUN bash fetch_v8_source.sh
RUN rm fetch_v8_source.sh
RUN echo V8 preparation is completed.

# Build V8
WORKDIR /google/v8
COPY ./scripts/deno /Javet/scripts/deno
COPY ./scripts/v8/gn /Javet/scripts/v8/gn
COPY ./scripts/shell/build_v8_source.sh .
RUN JAVET_HOME=/Javet CLANG_BASE_PATH=/usr/lib/llvm-24 bash ./build_v8_source.sh
RUN rm build_v8_source.sh
RUN echo V8 build is completed.

# Build Temporal CAPI
WORKDIR /
RUN git clone https://github.com/boa-dev/temporal.git
WORKDIR /temporal
RUN git checkout v${TEMPORAL_VERSION}
RUN deno run --allow-all /Javet/scripts/deno/patch_v8_temporal.ts -p ./
RUN cargo build --release --package temporal_capi --features compiled_data,zoneinfo64
RUN cp target/release/libtemporal_capi.a /google/v8/out.gn.linux.non-i18n/arm64.release/obj/
RUN cp target/release/libtemporal_capi.a /google/v8/out.gn.linux.i18n/arm64.release/obj/
WORKDIR /
RUN rm -rf /temporal
RUN echo Temporal CAPI build is completed.

# Prepare Node.js
WORKDIR /
COPY ./scripts/shell/fetch_node_source.sh .
RUN bash ./fetch_node_source.sh
RUN rm fetch_node_source.sh
RUN echo Node.js preparation is completed.

# Build Node.js
WORKDIR /node
COPY ./scripts/shell/build_node_source.sh .
RUN JAVET_HOME=/Javet bash ./build_node_source.sh
RUN rm build_node_source.sh
RUN rm -rf /Javet
RUN echo Node.js build is completed.

# Prepare Javet Build Environment
RUN apt-get install --upgrade -qq -y --no-install-recommends openjdk-8-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
RUN wget https://services.gradle.org/distributions/gradle-8.10.2-bin.zip
RUN unzip -q gradle-8.10.2-bin.zip -d /opt
RUN rm gradle-8.10.2-bin.zip
ENV PATH=/opt/gradle-8.10.2/bin:$PATH

# Shrink
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
