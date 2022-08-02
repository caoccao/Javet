===================
Release Notes 1.1.x
===================

1.1.7
--------------

* Fixed ``pom.xml`` for Android 
* Added optional i18n support for V8 mode

1.1.6 V8 v10.4
--------------

* Upgraded Node.js to ``v16.16.0`` `(2022-07-07) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.16.0>`_
* Upgraded V8 to ``v10.4.132.20`` `(2022-07-14) <https://v8.dev/blog/v8-release-104>`_
* Fixed improper error handling for custom error objects
* Added ``waitForEngineMaxRetryCount`` to ``JavetEngineConfig``
* Added ``EngineNotAvailable`` (901) to ``JavetError``

1.1.5 V8 v10.3
--------------

* Upgraded Node.js to ``v16.15.1`` `(2022-06-01) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.15.1>`_
* Upgraded V8 to ``v10.3.174.14`` `(2022-06-14) <https://v8.dev/blog/v8-release-103>`_
* Fixed JNI pending exception for Andoird by caching it in the runtime
* Added ``V8ValueBigInteger`` to primitive types and ``JavetPrimitiveConverter``

1.1.4 V8 v10.2
--------------

* Upgraded V8 to ``v10.2.154.4`` `(2022-05-06) <https://v8.dev/blog/v8-release-102>`_
* Applied C++ 17 to V8
* Added ``isPurgeEventLoopBeforeClose()`` and ``setPurgeEventLoopBeforeClose()`` to ``NodeRuntime``

1.1.3 V8 v10.1
--------------

* Upgraded Node.js to ``v16.15.0`` `(2022-04-26) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.15.0>`_
* Upgraded V8 to ``v10.1.124.11`` `(2022-04-20) <https://v8.dev/blog/v8-release-101>`_
* Removed internal symbols from public symbols for Linux

1.1.2 V8 v10.0
--------------

* Upgraded Node.js to ``v16.14.2`` `(2022-03-17) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.14.2>`_
* Upgraded V8 to ``v10.0.139.6`` `(2022-03-07) <https://v8.dev/blog/v8-release-100>`_
* Fixed a bug in exception handling in JNI callback

1.1.1 V8 v9.9
-------------

* Upgraded V8 to ``v9.9.115.9`` `(2022-03-07) <https://v8.dev/blog/v8-release-99>`_
* Enhanced createV8ValueArrayBuffer with ByteBuffer
* Added support to ``Optional``, ``Stream``, ``ZonedDateTime`` for Android
* Fixed a bug in ``V8ValueObject.get()`` when evaluation fails

1.1.0 V8 v9.8
-------------

* Upgraded Node.js to ``v16.14.0`` `(2022-02-08) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.14.0>`_
* Upgraded V8 to ``v9.8.177.11`` `(2022-02-08) <https://v8.dev/blog/v8-release-98>`_
* Upgraded Android NDK to r23b
* Updated Android ABI >= 23
* Added static libgcc and libstdc++ for Linux
* Supported legacy Linux distributions like CentOS 7, Ubuntu 16.04
