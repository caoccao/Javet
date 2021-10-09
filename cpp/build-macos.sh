#!/usr/bin/env bash

# Usage for V8: sh build-macos.sh -DV8_DIR=${HOME}/v8
# Usage for Node: sh build-macos.sh -DNODE_DIR=${HOME}/node
JAVET_VERSION=1.0.0
rm -rf build
mkdir build
cd build
mkdir -p ../../src/main/resources
mkdir -p ../../build/libs
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j4
if [ $? -eq 0 ]; then
  cp -f *.dylib ../../src/main/resources
  cp -f *.a ../../build/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

