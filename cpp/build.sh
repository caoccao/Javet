#!/usr/bin/env bash

# Usage sample: build -DV8_DIR=~/v8
JAVET_VERSION=0.7.3
rm -rf build
mkdir build
cd build
mkdir -p ../../src/main/resources
cmake ../ -DJAVET_VERSION=${JAVET_VERSION} "$@" \
  && make -j4 \
  && execstack -c libjavet-linux-x86_64.v.${JAVET_VERSION}.so \
  && strip --strip-unneeded -R .note -R .comment libjavet-linux-x86_64.v.${JAVET_VERSION}.so
if [ $? -eq 0 ]; then
  rm -f ../../src/main/resources/*.so
  cp -f *.so ../../src/main/resources
  echo Build Completed
else
  echo Build Failed
fi
cd ../

