===================
Release Notes 1.1.x
===================

1.1.2
-------------

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
