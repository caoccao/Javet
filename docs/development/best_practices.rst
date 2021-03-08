==============
Best Practices
==============

Thread, Engine and Pool
=======================

* Always get 1 Javet engine from the pool in 1 thread.
* If multiple context is required in 1 thread, there are 2 options.

    * Call ``resetContext()`` between context switch.
    * Obtain multiple V8Runtime instances.

* Do not pass Javet engine to other threads.
* Always return Javet engine to pool in the end via try-with-resource or calling ``close()`` explicitly.
* Subclass Javet engine pool and Javet engine to complete your customization. Indeed, they are open to full customization.

Resource Management
===================

* Dangling V8 objects will be forced to be recycled by Javet under the following scenarios and corresponding log will reflect that. Keeping an eye on the log helps address memory leak issues in the early stage.

    *  Engine is closed.
    *  Pool is closed.
    *  Context is reset.
    *  Isolate is reset.

* Always apply ``try-with-resource`` to Javet objects regardless of primitive or reference if they are not returned to Javet.
* Always prohibit calling ``close()`` of Javet objects if they will be returned to Javet. 
* If the lifecycle of V8 objects is uncertain, calling ``setWeak()`` is the only way so that calling ``close()`` is no longer required. Be careful, calling ``close()`` after calling ``setWeak()`` may lead to V8 core dump immediately.
* In performance sensitive scenarios, please explicitly acquire ``V8Locker``.

[`Home <../../README.rst>`_] [`Development <index.rst>`_]
