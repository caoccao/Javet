#!/usr/bin/env bash

# Usage for V8: sh build-linux-arm64.sh -DV8_DIR=${HOME}/v8
# Usage for Node: sh build-linux-arm64.sh -DNODE_DIR=${HOME}/node
JAVET_VERSION=3.0.2
rm -rf build_linux_arm64
mkdir build_linux_arm64
cd build_linux_arm64
mkdir -p ../../build/libs
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j$(proc) \
  && strip --strip-unneeded -R .note -R .comment ../../src/main/resources/libjavet-*-linux-arm64.v.${JAVET_VERSION}.so
if [ $? -eq 0 ]; then
  cp -f *.a ../../build/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

