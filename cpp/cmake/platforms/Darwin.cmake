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

include(${CMAKE_SOURCE_DIR}/cmake/platforms/PlatformCommon.cmake)

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
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-nullability-completeness -Wno-deprecated-declarations ")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-nullability-completeness -Wno-deprecated-declarations ")
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

set_target_properties(JavetStatic PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
