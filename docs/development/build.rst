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
    build_javet_with_pre_built_binaries
    build_javet_from_scratch

All of them share the same build environment in the next section.

Build Environment
=================

Linux Environment
-----------------

* Ubuntu 20.04
* CMake 3.21+
* JDK 8
* Gradle 7.2+

MacOS Environment
-----------------

* MacOS Catalina+
* Latest Brew
* Xcode 11.4.1+
* Cmake 3.16+
* JDK 8
* Gradle 7.2+

Windows Environment
-------------------

* Latest Windows 10
* Visual Studio 2019 Community
* CMake 3.16+ (comes with Visual Studio)
* Latest Windows 10 SDK with WinDbg
* JDK 8
* Gradle 7.2+

Android Environment
-------------------

* Ubuntu 20.04 or Ubuntu 20.04 in the WSL2 on the Latest Windows 10
* CMake 3.10+
* JDK 8
* Gradle 7.2+
* Latest Android Studio (Optional)
* Android NDK r21e
* Android SDK 30

Environment Variables
---------------------

* ``${JAVET_HOME}`` is the home directory of Javet.
* ``${NODE_HOME}`` is the home directory of Node.
* ``${V8_HOME}`` is the home directory of V8.
* ``${DEPOT_TOOLS_HOME}`` is the home directory of Google depot tools.
* ``${ANDROID_NDK_HOME}`` is the home directory of Android NDK.
* ``${ANDROID_SDK_HOME}`` is the home directory of Android SDK.
