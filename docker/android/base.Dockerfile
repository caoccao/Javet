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

# Usage: docker build -t sjtucaocao/javet-android:1.1.5 -f docker/android/base.Dockerfile .

FROM ubuntu:20.04
WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
RUN echo Cache V8
RUN apt-get update
RUN apt-get install --upgrade -qq -y --no-install-recommends git curl wget build-essential software-properties-common patchelf maven sudo zip unzip execstack cmake
RUN apt-get install --upgrade -qq -y --no-install-recommends python3 python python3-pip python3-distutils python3-testresources
RUN apt-get upgrade -y
RUN pip3 install coloredlogs

# Install CMake
RUN wget https://github.com/Kitware/CMake/releases/download/v3.21.4/cmake-3.21.4-linux-x86_64.sh
RUN chmod 755 cmake-3.21.4-linux-x86_64.sh
RUN mkdir -p /usr/lib/cmake
RUN ./cmake-3.21.4-linux-x86_64.sh --skip-license --exclude-subdir --prefix=/usr/lib/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /usr/bin/cmake
RUN ln -sf /usr/lib/cmake/bin/cmake /bin/cmake
RUN rm cmake-3.21.4-linux-x86_64.sh

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
RUN git checkout 10.3.174.14
RUN sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
RUN ./build/install-build-deps.sh
RUN sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
WORKDIR /google
RUN echo 'target_os = ["android"]' >> .gclient
RUN gclient sync
RUN echo V8 preparation is completed.

# Prepare Android NDK
WORKDIR /
RUN wget https://dl.google.com/android/repository/android-ndk-r23b-linux.zip
RUN unzip android-ndk-r23b-linux.zip
RUN rm android-ndk-r23b-linux.zip

# Prepare Android SDK
WORKDIR /google/v8/third_party/android_sdk/public/cmdline-tools/latest/bin
RUN yes | ./sdkmanager --licenses
RUN ./sdkmanager "build-tools;30.0.2" "platforms;android-30"
ENV ANDROID_SDK_ROOT=/google/v8/third_party/android_sdk

# Patch Docker
RUN apt-get install --upgrade -qq -y --no-install-recommends gcc-multilib

# Build V8
WORKDIR /google/v8
COPY ./scripts/python/patch_v8_build.py .
RUN python tools/dev/v8gen.py arm.release -- 'target_os="android"' 'target_cpu="arm"' 'v8_target_cpu="arm"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/arm.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/arm.release v8_monolith
RUN python tools/dev/v8gen.py arm64.release -- 'target_os="android"' 'target_cpu="arm64"' 'v8_target_cpu="arm64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/arm64.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/arm64.release v8_monolith
RUN python tools/dev/v8gen.py ia32.release -- 'target_os="android"' 'target_cpu="x86"' 'v8_target_cpu="x86"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/ia32.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/ia32.release v8_monolith
RUN python tools/dev/v8gen.py x64.release -- 'target_os="android"' 'target_cpu="x64"' 'v8_target_cpu="x64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/x64.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/x64.release v8_monolith
RUN rm patch_v8_build.py
RUN echo V8 build is completed.

# Prepare Javet Build Environment
RUN apt-get install --upgrade -qq -y --no-install-recommends openjdk-11-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
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
WORKDIR /Javet/android
RUN gradle dependencies
WORKDIR /
RUN rm -rf /Javet

# Completed
RUN echo Javet Android build base image is completed.
