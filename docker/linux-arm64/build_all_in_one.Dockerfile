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

# Usage: docker build -t javet:local -f docker/linux-arm64/build_all_in_one.Dockerfile .

# The base image is built by docker/linux-arm64/base_all_in_one.Dockerfile.
FROM sjtucaocao/javet-arm64:6.0.4
WORKDIR /

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN deno run build --os linux --arch arm64 --v8-dir /google/v8
RUN deno run build --os linux --arch arm64 --v8-dir /google/v8 --i18n
RUN deno run build --os linux --arch arm64 --node-dir /node
RUN deno run build --os linux --arch arm64 --node-dir /node --i18n

# Build Jar
WORKDIR /Javet
RUN bash scripts/shell/build_javet_artifacts.sh

VOLUME /output

# Completed
RUN echo Javet build is completed.
