@echo off
REM Usage sample: build -DV8_DIR=C:\v8 
SET JAVET_VERSION=0.7.2
rd /s/q build
mkdir build
cd build
mkdir ..\..\src\main\resources
cmake ..\ -G "Visual Studio 16 2019" -A x64 -DJAVET_VERSION=%JAVET_VERSION% %* ^
  && cmake --build . -- /p:CharacterSet=Unicode /p:Configuration=Release /p:Platform=x64
IF %ERRORLEVEL% EQU 0 (
del /q ..\..\src\main\resources\*.dll
copy /y Release\*.dll ..\..\src\main\resources
echo Build Completed
) ELSE (
echo Build Failed
)
cd ..\
