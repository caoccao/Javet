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

* x86_64-non-i18n

.. include:: ../../scripts/v8/gn/linux-x86_64-non-i18n-args.gn
    :code: ini

* x86_64-i18n

.. include:: ../../scripts/v8/gn/linux-x86_64-i18n-args.gn
    :code: ini

* arm64-non-i18n

.. include:: ../../scripts/v8/gn/linux-arm64-non-i18n-args.gn
    :code: ini

* arm64-i18n

.. include:: ../../scripts/v8/gn/linux-arm64-i18n-args.gn
    :code: ini

Build V8 for Linux arm64
------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}

    # non-i18n
    mkdir -p out.gn.non-i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/linux-arm64-non-i18n-args.gn out.gn.non-i18n/arm64.release/args.gn
    gn gen out.gn.non-i18n/arm64.release
    ninja -C out.gn.non-i18n/arm64.release v8_monolith

    # i18n
    mkdir -p out.gn.i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/linux-arm64-i18n-args.gn out.gn.i18n/arm64.release/args.gn
    gn gen out.gn.i18n/arm64.release
    ninja -C out.gn.i18n/arm64.release v8_monolith

Build V8 for Linux x86_64
-------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}

    # non-i18n
    mkdir -p out.gn.non-i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/linux-x86_64-non-i18n-args.gn out.gn.non-i18n/x86_64.release/args.gn
    gn gen out.gn.non-i18n/x64.release
    ninja -C out.gn.non-i18n/x64.release v8_monolith

    # i18n
    mkdir -p out.gn.i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/linux-x86_64-i18n-args.gn out.gn.i18n/x86_64.release/args.gn
    gn gen out.gn.i18n/x64.release
    ninja -C out.gn.i18n/x64.release v8_monolith

Build V8 for Mac OS arm64
-------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}

    # non-i18n
    mkdir -p out.gn.non-i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/macos-arm64-non-i18n-args.gn out.gn.non-i18n/arm64.release/args.gn
    gn gen out.gn.non-i18n/arm64.release
    ninja -C out.gn.non-i18n/arm64.release v8_monolith

    # i18n
    mkdir -p out.gn.i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/macos-arm64-i18n-args.gn out.gn.i18n/arm64.release/args.gn
    gn gen out.gn.i18n/arm64.release
    ninja -C out.gn.i18n/arm64.release v8_monolith

Build V8 for Mac OS x86_64
--------------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}

    # non-i18n
    mkdir -p out.gn.non-i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/macos-x86_64-non-i18n-args.gn out.gn.non-i18n/x86_64.release/args.gn
    gn gen out.gn.non-i18n/x64.release
    ninja -C out.gn.non-i18n/x64.release v8_monolith

    # i18n
    mkdir -p out.gn.i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/macos-x86_64-i18n-args.gn out.gn.i18n/x86_64.release/args.gn
    gn gen out.gn.i18n/x64.release
    ninja -C out.gn.i18n/x64.release v8_monolith

Build V8 for Windows
--------------------

.. caution::

    The patch script requires Deno.

.. code-block:: shell

    set PATH=%DEPOT_TOOLS_HOME%;%PATH%
    set DEPOT_TOOLS_WIN_TOOLCHAIN=0
    cd %V8_HOME%

    # non-i18n
    md out.gn\x64.release
    copy %JAVET_HOME%\scripts\v8\gn\windows-x86_64-non-i18n-args.gn out.gn\x86_64.release\args.gn
    gn gen out.gn/x64.release
    ninja -C out.gn/x64.release v8_monolith
    deno --allow-all %JAVET_HOME%\scripts\deno\patch_v8_build.ts -p .\
    ninja -C out.gn/x64.release v8_monolith
    move out.gn\x64.release out.gn.non-i18n
    gn gen --ide=vs out.gn.non-i18n\x64.solution

    # i18n
    md out.gn\x64.release
    copy %JAVET_HOME%\scripts\v8\gn\windows-x86_64-i18n-args.gn out.gn\x86_64.release\args.gn
    gn gen out.gn/x64.release
    ninja -C out.gn/x64.release v8_monolith
    deno --allow-all %JAVET_HOME%\scripts\deno\patch_v8_build.ts -p .\
    ninja -C out.gn/x64.release v8_monolith
    move out.gn\x64.release out.gn.i18n
    gn gen --ide=vs out.gn.i18n\x64.solution

Build V8 for Android
--------------------

.. code-block:: shell

    export PATH=${DEPOT_TOOLS_HOME}:$PATH
    cd ${V8_HOME}

    # arm non-i18n
    mkdir -p out.gn.non-i18n/arm.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-arm-non-i18n-args.gn out.gn.non-i18n/arm.release/args.gn
    gn gen out.gn.non-i18n/arm.release
    ninja -C out.gn.non-i18n/arm.release v8_monolith

    # arm i18n
    mkdir -p out.gn.i18n/arm.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-arm-i18n-args.gn out.gn.i18n/arm.release/args.gn
    gn gen out.gn.i18n/arm.release
    ninja -C out.gn.i18n/arm.release v8_monolith

    # arm64 non-i18n
    mkdir -p out.gn.non-i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-arm64-non-i18n-args.gn out.gn.non-i18n/arm64.release/args.gn
    gn gen out.gn.non-i18n/arm64.release
    ninja -C out.gn.non-i18n/arm64.release v8_monolith

    # arm64 i18n
    mkdir -p out.gn.i18n/arm64.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-arm64-i18n-args.gn out.gn.i18n/arm64.release/args.gn
    gn gen out.gn.i18n/arm64.release
    ninja -C out.gn.i18n/arm64.release v8_monolith

    # x86 non-i18n
    mkdir -p out.gn.non-i18n/ia32.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-x86-non-i18n-args.gn out.gn.non-i18n/ia32.release/args.gn
    gn gen out.gn.non-i18n/ia32.release
    ninja -C out.gn.non-i18n/ia32.release v8_monolith

    # x86 i18n
    mkdir -p out.gn.i18n/ia32.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-x86-i18n-args.gn out.gn.i18n/ia32.release/args.gn
    gn gen out.gn.i18n/ia32.release
    ninja -C out.gn.i18n/ia32.release v8_monolith

    # x86_64 non-i18n
    mkdir -p out.gn.non-i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-x86_64-non-i18n-args.gn out.gn.non-i18n/x64.release/args.gn
    gn gen out.gn.non-i18n/x64.release
    ninja -C out.gn.non-i18n/x64.release v8_monolith

    # x86_64 i18n
    mkdir -p out.gn.i18n/x64.release
    cp ${JAVET_HOME}/scripts/v8/gn/android-x86_64-i18n-args.gn out.gn.i18n/x64.release/args.gn
    gn gen out.gn.i18n/x64.release
    ninja -C out.gn.i18n/x64.release v8_monolith

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

    The patch script requires Deno.

.. code-block:: shell

    cd ${NODE_HOME}
    deno --allow-all ${JAVET_HOME}/scripts/deno/patch_node_build.ts -p ${NODE_HOME}
    ./configure --enable-static --without-intl
    deno --allow-all ${JAVET_HOME}/scripts/deno/patch_node_build.ts -p ${NODE_HOME}
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

.. caution::

    The build script requires Deno.

Once Node.js and V8 are ready, please navigate to ``${JAVET_HOME}/cpp``, make sure CMake is accessible and execute the unified build script.

The build script supports additional options:

* ``--i18n`` - Enable internationalization support
* ``--cpu-count <n>`` - Set the number of CPU cores for parallel builds (default: auto-detect)
* ``--log-debug``, ``--log-error``, ``--log-info``, ``--log-trace`` - Enable logging for debugging

============== ======================================================================= =======================================================================
OS             Node.js Command                                                         V8 Command
============== ======================================================================= =======================================================================
Linux arm64    ``deno run build --os linux --arch arm64 --node-dir ${NODE_HOME}``      ``deno run build --os linux --arch arm64 --v8-dir ${V8_HOME}``
Linux x86_64   ``deno run build --os linux --arch x86_64 --node-dir ${NODE_HOME}``     ``deno run build --os linux --arch x86_64 --v8-dir ${V8_HOME}``
Mac OS arm64   ``deno run build --os macos --arch arm64 --node-dir ${NODE_HOME}``      ``deno run build --os macos --arch arm64 --v8-dir ${V8_HOME}``
Mac OS x86_64  ``deno run build --os macos --arch x86_64 --node-dir ${NODE_HOME}``     ``deno run build --os macos --arch x86_64 --v8-dir ${V8_HOME}``
Windows        ``deno run build --os windows --arch x86_64 --node-dir %NODE_HOME%``    ``deno run build --os windows --arch x86_64 --v8-dir %V8_HOME%``
============== ======================================================================= =======================================================================

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

.. caution::

    The build script requires Deno.

Once V8 are ready, please navigate to ``${JAVET_HOME}/cpp``, make sure CMake is accessible and execute the unified build script.

The Android NDK path can be specified using ``--android-ndk`` option. For additional options, see the Build Javet JNI Library section above.

======= ====================================================================================================
Arch    Command
======= ====================================================================================================
arm     ``deno run build --os android --arch arm --v8-dir ${V8_HOME} --android-ndk ${ANDROID_NDK_HOME}``
arm64   ``deno run build --os android --arch arm64 --v8-dir ${V8_HOME} --android-ndk ${ANDROID_NDK_HOME}``
x86     ``deno run build --os android --arch x86 --v8-dir ${V8_HOME} --android-ndk ${ANDROID_NDK_HOME}``
x86_64  ``deno run build --os android --arch x86_64 --v8-dir ${V8_HOME} --android-ndk ${ANDROID_NDK_HOME}``
======= ====================================================================================================

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

To enable i18n support, please append ``--i18n`` to the command.

For example:

.. code-block:: shell

    # V8 build with i18n on Linux x86_64
    deno run build --os linux --arch x86_64 --v8-dir ${V8_HOME} --i18n

    # Node.js build with i18n on macOS arm64
    deno run build --os macos --arch arm64 --node-dir ${NODE_HOME} --i18n

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
