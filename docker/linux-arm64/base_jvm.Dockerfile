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

# Usage: docker build -t sjtucaocao/javet:arm64-base-jvm -f docker/linux-arm64/base_jvm.Dockerfile .

FROM ubuntu:24.04
WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
# files need to be cleaned/deleted in the same RUN layer that adds them, else there is no actual size reduction benefit
# the files remain in the image, just the layer marks the file as deleted and is not visible inside the OS
RUN apt-get update --yes \
	&& apt-get install --upgrade -qq --yes --no-install-recommends \
	build-essential ca-certificates curl execstack file git gnupg lsb-release maven openjdk-8-jdk \
	patchelf python3 python3-pip software-properties-common sudo unzip wget xz-utils zip pkg-config \
	&& apt-get upgrade --yes \
	&& apt-get clean --yes

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v4.2.3/cmake-4.2.3-linux-aarch64.tar.gz \
	&& tar -xzf cmake-4.2.3-linux-aarch64.tar.gz -C /opt \
	&& rm cmake-4.2.3-linux-aarch64.tar.gz
ENV PATH=/opt/cmake-4.2.3-linux-aarch64/bin:$PATH

# Install Deno
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH=/root/.deno/bin:$PATH

# Install Rust
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH=/root/.cargo/bin:$PATH

# Install LLVM
# V8 only ships the clang toolchain for x86_64 Linux hosts, so V8, Node.js and Javet are built with this LLVM.
RUN wget https://apt.llvm.org/llvm.sh \
	&& chmod +x llvm.sh \
	&& ./llvm.sh 24 all \
	&& rm llvm.sh \
	&& apt-get clean --yes
ENV CC=/usr/lib/llvm-24/bin/clang
ENV CXX=/usr/lib/llvm-24/bin/clang++

# Prepare Javet Build Environment
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
RUN wget https://services.gradle.org/distributions/gradle-8.10.2-bin.zip \
	&& unzip -q gradle-8.10.2-bin.zip -d /opt \
	&& rm gradle-8.10.2-bin.zip
ENV PATH=/opt/gradle-8.10.2/bin:$PATH
