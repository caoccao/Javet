@echo off
REM Usage for V8: build -DV8_DIR=C:\v8 
REM Usage for Node: build -DNODE_DIR=C:\node 
SET JAVET_VERSION=0.9.11
rd /s/q build
mkdir build
cd build
mkdir ..\..\src\main\resources
mkdir ..\..\build\libs
cmake ..\ -G "Visual Studio 16 2019" -A x64 -DJAVET_VERSION=%JAVET_VERSION% %* ^
  && cmake --build . -- /p:CharacterSet=Unicode /p:Configuration=Release /p:Platform=x64
IF %ERRORLEVEL% EQU 0 (
copy /y Release\*.dll ..\..\src\main\resources
copy /y Release\*.lib ..\..\build\libs
echo Build Completed
) ELSE (
echo Build Failed
)
cd ..\
