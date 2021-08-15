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

# Usage: docker build -t sjtucaocao/javet-windows:0.9.9 -f docker/windows-x86_64/base.Dockerfile .

# Note: This is experimental and it doesn't work as expected yet.

# https://hub.docker.com/_/microsoft-dotnet-framework-sdk/
FROM mcr.microsoft.com/dotnet/framework/sdk:4.8-windowsservercore-20H2

SHELL ["cmd", "/S", "/C"]
RUN curl -SL --output vs_buildtools.exe https://aka.ms/vs/16/release/vs_buildtools.exe

SHELL ["cmd", "/S", "/C"]
RUN start /w vs_buildtools.exe --quiet --wait --norestart --nocache modify \
        --installPath "%ProgramFiles(x86)%\Microsoft Visual Studio\2019\BuildTools" \
        --remove Microsoft.VisualStudio.Component.Windows10SDK.10240 \
        --remove Microsoft.VisualStudio.Component.Windows10SDK.10586 \
        --remove Microsoft.VisualStudio.Component.Windows81SDK \
        || IF "%ERRORLEVEL%"=="3010" EXIT 0
RUN del /q vs_buildtools.exe

ENTRYPOINT ["C:\\Program Files (x86)\\Microsoft Visual Studio\\2019\\BuildTools\\Common7\\Tools\\VsDevCmd.bat", "&&", "powershell.exe", "-NoLogo", "-ExecutionPolicy", "Bypass"]
