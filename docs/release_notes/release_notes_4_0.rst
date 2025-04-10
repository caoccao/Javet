===========================
Release Notes 4.0.x - 4.1.x
===========================

4.1.2
-----

* Upgraded Node.js to ``v22.14.0`` `(2025-02-11) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V22.md#22.14.0>`_
* Upgraded V8 to ``v13.5.212.10`` (2025-03-20)
* Upgraded Visual Studio 2022 to `v17.13.5 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.13>`_
* Upgraded GCC to v14.2.0
* Switched from MSVC to Clang on Windows for V8 mode
* Linux build started to reference libatomic
* Arm and x86 were not supported for Android Node.js
* Moved float 16 detection to get better compatibility
* Fixed improper lowercasing of method name for the proxy converter
* Removed a warning in the engine pool when an empty engine is found by the daemon internally
* Known issue: Snapshot in V8 mode is broken (1)
* Known issue: Linux x86_64 workflow is broken (2)
* Known issue: Linux arm64 is broken (1)
* Known issue: Android Node.js non-i18n is not packaged (1)

.. note::

    1. This release took me 2 weeks of all spare time so that I don't have time to investigate.
    2. The Linux x86_64 build process becomes very hacky because Google shipped a broken build environment.

4.1.1
-----

* Upgraded Node.js to ``v22.12.0`` `(2024-12-03) <https://github.com/nodejs/node/blob/main/doc/changelogs/CHANGELOG_V22.md#22.12.0>`_
* Upgraded V8 to ``v13.2.152.16`` (2024-12-05)
* Upgraded Visual Studio 2022 to `v17.12.3 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.12>`_
* Added ``getAbsoluteResourceName()`` to ``IV8ModuleResolver``
* Fixed memory leaks in function callback in edge cases
* ``require(esm)`` is enabled by default
* Removed ``isExperimentalRequireModule()``, ``setExperimentalRequireModule`` from ``NodeFlags``
* Added ``isNoExperimentalRequireModule()``, ``setNoExperimentalRequireModule`` to ``NodeFlags``

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
