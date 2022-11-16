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
#  -t javet-local \
#  --build-arg JAVET_GRADLE_IMAGE_TAG=sjtucaocao/javet-linux-dev:2.0.1 \
#  -f docker/linux-x86_64/artifact.Dockerfile .

ARG JAVET_GRADLE_IMAGE_TAG=sjtucaocao/javet-linux-dev:2.0.1
FROM $JAVET_GRADLE_IMAGE_TAG

WORKDIR /Javet
COPY . .

RUN scripts/shell/build_javet_artifacts.sh

