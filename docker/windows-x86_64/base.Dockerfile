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

# Usage: docker build -t sjtucaocao/javet-windows:1.1.5 -m 4G -f docker/windows-x86_64/base.Dockerfile .

# https://hub.docker.com/_/microsoft-windows
FROM mcr.microsoft.com/windows:20H2-amd64

SHELL ["cmd", "/S", "/C"]
WORKDIR /

# Install Python 3
RUN curl -SL --output python-3.9.6-amd64.exe https://www.python.org/ftp/python/3.9.6/python-3.9.6-amd64.exe
RUN start /w python-3.9.6-amd64.exe /quiet InstallAllUsers=1 PrependPath=0 
RUN del /q python-3.9.6-amd64.exe

# Install Python 2
RUN curl -SL --output python-2.7.18.msi https://www.python.org/ftp/python/2.7.18/python-2.7.18.msi
RUN start /w msiexec.exe /i python-2.7.18.msi ALLUSERS=1 ADDLOCAL=ALL /qn
RUN del /q python-2.7.18.msi

# Install Git for Windows
# https://github.com/git-for-windows/git/wiki/Silent-or-Unattended-Installation
RUN curl -SL --output Git-2.32.0.2-64-bit.exe https://github.com/git-for-windows/git/releases/download/v2.32.0.windows.2/Git-2.32.0.2-64-bit.exe
RUN start /w Git-2.32.0.2-64-bit.exe /VERYSILENT /NORESTART /NOCANCEL /SP- /CLOSEAPPLICATIONS /RESTARTAPPLICATIONS /COMPONENTS="icons,ext\reg\shellhere,assoc,assoc_sh"
RUN del /q Git-2.32.0.2-64-bit.exe

# Prepare V8
RUN mkdir google
WORKDIR /google
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
WORKDIR /google/depot_tools
RUN git checkout remotes/origin/main
RUN setx /M PATH "C:\google\depot_tools;%PATH%"
ENV DEPOT_TOOLS_WIN_TOOLCHAIN=0
WORKDIR /google
RUN fetch v8
WORKDIR /google/v8
RUN git checkout 10.3.174.14
WORKDIR /google
RUN gclient sync
RUN echo V8 preparation is completed.

# Install Visual Studio 2019 Community
# https://docs.microsoft.com/en-us/visualstudio/install/workload-component-id-vs-community?view=vs-2019
# https://docs.microsoft.com/en-us/visualstudio/install/create-an-offline-installation-of-visual-studio?view=vs-2019
WORKDIR /
RUN curl -SL --output vs_community.exe https://aka.ms/vs/16/release/vs_community.exe
RUN echo Installing Visual Studio 2019 Community
RUN start /w vs_community.exe \
        --wait --quiet --norestart --nocache --includeRecommended --includeOptional \
        --installPath "%ProgramFiles(x86)%\Microsoft Visual Studio\2019\Community" \
        --add Microsoft.VisualStudio.Workload.NativeDesktop \
        --add Microsoft.VisualStudio.Workload.NativeCrossPlat \
        --remove Microsoft.VisualStudio.Component.Windows10SDK.10240 \
        --remove Microsoft.VisualStudio.Component.Windows10SDK.10586 \
        || IF "%ERRORLEVEL%"=="3010" EXIT 0

# Install Windows SDK 10.0.19041.x
RUN curl -SL --output winsdksetup.exe https://go.microsoft.com/fwlink/p/?linkid=2120843
RUN start /w winsdksetup.exe /norestart /quiet /ceip off /features +
RUN del /q vs_community.exe
RUN del /q winsdksetup.exe

# Build V8
WORKDIR /google/v8
RUN setx /M PATH "C:\Python27;C:\Python27\Scripts;%PATH%"
RUN python tools/dev/v8gen.py x64.release -vv -- v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false
RUN ninja -C out.gn/x64.release v8_monolith || EXIT 0
COPY ./scripts/python/patch_v8_build.py .
RUN ["C:\\Program Files\\Python39\\python.exe", "C:\\google\\v8\\patch_v8_build.py", "-p", "C:\\google\\v8\\"]
RUN ninja -C out.gn/x64.release v8_monolith
RUN del patch_v8_build.py
RUN echo V8 build is completed.

# Prepare Node.js v16
WORKDIR /
RUN powershell -ExecutionPolicy Bypass -c "iex(New-Object Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1')"
RUN choco install -y nasm
RUN git clone https://github.com/nodejs/node.git
WORKDIR /node
RUN git checkout v16.15.1
RUN echo Node.js preparation is completed.

# Build Node.js
RUN vcbuild.bat static without-intl
RUN echo Node.js build is completed.

# Prepare Javet Build Environment
RUN choco install -y openjdk8
RUN setx /M PATH "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin;%PATH%"
RUN setx /M PATH "C:\Program Files\Git\usr\bin;%PATH%"

# Shrink
WORKDIR /
RUN rd /s /q "C:\Users\ContainerAdministrator\AppData\Local\Temp"

# Completed
RUN echo Javet build base image is completed.
