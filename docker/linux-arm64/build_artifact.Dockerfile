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

# Usage: docker build \
#  -t javet-local \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  --build-arg JAVET_NODE_VERSION=18.12.1 \
#  --build-arg JAVET_V8_VERSION=10.8.168.20 \
#  --build-arg JAVET_VERSION=3.0.3 \
#  -f docker/linux-arm64/build_artifact.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_NODE_VERSION=18.12.1
ARG JAVET_V8_VERSION=10.8.168.20
ARG JAVET_VERSION=3.0.3

FROM ${JAVET_REPO}:arm64-base-node_${JAVET_NODE_VERSION} as base-node

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
RUN mkdir Javet
WORKDIR /Javet
COPY . .
WORKDIR /Javet/cpp
RUN sh ./build-linux-arm64.sh -DNODE_DIR=/node

ARG JAVET_REPO
ARG JAVET_V8_VERSION

FROM ${JAVET_REPO}:arm64-base-v8_${JAVET_V8_VERSION} as base-v8

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64
RUN mkdir Javet
WORKDIR /Javet
COPY . .
WORKDIR /Javet/cpp
RUN sh ./build-linux-arm64.sh -DV8_DIR=/google/v8

ARG JAVET_REPO
ARG JAVET_VERSION

FROM ${JAVET_REPO}:arm64-${JAVET_VERSION}

RUN mkdir Javet
WORKDIR /Javet
COPY . .

COPY --from=base-node /Javet/src/main/resources /Javet/src/main/resources
COPY --from=base-v8 /Javet/src/main/resources /Javet/src/main/resources
RUN scripts/shell/build_javet_artifacts.sh
