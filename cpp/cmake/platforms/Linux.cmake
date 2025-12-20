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

include(${CMAKE_SOURCE_DIR}/cmake/platforms/PlatformCommon.cmake)

set(JAVET_LIB_SYSTEM "linux")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-deprecated -Wno-deprecated-declarations ")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-deprecated -Wno-deprecated-declarations ")
# We use the LLVM.lld linker for Linux.
set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -fuse-ld=lld")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -fuse-ld=lld")
set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} -fuse-ld=lld")
if (CMAKE_HOST_SYSTEM_PROCESSOR STREQUAL "x86_64")
    set(JAVET_LIB_ARCH "-x86_64")
    add_definitions(-D__x86_64__)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m64 ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m64 ")
else()
    set(JAVET_LIB_ARCH "-arm64")
    add_definitions(-D__arm64__)
endif()
add_definitions(-D__linux__)
list(APPEND includeDirs $ENV{JAVA_HOME}/include/linux)
if(DEFINED V8_DIR)
    # We use clang for V8 mode on Linux.
    if(CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|aarch64)")
        set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -flax-vector-conversions ")
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -flax-vector-conversions ")
    endif()
    add_definitions(-D_LIBCPP_HARDENING_MODE_DEFAULT=_LIBCPP_HARDENING_MODE_NONE)
    add_definitions(-DV8_TEMPORAL_SUPPORT)
    list(APPEND includeDirs
        ${V8_DIR}/buildtools/third_party/libc++
        ${V8_DIR}/third_party/libc++/src/include
    )
    foreach(importLibrary ${importLibraries})
        set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_RELEASE_DIR}/obj/lib${importLibrary}.a)
    endforeach(importLibrary)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -nostdinc++ -fno-exceptions -fno-modules -fno-implicit-modules -fno-builtin-module-map ")
    find_library(LIBCXX_STATIC NAMES libc++.a c++ PATHS ${V8_RELEASE_DIR}/obj/buildtools/third_party/libc++/ NO_DEFAULT_PATH REQUIRED)
    find_library(LIBCXXABI_STATIC NAMES libc++abi.a c++abi PATHS ${V8_RELEASE_DIR}/obj/buildtools/third_party/libc++abi/ NO_DEFAULT_PATH REQUIRED)
    target_link_libraries(Javet PUBLIC
        -Wl,-Bstatic -latomic -Wl,-Bdynamic
        -Wl,--compress-sections=.text=none
        -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
        ${LIBCXX_STATIC} ${LIBCXXABI_STATIC}
        debug "-lrt" -static-libgcc -stdlib=libc++ optimized "-lrt" "${libgcc}")
    target_link_libraries(JavetStatic PUBLIC
        -Wl,-Bstatic -latomic -Wl,-Bdynamic
        -Wl,--compress-sections=.text=none
        -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
        ${LIBCXX_STATIC} ${LIBCXXABI_STATIC}
        debug "-lrt" -static-libgcc -stdlib=libc++ optimized "-lrt" "${libgcc}")
endif()
if(DEFINED NODE_DIR)
    # We use gcc for Node.js mode on Linux.
    list(APPEND includeDirs
        ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen/generate-bytecode-output-root
        ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen/inspector-generated-output-root
        ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fpermissive -w ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fpermissive -w ")
    foreach(importLibrary ${importLibraries})
        set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/lib${importLibrary}.a)
    endforeach(importLibrary)
    list(REMOVE_ITEM importLibraries v8_init)
    target_link_libraries(Javet PUBLIC
        -Wl,--compress-sections=.text=none
        -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
        v8_init debug "-lrt" -static-libgcc -static-libstdc++ optimized "-lrt" "${libgcc}")
    target_link_libraries(JavetStatic PUBLIC
        -Wl,--compress-sections=.text=none
        -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
        v8_init debug "-lrt" -static-libgcc -static-libstdc++ optimized "-lrt" "${libgcc}")
endif()
# https://www.gnu.org/software/gnulib/manual/html_node/LD-Version-Scripts.html
target_link_libraries(Javet PUBLIC -Wl,--version-script=${CMAKE_SOURCE_DIR}/jni/version_script.map)

set_target_properties(JavetStatic PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
