===========
Termination
===========

Terminating scripts that run out of control is quite important in terms of protecting the applications from being attacked by malicious scripts. In Javet, there are 2 typical ways of terminating scripts.

Automatic Termination with Pool and Engine
==========================================

``IJavetEngineGuard`` is the built-in support for terminating a script which runs out of control.

.. code-block:: java

    // Get an engine from the pool as usual.
    try (IJavetEngine iJavetEngine = iJavetEnginePool.getEngine()) {
        V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
        // Get a guard from the engine and apply try-with-resource pattern.
        try (IJavetEngineGuard iJavetEngineGuard = iJavetEngine.getGuard(10000)) {
            v8Runtime.getExecutor("while (true) {}").executeVoid();
            // That infinite loop will be terminated in 10 seconds by the guard.
        } catch (JavetTerminatedException e) {
            // JavetTerminatedException will be thrown to mark that.
            assertFalse(e.isContinuable());
        }
        assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                "The V8 runtime is not dead and still be able to execute code afterwards.");
    }

Does ``IJavetEngineGuard`` hang normal scripts till timeout is hit? No, it doesn't cause any overhead. If the script completes, ``IJavetEngineGuard.close()`` will be called via try-with-resource pattern and cancel the daemon thread immediately.

Please refer to `source code <../../src/test/java/com/caoccao/javet/interop/engine/TestJavetEngineGuard.java>`_ for more detail.

Manual Termination
==================

Manual termination gives applications complete control. In return, the coding effort is considerable.

.. code-block:: java

    V8Host v8Host = V8Host.getV8Instance();
    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        // Create a daemon thread monitoring the V8 runtime status.
        Thread daemonThread = new Thread(() -> {
            // V8 runtime isInUse() does not require lock.
            while (!v8Runtime.isInUse()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // V8 runtime terminateExecution() does not require lock.
            v8Runtime.terminateExecution();
        });
        daemonThread.start();
        try {
            v8Runtime.getExecutor(
                    "var count = 0; while (true) { ++count; }")
                    .executeVoid();
            fail("Failed to throw exception when execution is terminated.");
        } catch (JavetTerminatedException e) {
            assertFalse(e.isContinuable());
        }
        final int count = v8Runtime.getGlobalObject().getInteger("count");
        assertTrue(count > 0, "Count should be greater than 0.");
        assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                "V8 runtime should still be able to execute script after being terminated.");
    }

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
