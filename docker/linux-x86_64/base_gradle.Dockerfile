# Copyright (c) 2021-2022 caoccao.com Sam Cao
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
<<<<<<< HEAD
#  -t sjtucaocao/javet:x86_64-2.0.2 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
=======
#  -t sjtucaocao/javet:x86_64-2.0.3 \
#  --build-arg BASE_NODE_V8_IMAGE_TAG=sjtucaocao/javet:x86_64-base-node_18.12.1-v8_10.8.168.20 \
>>>>>>> d98f2c94 (🔧 build(Version): Update version to v2.0.3)
#  -f docker/linux-x86_64/base_gradle.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet

FROM ${JAVET_REPO}:x86_64-base-jvm

RUN mkdir Javet
WORKDIR /Javet
COPY . .
RUN gradle dependencies
WORKDIR /
RUN rm -rf /Javet
