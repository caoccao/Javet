===================
Release Notes 3.1.x
===================

3.1.2 V8 v12.5
--------------

* Added ``V8AwaitMode.RunNoWait``
* Fixed unexpected behavior of ``V8AwaitMode.RunOnce``

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
* Upgraded Visual Studio 2022 to v17.9.3
* Added support for Android Node.js mode
* Added ``default`` to built-in Node.js modules for ESM
* Fixed inspector locking issue
* Added ``IV8Module.getIdentityHash()``
* Revised ``IV8Module.getNamespace()``, ``IV8Module.getScriptId()``, ``IV8Module.getResourceName()``, ``IV8Script.getResourceName()``
* Removed ``IV8Module.setResourceName()``, ``IV8Script.setResourceName()``
