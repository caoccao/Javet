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

# Usage: docker build -t javet-android:local -f docker/android/build.Dockerfile .

FROM sjtucaocao/javet-android:1.1.5
WORKDIR /

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN sh ./build-android.sh -DV8_DIR=/google/v8 -DCMAKE_ANDROID_NDK=/android-ndk-r23b -DCMAKE_ANDROID_ARCH=arm
RUN sh ./build-android.sh -DV8_DIR=/google/v8 -DCMAKE_ANDROID_NDK=/android-ndk-r23b -DCMAKE_ANDROID_ARCH=arm64
RUN sh ./build-android.sh -DV8_DIR=/google/v8 -DCMAKE_ANDROID_NDK=/android-ndk-r23b -DCMAKE_ANDROID_ARCH=x86
RUN sh ./build-android.sh -DV8_DIR=/google/v8 -DCMAKE_ANDROID_NDK=/android-ndk-r23b -DCMAKE_ANDROID_ARCH=x86_64

# Build AAR
WORKDIR /Javet/scripts/python
RUN python3 patch_android_build.py
WORKDIR /Javet/android
RUN gradle build --debug

VOLUME /output

# Completed
RUN echo Javet Android build is completed.
