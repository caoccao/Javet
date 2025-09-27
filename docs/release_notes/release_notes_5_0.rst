===================
Release Notes 5.0.x
===================

5.0.0
-----

* Upgraded Node.js to ``v24.8.0`` `(2025-09-10) <https://nodejs.org/en/blog/release/v24.8.0>`_
* Upgraded V8 to ``v14.1.146.11`` (2025-09-22)
* Upgraded Visual Studio 2022 to `v17.14.15 <https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes-v17.14>`_
* Switched from MSVC to Clang on Windows for Node.js mode
* Fixed an extra latency in holding a lock in V8GuardDaemon
* Renamed ``isExperimentalPermission()`` to ``isPermission()`` in ``NodeFlags``
* Renamed ``setExperimentalPermission()`` to ``setPermission()`` in ``NodeFlags``
* Known issue: ``getNearHeapLimitCallback()``, ``setNearHeapLimitCallback()`` break in Node.js mode
