===========================
Release Notes 4.0.x - 4.1.x
===========================

4.1.6
-----

* Upgraded Node.js to ``v22.18.0`` `(2025-07-31) <https://nodejs.org/en/blog/release/v22.18.0>`_
* Upgraded V8 to ``v13.9.205.19`` (2025-08-04)
* Fixed `libatomic` linking issue for Linux

4.1.5
-----

* Upgraded Node.js to ``v22.16.0`` `(2025-05-21) <https://nodejs.org/en/blog/release/v22.16.0>`_
* Upgraded V8 to ``v13.8.258.19`` (2025-06-17)
* Supported JS Temporal in V8 mode for Linux

4.1.4
-----

* Upgraded Node.js to ``v22.15.1`` `(2025-05-14) <https://nodejs.org/en/blog/release/v22.15.1>`_
* Upgraded V8 to ``v13.7.152.9`` (2025-05-15)
* Upgraded Android NDK to ``r27c``
* Enabled 16K page size for Android
* Fixed Snapshot for V8 mode
* Revised ``JSScopeType`` for V8 mode

4.1.3
-----

* Upgraded Node.js to ``v22.15.0`` `(2025-04-23) <https://nodejs.org/en/blog/release/v22.15.0>`_
* Upgraded V8 to ``v13.6.233.5`` (2025-04-10)
* Upgraded Visual Studio 2022 to `v17.13.6 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.13>`_
* Fixed Linux arm64 build
* Fixed Android Node.js non-i18n build

4.1.2
-----

* Upgraded Node.js to ``v22.14.0`` `(2025-02-11) <https://nodejs.org/en/blog/release/v22.14.0>`_
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

* Upgraded Node.js to ``v22.12.0`` `(2024-12-03) <https://nodejs.org/en/blog/release/v22.12.0>`_
* Upgraded V8 to ``v13.2.152.16`` (2024-12-05)
* Upgraded Visual Studio 2022 to `v17.12.3 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.12>`_
* Added ``getAbsoluteResourceName()`` to ``IV8ModuleResolver``
* Fixed memory leaks in function callback in edge cases
* ``require(esm)`` is enabled by default
* Removed ``isExperimentalRequireModule()``, ``setExperimentalRequireModule`` from ``NodeFlags``
* Added ``isNoExperimentalRequireModule()``, ``setNoExperimentalRequireModule`` to ``NodeFlags``

4.1.0
-----

* Upgraded Node.js to ``v22.11.0`` `(2024-10-29) <https://nodejs.org/en/blog/release/v22.11.0>`_
* Upgraded V8 to ``v13.1.201.8`` (2024-11-11)
* Upgraded clang to v20 for V8 mode on MacOS
* Switched to core dependency + individual native dependency
* Supported ``Float16Array``
* Added ``Float16``
* Fixed ``JavetJVMInterceptor`` to allow arbitrary name
* Added ``addCallbackContexts()`` to ``JavetJVMInterceptor``

4.0.0
-----

* Upgraded Node.js to ``v22.9.0`` `(2024-09-17) <https://nodejs.org/en/blog/release/v22.9.0>`_
* Upgraded V8 to ``v13.0.245.16`` (2024-10-08)
* Added ``NodeI18n``, ``V8I18n`` to ``JSRuntimeType``
* Added ``getNodeI18nInstance()``, ``getV8I18nInstance()`` to ``V8Host``
* Renamed ``V8Runtime.hasPendingException()`` to ``V8Runtime.hasException()``
* Removed ``V8Runtime.hasScheduledException()`` and ``V8Runtime.promoteScheduledException()``
* Moved ``JavetReflectionObjectFactory`` to ``JavetBuddy``
* Added ``NodeFlags``
* Added ``NODE_FLAGS`` to ``NodeRuntimeOptions``
* Supported ``node:sqlite``
