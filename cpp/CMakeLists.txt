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

# Initialization
project(Javet)
aux_source_directory("jni" sourceFiles)
set(includeDirs $ENV{JAVA_HOME}/include)
set(importLibraries)
set(JAVET_LIB_PREFIX)
set(JAVET_LIB_TYPE)
set(JAVET_LIB_SYSTEM)
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)
set(CMAKE_CONFIGURATION_TYPES "Debug;Release" CACHE STRING "limited configs" FORCE)
if(DEFINED ENABLE_LOGGING)
    add_definitions(-DJAVET_INFO -DJAVET_DEBUG -DJAVET_ERROR -DJAVET_TRACE)
endif()

# Preparation
if(DEFINED V8_DIR)
    list(APPEND includeDirs
        ${V8_DIR}/include)
    list(APPEND importLibraries v8_monolith)
    set(JAVET_LIB_TYPE "v8")
endif()
if(DEFINED NODE_DIR)
    list(APPEND includeDirs
        ${NODE_DIR}/src
        ${NODE_DIR}/deps/uv/include
        ${NODE_DIR}/deps/v8/include)
    list(APPEND importLibraries
        brotli cares histogram llhttp nghttp2 openssl torque_base uvwasi
        v8_base_without_compiler v8_compiler v8_init v8_initializers
        v8_libbase v8_libplatform v8_libsampler v8_snapshot v8_zlib zlib)
    if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
        list(APPEND importLibraries
            libnode libuv)
    elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux")
        list(APPEND importLibraries
            node node_text_start uv)
    endif()
    add_definitions(-DENABLE_NODE)
    set(JAVET_LIB_TYPE "node")
endif()
foreach(importLibrary ${importLibraries})
    add_library(${importLibrary} STATIC IMPORTED)
endforeach(importLibrary)

# Setting
if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    set(JAVET_LIB_PREFIX "libjavet")
    set(JAVET_LIB_SYSTEM "windows")
    add_definitions(-D_ITERATOR_DEBUG_LEVEL=0)
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/win32)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    if(DEFINED V8_DIR)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_DIR}/out.gn/x64.release/obj/${importLibrary}.lib)
        endforeach(importLibrary)
        set_target_properties(v8_monolith PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:v8_monolith.lib")
        add_library(Javet SHARED ${sourceFiles} "jni/javet_resource_v8.rc")
    endif()
    if(DEFINED NODE_DIR)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/release/lib/${importLibrary}.lib)
        endforeach(importLibrary)
        set_target_properties(libnode PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:libnode.lib")
        add_library(Javet SHARED ${sourceFiles} "jni/javet_resource_node.rc")
    endif()
    set_property(TARGET Javet APPEND_STRING PROPERTY LINK_FLAGS_RELEASE "")
    set_property(TARGET Javet PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    target_link_libraries(Javet ${importLibraries})
elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    set(JAVET_LIB_PREFIX "javet")
    set(JAVET_LIB_SYSTEM "linux")
    add_library(Javet SHARED ${sourceFiles})
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/linux)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m64 ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m64 ")
    if(DEFINED V8_DIR)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_DIR}/out.gn/x64.release/obj/lib${importLibrary}.a)
        endforeach(importLibrary)
        target_link_libraries(Javet PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
            debug "-lrt" optimized "-lrt" "${libgcc}")
    endif()
    if(DEFINED NODE_DIR)
        set(CMAKE_POSITION_INDEPENDENT_CODE ON)
        foreach(importLibrary ${importLibraries})
            set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/Release/lib${importLibrary}.a)
        endforeach(importLibrary)
        list(REMOVE_ITEM importLibraries v8_init)
        target_link_libraries(Javet PUBLIC -Wl,--whole-archive ${importLibraries} -Wl,--no-whole-archive
            v8_init debug "-lrt" optimized "-lrt" "${libgcc}")
    endif()
else()
    message(FATAL_ERROR "OS that is not either Windows or Linux hasn't been supported yet.")
endif()

set_target_properties(Javet PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}-x86_64.v.${JAVET_VERSION}")
include_directories(${includeDirs})
