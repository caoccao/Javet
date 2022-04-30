#!/usr/bin/env bash

# Usage for V8: sh build-android.sh -DV8_DIR=${HOME}/v8 -DCMAKE_ANDROID_NDK=${HOME}/android -DCMAKE_ANDROID_ARCH=arm64
# Usage for Node: sh build-android.sh -DNODE_DIR=${HOME}/node -DCMAKE_ANDROID_NDK=${HOME}/android -DCMAKE_ANDROID_ARCH=arm64
JAVET_VERSION=1.1.3
rm -rf build
mkdir build
cd build
mkdir -p ../../build/libs
cmake ../ -DCMAKE_SYSTEM_NAME=Android -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j4
if [ $? -eq 0 ]; then
  cp -f *.a ../../build/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

