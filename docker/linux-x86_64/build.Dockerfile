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

# Usage: docker build -f docker/linux-x86_64/build.Dockerfile .

FROM sjtucaocao/javet:0.9.8
WORKDIR /

# Preparation
RUN apt-get install --upgrade -qq -y --no-install-recommends execstack
RUN apt-get install --upgrade -qq -y --no-install-recommends openjdk-8-jdk
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

# Copy Javet
RUN mkdir Javet
WORKDIR /Javet
COPY . .

# Build JNI
WORKDIR /Javet/cpp
RUN ls -al .
RUN sh ./build.sh -DV8_DIR=/google/v8
RUN sh ./build.sh -DNODE_DIR=/node

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
ENV SDKMAN_HOME="/root/.sdkman"
ENV GRADLE_HOME="${SDKMAN_HOME}/candidates/gradle/current"
RUN curl -s https://get.sdkman.io | bash
RUN source ${SDKMAN_HOME}/bin/sdkman-init.sh \
    && sdk install gradle 7.0.2 \
    && rm -rf ${SDKMAN_HOME}/archives/* \
    && rm -rf ${SDKMAN_HOME}/tmp/*
ENV PATH=$GRADLE_HOME/bin:$PATH

# Build Jar
WORKDIR /Javet
RUN touch src/main/resources/libjavet-v8*
RUN gradle test --rerun-tasks; exit 0
RUN gradle test --rerun-tasks
RUN touch src/main/resources/libjavet-node*
RUN gradle test --rerun-tasks

# Completed
RUN echo Javet build is completed.
