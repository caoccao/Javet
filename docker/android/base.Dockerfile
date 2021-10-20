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

# Usage: docker build -t sjtucaocao/javet-android:1.1.0 -f docker/android/base.Dockerfile .

# This is experimental and does not completely work.

FROM sjtucaocao/v8_fetch:9.4.146.19

# Prepare V8
WORKDIR /google
RUN echo 'target_os = ["android"]' >> .gclient
RUN gclient sync
RUN echo V8 preparation is completed.

# Build V8
WORKDIR /google/v8
RUN python tools/dev/v8gen.py arm.release -- 'target_os="android"' 'target_cpu="arm"' 'v8_target_cpu="arm"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/arm.release v8_monolith
RUN python tools/dev/v8gen.py arm64.release -- 'target_os="android"' 'target_cpu="arm64"' 'v8_target_cpu="arm64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/arm64.release v8_monolith
RUN python tools/dev/v8gen.py ia32.release -- 'target_os="android"' 'target_cpu="x86"' 'v8_target_cpu="x86"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/ia32.release v8_monolith
RUN python tools/dev/v8gen.py x64.release -- 'target_os="android"' 'target_cpu="x64"' 'v8_target_cpu="x64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/x64.release v8_monolith
RUN echo V8 build is completed.

# Prepare Android NDK
WORKDIR /
RUN wget https://dl.google.com/android/repository/android-ndk-r21e-linux-x86_64.zip
RUN unzip android-ndk-r21e-linux-x86_64.zip

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

# Completed
RUN echo Javet Android build base image is completed.
