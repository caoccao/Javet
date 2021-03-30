===========
Build Javet
===========

Build Environment
=================

Linux Environment
-----------------

* CMake 3.10+
* Ubuntu 18.04
* JDK 8
* Gradle 6.7+

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2019 Community
* CMake 3.16+ (comes with Visual Studio)
* Latest Windows 10 SDK with WinDbg
* JDK 8
* Gradle 6.7+

Download Pre-built Node.js and V8
=================================

I have prepared pre-built Linux and Windows version of Node.js ``v14.16.0`` and V8 ``v8.9.255``. Please download the zipped headers and binaries from this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_ and unzip them to local folders respectively.

Build Javet JNI Library
=======================

Once V8 is ready, please navigate to ``./cpp``, make sure CMake is accessible and execute corresponding build script.

=========== =========================================================== ==========================================================
Type        Linux                                                       Windows
=========== =========================================================== ==========================================================
Node        ``sh build.sh -DNODE_DIR=/where_the_node_directory_is``     ``build.cmd -DNODE_DIR=\where_the_node_directory_is``
V8          ``sh build.sh -DV8_DIR=/where_the_v8_directory_is``         ``build.cmd -DV8_DIR=\where_the_v8_directory_is``
=========== =========================================================== ==========================================================

Note: The V8 directory needs to be absolute path.

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

Please follow the `official guide <https://v8.dev/docs/build>`_ to build V8 ``v8.9.255``. If you face any issues, you may contact `@sjtucaocao <https://twitter.com/sjtucaocao>`_.

Some Tips on Building V8
------------------------

* Linux requires Python 2.7, CMake 3.10+. Ubuntu 18.04 is the recommended Linux distribution.
* Windows requires Windows 10, Python 2.7, Visual Studio 2019 Community, CMake (comes with Visual Studio), Windows 10 SDK with WinDbg.

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

``v8_monolith`` is the build target.

Build Node.js (Optional)
========================

Please follow `Building Node.js <https://github.com/nodejs/node/blob/master/BUILDING.md>`_ to build the static and LTS version of Node.js libraries. Please make sure ``without-intl`` is set so that the library size can be reduced.

Build Node.js on Linux
------------------------

* Clone the source code.
* Checkout a proper version.
* Execute ``python3 script/python/patch_node_build.py -p root_path_to_node_js``.
* Execute ``cd root_path_to_node_js && ./configure --enable-static --fully-static --without-intl``.
* Execute ``python3 script/python/patch_node_build.py -p root_path_to_node_js`` again.
* Execute ``cd root_path_to_node_js && make -j4``.

Why Patching?

* First patch: All static node libraries are ``<thin>`` libraries. The patch is to disable ``<thin>``.
* Second patch: Many static node libraries are not compiled to `position independent code <https://en.wikipedia.org/wiki/Position-independent_code>`_ and link phase is broken with the following error. The patch is to set ``-fPIC`` to those make files.

.. code-block:: cpp

    /usr/bin/ld: /***/out/Release/libnode.a(node_binding.o): relocation R_X86_64_TPOFF32 against `_ZN4nodeL23thread_local_modpendingE` can not be used when making a shared objeect; recompile with -fPIC
    ......

Build Node.js on Windows
------------------------

* Clone the source code.
* Checkout a proper version.
* Execute ``vcbuild.bat static without-intl``.

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
