===========================
Release Notes 4.0.x - 4.1.x
===========================

4.1.1
-----

* Upgraded Visual Studio 2022 to `v17.12.0 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.12>`_
* Added ``getAbsoluteResourceName()`` to ``IV8ModuleResolver``
* Fixed memory leaks in function callback in edge cases

4.1.0
-----

* Upgraded Node.js to ``v22.11.0`` `(2024-10-29) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V22.md#22.11.0>`_
* Upgraded V8 to ``v13.1.201.8`` (2024-11-11)
* Upgraded clang to v20 for V8 mode on MacOS
* Switched to core dependency + individual native dependency
* Supported ``Float16Array``
* Added ``Float16``
* Fixed ``JavetJVMInterceptor`` to allow arbitrary name
* Added ``addCallbackContexts()`` to ``JavetJVMInterceptor``

4.0.0
-----

* Upgraded Node.js to ``v22.9.0`` `(2024-09-17) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V22.md#22.9.0>`_
* Upgraded V8 to ``v13.0.245.16`` (2024-10-08)
* Added ``NodeI18n``, ``V8I18n`` to ``JSRuntimeType``
* Added ``getNodeI18nInstance()``, ``getV8I18nInstance()`` to ``V8Host``
* Renamed ``V8Runtime.hasPendingException()`` to ``V8Runtime.hasException()``
* Removed ``V8Runtime.hasScheduledException()`` and ``V8Runtime.promoteScheduledException()``
* Moved ``JavetReflectionObjectFactory`` to ``JavetBuddy``
* Added ``NodeFlags``
* Added ``NODE_FLAGS`` to ``NodeRuntimeOptions``
* Supported ``node:sqlite``
