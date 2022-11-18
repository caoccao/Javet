#!/usr/bin/env bash

set -euxo pipefail

build_v8(){
  python3 tools/dev/v8gen.py x64.release -- \
    v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false \
    v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 \
    use_custom_libcxx=false v8_enable_sandbox=false

  ninja -C out.gn/x64.release v8_monolith || python3 patch_v8_build.py -p ./
  ninja -C out.gn/x64.release v8_monolith
  rm patch_v8_build.py
  echo "V8 build is completed"
}

build_v8