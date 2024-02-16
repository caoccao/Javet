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
#  -t sjtucaocao/javet:arm64-base-v8_12.2.281.16 \
#  --build-arg JAVET_REPO=sjtucaocao/javet \
#  --build-arg JAVET_V8_VERSION=12.2.281.16 \
#  -f docker/linux-arm64/base_v8.Dockerfile .

ARG JAVET_REPO=sjtucaocao/javet
ARG JAVET_V8_VERSION=12.2.281.16

FROM ${JAVET_REPO}:arm64-base-jvm

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
RUN sed -i -e "s/target_cpu=\"x64\" v8_target_cpu=\"arm64/target_cpu=\"arm64\" v8_target_cpu=\"arm64/" infra/mb/mb_config.pyl
RUN python3 tools/dev/v8gen.py arm64.release -- 'target_cpu="arm64"' 'v8_target_cpu="arm64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false 'clang_base_path="/usr/lib/llvm-15"' clang_use_chrome_plugins=false blink_gc_plugin=false
COPY ./scripts/python/patch_v8_build.py .
RUN ninja -C out.gn/arm64.release v8_monolith || python3 patch_v8_build.py -p ./
RUN ninja -C out.gn/arm64.release v8_monolith
RUN rm patch_v8_build.py
RUN echo V8 build is completed.
