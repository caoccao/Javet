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

# Usage: docker build -t javet:local -f docker/windows-x86_64/build.Dockerfile .

FROM sjtucaocao/javet-windows:1.1.5

SHELL ["cmd", "/S", "/C"]
WORKDIR /

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN build-windows.cmd -DV8_DIR="C:\google\v8"
RUN build-windows.cmd -DNODE_DIR="C:\node"

# Build Jar
WORKDIR /Javet
RUN touch src/main/resources/libjavet-v8*
RUN gradlew build test --rerun-tasks --debug
RUN touch src/main/resources/libjavet-node*
RUN gradlew test --rerun-tasks --debug

VOLUME C:\\output

# Completed
RUN echo Javet build is completed.
