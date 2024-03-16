@echo off
REM Usage for V8: build -DV8_DIR=C:\v8 
REM Usage for Node: build -DNODE_DIR=C:\node 
SET JAVET_VERSION=3.1.0
rd /s/q build_windows
mkdir build_windows
cd build_windows
mkdir ..\..\build\libs
cmake ..\ -G "Visual Studio 17 2022" -A x64 -DJAVET_VERSION=%JAVET_VERSION% %* ^
  && cmake --build . -- /p:CharacterSet=Unicode /p:Configuration=Release /p:Platform=x64
IF %ERRORLEVEL% EQU 0 (
copy /y Release\*.lib ..\..\build\libs
echo Build Completed
) ELSE (
echo Build Failed
)
cd ..\
