==================================
Can Node.js Event Loop be Skipped?
==================================

By default, the Node.js event loop will to be drained before it is closed. Sometimes that causes the Node.js runtime hang forever if there are tasks staying in the queue forever. E.g. ``setInterval()``.

So, how to skip the event loop in this case? Just call ``nodeRuntime.setStopping(true)``.

In the following example, ``setInterval()`` will not block the Node.js runtime from being closed.

.. code-block:: java

    try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
        String codeString = "let count = 0; setInterval(()=> console.log(count++), 1000);";
        nodeRuntime.getExecutor(codeString).executeVoid();
        nodeRuntime.setStopping(true);
    }
