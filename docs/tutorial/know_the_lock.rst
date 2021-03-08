=============
Know the Lock
=============

What does Lock Mean in Javet?
=============================

V8 runtime runs in an isolated and single-threaded environment so that there is no race condition issue. How about playing V8 runtime in JVM among multiple threads? Yes, that is possible in 2 modes.

1. Implicit Mode
----------------

Javet automatically handles the ``lock()`` and ``unlock()`` regardless of thread context switch. So, in most of the cases, application just calls regular API without calling single line of locking API.

However, implicit mode comes with a slight performance overhead in V8 because every native call is protected by a new V8 locker. If application performs performance sensitive jobs, explicit mode is recommended.

2. Explicit Mode
----------------

In explicit mode, application just needs to surround the performance sensitive block with a ``V8Locker`` applied by ``try-with-resource``. Internally, Javet allocates a long-live V8 locker instead of creating V8 locker per API call to achieve better performance.

.. code-block:: java

    try (V8Locker v8Locker = v8Runtime.getV8Locker()) {
        // Do whatever you want to do.
    }

Be careful, ``V8Locker`` cannot be nested, otherwise checked exception will be thrown reporting lock conflict. Please refer to `TestPerformance.java <../../src/test/java/com/caoccao/javet/interop/engine/TestPerformance.java>`_ for detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
