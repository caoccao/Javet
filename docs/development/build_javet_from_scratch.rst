========================
Build Javet from Scratch
========================

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

* MacOS Catalina+
* Latest Brew
* Xcode 11.4.1+
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

Build V8
========

Please follow the `official guide <https://v8.dev/docs/build>`_ to build V8. If you face any issues, you may contact `@sjtucaocao <https://twitter.com/sjtucaocao>`_.

Prepare V8
----------

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

Build V8 on Linux and Mac OS
----------------------------

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

Build Node.js
=============

Please follow `Building Node.js <https://github.com/nodejs/node/blob/master/BUILDING.md>`_ to build the static and LTS version of Node.js libraries.

Prepare Node.js
---------------

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

    /usr/bin/ld: /....../out/Release/libnode.a(node_binding.o): 
    relocation R_X86_64_TPOFF32 against ``_ZN4nodeL23thread_local_modpendingE`` 
    can not be used when making a shared object; 
    recompile with -fPIC

Build Node.js on Mac OS
-----------------------

.. code-block:: shell

    ./configure --enable-static --without-intl
    make -j4

Build Node.js on Windows
------------------------

* Clone the source code.
* Checkout a proper version.
* Execute ``vcbuild.bat static without-intl``.

Build Javet
===========

Once Node.js and V8 are ready, please proceed with `Build Javet with Pre-built Binaries <build_javet_with_pre_built_binaries.rst>`_.

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
