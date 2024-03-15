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

MacOS Environment
-----------------

* MacOS Catalina+
* Latest Brew
* Xcode 11.4.1+
* Cmake 3.16+
* JDK 8
* Gradle 8.5+
* Python 3.11+

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2022 Community v17.9.3+
* CMake 3.24+ (comes with Visual Studio 2022)
* Latest Windows 10 SDK with WinDbg
* JDK 8
* Gradle 8.5+
* Python 3.11+

Android Environment
-------------------

* Ubuntu 20.04+ or Ubuntu 20.04+ in the WSL2 on the Latest Windows 10
* CMake 3.25.1+
* JDK 8
* Gradle 8.5+
* Python 3.11+
* Latest Android Studio (Optional)
* Android NDK r25b
* Android SDK 30

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

======================= ======= ==================================================================================================================================
Feature                 Flag    Description
======================= ======= ==================================================================================================================================
Custom libcxx           Off     The official release doesn't support custom libcxx.
Debug                   Off     The official release is a **release** build.
External Startup Data   Off     The official release doesn't support external startup data.
i18n                    Off     The official release doesn't support i18n. Please contact the maintainer for private builds supporting this feature.
Pointer Compression     Off     The official release doesn't support pointer compression.
Sandbox                 Off     The official release doesn't support sandbox. Please contact the maintainer for private builds supporting this feature.
Snapshot                Off     The official release doesn't support snapshot. Please contact the maintainer for private builds supporting this feature.
======================= ======= ==================================================================================================================================
