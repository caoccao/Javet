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
    add_definitions(-D_WIN32_WINNT -DV8_TEMPORAL_SUPPORT)
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
