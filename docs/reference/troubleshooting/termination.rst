===========
Termination
===========

Terminating scripts that run out of control is quite important in terms of protecting the applications from being attacked by malicious scripts. In Javet, there are 2 typical ways of terminating scripts.

Automatic Termination
=====================

``V8Guard`` is the built-in support for terminating a script which runs out of control.

With Engine Pool
----------------

.. code-block:: java

    // Get an engine from the pool as usual.
    try (IJavetEngine iJavetEngine = iJavetEnginePool.getEngine()) {
        V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
        // Get a guard and apply try-with-resource pattern.
        try (V8Guard v8Guard = iJavetEngine.getGuard(10000)) {
            v8Guard.setDebugModeEnabled(true);
            v8Runtime.getExecutor("while (true) {}").executeVoid();
            // That infinite loop will be terminated in 10 seconds by the guard.
        } catch (JavetTerminatedException e) {
            // JavetTerminatedException will be thrown to mark that.
            assertFalse(e.isContinuable());
        }
        assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                "The V8 runtime is not dead and is still able to execute code afterwards.");
    }

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/interop/engine/TestJavetEnginePool.java>` for more detail.

Without Engine Pool
-------------------

.. code-block:: java

    try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
        try (V8Guard v8Guard = v8Runtime.getGuard(10000)) {
            v8Guard.setDebugModeEnabled(true);
            assertEquals(1, v8Host.getV8GuardDaemon().getV8GuardQueue().size());
            v8Runtime.getExecutor("var count = 0; while (true) { ++count; }").executeVoid();
            fail("Failed to terminate execution.");
        } catch (JavetException e) {
            assertInstanceOf(JavetTerminatedException.class, e);
            assertEquals(JavetError.ExecutionTerminated, e.getError());
            assertFalse(((JavetTerminatedException) e).isContinuable());
        }
        assertTrue(v8Runtime.getGlobalObject().getInteger("count") > 0);
    }

Please refer to the :extsource3:`source code <../../../src/test/java/com/caoccao/javet/interop/TestV8Guard.java>` for more detail.

How does ``V8Guard`` work internally? It adds itself to a priority queue held by ``V8Host`` which has a daemon thread doing the following:

* For each of the ``V8Runtime`` in the queue.
* If the end time of a ``V8Runtime`` is before now, terminate that ``V8Runtime``.

There is only one daemon thread managing all the V8 runtime instances so that the overhead is fixed and the process is non-blocking.

Does ``V8Guard`` hang normal scripts till timeout is hit? No, it doesn't cause any overhead. If the script completes, ``V8Guard.close()`` will be called via try-with-resource pattern and there will be no termination.

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

How about Debug Mode?
=====================

Usually, when application is being debugged, ``V8Guard`` may easily interrupt the debug. No worry, ``V8Guard`` is by default disabled in debug mode. Please refer to ``setDebugModeEnabled()`` for details.
