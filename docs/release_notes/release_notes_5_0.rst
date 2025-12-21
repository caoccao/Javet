===================
Release Notes 5.0.x
===================

5.0.3
-----

* Upgraded Node.js to ``v24.12.0`` `(2025-12-10) <https://nodejs.org/en/blog/release/v24.12.0>`_
* Upgraded V8 to ``v14.4.258.13`` (2025-12-16)
* Upgraded Visual Studio 2022 to `v17.14.23 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.14>`_
* Supported Temporal in V8 mode
* Added ``isHarmonyTemporal()``, ``setHarmonyTemporal()`` to ``NodeFlags``

5.0.2
-----

* Upgraded Node.js to ``v24.11.1`` `(2025-11-11) <https://nodejs.org/en/blog/release/v24.11.1>`_
* Upgraded V8 to ``v14.3.127.14`` (2025-11-14)
* Upgraded Android NDK to ``r29``
* Added ``batchPush()`` to ``IV8ValueArray``
* Fixed bug in ``JavetObjectConverter`` when converting a large Java array or list
* Added ``getGetPriorities()``, ``getSetPriorities()`` to ``ClassDescriptor``
* Added ``cancelTerminateExecution()``, ``isExecutionTerminating()`` to ``V8Runtime``

5.0.1
-----

* Upgraded Node.js to ``v24.10.0`` `(2025-10-08) <https://nodejs.org/en/blog/release/v24.10.0>`_
* Upgraded V8 to ``v14.2.231.5`` (2025-10-08)
* Tweaked build scripts for better performance for V8 mode

5.0.0
-----

* Upgraded Node.js to ``v24.8.0`` `(2025-09-10) <https://nodejs.org/en/blog/release/v24.8.0>`_
* Upgraded V8 to ``v14.1.146.11`` (2025-09-22)
* Upgraded Visual Studio 2022 to `v17.14.15 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.14>`_
* Upgraded Android NDK to ``r27d``
* Switched from MSVC to Clang on Windows for Node.js mode
* Switched from GCC to Clang on Linux for V8 mode
* Fixed an extra latency in holding a lock in V8GuardDaemon
* Renamed ``isExperimentalPermission()`` to ``isPermission()`` in ``NodeFlags``
* Renamed ``setExperimentalPermission()`` to ``setPermission()`` in ``NodeFlags``
* Removed temporal
* Known issue: ``getNearHeapLimitCallback()``, ``setNearHeapLimitCallback()`` break in Node.js mode
