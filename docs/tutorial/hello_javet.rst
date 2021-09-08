===========
Hello Javet
===========

Print **Hello Javet** in V8 Mode
================================

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try-with-resource.
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // Step 2: Execute a string as JavaScript code and print the result to console.
        System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString()); // Hello Javet
        // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
    }

Print **1 + 1** in Node.js Mode
===============================

.. code-block:: java

    // Step 1: Create a Node runtime from V8 host in try-with-resource.
    try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
        // Step 2: Execute a string as JavaScript code and print the result to console.
        System.out.println("1 + 1 = " + nodeRuntime.getExecutor("1 + 1").executeInteger()); // 2
        // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
    }

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/HelloJavet.java>`_ for more detail.
