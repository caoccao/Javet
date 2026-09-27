===================
Release Notes 6.0.x
===================

6.0.2
-----

* Upgraded Node.js to ``v26.10.0`` `(2026-09-22) <https://nodejs.org/en/blog/release/v26.10.0>`_
* Fixed ``resetContext()`` for Node.js snapshot runtimes to recreate the environment and restore the original snapshot state
* Fixed ``V8Guard`` losing expired guards while runtimes are idle or in calls that don't run scripts
* Fixed ``V8Guard`` timeout update and cancellation races
* Fixed guard queue ordering and end time overflow for long timeouts
* Enhanced ``V8Guard`` to keep terminating scripts after it expires till it is closed, and to log the termination once
* Enhanced the guard daemon to wait for the next due guard instead of polling the queue
* Fixed late engine returns corrupting pool counts after shutdown
* Fixed closing an engine twice corrupting pool counts
* Fixed engine borrows racing with ``JavetEnginePool.close()``, which leaked the new engine or threw ``NullPointerException``

6.0.1
-----

* Upgraded Node.js to ``v26.9.0`` `(2026-09-16) <https://nodejs.org/en/blog/release/v26.9.0>`_
* Upgraded V8 to ``v15.4.80.9`` (2026-09-17)
* Removed ``isJsFloat16Array()``, ``setJsFloat16Array()`` from ``V8Flags`` because V8 has removed the ``--js-float16array`` flag
* Added ``performMicrotaskCheckpoint()``, ``getMicrotasksPolicy()``, ``setMicrotasksPolicy()`` to ``V8Runtime``
* Added ``IJavetMicrotasksCompletedCallback`` with ``addMicrotasksCompletedCallback()``, ``removeMicrotasksCompletedCallback()`` in ``V8Runtime``
* Added ``isRunningMicrotasks()``, ``getMicrotasksScopeDepth()`` to ``V8Runtime``
* Fixed ``await()`` not draining the microtask queue in the V8 mode, which silently dropped the promise reaction jobs registered by ``V8ValuePromise.register()``

6.0.0
-----

* Upgraded Node.js to ``v26.8.1`` `(2026-08-26) <https://nodejs.org/en/blog/release/v26.8.1>`_
* Upgraded V8 to ``v15.3.76.9`` (2026-09-04)
* Upgraded Visual Studio 2026 to `v18.9.2 <https://learn.microsoft.com/en-us/visualstudio/releases/2026/release-notes#18.9.2>`_
* Removed ``isHarmonyTemporal()``, ``setHarmonyTemporal()`` from ``NodeFlags`` because Node.js v26 enables Temporal by default
* Upgraded Android NDK to ``r29`` for Node.js mode
* Fixed ``JavetEnginePool.close()`` waiting out a whole ``poolDaemonCheckIntervalMillis`` (1000ms by default) before the daemon noticed the quitting flag
