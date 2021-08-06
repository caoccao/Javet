===========
Build Javet
===========

Build Environment
=================

Linux Environment
-----------------

* Ubuntu 20.04
* CMake 3.10+
* JDK 8
* Gradle 7.0+

MacOS Environment (Experimental)
--------------------------------

* MacOS Mojave+
* Latest Brew
* Xcode 10.1+
* Cmake 3.16+
* JDK 8
* Gradle 7.0+

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2019 Community
* CMake 3.16+ (comes with Visual Studio)
* Latest Windows 10 SDK with WinDbg
* JDK 8
* Gradle 7.0+

Download Pre-built Node.js and V8
=================================

I have prepared pre-built Linux and Windows version of Node.js ``v14.17.2`` and V8 ``v9.2.230.21``. Please download the zipped headers and binaries from this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_ and unzip them to local folders respectively.

Build Javet JNI Library
=======================

Once Node.js and V8 are ready, please navigate to ``./cpp``, make sure CMake is accessible and execute corresponding build script.

=========== =========================================================== ==========================================================
Type        Linux                                                       Windows
=========== =========================================================== ==========================================================
Node        ``sh build.sh -DNODE_DIR=/absolute_path_to_node_js_build``     ``build.cmd -DNODE_DIR=\absolute_path_to_node_js_build``
V8          ``sh build.sh -DV8_DIR=/absolute_path_to_v8_build``         ``build.cmd -DV8_DIR=\absolute_path_to_v8_build``
=========== =========================================================== ==========================================================

After a while, the following libraries will be placed in folder ``src/main/resources``.

=========== =========================================================== ==========================================================
Type        Linux                                                       Windows
=========== =========================================================== ==========================================================
Node        ``libjavet-node-linux-x86_64.v.*.*.*.so``                   ``libjavet-node-windows-x86_64.v.*.*.*.dll``
V8          ``libjavet-v8-linux-x86_64.v.*.*.*.so``                     ``libjavet-v8-windows-x86_64.v.*.*.*.dll``
=========== =========================================================== ==========================================================

Build Javet Jar
===============

Once both ``libjavet-*-linux-x86_64.v.*.*.*.so`` and ``libjavet-*-windows-x86_64.v.*.*.*.dll`` are built, please put them altogether under ``src/main/resources`` then kick off ``gradle build test``.

After a while, ``javet-*.*.*.jar`` will be placed in folder ``build/libs``.

Note: This jar file supports both Linux and Windows.

Upload Javet to Maven Central (Optional)
----------------------------------------

Package Jar files in Maven.

.. code-block:: sh

    # mvn package
    mvn clean
    mvn release:prepare
    mvn release:perform

Jar files are built under ``./target``.

Build V8 (Optional)
===================

Please follow the `official guide <https://v8.dev/docs/build>`_ to build V8. If you face any issues, you may contact `@sjtucaocao <https://twitter.com/sjtucaocao>`_.

Preparation (V8)
----------------

* Linux requires Python 2.7, CMake 3.10+. Ubuntu 20.04 is the recommended Linux distribution.
* Windows requires Windows 10, Python 2.7, Visual Studio 2019 Community, CMake (comes with Visual Studio), Windows 10 SDK with WinDbg.
* Clone the source code.
* Checkout a proper version.

Also, please make sure ``args.gn`` file looks like the following.

.. code-block:: ini

    is_debug = false
    target_cpu = "x64"
    v8_monolithic = true
    v8_use_external_startup_data = false
    is_component_build = false
    v8_enable_i18n_support= false
    v8_enable_pointer_compression = false
    v8_static_library = true
    symbol_level = 0
    use_custom_libcxx = false

Build V8 on Linux
-----------------

.. code-block:: shell

    export PATH=path_to_depot_tools:$PATH
    cd root_path_to_v8
    ninja -C out.gn/x64.release v8_monolith

Build V8 on Windows
-------------------

Note: The patch script requires Python 3.

.. code-block:: shell

    set PATH=path_to_depot_tools;%PATH%
    set DEPOT_TOOLS_WIN_TOOLCHAIN=0
    cd root_path_to_v8
    ninja -C out.gn/x64.release v8_monolith
    python root_path_to_javet\scripts\python\patch_v8_build.py -p .\
    ninja -C out.gn/x64.release v8_monolith
    gn gen --ide=vs out.gn\x64.solution

Why Patching?

A few ninja files set certain warnings as errors so that MSVC stops compilation. The patch is to turn off those errors.

Build Node.js (Optional)
========================

Please follow `Building Node.js <https://github.com/nodejs/node/blob/master/BUILDING.md>`_ to build the static and LTS version of Node.js libraries.

Preparation (Node.js)
---------------------

* Linux requires Python 2.7, CMake 3.10+, GCC 9.3+. Ubuntu 20.04 is the recommended Linux distribution (V8 v8.9 is recommended to be built on Ubuntu 18.04, and V8 v9.0+ is recommended to be built on Ubuntu 20.04).
* Windows requires Windows 10, Python 2.7, Visual Studio 2019 Community, CMake (comes with Visual Studio), Windows 10 SDK with WinDbg.
* Clone the source code.
* Checkout a proper version.

Build Node.js on Linux
----------------------

Note: The patch script requires Python 3.

.. code-block:: shell

    cd root_path_to_node_js
    python3 root_path_to_javet/scripts/python/patch_node_build.py -p ./
    ./configure --enable-static --without-intl
    python3 root_path_to_javet/scripts/python/patch_node_build.py -p ./
    make -j4

Why Patching?

* First patch: All static node libraries are ``<thin>`` libraries. The patch is to disable ``<thin>``.
* Second patch: Many static node libraries are not compiled to `position independent code <https://en.wikipedia.org/wiki/Position-independent_code>`_ and link phase is broken with the following error. The patch is to set ``-fPIC`` to those make files.

    /usr/bin/ld: /....../out/Release/libnode.a(node_binding.o): relocation R_X86_64_TPOFF32 against ``_ZN4nodeL23thread_local_modpendingE`` can not be used when making a shared objeect; recompile with -fPIC

Build Node.js on Windows
------------------------

* Clone the source code.
* Checkout a proper version.
* Execute ``vcbuild.bat static without-intl``.

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
