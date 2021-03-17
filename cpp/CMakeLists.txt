cmake_minimum_required(VERSION 3.10)

if(!$ENV{JAVA_HOME})
    message(FATAL_ERROR "JAVA_HOME is not found. Please make sure you have JDK 8 or 11 installed properly.")
endif()

if(DEFINED V8_DIR AND DEFINED NODE_DIR)
    message(FATAL_ERROR "V8_DIR and NODE_DIR cannot be both defined.")
endif()

if((NOT DEFINED V8_DIR) AND (NOT DEFINED NODE_DIR))
    message(FATAL_ERROR "Either V8_DIR or NODE_DIR needs to be defined.")
endif()

set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)
set(CMAKE_CONFIGURATION_TYPES "Debug;Release" CACHE STRING "limited configs" FORCE)

# CMP0091 must be set before first project().
# @see: https://cmake.org/cmake/help/latest/prop_tgt/MSVC_RUNTIME_LIBRARY.html
if (POLICY CMP0091)
    cmake_policy(SET CMP0091 NEW)
endif()

project(Javet)
aux_source_directory("jni" sourceFiles)

set(includeDirs $ENV{JAVA_HOME}/include)

if(DEFINED V8_DIR)
    list(APPEND includeDirs 
        ${V8_DIR}/include)
    add_library(v8_monolith STATIC IMPORTED)
endif()

if(DEFINED NODE_DIR)
    list(APPEND includeDirs 
        ${NODE_DIR}/src
        ${NODE_DIR}/deps/uv/include)
    add_library(node STATIC IMPORTED)
    add_library(uv STATIC IMPORTED)
    add_definitions(-DENABLE_NODE)
endif()

add_library(Javet SHARED ${sourceFiles} "jni/javet.rc")
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)

if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    add_definitions(-D_ITERATOR_DEBUG_LEVEL=0)
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/win32)
    set_property(TARGET Javet APPEND_STRING PROPERTY LINK_FLAGS_RELEASE "")
    set_property(TARGET Javet PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    if(DEFINED V8_DIR)
        set_target_properties(v8_monolith PROPERTIES IMPORTED_LOCATION ${V8_DIR}/out.gn/x64.release/obj/v8_monolith.lib)
        target_link_libraries(Javet v8_monolith)
    endif()
    if(DEFINED NODE_DIR)
        set_target_properties(node PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/release/node.lib)
        set_target_properties(uv PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/release/lib/libuv.lib)
        set_target_properties(node PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:node.lib")
        set_target_properties(uv PROPERTIES LINK_FLAGS "/WHOLEARCHIVE:libuv.lib")
        target_link_libraries(Javet node uv)
    endif()
    set_target_properties(Javet PROPERTIES OUTPUT_NAME "libjavet-windows-x86_64.v.${JAVET_VERSION}")
elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/linux)
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -m64 ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -m64 ")
    if(DEFINED V8_DIR)
        set_target_properties(v8_monolith PROPERTIES IMPORTED_LOCATION ${V8_DIR}/out.gn/x64.release/obj/libv8_monolith.a)
        target_link_libraries(Javet v8_monolith debug "-lrt" optimized "-lrt" "${libgcc}")
    endif()
    if(DEFINED NODE_DIR)
        set_target_properties(node PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/release/libnode.a)
        set_target_properties(uv PROPERTIES IMPORTED_LOCATION ${NODE_DIR}/out/release/lib/libuv.a)
        set_target_properties(node PROPERTIES LINK_FLAGS "--whole-archive")
        set_target_properties(uv PROPERTIES LINK_FLAGS "--whole-archive")
        target_link_libraries(Javet node uv debug "-lrt" optimized "-lrt" "${libgcc}")
    endif()
    set_target_properties(Javet PROPERTIES OUTPUT_NAME "javet-linux-x86_64.v.${JAVET_VERSION}")
else()
    message(FATAL_ERROR "OS that is not either Windows or Linux hasn't been supported yet.")
endif()

include_directories(${includeDirs})
