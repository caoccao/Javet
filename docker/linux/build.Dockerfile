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

# Usage: docker build \
#	-t javet:local \
#	--build-context gradle_javet_buildenv=docker/linux/base.Dockerfile \
#	-f docker/linux/build.Dockerfile .

# OR for pulling from dockerhub instead of building v8 and Node from scratch

# Usage: docker build \
#	-t javet:local \
#	--build-arg JAVET_GRADLE_IMAGE_TAG=sjtucaocao/javet \
#	-f docker/linux/build.Dockerfile .

ARG JAVET_GRADLE_IMAGE_TAG=gradle_javet_buildenv
FROM $JAVET_GRADLE_IMAGE_TAG
WORKDIR /

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN sh ./build-linux.sh -DV8_DIR=/google/v8
RUN sh ./build-linux.sh -DNODE_DIR=/node

# Build Jar
WORKDIR /Javet
RUN touch src/main/resources/libjavet-v8*
RUN gradle build test --rerun-tasks --debug
RUN touch src/main/resources/libjavet-node*
RUN gradle test --rerun-tasks --debug

VOLUME /output

# Completed
RUN echo Javet build is completed.
