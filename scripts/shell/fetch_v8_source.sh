#!/usr/bin/env bash

set -euxo pipefail

fetch_depot_tools() {
  git clone --depth=10 --branch=main https://chromium.googlesource.com/chromium/tools/depot_tools.git
  pushd depot_tools
  git checkout remotes/origin/main
  popd
}

fetch_v8_source() {
  # reference - https://stackoverflow.com/a/47093174/30007
  mkdir v8

  pushd v8
  git init . &&
  git fetch https://chromium.googlesource.com/v8/v8.git +refs/tags/${JAVET_V8_VERSION}:v8_${JAVET_V8_VERSION} --depth 1
  git checkout tags/${JAVET_V8_VERSION}
  popd

  gclient root
  gclient config --spec 'solutions = [{"name": "v8","url": "https://chromium.googlesource.com/v8/v8.git","deps_file": "DEPS","managed": False,"custom_deps": {},},]'
  gclient sync --no-history
  gclient runhooks

  pushd v8
  sed -i 's/snapcraft/nosnapcraft/g' ./build/install-build-deps.sh
  ./build/install-build-deps.sh
  sed -i 's/nosnapcraft/snapcraft/g' ./build/install-build-deps.sh
  popd

  gclient sync --no-history
  echo "V8 source fetched"
}

fetch_depot_tools
fetch_v8_source