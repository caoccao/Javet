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

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2019 Community
* CMake 3.16+ (comes with Visual Studio)
* Latest Windows 10 SDK with WinDbg
* JDK 8

Download Pre-built V8
=====================

I have prepared pre-built Linux and Windows version of V8 ``v8.9.255``. Please download the headers and binary from this `drive <https://drive.google.com/drive/folders/18wcF8c-zjZg9iZeGfNSL8-bxqJwDZVEL?usp=sharing>`_ and unzip them locally.

Build Javet JNI Library
=======================

Once V8 is ready, please navigate to ``./cpp``, make sure CMake is accessible and execute corresponding build script.

* Linux - ``sh build.sh -DV8_DIR=/where_the_v8_directory_is``.
* Windows - ``build.cmd -DV8_DIR=\where_the_v8_directory_is``.

Note: The V8 directory needs to be absolute path.

After a while, ``libjavet-linux-x86_64.v.*.*.*.so`` or ``libjavet-windows-x86_64.v.*.*.*.dll`` will be placed in folder ``src/main/resources``.

Build Javet Jar
===============

Once both ``libjavet-linux-x86_64.v.*.*.*.so`` and ``libjavet-windows-x86_64.v.*.*.*.dll`` are built, please put them altogether under ``src/main/resources`` then kick off ``gradle build test``.

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

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
