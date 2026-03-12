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
    # On Windows/MSVC ABI, private/public static class members use different
    # mangled names. `#define private public` changes the reference to `SA`
    # while Node defines a non-public variant in module_wrap.obj.
    # Discover the actual symbol at configure time, then alias SA -> actual.
    set(JAVET_NODE_MODULE_WRAP_OBJ "${NODE_DIR}/out.${OUT_DIR_SUFFIX}/Release/obj/libnode/src/module_wrap.obj")
    set(JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PRIVATE "")
    if(EXISTS "${JAVET_NODE_MODULE_WRAP_OBJ}")
        set(JAVET_NM_TOOL "${CMAKE_NM}")
        if(NOT JAVET_NM_TOOL)
            find_program(JAVET_NM_TOOL NAMES llvm-nm nm)
        endif()
        if(JAVET_NM_TOOL)
            execute_process(
                COMMAND "${JAVET_NM_TOOL}" "--defined-only" "--format=just-symbols" "${JAVET_NODE_MODULE_WRAP_OBJ}"
                OUTPUT_VARIABLE JAVET_NODE_MODULE_WRAP_SYMBOLS
                RESULT_VARIABLE JAVET_NM_RESULT
                ERROR_QUIET)
            if(JAVET_NM_RESULT EQUAL 0)
                string(REPLACE "\r\n" "\n" JAVET_NODE_MODULE_WRAP_SYMBOLS_NL "${JAVET_NODE_MODULE_WRAP_SYMBOLS}")
                string(REPLACE "\n" ";" JAVET_NODE_MODULE_WRAP_SYMBOL_LINES "${JAVET_NODE_MODULE_WRAP_SYMBOLS_NL}")
                foreach(JAVET_NODE_MODULE_WRAP_SYMBOL_LINE IN LISTS JAVET_NODE_MODULE_WRAP_SYMBOL_LINES)
                    if(JAVET_NODE_MODULE_WRAP_SYMBOL_LINE MATCHES "^\\?ResolveModuleCallback@ModuleWrap@loader@node@@C[A-Z].*")
                        string(STRIP "${JAVET_NODE_MODULE_WRAP_SYMBOL_LINE}" JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PRIVATE)
                        break()
                    endif()
                endforeach()
            endif()
        endif()
    endif()
    if(JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PRIVATE)
        string(
            REGEX REPLACE
            "@@C([A-Z])"
            "@@S\\1"
            JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PUBLIC
            "${JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PRIVATE}")
        # Put alternatename in a response file to avoid XML/MSBuild escaping
        # of '$' in decorated symbols.
        set(JAVET_NODE_MODULE_WRAP_ALIAS_RSP "${CMAKE_BINARY_DIR}/javet_module_wrap_alternatename.rsp")
        file(WRITE
            "${JAVET_NODE_MODULE_WRAP_ALIAS_RSP}"
            "/alternatename:${JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PUBLIC}=${JAVET_NODE_MODULE_WRAP_RESOLVE_MODULE_CALLBACK_PRIVATE}\n")
        set_property(
            TARGET Javet
            APPEND_STRING
            PROPERTY LINK_FLAGS
            " @\"${JAVET_NODE_MODULE_WRAP_ALIAS_RSP}\"")
    endif()
endif()
set_property(TARGET Javet APPEND_STRING PROPERTY LINK_FLAGS_RELEASE "")
set_property(TARGET Javet PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
target_link_libraries(Javet ${importLibraries})
