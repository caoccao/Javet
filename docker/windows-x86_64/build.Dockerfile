# Copyright (c) 2021-2025. caoccao.com Sam Cao
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

# Preparation:
#   Visual Studio installer validates free disk space and refuses to work with the docker default one.
#   Please follow the steps to set free disk space to 120GB.
#   1. Update daemon.json
#     "storage-opts": [
#       "dm.basesize=120GB",
#       "size=120GB"
#     ]
#   2. Restart WSL2
#   3. Restart docker

# set DOCKER_DEFAULT_PLATFORM=windows/amd64
# Usage: docker build -t sjtucaocao/javet-windows-x86_64:5.0.3 -m 8G -f docker/windows-x86_64/build.Dockerfile .

# Multi-stage Dockerfile for building Javet on Windows x86_64
# Based on .github/workflows/windows_x86_64_build.yml

# Build arguments
ARG JAVET_NODE_VERSION=24.11.1
ARG JAVET_V8_VERSION=14.3.127.14
ARG JAVET_VERSION=5.0.3

###########################################
# Stage 1: Base with common dependencies
###########################################
FROM mcr.microsoft.com/windows/server:ltsc2022 AS base

ARG JAVET_NODE_VERSION
ARG JAVET_V8_VERSION
ARG JAVET_VERSION

ENV JAVET_NODE_VERSION=${JAVET_NODE_VERSION}
ENV JAVET_V8_VERSION=${JAVET_V8_VERSION}
ENV JAVET_VERSION=${JAVET_VERSION}
ENV ROOT=C:/

SHELL ["cmd", "/S", "/C"]

# Install Chocolatey
RUN powershell -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"

# Install Python 3.12
RUN choco install -y python --version=3.12.0
RUN setx /M PATH "C:\Python312;C:\Python313\Scripts;%PATH%"

# Install Git for Windows
RUN choco install -y git

# Configure Git to use LF
RUN git config --global core.autocrlf false && \
    git config --global core.eol lf

# Install JDK 8
RUN choco install -y openjdk8

# Install Deno
RUN powershell -Command "\
    $ProgressPreference = 'SilentlyContinue'; \
    irm https://deno.land/install.ps1 | iex"
RUN setx /M PATH "C:\Users\ContainerAdministrator\.deno\bin;%PATH%"

# Install Visual Studio 2022 Community
# https://docs.microsoft.com/en-us/visualstudio/install/workload-component-id-vs-community
RUN powershell -Command "\
    $ProgressPreference = 'SilentlyContinue'; \
    Invoke-WebRequest -Uri 'https://aka.ms/vs/17/release/vs_community.exe' -OutFile 'vs_community.exe'"

RUN vs_community.exe --wait --quiet --norestart --nocache \
    --installPath "C:\Program Files\Microsoft Visual Studio\2022\Community" \
    --add Microsoft.VisualStudio.Workload.NativeDesktop \
    --add Microsoft.VisualStudio.Workload.NativeCrossPlat \
    --add Microsoft.VisualStudio.Component.VC.Llvm.Clang \
    --add Microsoft.VisualStudio.Component.VC.Llvm.ClangToolset \
    --includeRecommended \
    || IF "%ERRORLEVEL%"=="3010" EXIT 0

RUN del /q vs_community.exe

# Setup Visual Studio environment
RUN setx /M PATH "C:\Program Files\Microsoft Visual Studio\2022\Community\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin;%PATH%"

WORKDIR C:/

###########################################
# Stage 2: Build V8 (both i18n and non-i18n)
###########################################
FROM base AS build-v8

# Copy Javet source code
COPY . C:/Javet/

# Setup depot_tools
RUN mkdir google && \
    cd google && \
    git clone --depth=10 --branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git && \
    cd depot_tools && \
    git checkout remotes/origin/main

ENV PATH=C:/google/depot_tools;%PATH%
ENV DEPOT_TOOLS_WIN_TOOLCHAIN=0

# Fetch V8
RUN cd google && depot_tools\fetch.bat v8

# Checkout V8 version
RUN cd google\v8 && git checkout %JAVET_V8_VERSION%

# Sync V8 dependencies
RUN cd google && depot_tools\gclient.bat sync -D

# Build V8 non-i18n
RUN cd google\v8 && \
    mkdir out.gn\x64.release && \
    copy C:\Javet\src\scripts\v8\gn\windows-x86_64-non-i18n-args.gn out.gn\x64.release\args.gn && \
    ..\depot_tools\gn.bat gen out.gn\x64.release && \
    ..\depot_tools\ninja.bat -C out.gn\x64.release v8_monolith || deno --allow-all C:\Javet\src\scripts\deno\patch_v8_build.ts -p .\ && \
    ..\depot_tools\ninja.bat -C out.gn\x64.release v8_monolith && \
    move out.gn out.gn.non-i18n

# Build V8 i18n
RUN cd google\v8 && \
    mkdir out.gn\x64.release && \
    copy C:\Javet\src\scripts\v8\gn\windows-x86_64-i18n-args.gn out.gn\x64.release\args.gn && \
    ..\depot_tools\gn.bat gen out.gn\x64.release && \
    ..\depot_tools\ninja.bat -C out.gn\x64.release v8_monolith || deno --allow-all C:\Javet\src\scripts\deno\patch_v8_build.ts -p .\ && \
    ..\depot_tools\ninja.bat -C out.gn\x64.release v8_monolith && \
    move out.gn out.gn.i18n

# Copy i18n data
RUN mkdir C:\icu-v8 && \
    copy google\v8\third_party\icu\common\*.dat C:\icu-v8

# Build Javet JNI for V8 non-i18n
RUN cd C:\Javet\src\cpp && \
    build-windows.cmd -DV8_DIR=C:\google\v8 -T ClangCL
RUN mkdir C:\artifacts-v8-non-i18n && \
    copy C:\Javet\src\main\resources\*.dll C:\artifacts-v8-non-i18n

# Build Javet JNI for V8 i18n
RUN cd C:\Javet\src\cpp && \
    build-windows.cmd -DV8_DIR=C:\google\v8 -T ClangCL -DENABLE_I18N=1
RUN mkdir C:\artifacts-v8-i18n && \
    copy C:\Javet\src\main\resources\*.dll C:\artifacts-v8-i18n

###########################################
# Stage 3: Build Node.js (both i18n and non-i18n)
###########################################
FROM base AS build-node

# Copy Javet source code
COPY . C:/Javet/

# Install NASM
RUN choco install -y nasm

# Clone Node.js
RUN git clone https://github.com/nodejs/node.git && \
    cd node && \
    git checkout v%JAVET_NODE_VERSION%

# Build Node.js non-i18n
RUN cd node && \
    vcbuild.bat static without-intl vs2022 && \
    move out out.non-i18n

# Build Node.js i18n
RUN cd node && \
    vcbuild.bat static full-icu vs2022 && \
    move out out.i18n

# Copy i18n data
RUN mkdir C:\icu-node && \
    copy node\deps\icu-tmp\*.dat C:\icu-node

# Build Javet JNI for Node non-i18n
RUN cd C:\Javet\src\cpp && \
    build-windows.cmd -DNODE_DIR=C:\node -T ClangCL
RUN mkdir C:\artifacts-node-non-i18n && \
    copy C:\Javet\src\main\resources\*.dll C:\artifacts-node-non-i18n

# Build Javet JNI for Node i18n
RUN cd C:\Javet\src\cpp && \
    build-windows.cmd -DNODE_DIR=C:\node -T ClangCL -DENABLE_I18N=1
RUN mkdir C:\artifacts-node-i18n && \
    copy C:\Javet\src\main\resources\*.dll C:\artifacts-node-i18n

###########################################
# Stage 4: Build final JAR
###########################################
FROM base AS build-jar

# Copy Javet source code
COPY . C:/Javet/

# Copy V8 artifacts from build-v8 stage
COPY --from=build-v8 C:/artifacts-v8-non-i18n/*.dll C:/Javet/src/main/resources/
COPY --from=build-v8 C:/artifacts-v8-i18n/*.dll C:/Javet/src/main/resources/
COPY --from=build-v8 C:/icu-v8/ C:/Javet/icu-v8/

# Copy Node artifacts from build-node stage
COPY --from=build-node C:/artifacts-node-non-i18n/*.dll C:/Javet/src/main/resources/
COPY --from=build-node C:/artifacts-node-i18n/*.dll C:/Javet/src/main/resources/
COPY --from=build-node C:/icu-node/ C:/Javet/icu-node/

# Setup i18n locations
RUN mkdir ..\node\deps\icu-tmp && \
    move Javet\icu-node\*.dat ..\node\deps\icu-tmp\ && \
    mkdir ..\google\v8\third_party\icu\common && \
    move Javet\icu-v8\*.dat ..\google\v8\third_party\icu\common

# Install Gradle
RUN powershell -Command "\
    $ProgressPreference = 'SilentlyContinue'; \
    Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.10.2-bin.zip' -OutFile 'gradle.zip'; \
    Expand-Archive -Path gradle.zip -DestinationPath C:\; \
    Remove-Item gradle.zip"
RUN setx /M PATH "C:\gradle-8.10.2\bin;%PATH%"

# Install 7zip
RUN choco install -y 7zip

# Build JAR with tests
SHELL ["powershell", "-Command"]
RUN cd Javet; \
    Get-ChildItem -Path src\main\resources -Filter "libjavet-v8-*-x86_64.v*" | ForEach-Object { $_.LastWriteTime = Get-Date }; \
    gradle build test --rerun-tasks; \
    Get-ChildItem -Path src\main\resources -Filter "libjavet-node-*-x86_64.v*" | ForEach-Object { $_.LastWriteTime = Get-Date }; \
    gradle build test --rerun-tasks; \
    Get-ChildItem -Path src\main\resources -Filter "libjavet-v8-*-x86_64-i18n.v*" | ForEach-Object { $_.LastWriteTime = Get-Date }; \
    gradle build test --rerun-tasks; \
    Get-ChildItem -Path src\main\resources -Filter "libjavet-node-*-x86_64-i18n.v*" | ForEach-Object { $_.LastWriteTime = Get-Date }; \
    gradle build test --rerun-tasks; \
    gradle build generatePomFileForGeneratePomPublication;

# Output directory
WORKDIR C:/Javet/build/libs

# Default command
CMD ["cmd"]
