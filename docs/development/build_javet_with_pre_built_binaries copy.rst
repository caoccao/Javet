===================================
Build Javet with Pre-built Binaries
===================================

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

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
