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
    # V8 is built with use_custom_libcxx=true (required for v8_enable_sandbox).
    # v8_monolith.a references symbols in libc++'s std::__Cr ABI namespace, so
    # Javet must compile against the same hermetic libc++ (headers from
    # buildtools/third_party/libc++ supply __config_site, which pins
    # _LIBCPP_ABI_NAMESPACE=__Cr) and link the matching libc++ objects.
    # Apple's clang would otherwise pick up the SDK's /usr/include/c++/v1 and
    # auto-link libc++.dylib, causing std symbol mismatches.
    add_definitions(-D_LIBCPP_HARDENING_MODE_DEFAULT=_LIBCPP_HARDENING_MODE_NONE)
    list(APPEND includeDirs
        ${V8_DIR}/buildtools/third_party/libc++
        ${V8_DIR}/third_party/libc++/src/include
    )
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -nostdinc++ -fno-exceptions -fno-modules -fno-implicit-modules -fno-builtin-module-map ")
    foreach(importLibrary ${importLibraries})
        set_target_properties(${importLibrary} PROPERTIES IMPORTED_LOCATION ${V8_RELEASE_DIR}/obj/lib${importLibrary}.a)
        target_link_libraries(Javet PUBLIC -force_load ${importLibrary})
        target_link_libraries(JavetStatic PUBLIC -force_load ${importLibrary})
    endforeach(importLibrary)
    # V8 builds libc++ as a static_library on non-Windows platforms, but the
    # GN toolchain on macOS produces "thin" GNU archives (containing only
    # references to the .o files, not the objects themselves). Apple's ld
    # doesn't understand thin archives, so we can't link libc++.a / libc++abi.a
    # directly the way Linux.cmake does with lld. Instead, glob the individual
    # .o files - they live next to the thin archive and are what the archive
    # references anyway. Same pattern as the Windows build (which globs because
    # V8 builds libc++ as a source_set there).
    file(GLOB JAVET_V8_LIBCXX_OBJS
        "${V8_RELEASE_DIR}/obj/buildtools/third_party/libc++/libc++/*.o")
    file(GLOB JAVET_V8_LIBCXXABI_OBJS
        "${V8_RELEASE_DIR}/obj/buildtools/third_party/libc++abi/libc++abi/*.o")
    if(NOT JAVET_V8_LIBCXX_OBJS)
        message(FATAL_ERROR
            "libc++ object files not found under "
            "${V8_RELEASE_DIR}/obj/buildtools/third_party/libc++/libc++/. "
            "Ensure V8 was built with use_custom_libcxx=true.")
    endif()
    target_link_libraries(Javet PUBLIC
        -nostdlib++
        ${JAVET_V8_LIBCXX_OBJS} ${JAVET_V8_LIBCXXABI_OBJS}
        "-framework Foundation" "-framework Security" -lobjc)
    target_link_libraries(JavetStatic PUBLIC
        -nostdlib++
        ${JAVET_V8_LIBCXX_OBJS} ${JAVET_V8_LIBCXXABI_OBJS}
        "-framework Foundation" "-framework Security" -lobjc)
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
    if(DEFINED NODE_CRATES_LIBRARY)
        target_link_libraries(Javet PUBLIC ${NODE_CRATES_LIBRARY})
        target_link_libraries(JavetStatic PUBLIC ${NODE_CRATES_LIBRARY})
    endif()
endif()
# https://caoccao.blogspot.com/2021/08/jni-symbol-conflicts-in-mac-os.html
target_link_libraries(Javet PUBLIC -exported_symbols_list ${CMAKE_SOURCE_DIR}/jni/exported_symbols_list.txt)

set_target_properties(JavetStatic PROPERTIES OUTPUT_NAME "${JAVET_LIB_PREFIX}-${JAVET_LIB_TYPE}-${JAVET_LIB_SYSTEM}${JAVET_LIB_ARCH}${JAVET_LIB_I18N}.v.${JAVET_VERSION}")
