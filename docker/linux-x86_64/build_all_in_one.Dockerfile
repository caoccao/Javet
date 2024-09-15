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

# Usage: docker build -t javet:local -f docker/linux-x86_64/build_all_in_one.Dockerfile .

FROM sjtucaocao/javet:3.1.8
WORKDIR /

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN sh ./build-linux-x86_64.sh -DV8_DIR=/google/v8
RUN sh ./build-linux-x86_64.sh -DNODE_DIR=/node

# Build Jar
WORKDIR /Javet
RUN touch src/main/resources/libjavet-v8*
RUN gradle build test --rerun-tasks --debug
RUN touch src/main/resources/libjavet-node*
RUN gradle test --rerun-tasks --debug

VOLUME /output

# Completed
RUN echo Javet build is completed.
