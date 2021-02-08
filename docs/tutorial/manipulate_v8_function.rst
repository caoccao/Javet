======================
Manipulate V8 Function
======================

Know the Implication
====================

Lifecycle of a function is recommended to be managed by V8. This is a bit different from the common usage of other V8 value objects.

Why? Because in order to keep track of the callback capability, Javet needs to persist few tiny objects in JVM as well as in V8. Those persisted objects get released immediately when ``close()`` is explicitly called and ``isWeak()`` is ``false``. However, once a function is set to a certain object, it is typically no longer needed. If closing that function explicitly really recycles it, the following callback will cause memory corruption.

The solution is to set the function to weak by ``setWeak()`` so that the lifecycle management is handed over to V8. V8 decides when to recycle the function and notifies Javet to recycle those persisted objects.

Option 1: The Common Way
========================

.. code-block:: java

    // Create a function and wrap it with try resource.
    try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
        // Do whatever you want to do with this function
    }
    // Outside the code block, this function is no longer valid. Calling this function in V8 will result in memory corruption.

Option 2: The Recommended Way
=============================

.. code-block:: java

    V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext);
    // Set this function to the certain V8 value objects.
    v8ValueFunction.setWeak();
    // Once this function is set to weak, its lifecycle is automatically managed by Javet + V8.
    // There is no need to call close() any more.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
