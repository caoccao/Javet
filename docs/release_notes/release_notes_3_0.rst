===================
Release Notes 3.0.x
===================

3.0.2 V8 v12.0
--------------

* Upgraded V8 to ``v12.0.267.8`` (2023-11-21)
* Upgraded Node.js to ``v20.10.0`` `(2023-11-22) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.10.0>`_
* Fixed Linux arm64 build addressing the changes in V8 v12

3.0.1 V8 v11.9
--------------

* Upgraded V8 to ``v11.9.169.6`` (2023-10-25)
* Upgraded Node.js to ``v20.9.0`` `(2023-10-24) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.9.0>`_
* Upgraded to Ubuntu 22.04
* Upgraded GCC to v11 on Linux
* Turned on a few compiler options for performance
* Added ``isSourceTextModule()``, ``isSyntheticModule()`` to ``V8Module``
* Added ``createV8Module()`` to ``V8Runtime``
* Added ``freeze()`` to ``V8ValueBuiltInObject``
* Added ``JavetBuiltInModuleResolver``

3.0.0 V8 v11.8
--------------

* Upgraded V8 to ``v11.8.172.15`` (2023-10-09)
* Upgraded Node.js to ``v20.8.0`` `(2023-09-28) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.8.0>`_
* Upgraded GCC to v10 on Linux
* Supported Linux arm64
* Fixed a contention issue in closing the reference
