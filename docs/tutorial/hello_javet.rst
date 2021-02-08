===========
Hello Javet
===========

Print **Hello Javet**
=====================

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try resource.
    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
        // Step 2: Request a lock.
        v8Runtime.lock();
        // Step 3: Execute a string as JavaScript code and print the result to console.
        System.out.println(v8Runtime.executeString("'Hello Javet'")); // Hello Javet
        // Step 4: Resource including the lock is recycled automatically at the end of the try resource block.
    }

Print **1 + 1**
=====================

.. code-block:: java

    // Step 1: Create a V8 runtime from V8 host in try resource.
    try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
        // Step 2: Request a lock.
        v8Runtime.lock();
        // Step 3: Execute a string as JavaScript code and print the result to console.
        System.out.println(v8Runtime.executeInteger("1 + 1")); // 2
        // Step 4: Resource including the lock is recycled automatically at the end of the try resource block.
    }

Please refer to `source code <../../src/test/java/com/caoccao/javet/tutorial/HelloJavet.java>`_ for more detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
