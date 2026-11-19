===================
Release Notes 6.0.x
===================

6.0.4
-----

* Upgraded Node.js to ``v26.13.1`` `(2026-11-11) <https://nodejs.org/en/blog/release/v26.13.1>`_
* Upgraded V8 to ``v15.8.62.52`` (2026-11-18)
* Fixed the JVM crash when a Node.js runtime is created or reset with a malformed or incompatible snapshot blob
* Added ``RuntimeCreateFailure``, ``RuntimeResetFailure`` to ``JavetError``
* Changed ``createV8Runtime()`` in ``V8Host`` to throw ``RuntimeCreateFailure`` if the runtime cannot be created
* Changed ``resetContext()``, ``resetIsolate()`` in ``V8Runtime`` to close the runtime and throw ``RuntimeResetFailure`` if the runtime cannot be recreated
* Fixed ``JavetEnginePool`` lending an engine whose runtime is closed
* Fixed ``JavetEnginePool`` losing an engine slot permanently when the engine cannot be created
* Enhanced ``getEngine()`` in ``JavetEnginePool`` to throw ``EngineNotAvailable`` with the last engine creation failure as its cause
* Changed ``resetContext()``, ``resetIsolate()`` in ``V8Runtime`` to remove the GC callbacks, the microtasks completed callbacks and the near heap limit callback
* Fixed ``V8Runtime`` not registering the callbacks added after ``resetContext()`` or ``resetIsolate()`` to the native isolate
* Fixed the JVM crash when ``setNearHeapLimitCallback()`` replaces the near heap limit callback
* Added ``getHeapSnapshotNearHeapLimit()``, ``setHeapSnapshotNearHeapLimit()`` to ``NodeFlags``
* Fixed the known issue that ``getNearHeapLimitCallback()``, ``setNearHeapLimitCallback()`` break in Node.js mode
* Fixed ``JavetEnginePool`` ignoring the snapshot blob in ``JavetEngineConfig`` in Node.js mode
* Fixed ``removeV8Modules(true)`` in ``V8Runtime`` throwing ``ConcurrentModificationException`` when more than one module is cached
* Fixed some bugs in ``splice()`` in ``JavetProxyPluginList`` and ``toSpliced()`` in ``JavetProxyPluginArray`` and ``JavetProxyPluginList``
* Fixed the JVM crash when an expired ``V8Guard``, ``isInUse()`` or ``terminateExecution()`` accesses a ``V8Runtime`` that is being reset by ``resetIsolate()`` or ``resetContext()``, or closed
* Fixed ``JavetEngine`` losing the eval policy in ``JavetEngineConfig`` after ``resetContext()`` or ``resetIsolate()``
* Fixed the deadlock when ``addV8Module()`` in ``V8Runtime`` replaces a module while another module is being closed
* Fixed closing a ``V8Module`` removing another module with the same resource name from the module cache
* Fixed ``removeV8Module(IV8Module)`` in ``V8Runtime`` removing or force closing another module with the same resource name
* Fixed ``bigint`` just out of the range of ``long`` (e.g. ``2n ** 63n``) silently changing value when it is converted to ``long`` instead of ``BigInteger``
* Fixed the JVM crash when a ``V8Runtime`` with an open ``V8Inspector`` is closed or its isolate is reset
* Changed ``close()``, ``resetIsolate()`` in ``V8Runtime``, and ``resetContext()`` for Node.js snapshot runtimes, to close the ``V8Inspector`` sessions, so that a session created before the isolate is reset no longer closes or sends requests to a session created after the reset
* Fixed the futures of ``getV8HeapSpaceStatistics()``, ``getV8HeapStatistics()``, ``getV8SharedMemoryStatistics()`` in ``V8Runtime`` never completing when the request times out after 60 seconds, by cancelling them

6.0.3
-----

* Upgraded Node.js to ``v26.13.0`` `(2026-10-28) <https://nodejs.org/en/blog/release/v26.13.0>`_
* Upgraded V8 to ``v15.7.112.19`` (2026-10-31)
* Fixed the JVM crash when closing or resetting a Node.js runtime with pending ``FinalizationRegistry`` cleanup tasks

6.0.2
-----

* Upgraded Node.js to ``v26.10.0`` `(2026-09-22) <https://nodejs.org/en/blog/release/v26.10.0>`_
* Upgraded V8 to ``v15.5.35.14`` (2026-09-28)
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
