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

cmake_minimum_required(VERSION 3.10)

# CMP0091 must be set before first project().
# @see: https://cmake.org/cmake/help/latest/prop_tgt/MSVC_RUNTIME_LIBRARY.html
if (POLICY CMP0091)
    cmake_policy(SET CMP0091 NEW)
endif()

include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/JavetValidation.cmake)
include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/JavetConfig.cmake)

# Project definition
project(Javet)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)

# Include engine-specific configuration (V8 or Node)
if(DEFINED V8_DIR)
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/JavetV8.cmake)
elseif(DEFINED NODE_DIR)
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/JavetNode.cmake)
endif()

# Create imported library targets
include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/JavetTargets.cmake)

# Include platform-specific configuration
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/platforms/Windows.cmake)
elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux" AND CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|x86_64|aarch64)")
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/platforms/Linux.cmake)
elseif(CMAKE_SYSTEM_NAME STREQUAL "Android" AND CMAKE_HOST_SYSTEM_PROCESSOR STREQUAL "x86_64")
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/platforms/Android.cmake)
elseif(CMAKE_SYSTEM_NAME STREQUAL "Darwin" AND CMAKE_HOST_SYSTEM_PROCESSOR MATCHES "(arm64|x86_64)")
    include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/platforms/Darwin.cmake)
else()
    message(FATAL_ERROR "Linux (x86-64, arm64), Mac OS (x86-64, arm64), Windows (x86-64) and Android are the only supported Operating Systems.")
endif()

# Set final target properties
set_target_properties(Javet PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
include_directories(${includeDirs})
add_custom_command(TARGET Javet POST_BUILD COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:Javet> "${JAVA_RESOURCES_DIR}/$<TARGET_FILE_NAME:Javet>")
