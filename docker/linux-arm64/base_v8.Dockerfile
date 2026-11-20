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

# Usage: docker build \
#  -t sjtucaocao/javet:arm64-base-v8_15.8.62.52 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  --build-arg JAVET_V8_VERSION=15.8.62.52 \
#  -f docker/linux-arm64/base_v8.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_V8_VERSION=15.8.62.52
ARG TEMPORAL_VERSION=0.2.3

FROM ${JAVET_REPO}:arm64-base-jvm

ARG JAVET_V8_VERSION
ARG TEMPORAL_VERSION

RUN if [ -z "$JAVET_V8_VERSION" ]; then echo 'Build argument JAVET_V8_VERSION must be specified. Exiting.'; exit 1; fi

# Prepare V8
WORKDIR /google
ENV DEPOT_TOOLS_UPDATE=0
ENV PATH=/google/depot_tools:$PATH
COPY ./scripts/shell/fetch_v8_source.sh .
RUN bash fetch_v8_source.sh

# Build V8
# The clang toolchain from V8 only runs on x86_64, so V8 is built with the LLVM from the base image.
WORKDIR /google/v8
COPY ./scripts/deno /Javet/scripts/deno
COPY ./scripts/v8/gn /Javet/scripts/v8/gn
COPY ./scripts/shell/build_v8_source.sh .
RUN JAVET_HOME=/Javet CLANG_BASE_PATH=/usr/lib/llvm-24 bash ./build_v8_source.sh
RUN rm build_v8_source.sh
RUN echo V8 build is completed.

# Build Temporal CAPI
WORKDIR /
RUN git clone https://github.com/boa-dev/temporal.git
WORKDIR /temporal
RUN git checkout v${TEMPORAL_VERSION} && \
    deno run --allow-all /Javet/scripts/deno/patch_v8_temporal.ts -p ./ && \
    cargo build --release --package temporal_capi --features compiled_data,zoneinfo64 && \
    cp target/release/libtemporal_capi.a /google/v8/out.gn.linux.non-i18n/arm64.release/obj/ && \
    cp target/release/libtemporal_capi.a /google/v8/out.gn.linux.i18n/arm64.release/obj/
WORKDIR /
RUN rm -rf /temporal /Javet
RUN echo Temporal CAPI build is completed.
