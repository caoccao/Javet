=================
Javet Engine Pool
=================

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

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/HelloJavet.java>`_ for more detail.
