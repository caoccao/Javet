===================
Release Notes 5.0.x
===================

5.0.0
-----

* Upgraded Node.js to ``v24.8.0`` `(2025-09-10) <https://nodejs.org/en/blog/release/v24.8.0>`_
* Switched from MSVC to Clang on Windows for Node.js mode
* Renamed ``isExperimentalPermission()`` to ``isPermission()`` in ``NodeFlags``
* Renamed ``setExperimentalPermission()`` to ``setPermission()`` in ``NodeFlags``
* Known issue: ``getNearHeapLimitCallback()``, ``setNearHeapLimitCallback()`` break in Node.js mode
