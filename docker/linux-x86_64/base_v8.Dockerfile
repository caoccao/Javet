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
#  -t sjtucaocao/javet:x86_64-base-v8_12.3.219.10 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  --build-arg JAVET_V8_VERSION=12.3.219.10 \
#  -f docker/linux-x86_64/base_v8.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_V8_VERSION=12.3.219.10

FROM ${JAVET_REPO}:x86_64-base-jvm

ARG JAVET_V8_VERSION

RUN if [ -z "$JAVET_V8_VERSION" ]; then echo 'Build argument JAVET_V8_VERSION must be specified. Exiting.'; exit 1; fi

# Prepare V8
WORKDIR /google
ENV DEPOT_TOOLS_UPDATE=0
ENV PATH=/google/depot_tools:$PATH
COPY ./scripts/shell/fetch_v8_source.sh .
RUN bash fetch_v8_source.sh

# Build V8
WORKDIR /google/v8
COPY ./scripts/python/patch_v8_build.py .
COPY ./scripts/shell/build_v8_source.sh .
RUN bash ./build_v8_source.sh
