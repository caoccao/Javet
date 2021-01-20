cmake_minimum_required(VERSION 3.16)

if(!$ENV{JAVA_HOME})
    message(FATAL_ERROR "JAVA_HOME is not found. Please make sure you have JDK 8 or 11 installed properly.")
endif()

set(CMAKE_TRY_COMPILE_TARGET_TYPE STATIC_LIBRARY)
set(CMAKE_CONFIGURATION_TYPES "Debug;Release" CACHE STRING "limited configs" FORCE)

project(Javet)
aux_source_directory("jni" sourceFiles)
add_library(v8_monolith STATIC IMPORTED)
add_library(Javet SHARED ${sourceFiles} "jni/javet.rc")
target_link_libraries(Javet v8_monolith)
set(CMAKE_CXX_STANDARD 14)

set(includeDirs
    $ENV{JAVA_HOME}/include
    ${V8_DIR}/include
    ${V8_DIR}/include/libplatform)

if(CMAKE_SYSTEM_NAME STREQUAL "Windows")
    add_definitions(-D_ITERATOR_DEBUG_LEVEL=0)
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/win32)
    set_property(TARGET Javet APPEND_STRING PROPERTY LINK_FLAGS_RELEASE "")
    set_property(TARGET Javet PROPERTY MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded$<$<CONFIG:Debug>:Debug>")
    set_target_properties(Javet PROPERTIES OUTPUT_NAME "libjavet-windows-x86_64.v.${JAVET_VERSION}")
    set_target_properties(v8_monolith PROPERTIES IMPORTED_LOCATION ${V8_DIR}/out.gn/x64.release/obj/v8_monolith.lib)
    target_link_libraries(Javet DbgHelp Winmm)
elseif(CMAKE_SYSTEM_NAME STREQUAL "Linux")
    list(APPEND includeDirs $ENV{JAVA_HOME}/include/linux)
    set_target_properties(Javet PROPERTIES OUTPUT_NAME "libjavet-linux-x86_64.v.${JAVET_VERSION}")
else()
    message(FATAL_ERROR "OS that is not either Windows or Linux hasn't been supported yet.")
endif()

include_directories(${includeDirs})
