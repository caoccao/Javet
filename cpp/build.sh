#!/usr/bin/env bash

# Usage for V8: build -DV8_DIR=~/v8
# Usage for Node: build -DNODE_DIR=~/node
JAVET_VERSION=0.8.5
rm -rf build
mkdir build
cd build
mkdir -p ../../src/main/resources
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j4 \
  && execstack -c libjavet-*-linux-x86_64.v.${JAVET_VERSION}.so \
  && strip --strip-unneeded -R .note -R .comment libjavet-*-linux-x86_64.v.${JAVET_VERSION}.so
if [ $? -eq 0 ]; then
  cp -f *.so ../../src/main/resources
  echo Build Completed
else
  echo Build Failed
fi
cd ../

