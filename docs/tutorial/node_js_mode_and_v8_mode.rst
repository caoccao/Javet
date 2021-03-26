========================
Node.js Mode and V8 Mode
========================

Co-existence
============

Javet support both Node.js mode and V8 mode both of which can co-exist in one JVM. This is an awesome feature with the following highlights.

* Latest V8 features are available in V8 mode.
* Node.js ecosystem is available in Node.js mode.
* There are 2 versions of V8 runtime in 1 JVM and they don't see each other.

Usage
=====

It's very simple to get into Node.js mode or V8 mode. E.g.

V8Host
------

.. code-block:: java

    // Node.js Mode
    try (V8Runtime v8Runtime = V8Host.getNodeInstance().createV8Runtime()) {
        // ...
    }

    // V8 Mode
    try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
        // ...
    }

Pool
----

.. code-block:: java

    // Node.js Mode
    try (JavetEnginePool javetEnginePool = new JavetEnginePool()) {
        javetEnginePool.getConfig().setJSRuntimeType(JSRuntimeType.Node);
        try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
            // ...
        }
    }

    // V8 Mode
    try (JavetEnginePool javetEnginePool = new JavetEnginePool()) {
        javetEnginePool.getConfig().setJSRuntimeType(JSRuntimeType.V8);
        try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
            // ...
        }
    }

Note: The default mode in pool is V8 mode. More technical detail is available at `here <../development/design.rst>`_.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
