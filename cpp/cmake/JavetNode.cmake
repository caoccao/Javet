# Copyright (c) 2021-2026. caoccao.com Sam Cao
# All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

list(APPEND includeDirs
    ${NODE_DIR}/deps/openssl/openssl/include
    ${NODE_DIR}/deps/uv/include
    ${NODE_DIR}/deps/v8
    ${NODE_DIR}/deps/v8/include
    ${NODE_DIR}/deps/v8/third_party/abseil-cpp
    ${NODE_DIR}/deps/v8/third_party/fp16/src/include
    ${NODE_DIR}/deps/ncrypto
    ${NODE_DIR}/deps/simdjson
    ${NODE_DIR}/src)
if(DEFINED ENABLE_I18N)
    add_definitions(-DENABLE_I18N -DV8_INTL_SUPPORT -DNODE_HAVE_I18N_SUPPORT)
    list(APPEND includeDirs
        ${NODE_DIR}/deps/icu-small/source/common)
endif()
# temporal
# V8's Temporal builtins call into temporal_capi, the Rust crate that cargo
# archives as node_crates. Whether they are compiled in at all is a Node.js
# build option (v8_enable_temporal_support), independent of i18n, and Javet's
# headers have to declare the same JSTemporal* types as the V8 that was built
# or they stop matching the Torque-generated instance types. So detect it from
# the build output rather than assume. The archive lands under gyp's
# intermediate directory instead of next to the other Node.js static libraries,
# so the platforms link it by path rather than through importLibraries. cargo
# nests it under the Rust target triple whenever Node.js pins one (Windows,
# macOS x86-64, Android) and writes it straight under the profile when it
# doesn't, so search for it rather than spell out every layout.
# @see: deps/crates/crates.gyp, tools/v8_gypfiles/v8.gyp
if(CMAKE_SYSTEM_NAME STREQUAL "Android")
    set(nodeReleaseDir ${NODE_DIR}/out.${CMAKE_ANDROID_ARCH}.${OUT_DIR_SUFFIX}/Release)
else()
    set(nodeReleaseDir ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release)
endif()
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    file(GLOB_RECURSE nodeCratesLibraries ${nodeReleaseDir}/obj/global_intermediate/node_crates.lib)
elseif(CMAKE_SYSTEM_NAME STREQUAL "Android")
    # Android is always cross compiled, so Node.js builds node_crates twice into
    # the same shared intermediate directory: once natively for the host toolset
    # that mksnapshot links against, and once for the Android triple. Only the
    # latter can be linked here, and the host one is the copy without a triple
    # directory, so drop it. The glob is sorted, so leaving it in would pick the
    # host archive for x86_64 alone (release sorts before x86_64-linux-android)
    # while every other CPU arch kept working.
    file(GLOB_RECURSE nodeCratesLibraries ${nodeReleaseDir}/obj/gen/libnode_crates.a)
    list(FILTER nodeCratesLibraries EXCLUDE REGEX "/obj/gen/release/")
else()
    file(GLOB_RECURSE nodeCratesLibraries ${nodeReleaseDir}/obj/gen/libnode_crates.a)
endif()
if(nodeCratesLibraries)
    list(GET nodeCratesLibraries 0 NODE_CRATES_LIBRARY)
    message(STATUS "Node.js is built with V8 Temporal support: ${NODE_CRATES_LIBRARY}")
    add_definitions(-DV8_TEMPORAL_SUPPORT)
else()
    message(STATUS "Node.js is built without V8 Temporal support.")
endif()
list(APPEND importLibraries
    abseil ada brotli cares crdtp highway histogram llhttp merve nbytes ncrypto ncrypto_engine nghttp2 node_base openssl simdjson simdutf sqlite torque_base uvwasi
    v8_base_without_compiler v8_compiler v8_init v8_initializers
    v8_libbase v8_libplatform v8_snapshot v8_zlib zlib zstd)
# lief
# LIEF backs single executable applications, which Node.js only builds for
# macOS, Linux and Windows (node_use_lief in configure.py).
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    list(APPEND importLibraries liblief)
elseif(NOT CMAKE_SYSTEM_NAME STREQUAL "Android")
    list(APPEND importLibraries lief)
endif()
# ffi, node, uv
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    list(APPEND importLibraries libffi libnode libuv)
else()
    list(APPEND importLibraries ffi node uv)
endif()
# node_text_start
if(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    if (CMAKE_HOST_SYSTEM_PROCESSOR STREQUAL "x86_64")
        list(APPEND importLibraries node_text_start)
    endif()
endif()
# zlib
if(CMAKE_SYSTEM_NAME STREQUAL "Android")
    if(CMAKE_ANDROID_ARCH MATCHES "(x86|x86_64)")
        list(APPEND importLibraries zlib_adler32_simd zlib_data_chunk_simd)
    endif()
else()
    list(APPEND importLibraries zlib_adler32_simd zlib_data_chunk_simd)
    if(CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|aarch64)")
        list(APPEND importLibraries zlib_arm_crc32)
    endif()
endif()
# icu
if(DEFINED ENABLE_I18N)
    list(APPEND importLibraries icudata icui18n icuucx)
endif()
add_definitions(-DENABLE_NODE)
set(JAVET_LIB_TYPE "node")
