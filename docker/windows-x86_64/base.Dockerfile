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

# Usage: docker build -t sjtucaocao/javet-windows:0.9.10 -f docker/windows-x86_64/base.Dockerfile .

# Note: This is experimental and it doesn't work as expected yet.

# https://hub.docker.com/_/microsoft-dotnet-framework-sdk/
FROM mcr.microsoft.com/dotnet/framework/sdk:4.8-windowsservercore-20H2

SHELL ["cmd", "/S", "/C"]

# Python 3
RUN curl -SL --output python-3.9.6-amd64.exe https://www.python.org/ftp/python/3.9.6/python-3.9.6-amd64.exe
RUN start /w python-3.9.6-amd64.exe /quiet InstallAllUsers=1 PrependPath=0 
RUN del /q python-3.9.6-amd64.exe

# Python 2
RUN curl -SL --output python-2.7.18.msi https://www.python.org/ftp/python/2.7.18/python-2.7.18.msi
RUN start /w msiexec.exe /i python-2.7.18.msi ALLUSERS=1 ADDLOCAL=ALL /qn
RUN del /q python-2.7.18.msi

# https://github.com/git-for-windows/git/wiki/Silent-or-Unattended-Installation
RUN curl -SL --output Git-2.32.0.2-64-bit.exe https://github.com/git-for-windows/git/releases/download/v2.32.0.windows.2/Git-2.32.0.2-64-bit.exe
RUN start /w Git-2.32.0.2-64-bit.exe /VERYSILENT /NORESTART /NOCANCEL /SP- /CLOSEAPPLICATIONS /RESTARTAPPLICATIONS /COMPONENTS="icons,ext\reg\shellhere,assoc,assoc_sh"
RUN del /q Git-2.32.0.2-64-bit.exe

# https://docs.microsoft.com/en-us/visualstudio/install/workload-component-id-vs-build-tools?view=vs-2019&preserve-view=true
# https://docs.microsoft.com/en-us/visualstudio/install/create-an-offline-installation-of-visual-studio?view=vs-2019
RUN curl -SL --output vs_buildtools.exe https://aka.ms/vs/16/release/vs_buildtools.exe
RUN start /w vs_buildtools.exe --quiet --wait --norestart --nocache --includeRecommended --includeOptional --lang en-US modify \
        --installPath "%ProgramFiles(x86)%\Microsoft Visual Studio\2019\BuildTools" \
        --add Microsoft.VisualStudio.Workload.VCTools \
        --add Microsoft.VisualStudio.Workload.VisualStudioExtensionBuildTools \
        || IF "%ERRORLEVEL%"=="3010" EXIT 0
RUN del /q vs_buildtools.exe

WORKDIR /
RUN mkdir google
WORKDIR /google
RUN git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
WORKDIR /google/depot_tools
RUN git checkout remotes/origin/master
ENV PATH=/google/depot_tools;/Windows/System32/WindowsPowerShell/v1.0;%PATH%
WORKDIR /google
RUN fetch v8
WORKDIR /google/v8
RUN git checkout 9.2.230.21
WORKDIR /google
RUN gclient sync
RUN echo V8 preparation is completed.

ENTRYPOINT ["C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\BuildTools\\Common7\\Tools\\VsDevCmd.bat", "&&", "powershell.exe", "-NoLogo", "-ExecutionPolicy", "Bypass"]
