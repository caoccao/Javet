#!/usr/bin/env bash

# Usage for V8: sh build-android.sh -DV8_DIR=${HOME}/v8 -DCMAKE_ANDROID_NDK=${HOME}/android -DCMAKE_ANDROID_ARCH=arm64
# Usage for Node: sh build-android.sh -DNODE_DIR=${HOME}/node -DCMAKE_ANDROID_NDK=${HOME}/android -DCMAKE_ANDROID_ARCH=arm64
JAVET_VERSION=3.1.0
rm -rf build_android
mkdir build_android
cd build_android
mkdir -p ../../build/libs
cmake ../ -DCMAKE_SYSTEM_NAME=Android -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j `nproc`
if [ $? -eq 0 ]; then
  cp -f *.a ../../build/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

