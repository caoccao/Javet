:orphan:

===================
Release Notes 1.0.x
===================

1.0.1
---------------------------------

* Upgraded Node.js to ``v16.11.1`` `(2021-10-12) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.11.1>`_
* Upgraded V8 to ``v9.4.146.19`` `(2021-10-01) <https://v8.dev/blog/v8-release-94>`_
* Upgraded callback function and proxy converter to implicitly cast more primitive types
* Added ``IJavetLibLoadingListener`` and ``JavetLibLoadingListener`` to allow custom lib loading
* Fixed a potential memory leak introduced mistakenly in v1.0.0

1.0.0 First Time with Node.js v16
---------------------------------

* Upgraded Node.js to ``v16.10.0`` `(2021-09-22) <https://github.com/nodejs/node/blob/master/doc/changelogs/CHANGELOG_V16.md#16.10.0>`_
* Added ``V8RuntimeOptions`` and ``NodeRuntimeOptions``
* Added ``getV8HeapStatistics()``, ``getV8HeapSpaceStatistics()`` and ``getV8SharedMemoryStatistics()`` to ``V8Runtime``
