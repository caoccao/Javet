========
Snapshot
========

A V8 snapshot is a way to speed up the startup time of JavaScript applications built with the V8 JavaScript engine, which powers Chrome, Node.js, and many other popular platforms. Think of it like pre-cooking a meal so you don't have to start from scratch when you're hungry.

How Snapshot Works
==================

1. **Creating the Snapshot**: During development or deployment, you can take a snapshot of the V8 heap, which is where JavaScript objects and data are stored in memory. This snapshot captures the initial state of your application, including all the built-in JavaScript functions and any custom code you've written. 

2. **Embedding the Snapshot**: When you launch your application, the V8 engine can load the pre-made snapshot instead of having to compile and execute all the JavaScript code from scratch. This significantly reduces the startup time, making your application launch faster and feel more responsive.

Benefits
--------

* **Faster Startup Times**: This is the primary benefit, as applications can launch in milliseconds instead of seconds.
* **Reduced Memory Usage**: Snapshots can sometimes be smaller than the equivalent compiled code, leading to lower memory usage.
* **Improved Security**: Snapshots can help mitigate certain security vulnerabilities by making it harder to inject malicious code.

Limitations
-----------

* **Not all modules supported**: Only a subset of the built-in Node.js modules are currently supported in snapshots, so some applications might not be suitable for snapshotting.
* **Dynamic code updates not possible**: Once a snapshot is created, it's static and cannot be updated with new code without creating a new snapshot.
* **V8 version dependent**: The snapshot created from a particular version of V8 only works in that version of V8. If you want to support a new version of V8, you will have to create a new snapshot in that new version of V8.

How to Create a Snapshot
========================

Create a Snapshot in Javet
--------------------------

It's simple to create a snapshot or provide an existing snapshot in Javet via the ``V8RuntimeOptions``.

* ``setCreateSnapshotEnabled(boolean)``: Enable or disable the snapshot creation.
* ``setSnapshotBlob(byte[])``: Provide an existing snapshot blob.

.. code-block:: java

    V8RuntimeOptions options = new V8RuntimeOptions();
    // Set create snapshot enabled.
    options.setCreateSnapshotEnabled(true);
    byte[] snapshotBlob1;
    byte[] snapshotBlob2;
    try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
        // Prepare function add.
        v8Runtime.getExecutor("const add = (a, b) => a + b;").executeVoid();
        assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
        // Create a snapshot with function add.
        snapshotBlob1 = v8Runtime.createSnapshot();
        assertNotNull(snapshotBlob1);
        assertTrue(snapshotBlob1.length > 0);
        // Test the runtime is still usable after the snapshot is created.
        assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
    }
    // Set the snapshot blob.
    options.setSnapshotBlob(snapshotBlob1);
    for (int i = 0; i < 5; ++i) {
        // Create a new V8 runtime with the snapshot 5 times.
        try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
            // Test the function add.
            assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
        }
    }
    // Create a new V8 runtime with the snapshot.
    try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
        // Test the function add.
        assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
        // Prepare function subtract.
        v8Runtime.getExecutor("const subtract = (a, b) => a - b;").executeVoid();
        // Create a new snapshot with function add and subtract.
        snapshotBlob2 = v8Runtime.createSnapshot();
        assertNotNull(snapshotBlob2);
        assertTrue(snapshotBlob2.length > 0);
    }
    // Set the new snapshot blob.
    options.setSnapshotBlob(snapshotBlob2);
    for (int i = 0; i < 5; ++i) {
        // Create a new V8 runtime with the snapshot 5 times.
        try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
            // Test the function add and subtract.
            assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("subtract(3, 2)").executeInteger());
        }
    }

.. note:: 

    * Snapshot is only available in the V8 mode. There are some technical difficulties to be resolved in the Node.js mode.
    * Both ``setCreateSnapshotEnabled()`` and ``setSnapshotBlob()`` can be used together so that you may create a V8 runtime by an existing snapshot, then create a new snapshot from that V8 runtime.

Create a Snapshot via mksnapshot
--------------------------------

``mksnapshot`` is a powerful tool in the V8 development toolkit. If you are able to build ``mksnapshot``, you may use it to create snapshots. Its usage is as follows:

.. code-block:: shell

    Usage: mksnapshot [--startup-src=file] [--startup-blob=file] [--embedded-src=file]
      [--embedded-variant=label] [--static-roots-src=file] [--target-arch=arch]
      [--target-os=os] [extras]

Let's take a try.

* Create ``test.js`` with a simple function that adds the 2 arguments as follows.

.. code-block:: js

    const add = (a, b) => a + b;

* Execute the following command.

.. code-block:: shell

    mksnapshot.exe --startup-src=startup.cpp --startup-blob=startup.blob --embedded-src=embedded.js test.js

* The following 3 files will be generated.

.. code-block:: js

    5.1M embedded.js    // Embedded blob with assembly directives
    298K startup.blob   // The blob that can be used in Javet
    745K startup.cpp    // The CPP file that can be built in a CPP application
      28 test.js

* Read ``startup.blob`` to a ``byte[]`` and follow the previous section.
