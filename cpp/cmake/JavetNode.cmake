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
    ${NODE_DIR}/src)
if(DEFINED ENABLE_I18N)
    add_definitions(-DENABLE_I18N -DV8_INTL_SUPPORT -DNODE_HAVE_I18N_SUPPORT)
    list(APPEND includeDirs
        ${NODE_DIR}/deps/icu-small/source/common)
endif()
list(APPEND importLibraries
    abseil ada brotli cares crdtp highway histogram llhttp merve nbytes ncrypto nghttp2 openssl simdjson simdutf sqlite torque_base uvwasi
    v8_base_without_compiler v8_compiler v8_init v8_initializers v8_initializers_slow
    v8_libbase v8_libplatform v8_snapshot v8_zlib zlib zstd)
# node, uv
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    list(APPEND importLibraries libnode libuv)
else()
    list(APPEND importLibraries node uv)
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
