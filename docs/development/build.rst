===========
Build Javet
===========

It's quite hard for developers to build Javet successfully for various reasons.

* Node.js needs to be built to static libraries.
* V8 needs to be built to monolith.
* Every OS platform has its own pitfalls which usually make developers frustrated, or even desperate.

Here are 3 ways of building Javet.

.. toctree::
    :maxdepth: 1

    build_javet_with_docker
    build_javet_from_scratch

All of them share the same build environment in the next section.

Build Environment
=================

Linux Environment
-----------------

* Ubuntu 20.04+
* CMake 3.25.1+
* JDK 8
* Gradle 8.5+
* GCC 11+
* Python 3.11+
* Deno 2.0+

MacOS Environment
-----------------

* MacOS Catalina+
* Latest Brew
* Xcode 16.3+
* Cmake 3.24+
* JDK 8
* Gradle 8.5+
* Python 3.11+
* Deno 2.0+

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2026 Community v18.4+ with ClangCL
* CMake 3.24+ (comes with Visual Studio 2026)
* Latest Windows 10 SDK with WinDbg
* JDK 8
* Gradle 8.5+
* Python 3.11+
* Deno 2.0+

Android Environment
-------------------

* Ubuntu 20.04+ or Ubuntu 20.04+ in the WSL2 on the Latest Windows 10
* CMake 3.25.1+
* JDK 8
* Gradle 8.5+
* Python 3.11+
* Latest Android Studio (Optional)
* Android NDK r27
* Android SDK 30
* Deno 2.0+

Environment Variables
---------------------

* ``${JAVET_HOME}`` is the home directory of Javet.
* ``${NODE_HOME}`` is the home directory of Node.
* ``${V8_HOME}`` is the home directory of V8.
* ``${DEPOT_TOOLS_HOME}`` is the home directory of Google depot tools.
* ``${ANDROID_NDK_HOME}`` is the home directory of Android NDK.
* ``${ANDROID_SDK_HOME}`` is the home directory of Android SDK.

V8 Feature Flags
================

======================= ==================================================================================================================================
Feature                 Description
======================= ==================================================================================================================================
Custom libcxx           V8 is built with Chromium's custom libc++ on some platforms.
Debug                   The official release is a **release** build.
External Startup Data   The V8 snapshot is embedded directly into the monolithic library.
i18n                    Both non-i18n and i18n builds are produced for every platform.
Pointer Compression     Pointer compression is enabled for V8 mode on all 64-bit platforms.
Sandbox                 V8 sandbox is enabled for V8 mode on Linux and Windows.
Snapshot                The official release doesn't support snapshot.
======================= ==================================================================================================================================

Custom libcxx
-------------

V8 mode uses Chromium's custom libc++ on Linux and Windows. On macOS, the system libc++ is used. On Android, the NDK's ``c++_static`` is used. Node.js mode uses the system's standard C++ library on all platforms.

=============== ======== =============
Platform        V8 Mode  Node.js Mode
=============== ======== =============
Linux x86_64    On       Off
Linux arm64     On       Off
Windows x86_64  On       Off
macOS x86_64    Off      Off
macOS arm64     Off      Off
Android arm64   Off      Off
Android x86_64  Off      Off
Android arm     Off      Off
Android x86     Off      Off
=============== ======== =============

Debug
-----

Off for all platforms and modes. The official release is always a **release** build.

External Startup Data
---------------------

Off for all platforms and modes. The V8 snapshot is embedded directly into the monolithic library.

i18n
----

Both i18n and non-i18n builds are produced for every platform and architecture. Node.js mode uses ``--with-intl=full-icu`` (i18n) or ``--without-intl`` (non-i18n).

Pointer Compression
-------------------

Pointer compression is enabled for V8 mode on all 64-bit platforms and disabled on 32-bit platforms. Node.js mode does not enable pointer compression.

=============== ================= ==============
Platform        V8 Mode           Node.js Mode
=============== ================= ==============
Linux x86_64    On (from v5.0.8)  Off
Linux arm64     On (from v5.0.8)  Off
Windows x86_64  On (from v5.0.8)  Off
macOS x86_64    On (from v5.0.8)  Off
macOS arm64     On (from v5.0.8)  Off
Android arm64   On (from v5.0.8)  Off
Android x86_64  On (from v5.0.8)  Off
Android arm     Off               Off
Android x86     Off               Off
=============== ================= ==============

Sandbox
-------

V8 sandbox is enabled for V8 mode on Linux, Windows, and macOS. Sandbox requires V8's hermetic libc++ (``use_safe_libcxx = use_custom_libcxx && enable_safe_libcxx``); Android keeps the system libc++ and so the sandbox is off there. Node.js mode does not enable sandbox.

=============== ================= ==============
Platform        V8 Mode           Node.js Mode
=============== ================= ==============
Linux x86_64    On (from v5.0.8)  Off
Linux arm64     On (from v5.0.8)  Off
Windows x86_64  On (from v5.0.8)  Off
macOS x86_64    On (from v5.0.8)  Off
macOS arm64     On (from v5.0.8)  Off
Android arm64   Off               Off
Android x86_64  Off               Off
Android arm     Off               Off
Android x86     Off               Off
=============== ================= ==============

Snapshot
--------

Off for all platforms and modes. Please contact the maintainer for private builds supporting this feature.
