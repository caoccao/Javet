==============
Best Practices
==============

Thread, Engine and Pool
=======================

* Always get 1 Javet engine from the pool in 1 thread.
* If multiple context is required in 1 thread, there are 2 options.

    * Call ``resetContext()`` between context switch.
    * Obtain multiple V8Runtime instances.

* Do not pass Javet engine to other threads. V8 is single-threaded by design and each ``V8Runtime`` can only be accessed by one thread at a time.
* Always return Javet engine to pool in the end via try-with-resource or calling ``close()`` explicitly.
* Subclass Javet engine pool and Javet engine to complete your customization. Indeed, they are open to full customization.
* Configure pool size based on workload. By default, ``poolMinSize`` is ``max(1, cpuCount/2)`` and ``poolMaxSize`` is ``max(1, cpuCount)``. Adjust these for your deployment.
* Kotlin Coroutines and Java 21 Virtual Threads are incompatible with V8's threading model. They will crash without additional synchronization.

Resource Management
===================

* Dangling V8 objects will be forced to be recycled by Javet under the following scenarios and corresponding log will reflect that. Keeping an eye on the log helps address memory leak issues in the early stage.

    *  Engine is closed.
    *  Pool is closed.
    *  Context is reset.
    *  Isolate is reset.

* Always apply ``try-with-resource`` to Javet objects regardless of primitive or reference if they are not returned to Javet.

  .. code-block:: java

      try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
          v8ValueObject.set("key", "value");
          v8Runtime.getGlobalObject().set("obj", v8ValueObject);
      }

* Always prohibit calling ``close()`` of Javet objects if they will be returned to Javet.
* Use ``V8Scope`` for preventing memory leak when exception is thrown. ``V8Scope`` tracks all V8 values created within it and closes them automatically on exception. Call ``setEscapable()`` before returning values from the scope.

  .. code-block:: java

      try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
          V8ValueObject v8ValueObject = v8Scope.createV8ValueObject();
          // If an exception is thrown here, v8ValueObject is auto-closed.
          v8Scope.setEscapable();
          return v8ValueObject; // Safe: escapable prevents auto-close.
      }

* If the lifecycle of V8 objects is uncertain, calling ``setWeak()`` is the only way so that calling ``close()`` is no longer required. Be careful, calling ``close()`` after calling ``setWeak()`` may lead to V8 core dump immediately.
* Use ``JavetResourceUtils.safeClose()`` for cleanup in finally blocks. It is null-safe, handles arrays and collections, and silently ignores exceptions.
* In performance sensitive scenarios, please explicitly acquire ``V8Locker``. Explicit locking is faster than implicit per-call locking, but is not thread-safe.

  .. code-block:: java

      try (V8Locker v8Locker = v8Runtime.getV8Locker()) {
          // All V8 operations within this block share one lock.
          v8Runtime.getExecutor("1 + 1").executeInteger();
          v8Runtime.getExecutor("2 + 2").executeInteger();
      }

* When using engine pool, verify that callback context count and reference count are both 0 after each use. Non-zero counts indicate a resource leak.
* ``V8Locker`` is non-reentrant. Acquiring a second locker on the same thread throws ``LockAcquisitionFailure``. Closing a locker twice throws ``LockReleaseFailure``.
* Prefer typed execution methods (``executeString()``, ``executeInteger()``, ``executeBoolean()``, ``executeVoid()``) over generic ``execute()`` followed by a cast. Typed methods avoid unnecessary reference creation.

Garbage Collection
==================

* Call ``v8Runtime.lowMemoryNotification()`` to hint V8 to run garbage collection. This is useful after heavy operations or before memory-sensitive work.
* Enable passive GC notifications via ``V8Host.enableGCNotification()`` for automatic GC tracking.
* Engine pool automatically sends GC notifications when engines are idle (controlled by ``autoSendGCNotification`` config).
* After heavy proxy converter usage, call ``System.gc()`` and ``System.runFinalization()`` to help reclaim Java-side proxy objects.
* Use GC callbacks (``addGCEpilogueCallback``, ``addGCPrologueCallback``) to monitor V8 GC behavior and detect memory issues.
* For heap limit control, use ``V8Flags.setMaxHeapSize()`` and ``V8Flags.setMaxOldSpaceSize()``. Handle near-heap-limit callbacks to either increase the limit or terminate execution for malicious scripts.

Type Conversion
===============

* Choose the right converter for your use case:

    * ``JavetPrimitiveConverter``: Maximum performance, simple types only. Use when you only need primitives (int, boolean, double, String).
    * ``JavetObjectConverter`` (default): Full-featured conversion for collections (Array, List, Map, Set) and plain objects. Use for general-purpose Java-JS data exchange.
    * ``JavetProxyConverter``: Direct Java object access from JavaScript via proxies. Use when JavaScript needs to call Java methods and access Java fields directly. **Security warning**: this opens the JVM to JavaScript. Only enable for trusted scripts.
    * ``JavetBridgeConverter``: Extends ``JavetProxyConverter`` with built-in proxy plugins for Map, Set, List, and Array. Use when you want full transparency between Java collections and JavaScript.

* Converter config only takes effect during actual conversion. After binding, config changes do not apply to existing bindings. This allows different objects to use different proxy settings.
* To bypass the built-in converter, manually create a ``V8Value`` and pass it to Javet APIs. Javet accepts ``V8Value`` in both directions.
* For custom POJO conversion, implement ``fromMap(Map)`` and ``toMap()`` methods (or ``IJavetMappable`` interface) and register via ``converter.registerCustomObject()``.
* When extracting function source code, configure ``getConfig().setExtractFunctionSourceCode(true)`` on the object converter. This is off by default for performance.

Callbacks and Interop
=====================

* Use ``@V8Function`` and ``@V8Property`` annotations for declarative Java-to-JavaScript binding. Call ``bind()`` to register all annotated methods on a V8 object.

  .. code-block:: java

      try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
          v8Runtime.getGlobalObject().set("myObj", v8ValueObject);
          v8ValueObject.bind(new MyAnnotatedClass());
      }

* In callback methods: **do not** close input arguments and **do not** apply try-with-resource to the return value. Javet manages their lifecycle.
* Use ``V8Value... v8Values`` (varargs) in callbacks for flexible argument handling. The application is responsible for validating argument types and count.
* Use ``IJavetAnonymous`` for quick anonymous callback classes. Always call ``unbind()`` before closing the bound V8 object to prevent callback context leaks.
* Use ``@V8RuntimeSetter`` for dependency injection of the ``V8Runtime`` into callback objects.
* For performance-critical callbacks, implement ``IJavetDirectCallable`` or ``IJavetDirectProxyHandler`` instead of annotation-based binding. Direct callables skip reflection overhead.

Proxy Objects
=============

* When injecting Java objects into the global scope, always clean up afterwards:

  .. code-block:: java

      v8Runtime.getGlobalObject().set("myClass", MyClass.class);
      // ... use myClass in JavaScript ...
      v8Runtime.getGlobalObject().delete("myClass");
      v8Runtime.lowMemoryNotification();

* Implement ``IJavetDirectProxyHandler`` for custom proxy behavior with fine-grained control over property access, function calls, and iteration.
* Use ``proxyGetStringGetterMap()`` and ``proxyGetStringSetterMap()`` for efficient property interception without reflection.
* When using ``JavetProxyConverter`` with dynamic interface implementation, the JavaScript object must implement ``AutoCloseable`` for proper recycling. If not owned, call ``System.gc()`` and ``System.runFinalization()``.
* For ``JavetJVMInterceptor`` (full JVM access), always unregister the interceptor and clean up global references when done:

  .. code-block:: java

      javetJVMInterceptor.register(v8Runtime.getGlobalObject());
      // ... use ...
      v8Runtime.getGlobalObject().set("javet", v8Runtime.createV8ValueUndefined());
      javetJVMInterceptor.unregister(v8Runtime.getGlobalObject());
      System.gc();
      System.runFinalization();

Modules
=======

* Set a custom ``IV8ModuleResolver`` on the runtime before executing module code. Return ``null`` from ``resolve()`` for unresolved modules.
* Always set ``setResourceName()`` on executors before compiling modules. This enables proper error reporting and module resolution.
* When using ES modules, call ``setModule(true)`` on the executor before execution. Module execution returns a ``V8ValuePromise``.
* After compiling a module with ``compileV8Module()``, call ``instantiate()`` before ``evaluate()``. Check that the evaluation promise is fulfilled.

  .. code-block:: java

      IV8Executor executor = v8Runtime.getExecutor(code)
              .setResourceName("./main.js").setModule(true);
      try (V8Module v8Module = executor.compileV8Module()) {
          v8Module.instantiate();
          try (V8ValuePromise promise = v8Module.evaluate()) {
              assertTrue(promise.isFulfilled());
          }
      }

* Use ``JavetBuiltInModuleResolver`` for resolving ``node:`` prefixed built-in modules in Node.js mode.

V8 Flags and Configuration
==========================

* V8 flags must be set **before** the first runtime is created. Once a runtime is created, flags are sealed and further changes are silently ignored.

  .. code-block:: java

      if (!V8RuntimeOptions.V8_FLAGS.isSealed()) {
          V8RuntimeOptions.V8_FLAGS.setMaxHeapSize(768);
          V8RuntimeOptions.V8_FLAGS.setMaxOldSpaceSize(512);
          V8RuntimeOptions.V8_FLAGS.setUseStrict(true);
      }

* For internationalization (ICU) support, set the ICU data file path via ``V8RuntimeOptions.V8_FLAGS.setIcuDataFile()`` and use ``V8Host.getV8I18nInstance()`` or ``V8Host.getNodeI18nInstance()``.
* Node.js has separate ``NodeRuntimeOptions.V8_FLAGS`` and ``NodeRuntimeOptions.NODE_FLAGS``. Both must be configured independently.
* Use ``setConsoleArguments()`` on ``NodeRuntimeOptions`` to set ``process.argv`` for Node.js runtimes.

Snapshots
=========

* Enable snapshot creation with ``runtimeOptions.setCreateSnapshotEnabled(true)`` before creating the runtime.
* Before creating a snapshot, ensure callback context count, reference count, and module count are all 0. Javet throws an exception otherwise.
* **V8 mode**: The runtime remains usable after ``createSnapshot()``. Chained snapshots are supported.
* **Node.js mode**: Snapshot creation is **destructive**. The runtime must be closed immediately after ``createSnapshot()``. Do not reuse it.
* Load a pre-created snapshot via ``runtimeOptions.setSnapshotBlob(bytes)`` or ``engineConfig.setSnapshotBlob(bytes)`` for faster runtime startup.

Security
========

* Malicious scripts are recommended to be executed in V8 mode or in ``vm`` module in Node.js mode.
* ``eval`` can be disabled in Javet via ``v8Runtime.allowEval(false)`` or ``engineConfig.setAllowEval(false)``.
* V8 0-day vulnerable issues most likely impact Node.js because the embedded V8 in Node.js is very old. It's recommended to use the V8 mode to minimize the risk.
* ``JavetProxyConverter`` opens the JVM to JavaScript. It is **not** enabled by default. Only enable it for trusted scripts.
* Use ``V8Guard`` with timeout to terminate long-running or infinite-loop scripts. The runtime remains usable after termination.

  .. code-block:: java

      try (IJavetEngine<?> engine = pool.getEngine();
           V8Guard v8Guard = engine.getGuard(3000)) {
          v8Runtime.getExecutor("while (true) {}").executeVoid();
      } catch (JavetTerminatedException e) {
          // Runtime is still usable after termination.
          assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
      }

* ``terminateExecution()`` and ``isInUse()`` are thread-safe and can be called from monitoring or daemon threads without acquiring a lock.
* Set heap size limits via V8 flags to prevent out-of-memory attacks. Handle near-heap-limit callbacks to terminate malicious scripts with ``terminateExecution()``.

Console and Logging
===================

* Use ``JavetStandardConsoleInterceptor`` to redirect JavaScript ``console.log/warn/error`` to Java output streams. Always register before use and unregister when done.

  .. code-block:: java

      JavetStandardConsoleInterceptor interceptor =
              new JavetStandardConsoleInterceptor(v8Runtime);
      interceptor.register(v8Runtime.getGlobalObject());
      // ... execute JavaScript that uses console.log ...
      interceptor.unregister(v8Runtime.getGlobalObject());

* Custom print streams can be set for each console level (``setDebug``, ``setError``, ``setInfo``, ``setLog``, ``setTrace``, ``setWarn``).
* Use ``IV8InspectorListener`` for Chrome DevTools Protocol integration. Implement ``receiveNotification()`` and ``receiveResponse()`` for bidirectional communication.

Node.js
=======

* Modularize the code as much as possible so that performance is maximized.
* Always register unhandled rejection event.
* In **non-module** mode (similar to V8 in web browser), always put launch script in a dedicated folder whose parent folder contains ``node_modules`` and avoid ``require`` modules in the same folder.
* In **module** mode (similar to V8 in Node.js), be aware that the execution result is a promise and the behavior is different from native Node.js runtime behavior unless ``await()`` is called.
* Set the require root directory via ``NodeModuleModule`` before loading modules:

  .. code-block:: java

      nodeRuntime.getNodeModule(NodeModuleModule.class)
              .setRequireRootDirectory(rootPath);

* Enable built-in module resolution with ``NodeRuntimeOptions.setBuiltInModuleResolution(true)`` for ``node:`` prefix support. Always restore to ``false`` in a finally block after use.
* Drive the Node.js event loop manually with ``await()``. Use ``V8AwaitMode.RunNoWait`` for non-blocking micro/macrotask processing, ``RunOnce`` for a single tick, or ``RunTillNoMoreTasks`` to drain all tasks.
* After executing scripts that call ``import()``, call ``nodeRuntime.await()`` to let the promise chain resolve.
* If Node.js hangs during ``close()`` due to pending promises or timers, call ``nodeRuntime.setPurgeEventLoopBeforeClose(true)`` for graceful shutdown.
* If a script uses ``setInterval()`` or similar infinite loops, call ``nodeRuntime.setStopping(true)`` to signal early termination of the event loop.

Error Handling
==============

* Catch ``JavetExecutionException`` for JavaScript runtime errors. Use ``getScriptingError()`` to access the error message, stack trace, source URL, line number, and column number.

  .. code-block:: java

      try {
          v8Runtime.getExecutor("throw new Error('test')").executeVoid();
      } catch (JavetExecutionException e) {
          JavetScriptingError error = e.getScriptingError();
          System.err.println(error.getDetailedMessage());
          System.err.println(error.getStack());
          Map<String, Object> context = error.getContext();
      }

* Catch ``JavetCompilationException`` for syntax errors during script compilation.
* Use ``JavetException`` as the base catch for all Javet-specific exceptions.
* Custom JavaScript error types can expose additional context via ``getContext()`` on the scripting error.

Performance Tips
================

* Runtime creation is expensive. Use ``JavetEnginePool`` in production instead of creating runtimes directly.
* Use ``resetContext()`` on engine pool engines between independent tasks to reuse the underlying V8 isolate while getting a clean JavaScript context.
* Use explicit ``V8Locker`` for batches of V8 operations to reduce lock overhead (~50% faster).
* Prefer ``IJavetDirectCallable`` over annotation-based binding (``@V8Function``) in performance-critical paths to avoid reflection overhead.
* For custom proxy handlers, implement ``IJavetDirectProxyHandler`` with getter/setter maps for efficient property interception.
* Use snapshots (``createSnapshot`` / ``setSnapshotBlob``) to amortize expensive initialization across many runtime instances.
* Call ``lowMemoryNotification()`` after heavy operations to reclaim V8 memory.
* Context reuse is critical. Ad-hoc context creation is orders of magnitude slower than reusing a single context. In Node.js mode, single-context TPS is ~750,000 vs ~65 for ad-hoc; in V8 mode, ~650,000 vs ~2,800.
* Use ``V8ValueTypedArray`` (Int8Array, Uint8Array, Float64Array, etc.) for zero-copy binary data exchange. TypedArrays share backing memory with JVM ``ByteBuffer``.
* Benchmark with ``@Tag("performance")`` tests to isolate performance measurements from standard test runs.

Common Pitfalls
===============

* Calling ``v8Runtime.close()`` inside a pool engine scope is a no-op. The pool manages the runtime lifecycle. Use ``pool.close()`` to shut down.
* Every global set via ``getGlobalObject().set(name, value)`` must be deleted via ``getGlobalObject().delete(name)`` before the runtime closes, or reference counts will not reach zero.
* After deleting a proxy from the global scope, callback contexts are not immediately recycled. Call ``lowMemoryNotification()`` to trigger cleanup.
* When using ``converter.toV8Value()``, the returned ``V8Value`` must be closed via try-with-resources. Forgetting this is a common leak source.
* Do not assume V8 handles GC automatically for Java proxy objects. Explicitly call ``System.gc()`` and ``System.runFinalization()`` after heavy proxy usage.
* ``V8Runtime`` is not ``globalThis``. To access global variables or call top-level functions, use ``v8Runtime.getGlobalObject()``. This is a common migration mistake from J2V8.
* ``JavetLibLoader.setLibLoadingListener()`` must be called before ``V8Host`` is accessed. Setting it afterwards has no effect.
* Javet has no built-in logging dependency. Implement ``IJavetLogger`` and inject via ``JavetEngineConfig.setJavetLogger()`` to bridge to SLF4J, Log4j, or other frameworks.
