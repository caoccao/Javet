#!/usr/bin/env bash

set -euxo pipefail

# Build V8 for Linux on the host arch into the out dirs expected by cpp/cmake/JavetV8.cmake.
# Run it in the V8 directory, with JAVET_HOME pointing to the Javet checkout.
# Set CLANG_BASE_PATH to build V8 with a system LLVM, because the clang toolchain from V8 only runs on x86_64.

JAVET_HOME="${JAVET_HOME:?JAVET_HOME must point to the Javet checkout}"

case "$(uname -m)" in
  x86_64)
    JAVET_ARCH=x86_64
    V8_ARCH=x64
    ;;
  aarch64|arm64)
    JAVET_ARCH=arm64
    V8_ARCH=arm64
    ;;
  *)
    echo "Linux on $(uname -m) is not supported"
    exit 1
    ;;
esac

# The patches are based on .github/workflows/linux_x86_64_build.yml.
patch_v8(){
  if ! grep -q JAVET_DISABLE_PKU src/libplatform/default-thread-isolated-allocator.cc; then
    sed -i '/#include "src\/libplatform\//a #include <cstdlib>' src/libplatform/default-thread-isolated-allocator.cc
    sed -i '/bool KernelHasPkruFix()/a const char* env = std::getenv("JAVET_DISABLE_PKU"); if (env && std::strlen(env) > 0) { return false; }' src/libplatform/default-thread-isolated-allocator.cc
  fi
  sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' src/execution/isolate.h
  sed -i 's/__attribute__((tls_model(V8_TLS_MODEL)))/ /g' src/heap/local-heap.h
  echo "V8 patch is completed"
}

build_v8(){
  local i18n_name=$1
  local out_dir="out.gn.linux.${i18n_name}/${V8_ARCH}.release"
  mkdir -p "${out_dir}"
  cp "${JAVET_HOME}/scripts/v8/gn/linux-${JAVET_ARCH}-${i18n_name}-args.gn" "${out_dir}/args.gn"
  if [ -n "${CLANG_BASE_PATH:-}" ]; then
    echo "clang_base_path = \"${CLANG_BASE_PATH}\"" >> "${out_dir}/args.gn"
  fi
  gn gen "${out_dir}"
  ninja -C "${out_dir}" v8_monolith || deno run --allow-all "${JAVET_HOME}/scripts/deno/patch_v8_build.ts" -p ./
  ninja -C "${out_dir}" v8_monolith
  echo "V8 ${i18n_name} build is completed"
}

patch_v8
build_v8 non-i18n
build_v8 i18n
