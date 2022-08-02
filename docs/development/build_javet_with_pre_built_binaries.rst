===================================
Build Javet with Pre-built Binaries
===================================

Download Pre-built Node.js and V8
=================================

.. note::

    As the docker builds are available, I have stopped publishing pre-built binaries. If you really need them, please contact the maintainer wisely. Legacy pre-built binaries are at this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_.

Build Javet JNI Library for Linux, Mac OS and Windows
=====================================================

Once Node.js and V8 are ready, please navigate to ``${JAVET_HOME}/cpp``, make sure CMake is accessible and execute corresponding build script.

=========== =================================================================== ===================================================================
OS          Node.js Command                                                     V8 Command
=========== =================================================================== ===================================================================
Linux       ``sh build-linux.sh -DNODE_DIR=${NODE_HOME}``                       ``sh build-linux.sh -DV8_DIR=${V8_HOME}``
Mac OS      ``sh build-macos.sh -DNODE_DIR=${NODE_HOME}``                       ``sh build-macos.sh -DV8_DIR=${V8_HOME}``
Windows     ``build-windows.cmd -DNODE_DIR=%NODE_HOME%``                        ``build-windows.cmd -DV8_DIR=%V8_HOME%``
=========== =================================================================== ===================================================================

After a while, the following libraries will be placed in folder ``${JAVET_HOME}/src/main/resources``.

=========== =========================================================== ==========================================================
OS          Node.js Library                                             V8 Library
=========== =========================================================== ==========================================================
Linux       ``libjavet-node-linux-x86_64.v.*.*.*.so``                   ``libjavet-v8-linux-x86_64.v.*.*.*.so``
Mac OS      ``libjavet-node-macos-x86_64.v.*.*.*.dylib``                ``libjavet-v8-macos-x86_64.v.*.*.*.dylib``
Windows     ``libjavet-node-windows-x86_64.v.*.*.*.dll``                ``libjavet-v8-windows-x86_64.v.*.*.*.dll``
=========== =========================================================== ==========================================================

Build Javet JNI Library for Android
===================================

Once V8 are ready, please navigate to ``./cpp``, make sure CMake is accessible and execute corresponding build script.

======= ==============================================================================================================
Arch    Command
======= ==============================================================================================================
arm     sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=arm
arm64   sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=arm64
x86     sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=x86
x86_64  sh ./build-android.sh -DV8_DIR=${V8_HOME} -DCMAKE_ANDROID_NDK=${ANDROID_NDK_HOME} -DCMAKE_ANDROID_ARCH=x86_64
======= ==============================================================================================================

After a while, the following libraries will be placed in folder ``${JAVET_HOME}/android/javet-android/src/main/jniLibs``.

======= ==============================================================================================================
Arch    Library
======= ==============================================================================================================
arm     ``armeabi-v7a/libjavet-v8-android-arm.v.*.*.*.so``
arm64   ``arm64-v8a/libjavet-v8-android-arm64.v.*.*.*.so``
x86     ``x86/libjavet-v8-android-x86.v.*.*.*.so``
x86_64  ``x86_64/libjavet-v8-android-x86_64.v.*.*.*.so``
======= ==============================================================================================================

.. note::

    * To enable i18n support for V8 mode, please append ``-DENABLE_I18N`` to the command.

Build Javet
===========

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
