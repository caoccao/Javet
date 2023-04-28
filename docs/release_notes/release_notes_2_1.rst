===================
Release Notes 2.1.x
===================

2.1.1 V8 v11.3
--------------

* Upgraded V8 to ``v11.3.244.8`` (2023-04-12)
* Upgraded Node.js to ``v18.16.0`` `(2023-04-12) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.16.0>`_
* Fixed a crash issue when closing the V8 runtime with dangling callback contexts on Android
* Update ABI to 24 for Android

2.1.1 V8 v11.2
--------------

* Upgraded V8 to ``v11.2.214.13`` (2023-04-04)
* Added dockerfiles for building Linux arm64 binaries
* Upgraded C++ standard to 20
* Fixed a bug in closing Node.js runtime

2.1.0 V8 v11.1
--------------

* Upgraded Node.js to ``v18.15.0`` `(2023-03-07) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V18.md#18.15.0>`_
* Upgraded V8 to ``v11.1.277.14`` (2023-03-04)
* Built Node.js with Visual Studio 2022 instead of 2019
* Added Automatic-Module-Name
* Stopped storing callback contexts in the JNI global reference table for Android
