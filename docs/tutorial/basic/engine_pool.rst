=================
Javet Engine Pool
=================

Motivation
==========

* It takes quite some time to spawn a ``V8Runtime``, especially in Node.js mode.
* It takes a lot of time to initialize a ``V8Runtime`` to prepare for script execution.
* ``V8Runtime`` is not recommended to be used in multi-threaded environments. (Though Javet allows it to be used that way.)

Goals
=====

* Provide a basic user experience similar to a database connection pool.
* Minimize the performance overhead of ``V8Runtime`` creation and initialization.
* Free applications from explicitly closing ``V8Runtime`` from time to time.
* Allow easy extension.

Usage
=====

.. code-block:: java

    // Create a Javet engine pool.
    try (IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>()) {
        // Get a Javet engine from the pool.
        try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
            // Get a V8 runtime from the engine.
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            // Create a Javet console interceptor.
            JavetStandardConsoleInterceptor javetConsoleInterceptor =
                    new JavetStandardConsoleInterceptor(v8Runtime);
            // Register the Javet console to V8 global object.
            javetConsoleInterceptor.register(v8Runtime.getGlobalObject());
            // V8 console log is redirected to JVM console log.
            v8Runtime.getExecutor("console.log('Hello Javet from Pool');").executeVoid();
            // Unregister the Javet console to V8 global object.
            javetConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
            // close() is not necessary because the Javet engine pool handles that.
            v8Runtime.lowMemoryNotification();
            // Force V8 to GC.
        }
    }

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/tutorial/HelloJavet.java>` for more detail.
