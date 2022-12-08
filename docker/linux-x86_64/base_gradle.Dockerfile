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
#  -t sjtucaocao/javet:x86_64-2.0.2 \
#  --build-arg BASE_NODE_V8_IMAGE_TAG=sjtucaocao/javet:x86_64-base-node_18.12.1-v8_10.8.168.20 \
#  -f docker/linux-x86_64/base_gradle.Dockerfile .

ARG BASE_NODE_V8_IMAGE_TAG=sjtucaocao/javet:x86_64-base-node_18.12.1-v8_10.8.168.20

FROM ${BASE_NODE_V8_IMAGE_TAG}

WORKDIR /Javet
COPY . .
RUN gradle dependencies && cd / && rm -rf /Javet