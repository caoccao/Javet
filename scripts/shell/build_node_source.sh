#!/usr/bin/env bash

set -euxo pipefail

# Build Node.js for Linux into the out dirs expected by cpp/cmake/JavetNode.cmake.
# Run it in the Node.js directory, with JAVET_HOME pointing to the Javet checkout.
# The steps are based on .github/workflows/linux_x86_64_build.yml.

JAVET_HOME="${JAVET_HOME:?JAVET_HOME must point to the Javet checkout}"

build_node(){
  local i18n_name=$1
  shift
  deno --allow-all "${JAVET_HOME}/scripts/deno/patch_node_build.ts" -p ./ --os linux
  ./configure "$@"
  export CFLAGS="-fPIC -ftls-model=global-dynamic -Wno-return-type"
  export CXXFLAGS="${CFLAGS}"
  export LDFLAGS="${CFLAGS}"
  make -j ${NUM_JOBS:-4}
  unset CFLAGS CXXFLAGS LDFLAGS
  mv out "out.${i18n_name}"
  echo "Node.js ${i18n_name} build is completed"
}

build_node non-i18n --without-intl --v8-enable-temporal-support
build_node i18n --with-intl=full-icu
