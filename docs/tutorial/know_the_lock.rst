=============
Know the Lock
=============

What does Lock Mean in Javet?
=============================

V8 runtime runs in an isolated and single-threaded environment so that there is no race condition issue. How about playing V8 runtime in JVM among multiple threads? Yes, that is possible in 2 modes.

1. Implicit Mode
----------------

Javet automatically handles the ``lock()`` and ``unlock()`` regardless of thread context switch. So, in most of the cases, application just calls regular API without calling single line of locking API.

2. Explicit Mode
----------------

In explicit mode, application just needs to surround the code block with a ``V8Locker`` applied by ``try-with-resource``. Internally, Javet allocates a long-live V8 locker instead of creating V8 locker per API call to achieve better performance.

.. code-block:: java

    try (V8Locker v8Locker = v8Runtime.getV8Locker()) {
        // Do whatever you want to do.
    }

Be careful, ``V8Locker`` cannot be nested, otherwise checked exception will be thrown reporting lock conflict. Please refer to `TestPerformance.java <../../src/test/java/com/caoccao/javet/interop/engine/TestPerformance.java>`_ for detail.

Comparisons
===========

Performance
-----------

Implicit mode comes with a slight performance overhead in V8 because every native call is protected by a new V8 locker.

Explicit mode is designed for performance sensitive work. In extreme performance test cases, the performance improvement may be up to 150% compared to implicit mode.

Thread-safety
-------------

Implicit mode is thread-safe because its locking granularity is at API call level. Multiple threads are free to share the same V8 runtime in concurrent scenarios. Of course, be careful that JavaScript variables may be changed by other threads. It's better not to touch the same JavaScript variable among multiple threads, otherwise, Javet may crash. Yes, crash, because Javet doesn't perform this kind of check.

Explicit mode is **NOT** thread-safe because it's designed to improve performance in single-threaded scenarios. Sharing V8 locker protected V8 runtime among multiple threads will result in Javet crash immediately.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
