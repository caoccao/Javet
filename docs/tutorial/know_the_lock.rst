=============
Know the Lock
=============

What does Lock Mean in Javet?
=============================

V8 runtime runs in an isolated and single-threaded environment so that there is no race condition issue. So playing V8 runtime in JVM among multiple threads is prohibited unless the V8 runtime is always protected by **Lock**.

Javet exposed ``lock()`` and ``unlock()`` in ``V8Runtime``. Any attempt of accessing V8 runtime before ``lock()`` or after ``unlock()`` will result in a checked exception reporting lock conflict or V8 runtime closed.

If **Javet Engine Pool** is used, ``lock()`` and ``unlock()`` are no longer required to be written explicitly because the Javet engine manages that automatically. Tutorial `Hello Javet <hello_javet.rst>`_ provides examples.

Best Practices for Manually Managing Lock
=========================================

* Always call ``lock()`` before any access to V8 runtime as well as V8 value objects. Especially:
    * V8 value reference objects are just Java wrappers of certain JNI native resource, so keeping the lock in effect makes sure V8 threading model is well maintained.
    * In function callback scenario, the callback needs to access V8 runtime and V8 value objects, so the presence of lock is required.
* Always keep the lock range as minimal as possible to allow best performance.
* ``unlock()`` is can be skipped if ``close()`` is called.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
