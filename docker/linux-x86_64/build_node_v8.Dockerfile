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
#  -t sjtucaocao/javet:x86_64-v8-10.8.168.20_node-18.12.1 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  -f docker/linux-x86_64/base_node.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_NODE_VERSION=18.12.1
ARG JAVET_V8_VERSION=10.8.168.20

FROM ${JAVET_REPO}:x86_64-base-v8-${JAVET_V8_VERSION} as base-v8

# the ARG JAVET_NODE_VERSION needs to be declared twice due to how Dockerfile treats ARG and FROM
# Reference - https://docs.docker.com/engine/reference/builder/#understand-how-arg-and-from-interact
ARG JAVET_REPO
ARG JAVET_NODE_VERSION
FROM ${JAVET_REPO}:x86_64-base-node-${JAVET_NODE_VERSION} as base-node

# we could base the final image off the `base-v8` image, that would save us time copying the /google folder
# however, the resulting image is almost 1GB bigger than using base-jvm as the base
# for now, we will optimize for getting a smaller image

# final image
ARG JAVET_REPO
FROM ${JAVET_REPO}:x86_64-base-jvm-latest

RUN mkdir -p /google && mkdir -p /node
COPY --from=base-v8 /google /google
COPY --from=base-node /node /node

WORKDIR /Javet