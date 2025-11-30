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

# Initialization
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

# I18N Configuration
if(DEFINED ENABLE_I18N)
    set(JAVET_LIB_I18N "-i18n")
    set(OUT_DIR_SUFFIX "i18n")
else()
    set(OUT_DIR_SUFFIX "non-i18n")
endif()

# Common definitions
add_definitions(-DV8_ENABLE_WEBASSEMBLY -DV8_ENABLE_LEAPTIERING)
