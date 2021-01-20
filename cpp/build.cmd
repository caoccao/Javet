@echo off
REM Usage sample: build -DV8_DIR=C:\v8 
SET JAVET_VERSION=0.7.0
rd /s/q build
mkdir build
cd build
cmake ..\ -G "Visual Studio 16 2019" -A x64 -DJAVET_VERSION=%JAVET_VERSION% %* ^
  && cmake --build . -- /p:CharacterSet=Unicode /p:Configuration=Release /p:Platform=x64 ^
  && del ..\..\src\main\resources\*.dll ^
  && copy Release\*.dll ..\..\src\main\resources ^
  && echo Build Completed
cd ..\