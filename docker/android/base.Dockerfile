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

# Usage: docker build -t sjtucaocao/javet-android:6.0.4 -f docker/android/base.Dockerfile .

# Base image for building Javet for Android in the V8 mode
# Based on .github/workflows/android_v8_build.yml

ARG ANDROID_NDK_VERSION=r30
ARG JAVET_V8_VERSION=15.8.62.52
ARG TEMPORAL_VERSION=0.2.3

# V8 and Javet cross compile Android on x86_64 Linux only.
FROM --platform=linux/amd64 ubuntu:24.04

ARG ANDROID_NDK_VERSION
ARG JAVET_V8_VERSION
ARG TEMPORAL_VERSION

ENV DEBIAN_FRONTEND=noninteractive
ENV ANDROID_HOME=/android-sdk
ENV ANDROID_NDK_HOME=/android-ndk-${ANDROID_NDK_VERSION}
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
WORKDIR /

# Update Ubuntu
RUN apt-get update -y && \
    apt-get install -y \
    build-essential \
    curl \
    execstack \
    file \
    gcc-multilib \
    git \
    lsb-release \
    openjdk-17-jdk-headless \
    python3 \
    sudo \
    unzip \
    wget \
    xz-utils \
    zip \
    && rm -rf /var/lib/apt/lists/*

# Setup Deno
RUN curl -fsSL https://deno.land/install.sh | sh
ENV PATH="/root/.deno/bin:${PATH}"

# Setup Rust
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
ENV PATH="/root/.cargo/bin:${PATH}"
RUN rustup target add aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android

# Setup CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v4.2.3/cmake-4.2.3-linux-x86_64.tar.gz && \
    tar -xzf cmake-4.2.3-linux-x86_64.tar.gz -C /opt && \
    rm cmake-4.2.3-linux-x86_64.tar.gz
ENV PATH="/opt/cmake-4.2.3-linux-x86_64/bin:${PATH}"

# Setup Gradle
RUN wget https://services.gradle.org/distributions/gradle-8.10.2-bin.zip && \
    unzip -q gradle-8.10.2-bin.zip -d /opt && \
    rm gradle-8.10.2-bin.zip
ENV PATH="/opt/gradle-8.10.2/bin:${PATH}"

# Setup Android NDK
RUN wget https://dl.google.com/android/repository/android-ndk-${ANDROID_NDK_VERSION}-linux.zip && \
    unzip -q android-ndk-${ANDROID_NDK_VERSION}-linux.zip && \
    rm android-ndk-${ANDROID_NDK_VERSION}-linux.zip

# Setup Android SDK
RUN wget https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    unzip -q commandlinetools-linux-13114758_latest.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm commandlinetools-linux-13114758_latest.zip
RUN yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager --licenses && \
    ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager "build-tools;33.0.1" "platforms;android-30"

# Prepare V8
WORKDIR /google
RUN git clone --depth=10 --branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git && \
    cd depot_tools && \
    git checkout remotes/origin/main && \
    ./ensure_bootstrap
ENV PATH="/google/depot_tools:${PATH}"
RUN fetch v8
WORKDIR /google/v8
RUN git checkout ${JAVET_V8_VERSION}
RUN ./build/install-build-deps.sh --no-prompt --no-chromeos-fonts
WORKDIR /google
RUN echo 'target_os = ["android"]' >> .gclient
RUN gclient sync -D
RUN echo V8 preparation is completed.

# Patch V8
# The NDK from V8 is broken, so it is replaced by the NDK installed above.
WORKDIR /google/v8
RUN rm -rf third_party/android_toolchain/ndk && \
    ln -s ${ANDROID_NDK_HOME} third_party/android_toolchain/ndk && \
    sed -i 's/max\.value_or({})/max.value_or(uint8_t{})/' src/compiler/turboshaft/wasm-shuffle-reducer.cc && \
    sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' src/execution/isolate.h && \
    sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' src/heap/local-heap.h

# Build V8
COPY ./scripts/v8/gn /Javet/scripts/v8/gn
RUN mkdir -p out.gn.android.non-i18n/arm.release && \
    cp /Javet/scripts/v8/gn/android-arm-non-i18n-args.gn out.gn.android.non-i18n/arm.release/args.gn && \
    gn gen out.gn.android.non-i18n/arm.release && \
    ninja -C out.gn.android.non-i18n/arm.release v8_monolith
RUN mkdir -p out.gn.android.i18n/arm.release && \
    cp /Javet/scripts/v8/gn/android-arm-i18n-args.gn out.gn.android.i18n/arm.release/args.gn && \
    gn gen out.gn.android.i18n/arm.release && \
    ninja -C out.gn.android.i18n/arm.release v8_monolith
RUN mkdir -p out.gn.android.non-i18n/arm64.release && \
    cp /Javet/scripts/v8/gn/android-arm64-non-i18n-args.gn out.gn.android.non-i18n/arm64.release/args.gn && \
    gn gen out.gn.android.non-i18n/arm64.release && \
    ninja -C out.gn.android.non-i18n/arm64.release v8_monolith
RUN mkdir -p out.gn.android.i18n/arm64.release && \
    cp /Javet/scripts/v8/gn/android-arm64-i18n-args.gn out.gn.android.i18n/arm64.release/args.gn && \
    gn gen out.gn.android.i18n/arm64.release && \
    ninja -C out.gn.android.i18n/arm64.release v8_monolith
RUN mkdir -p out.gn.android.non-i18n/ia32.release && \
    cp /Javet/scripts/v8/gn/android-x86-non-i18n-args.gn out.gn.android.non-i18n/ia32.release/args.gn && \
    gn gen out.gn.android.non-i18n/ia32.release && \
    ninja -C out.gn.android.non-i18n/ia32.release v8_monolith
RUN mkdir -p out.gn.android.i18n/ia32.release && \
    cp /Javet/scripts/v8/gn/android-x86-i18n-args.gn out.gn.android.i18n/ia32.release/args.gn && \
    gn gen out.gn.android.i18n/ia32.release && \
    ninja -C out.gn.android.i18n/ia32.release v8_monolith
RUN mkdir -p out.gn.android.non-i18n/x64.release && \
    cp /Javet/scripts/v8/gn/android-x86_64-non-i18n-args.gn out.gn.android.non-i18n/x64.release/args.gn && \
    gn gen out.gn.android.non-i18n/x64.release && \
    ninja -C out.gn.android.non-i18n/x64.release v8_monolith
RUN mkdir -p out.gn.android.i18n/x64.release && \
    cp /Javet/scripts/v8/gn/android-x86_64-i18n-args.gn out.gn.android.i18n/x64.release/args.gn && \
    gn gen out.gn.android.i18n/x64.release && \
    ninja -C out.gn.android.i18n/x64.release v8_monolith
RUN echo V8 build is completed.

# Build Temporal CAPI
COPY ./scripts/deno /Javet/scripts/deno
WORKDIR /
RUN git clone https://github.com/boa-dev/temporal.git
WORKDIR /temporal
RUN git checkout v${TEMPORAL_VERSION} && \
    deno run --allow-all /Javet/scripts/deno/patch_v8_temporal.ts -p ./
RUN cargo build --release --target armv7-linux-androideabi --package temporal_capi --features compiled_data,zoneinfo64 && \
    cp target/armv7-linux-androideabi/release/libtemporal_capi.a /google/v8/out.gn.android.non-i18n/arm.release/obj/ && \
    cp target/armv7-linux-androideabi/release/libtemporal_capi.a /google/v8/out.gn.android.i18n/arm.release/obj/
RUN cargo build --release --target aarch64-linux-android --package temporal_capi --features compiled_data,zoneinfo64 && \
    cp target/aarch64-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.non-i18n/arm64.release/obj/ && \
    cp target/aarch64-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.i18n/arm64.release/obj/
RUN cargo build --release --target i686-linux-android --package temporal_capi --features compiled_data,zoneinfo64 && \
    cp target/i686-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.non-i18n/ia32.release/obj/ && \
    cp target/i686-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.i18n/ia32.release/obj/
RUN cargo build --release --target x86_64-linux-android --package temporal_capi --features compiled_data,zoneinfo64 && \
    cp target/x86_64-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.non-i18n/x64.release/obj/ && \
    cp target/x86_64-linux-android/release/libtemporal_capi.a /google/v8/out.gn.android.i18n/x64.release/obj/
WORKDIR /
RUN rm -rf /temporal
RUN echo Temporal CAPI build is completed.

# Pre-cache Dependencies
WORKDIR /Javet
COPY . .
WORKDIR /Javet/android
RUN gradle dependencies
WORKDIR /
RUN rm -rf /Javet

# Shrink
RUN apt-get clean -y && \
    rm -rf /var/lib/apt/lists/*

# Completed
RUN echo Javet Android build base image is completed.
