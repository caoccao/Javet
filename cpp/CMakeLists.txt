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

cmake_minimum_required(VERSION 3.10)

# Validation
if(!$ENV{JAVA_HOME})
    message(FATAL_ERROR "JAVA_HOME is not found. Please make sure you have JDK 8 or 11 installed properly.")
endif()

if(DEFINED V8_DIR AND DEFINED NODE_DIR)
    message(FATAL_ERROR "V8_DIR and NODE_DIR cannot be both defined.")
endif()

if((NOT DEFINED V8_DIR) AND (NOT DEFINED NODE_DIR))
    message(FATAL_ERROR "Either V8_DIR or NODE_DIR needs to be defined.")
endif()

# CMP0091 must be set before first project().
# @see: https://cmake.org/cmake/help/latest/prop_tgt/MSVC_RUNTIME_LIBRARY.html
if (POLICY CMP0091)
    cmake_policy(SET CMP0091 NEW)
endif()

if(CMAKE_SYSTEM_NAME STREQUAL "Android")
    if(NOT DEFINED CMAKE_ANDROID_NDK)
        message(FATAL_ERROR "CMAKE_ANDROID_NDK needs to be defined.")
    endif()
    if(NOT DEFINED CMAKE_ANDROID_ARCH)
        message(FATAL_ERROR "CMAKE_ANDROID_ARCH needs to be defined.")
    elseif(CMAKE_ANDROID_ARCH STREQUAL "arm")
        set(CMAKE_ANDROID_ARCH_ABI armeabi-v7a)
        set(CMAKE_ANDROID_ARM_NEON 1)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "arm64")
        set(CMAKE_ANDROID_ARCH_ABI arm64-v8a)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "x86")
        set(CMAKE_ANDROID_ARCH_ABI x86)
    elseif(CMAKE_ANDROID_ARCH STREQUAL "x86_64")
        set(CMAKE_ANDROID_ARCH_ABI x86_64)
    else()
        message(FATAL_ERROR "CMAKE_ANDROID_ARCH must be one of arm, arm64, x86, x86_64.")
    endif()
    # The target ABI version is set to 24 because pre-24 is no longer supported by V8 v11+.
    # https://github.com/android/ndk/issues/1179
    if(DEFINED V8_DIR)
        set(CMAKE_SYSTEM_VERSION 29)
    endif()
    if(DEFINED NODE_DIR)
        set(CMAKE_SYSTEM_VERSION 24)
    endif()
    set(CMAKE_ANDROID_STL_TYPE c++_static)
    set(JAVA_RESOURCES_DIR ${CMAKE_SOURCE_DIR}/../android/javet-android/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI})
else()
    set(JAVA_RESOURCES_DIR ${CMAKE_SOURCE_DIR}/../src/main/resources)
endif()

# Initialization
project(Javet)
aux_source_directory("jni" sourceFiles)
set(includeDirs $ENV{JAVA_HOME}/include)
set(importLibraries)
set(JAVET_LIB_ARCH "")
set(JAVET_LIB_I18N "")
set(JAVET_LIB_PREFIX "")
set(JAVET_LIB_SYSTEM "")
set(JAVET_LIB_TYPE "")
set(OUT_DIR_SUFFIX "")
set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)
set(CMAKE_CONFIGURATION_TYPES "Debug;Release" CACHE STRING "limited configs" FORCE)
if(DEFINED ENABLE_LOGGING)
    add_definitions(-DJAVET_INFO -DJAVET_DEBUG -DJAVET_ERROR -DJAVET_TRACE)
endif()

# Preparation
set(CMAKE_CXX_STANDARD 20)
if(DEFINED ENABLE_I18N)
    set(JAVET_LIB_I18N "-i18n")
    set(OUT_DIR_SUFFIX "i18n")
else()
    set(OUT_DIR_SUFFIX "non-i18n")
endif()
if(DEFINED V8_DIR)
    if(CMAKE_SYSTEM_NAME STREQUAL "Android")
        if(CMAKE_ANDROID_ARCH STREQUAL "arm64")
            set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/arm64.release)
        elseif(CMAKE_ANDROID_ARCH STREQUAL "arm")
            set(V8_RELEASE_DIR ${V8_DIR}/out.gn.${OUT_DIR_SUFFIX}/arm.release)
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
    set(JAVET_LIB_TYPE "v8")
endif()
if(DEFINED NODE_DIR)
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
        abseil ada brotli cares crdtp highway histogram llhttp nbytes ncrypto nghttp2 openssl simdjson simdutf sqlite torque_base uvwasi
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
endif()
add_definitions(-DV8_ENABLE_WEBASSEMBLY -DV8_ENABLE_LEAPTIERING)
foreach(importLibrary ${importLibraries})
    add_library(${importLibrary} STATIC IMPORTED)
endforeach(importLibrary)

# Setting
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    set(JAVET_LIB_PREFIX "libjavet")
    set(JAVET_LIB_SYSTEM "windows")
    set(JAVET_LIB_ARCH "-x86_64")
    # Generate PDB file
    # https://learn.microsoft.com/en-us/cpp/build/reference/zc-cplusplus
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /Zi /MP /O2 /Ob2 /GS- /Zc:__cplusplus")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} /clang:-Wno-invalid-offsetof /clang:-Wno-nullability-completeness")
    set(CMAKE_SHARED_LINKER_FLAGS_RELEASE "${CMAKE_SHARED_LINKER_FLAGS_RELEASE} /DEBUG /OPT:REF /OPT:ICF")
    add_definitions(-D_ITERATOR_DEBUG_LEVEL=0 -D_WIN32)
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/win32)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    if(DEFINED V8_DIR)
        add_definitions(-D_WIN32_WINNT)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_RELEASE_DIR}/obj/${importLibrary}.lib)
        endforeach(importLibrary)
        set_target_properties(v8_monolith PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:v8_monolith.lib")
        add_library(Javet SHARED ${sourceFiles} "jni/javet_resource_v8.rc")
    endif()
    if(DEFINED NODE_DIR)
        list(APPEND includeDirs
            ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/global_intermediate/generate-bytecode-output-root
            ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/global_intermediate/inspector-generated-output-root
            ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/global_intermediate)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/lib/${importLibrary}.lib)
        endforeach(importLibrary)
        set_target_properties(libnode PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:libnode.lib")
        add_library(Javet SHARED ${sourceFiles} "jni/javet_resource_node.rc")
    endif()
    set_property(TARGET Javet APPEND_STRING PROPERTY LINK_FLAGS_RELEASE "")
    set_property(TARGET Javet PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    target_link_libraries(Javet ${importLibraries})
elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux" OR CMAKE_SYSTEM_NAME STREQUAL "Android" OR CMAKE_SYSTEM_NAME STREQUAL "Darwin")
    project(JavetStatic)
    set(JAVET_LIB_PREFIX "javet")
    set(CMAKE_POSITION_INDEPENDENT_CODE ON)
    add_library(Javet SHARED ${sourceFiles})
    add_library(JavetStatic STATIC ${sourceFiles})
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-invalid-offsetof -O3 -fno-rtti ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-invalid-offsetof -O3 -fno-rtti ")
    if(CMAKE_SYSTEM_NAME STREQUAL "Linux" AND CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|x86_64|aarch64)")
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
                list(APPEND includeDirs
                    ${V8_DIR}/third_party/libc++/src/include
                )
            endif()
            add_definitions(-D_LIBCPP_HARDENING_MODE_DEFAULT=_LIBCPP_HARDENING_MODE_NONE)
            list(APPEND includeDirs
                ${V8_DIR}/buildtools/third_party/libc++
                ${V8_RELEASE_DIR}/gen/third_party/libc++/src/include
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
    elseif(CMAKE_SYSTEM_NAME STREQUAL "Android" AND CMAKE_HOST_SYSTEM_PROCESSOR STREQUAL "x86_64")
        set(JAVET_LIB_SYSTEM "android")
        add_definitions(-D__ANDROID__)
        list(APPEND includeDirs $ENV{JAVA_HOME}/include/linux)
        set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall -Wno-unknown-pragmas -Wno-unused-function -Wno-unused-variable -funroll-loops -ftree-vectorize -ffast-math -fpermissive -fPIC ")
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wno-unknown-pragmas -Wno-unused-function -Wno-unused-variable -funroll-loops -ftree-vectorize -ffast-math -fpermissive -fPIC ")
        if(DEFINED V8_DIR)
            foreach(importLibrary ${importLibraries})
                set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_RELEASE_DIR}/obj/lib${importLibrary}.a)
            endforeach(importLibrary)
            target_link_libraries(Javet PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
                -Wl,-z,max-page-size=16384
                -llog -static-libgcc -static-libstdc++ "${libgcc}")
            target_link_libraries(JavetStatic PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
                -Wl,-z,max-page-size=16384
                -llog -static-libgcc -static-libstdc++ "${libgcc}")
        endif()
        if(DEFINED NODE_DIR)
            list(APPEND includeDirs
                ${NODE_DIR}/out.${CMAKE_ANDROID_ARCH}.${OUT_DIR_SUFFIX}/Release/obj/gen/generate-bytecode-output-root
                ${NODE_DIR}/out.${CMAKE_ANDROID_ARCH}.${OUT_DIR_SUFFIX}/Release/obj/gen/inspector-generated-output-root
                ${NODE_DIR}/out.${CMAKE_ANDROID_ARCH}.${OUT_DIR_SUFFIX}/Release/obj/gen)
            set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -w ")
            set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -w ")
            foreach(importLibrary ${importLibraries})
                set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out.${CMAKE_ANDROID_ARCH}.${OUT_DIR_SUFFIX}/Release/lib${importLibrary}.a)
            endforeach(importLibrary)
            list(REMOVE_ITEM importLibraries v8_init)
            target_link_libraries(Javet PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
                -Wl,-z,max-page-size=16384
                v8_init -llog -static-libgcc -static-libstdc++ "${libgcc}")
            target_link_libraries(JavetStatic PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
                -Wl,-z,max-page-size=16384
                v8_init -llog -static-libgcc -static-libstdc++ "${libgcc}")
        endif()
    elseif(CMAKE_SYSTEM_NAME STREQUAL "Darwin" AND CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|x86_64)")
        set(JAVET_LIB_SYSTEM "macos")
        set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-ambiguous-reversed-operator ")
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-ambiguous-reversed-operator ")
        if (CMAKE_HOST_SYSTEM_PROCESSOR STREQUAL "arm64")
            set(JAVET_LIB_ARCH "-arm64")
            add_definitions(-D__arm64__)
        else()
            set(JAVET_LIB_ARCH "-x86_64")
            add_definitions(-D__x86_64__)
        endif()
        add_definitions(-D__APPLE__)
        list(APPEND includeDirs $ENV{JAVA_HOME}/include/darwin)
        if(DEFINED V8_DIR)
            foreach(importLibrary ${importLibraries})
                set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_RELEASE_DIR}/obj/lib${importLibrary}.a)
                target_link_libraries(Javet PUBLIC -force_load ${importLibrary})
                target_link_libraries(JavetStatic PUBLIC -force_load ${importLibrary})
            endforeach(importLibrary)
            # From V8 v11.7 abseil is somehow not built properly.
            # This is a patch build.
            # https://github.com/abseil/abseil-cpp/blob/master/CMake/README.md
            add_subdirectory(${V8_DIR}/third_party/abseil-cpp ${V8_RELEASE_DIR}/third_party/abseil-cpp)
            target_link_libraries(Javet PUBLIC -lc++ absl::base absl::time)
            target_link_libraries(JavetStatic PUBLIC -lc++ absl::base absl::time)
        endif()
        if(DEFINED NODE_DIR)
            list(APPEND includeDirs
                ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen/generate-bytecode-output-root
                ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen/inspector-generated-output-root
                ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/gen)
            foreach(importLibrary ${importLibraries})
                set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/lib${importLibrary}.a)
            endforeach(importLibrary)
            list(REMOVE_ITEM importLibraries v8_init)
            foreach(importLibrary ${importLibraries})
                target_link_libraries(Javet PUBLIC -force_load ${importLibrary})
                target_link_libraries(JavetStatic PUBLIC -force_load ${importLibrary})
            endforeach(importLibrary)
            # From V8 v11.7 abseil is somehow not built properly.
            # This is a patch build.
            # https://github.com/abseil/abseil-cpp/blob/master/CMake/README.md
            add_subdirectory(${NODE_DIR}/deps/v8/third_party/abseil-cpp ${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/third_party/abseil-cpp)
            target_link_libraries(Javet PUBLIC v8_init absl::base absl::crc32c absl::time "-framework Security")
            target_link_libraries(JavetStatic PUBLIC v8_init absl::base absl::crc32c absl::time "-framework Security")
        endif()
        # https://caoccao.blogspot.com/2021/08/jni-symbol-conflicts-in-mac-os.html
        target_link_libraries(Javet PUBLIC -exported_symbols_list ${CMAKE_SOURCE_DIR}/jni/exported_symbols_list.txt)
    else()
        message(FATAL_ERROR "Windows (arm64) is not supported.")
    endif()
    set_target_properties(JavetStatic PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
else()
    message(FATAL_ERROR "Linux (x86-64, arm64), Mac OS (x86-64, arm64), Windows (x86-64) and Android are the only supported Operating Systems.")
endif()

set_target_properties(Javet PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
include_directories(${includeDirs})
add_custom_command(TARGET Javet POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:Javet> "${JAVA_RESOURCES_DIR}/$<TARGET_FILE_NAME:Javet>")
