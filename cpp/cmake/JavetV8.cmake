# Copyright (c) 2021-2025. caoccao.com Sam Cao
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

if(CMAKE_SYSTEM_NAME STREQUAL "Android")
    if(CMAKE_ANDROID_ARCH STREQUAL "arm")
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/arm.release)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "arm64")
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/arm64.release)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "x86")
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/ia32.release)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "x86_64")
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/x64.release)
    endif()
else()
    if(CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|aarch64)")
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/arm64.release)
    else()
        set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/x64.release)
    endif()
endif()
list(APPEND includeDirs
    ${V8_DIR}
    ${V8_DIR}/include
    ${V8_DIR}/third_party/abseil-cpp
    ${V8_DIR}/third_party/fp16/src/include
    ${V8_RELEASE_DIR}/gen
    ${V8_RELEASE_DIR}/gen/include)
if(DEFINED ENABLE_I18N)
    add_definitions(-DENABLE_I18N -DV8_INTL_SUPPORT)
    list(APPEND includeDirs
        ${V8_DIR}/third_party/icu/source/common)
endif()
list(APPEND importLibraries v8_monolith)
if(CMAKE_SYSTEM_NAME MATCHES "(Darwin|Windows)")
    list(APPEND importLibraries temporal_capi)
endif()
set(JAVET_LIB_TYPE "v8")
