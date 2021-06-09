===============
Load and Unload
===============

As documented in `design <../development/design.rst>`_, Javet supports loading and unloading the JNI libraries during runtime in both Node.js and V8 modes.

How?
====

Unload
------

Assuming the JNI library per mode is already loaded, here are the step-by-step on how to unload it.

.. code-block:: java

    // Step 1: Set library reloadable. Why? Because Javet defaults that switch to false.
    V8Host.setLibraryReloadable(true);
    // Step 2: Get V8Host per JS runtime type.
    V8Host v8Host = V8Host.getInstance(jsRuntimeType);
    // Step 3: Unload the library.
    v8Host.unloadLibrary();
    // Step 4: Restore the switch.
    V8Host.setLibraryReloadable(false);

Load
----

Assuming the JNI library per mode is already unloaded, here are the step-by-step on how to load it again.

.. code-block:: java

    // Step 1: Get V8Host per JS runtime type.
    V8Host v8Host = V8Host.getInstance(jsRuntimeType);
    // Step 2: Load the library.
    v8Host.loadLibrary();

Notes
=====

* ``loadLibrary()`` is internally called by Javet in the first time and only takes effect after ``unloadLibrary()`` is called.
* ``loadLibrary()`` and ``unloadLibrary()`` are for experiment only. **They may be unstable and crash JVM. Please use this feature at your own risk.**

[`Home <../../README.rst>`_] [`Javet Reference <index.rst>`_]
