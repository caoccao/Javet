===================
Release Notes 3.1.x
===================

3.1.3 V8 v12.6
--------------

* Upgraded Node.js to ``v20.14.0`` `(2024-05-28) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.14.0>`_
* Upgraded V8 to ``v12.6.228.13`` (2024-06-06)
* Upgraded Visual Studio 2022 to `v17.10.1 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.10>`_
* Rewrote ``toString()`` for ``V8ValueBigInteger``, ``V8ValueInteger``, ``V8ValueLong`` and ``V8ValueDouble``
* Added ``getPrototypeOf()`` to support ``instanceof`` for proxy converter
* Added ``getPrototypeOf()``, ``setPrototypeOf()``, ``create()``, ``seal()`` to ``V8ValueBuiltInObject``
* Added ``JavetProxyPrototypeStore``
* Added ``getPrototypeOf()`` to ``IJavetDirectProxyHandler`` and ``JavetDirectProxyObjectHandler``
* Added ``getGuard()`` to ``V8Runtime``
* Added ``isSealed()``, ``isFrozen()`` to ``V8ValueObject``
* Added ``sealedEnabled`` to ``JavetConverterConfig``
* Updated ``JavetObjectConverter`` to convert sealed array to ``Object[]`` instead of ``List<Object>``
* Replaced ``JavetEngineGuard`` with ``V8Guard``
* Removed ``executorService``, ``engineGuardCheckIntervalMillis`` from ``JavetEngineConfig``
* Patched V8 `Check failed: !IsFreeSpaceOrFillerMap(map) <https://groups.google.com/g/v8-dev/c/TCGnZKjYFEI/m/uDOciJsHAQAJ>`_

3.1.2 V8 v12.5
--------------

* Upgraded Node.js to ``v20.13.1`` `(2024-05-09) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.13.1>`_
* Upgraded V8 to ``v12.5.227.6`` (2024-04-26)
* Upgraded Visual Studio 2022 to `v17.9.6 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.9>`_
* Added ``V8AwaitMode.RunNoWait``
* Fixed unexpected behavior of ``V8AwaitMode.RunOnce``
* Enabled ``__dirname``, ``__filename``, ``require`` root injection for Node.js Android

3.1.1 V8 v12.4
--------------

* Upgraded Node.js to ``v20.12.2`` `(2024-04-10) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V20.md#20.12.2>`_
* Upgraded V8 to ``v12.4.254.9`` (2024-04-04)
* Updated Node.js to new process initialization and teardown functions
* Added crypto initialization to Node.js
* Removed ``isPurgeEventLoopBeforeClose()``, ``setPurgeEventLoopBeforeClose()`` from ``NodeRuntime``

3.1.0 V8 v12.3
--------------

* Upgraded V8 to ``v12.3.219.10`` (2024-03-11)
* Upgraded Visual Studio 2022 to `v17.9.3 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.9>`_
* Added support for Android Node.js mode
* Added ``default`` to built-in Node.js modules for ESM
* Fixed inspector locking issue
* Added ``IV8Module.getIdentityHash()``
* Revised ``IV8Module.getNamespace()``, ``IV8Module.getScriptId()``, ``IV8Module.getResourceName()``, ``IV8Script.getResourceName()``
* Removed ``IV8Module.setResourceName()``, ``IV8Script.setResourceName()``
