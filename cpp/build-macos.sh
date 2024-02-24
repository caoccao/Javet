#!/usr/bin/env bash

# Usage for V8: sh build-macos.sh -DV8_DIR=${HOME}/v8
# Usage for Node: sh build-macos.sh -DNODE_DIR=${HOME}/node
JAVET_VERSION=3.1.0
rm -rf build_macos
mkdir build_macos
cd build_macos
mkdir -p ../../build/libs
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j `nproc`
if [ $? -eq 0 ]; then
  cp -f *.a ../../build/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

