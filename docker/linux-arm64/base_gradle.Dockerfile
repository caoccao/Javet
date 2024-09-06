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
#  -t sjtucaocao/javet:arm64-3.1.7 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  -f docker/linux-arm64/base_gradle.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet

FROM ${JAVET_REPO}:arm64-base-jvm

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-arm64

RUN mkdir Javet
WORKDIR /Javet
COPY . .
RUN gradle dependencies
WORKDIR /
RUN rm -rf /Javet
