========================
Build Javet from Scratch
========================

Build V8
========

Please follow the `official guide <https://v8.dev/docs/build>`_ to build V8. If you face any issues, you may contact `@sjtucaocao <https://twitter.com/sjtucaocao>`_.

Prepare V8
----------

* Clone the source code.
* Checkout a proper version.

Also, please make sure ``args.gn`` file looks like the following.

.. code-block:: ini

    ; arm64

    is_debug = false
    target_cpu = "arm64"
    v8_monolithic = true
    v8_use_external_startup_data = false
    is_component_build = false
    v8_enable_i18n_support = false
    v8_enable_pointer_compression = false
    v8_static_library = true
    symbol_level = 0
    use_custom_libcxx = false
    v8_enable_sandbox=false

    ; x86_64

    is_debug = false
    target_cpu = "x64"
    v8_monolithic = true
    v8_use_external_startup_data = false
    is_component_build = false
    v8_enable_i18n_support = false
    v8_enable_pointer_compression = false
    v8_static_library = true
    symbol_level = 0
    use_custom_libcxx = false
    v8_enable_sandbox=false

.. note::

    * To enable i18n support, please set ``v8_enable_i18n_support = true``.

Build V8 for Linux arm64
------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}
    ninja -C out.gn/arm64.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/arm64.release v8_monolith

Build V8 for Linux x86_64
-------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}
    ninja -C out.gn/x64.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/x64.release v8_monolith

Build V8 for Mac OS arm64
-------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}
    ninja -C out.gn/arm64.release v8_monolith

Build V8 for Mac OS x86_64
--------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}
    ninja -C out.gn/x64.release v8_monolith

Build V8 for Windows
--------------------

.. caution::

    The patch script requires Python 3.

.. code-block:: shell

    set PATH=%DEPOT_TOOLS_HOME%;%PATH%
    set DEPOT_TOOLS_WIN_TOOLCHAIN=0
    cd %V8_HOME%
    ninja -C out.gn/x64.release v8_monolith
    python %JAVET_HOME%\scripts\python\patch_v8_build.py -p .\
    ninja -C out.gn/x64.release v8_monolith
    gn gen --ide=vs out.gn\x64.solution

Build V8 for Android
--------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}
    python3 tools/dev/v8gen.py arm.release -- 'target_os="android"' 'target_cpu="arm"' 'v8_target_cpu="arm"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
    ninja -C out.gn/arm.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/arm.release v8_monolith
    python3 tools/dev/v8gen.py arm64.release -- 'target_os="android"' 'target_cpu="arm64"' 'v8_target_cpu="arm64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
    ninja -C out.gn/arm64.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/arm64.release v8_monolith
    python3 tools/dev/v8gen.py ia32.release -- 'target_os="android"' 'target_cpu="x86"' 'v8_target_cpu="x86"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
    ninja -C out.gn/ia32.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/ia32.release v8_monolith
    python3 tools/dev/v8gen.py x64.release -- 'target_os="android"' 'target_cpu="x64"' 'v8_target_cpu="x64"' v8_monolithic=true v8_use_external_startup_data=false is_component_build=false v8_enable_i18n_support=false v8_enable_pointer_compression=false v8_static_library=true symbol_level=0 use_custom_libcxx=false v8_enable_sandbox=false
    ninja -C out.gn/x64.release v8_monolith
    python3 ${JAVET_HOME}/scripts/python/patch_v8_build.py -p ./
    ninja -C out.gn/x64.release v8_monolith

Why Patching V8?
----------------

A few ninja files set certain warnings as errors so that MSVC stops compilation. The patch is to turn off those errors.

Build Node.js
=============

Please follow `Building Node.js <https://github.com/nodejs/node/blob/master/BUILDING.md>`_ to build the static and LTS version of Node.js libraries.

Prepare Node.js
---------------

* Clone the source code.
* Checkout a proper version.

Build Node.js on Linux
----------------------

.. caution::

    The patch script requires Python 3.

.. code-block:: shell

    cd ${NODE_HOME}
    python3 ${JAVET_HOME}/scripts/python/patch_node_build.py -p ${NODE_HOME}
    ./configure --enable-static --without-intl
    python3 ${JAVET_HOME}/scripts/python/patch_node_build.py -p ${NODE_HOME}
    make -j4

Why Patching Node.js?
---------------------

* First patch: All static node libraries are ``<thin>`` libraries. The patch is to disable ``<thin>``.
* Second patch: Many static node libraries are not compiled to `position independent code <https://en.wikipedia.org/wiki/Position-independent_code>`_ and link phase is broken with the following error. The patch is to set ``-fPIC`` to those make files.

    /usr/bin/ld: /....../out/Release/libnode.a(node_binding.o): 
    relocation R_X86_64_TPOFF32 against ``_ZN4nodeL23thread_local_modpendingE`` 
    can not be used when making a shared object; 
    recompile with -fPIC

Build Node.js on Mac OS
-----------------------

.. code-block:: shell

    cd ${NODE_HOME}
    ./configure --enable-static --without-intl
    make -j4

Build Node.js on Windows
------------------------

.. code-block:: shell

    cd %NODE_HOME%
    vcbuild.bat static without-intl vs2022

Build Javet JNI Library
=======================

Once Node.js and V8 are ready, please navigate to ``${JAVET_HOME}/cpp``, make sure CMake is accessible and execute corresponding build script.

============= =================================================================== ===================================================================
OS            Node.js Command                                                     V8 Command
============= =================================================================== ===================================================================
Linux arm64   ``sh build-linux-arm64.sh -DNODE_DIR=${NODE_HOME}``                 ``sh build-linux-arm64.sh -DV8_DIR=${V8_HOME}``
Linux x86_64  ``sh build-linux-x86_64.sh -DNODE_DIR=${NODE_HOME}``                ``sh build-linux-x86_64.sh -DV8_DIR=${V8_HOME}``
Mac OS        ``sh build-macos.sh -DNODE_DIR=${NODE_HOME}``                       ``sh build-macos.sh -DV8_DIR=${V8_HOME}``
Windows       ``build-windows.cmd -DNODE_DIR=%NODE_HOME%``                        ``build-windows.cmd -DV8_DIR=%V8_HOME% -T ClangCL``
============= =================================================================== ===================================================================

After a while, the following libraries will be placed in folder ``${JAVET_HOME}/src/main/resources``.

=============== =========================================================== ==========================================================
OS              Node.js Library                                             V8 Library
=============== =========================================================== ==========================================================
Linux arm64     ``libjavet-node-linux-arm64.v.*.*.*.so``                    ``libjavet-v8-linux-arm64.v.*.*.*.so``
Linux x86_64    ``libjavet-node-linux-x86_64.v.*.*.*.so``                   ``libjavet-v8-linux-x86_64.v.*.*.*.so``
Mac OS arm64    ``libjavet-node-macos-arm64.v.*.*.*.dylib``                 ``libjavet-v8-macos-arm64.v.*.*.*.dylib``
Mac OS x86_64   ``libjavet-node-macos-x86_64.v.*.*.*.dylib``                ``libjavet-v8-macos-x86_64.v.*.*.*.dylib``
Windows         ``libjavet-node-windows-x86_64.v.*.*.*.dll``                ``libjavet-v8-windows-x86_64.v.*.*.*.dll``
=============== =========================================================== ==========================================================

Build Javet JNI Library for Android
===================================

Once V8 are ready, please navigate to ``./cpp``, make sure CMake is accessible and execute corresponding build script.

======= ==================================================================================================================
Arch    Command
======= ==================================================================================================================
arm     ``sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=arm``
arm64   ``sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=arm64``
x86     ``sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=x86``
x86_64  ``sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=x86_64``
======= ==================================================================================================================

After a while, the following libraries will be placed in folder ``${JAVET_HOME}/android/javet-android/src/main/jniLibs``.

======= ==============================================================================================================
Arch    Library
======= ==============================================================================================================
arm     ``armeabi-v7a/libjavet-v8-android.v.*.*.*.so``
arm64   ``arm64-v8a/libjavet-v8-android.v.*.*.*.so``
x86     ``x86/libjavet-v8-android.v.*.*.*.so``
x86_64  ``x86_64/libjavet-v8-android.v.*.*.*.so``
======= ==============================================================================================================

Build Javet JNI Library with i18n
=================================

To enable i18n support, please append ``-DENABLE_I18N=1`` to the command.

Build Javet Jar
===============

Build Javet for Linux, Mac OS and Windows
-----------------------------------------

.. code-block:: shell

    cd ${JAVET_HOME}
    gradle build test

After a while, ``javet-*.*.*.jar`` will be placed in folder ``${JAVET_HOME}/build/libs``.

Build Javet for Android
-----------------------

.. code-block:: shell

    cd ${JAVET_HOME}/android
    gradle build

After a while, ``javet-android-*.*.*-release.aar`` will be placed in folder ``${JAVET_HOME}/android/javet-android/build/outputs/aar``.
