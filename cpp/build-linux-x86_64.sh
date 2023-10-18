#!/usr/bin/env bash

# Usage for V8: sh build-linux-x86_64.sh -DV8_DIR=${HOME}/v8
# Usage for Node: sh build-linux-x86_64.sh -DNODE_DIR=${HOME}/node
JAVET_VERSION=3.0.1
rm -rf build_linux_x86_64
mkdir build_linux_x86_64
cd build_linux_x86_64
mkdir -p ../../build_linux_x86_64/libs
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j4 \
  && execstack -c ../../src/main/resources/libjavet-*-linux-x86_64.v.${JAVET_VERSION}.so \
  && strip --strip-unneeded -R .note -R .comment ../../src/main/resources/libjavet-*-linux-x86_64.v.${JAVET_VERSION}.so
if [ $? -eq 0 ]; then
  cp -f *.a ../../build_linux_x86_64/libs
  echo Build Completed
else
  echo Build Failed
fi
cd ../

