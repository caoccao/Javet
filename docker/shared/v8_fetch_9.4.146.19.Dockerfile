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

# Usage: docker build -t sjtucaocao/v8_fetch:9.4.146.19 -f docker/shared/v8_fetch_9.4.146.19.Dockerfile .

# This is experimental and does not completely work.

FROM ubuntu:20.04
WORKDIR /

# Update Ubuntu
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update
RUN apt-get install --upgrade -qq -y --no-install-recommends git curl wget build-essential software-properties-common patchelf maven sudo zip unzip execstack cmake
RUN apt-get install --upgrade -qq -y --no-install-recommends python3 python python3-pip python3-distutils python3-testresources
RUN apt-get upgrade -y
RUN pip3 install coloredlogs

# Prepare V8
RUN mkdir google
WORKDIR /google
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
WORKDIR /google/depot_tools
RUN git checkout remotes/origin/main
ENV PATH=/google/depot_tools:$PATH
WORKDIR /google
RUN fetch v8
WORKDIR /google/v8
RUN git checkout 9.4.146.19
RUN sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
RUN ./build/install-build-deps.sh
RUN sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
WORKDIR /google
RUN echo V8 Linux fetch is completed.
