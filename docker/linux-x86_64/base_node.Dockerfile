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
#  -t sjtucaocao/javet:x86_64-base-node_20.12.2 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  --build-arg JAVET_NODE_VERSION=20.12.2 \
#  -f docker/linux-x86_64/base_node.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_NODE_VERSION=20.12.2

FROM ${JAVET_REPO}:x86_64-base-jvm

ARG JAVET_NODE_VERSION

RUN if [ -z "$JAVET_NODE_VERSION" ]; then echo 'Build argument JAVET_NODE_VERSION must be specified. Exiting.'; exit 1; fi

# Prepare Node.js v18
WORKDIR /
COPY ./scripts/shell/fetch_node_source.sh .
RUN bash ./fetch_node_source.sh

ARG NUM_JOBS

# Build Node.js
WORKDIR /node
COPY ./scripts/python/patch_node_build.py .
COPY ./scripts/shell/build_node_source.sh .
RUN bash ./build_node_source.sh
