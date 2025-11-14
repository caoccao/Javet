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

set_target_properties(JavetStatic PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
