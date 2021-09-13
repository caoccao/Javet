======================================
Why Node.js Crashes When being Closed?
======================================

Background
==========

Some Javet users experience segfaults when using the Node.js mode. The segfaults occur when ``NodeRuntime`` is being closed. E.g. `issue #20 <https://github.com/caoccao/Javet/issues/20>`_ and `issue #82 <https://github.com/caoccao/Javet/issues/82>`_.

Root Cause
==========

The root cause is Node.js event loop is activated when Node.js is exiting. Let's see how ``NodeRuntime.close()`` works behind the scenes.

1. Close all Node.js modules
2. Close all reference objects
3. Close all callback context objects
4. Close all V8 modules
5. Hand over the control to Node.js exiting process

If the application registers interceptors, those interceptors will be closed at step 3. However, the corresponding JavaScript objects hasn't been recycled yet. So, at step 5, Node.js activates the event loop and async objects will get resolved or rejected. The callbacks land Javet which is not able to handle because the callback context objects are gone. That leads to memory corruption and segfaults.

Can Javet address that? Yes, but that implies hacking Node.js event loop. People choose Javet Node.js mode mostly because it is a genuine Node.js. So, no, Javet does not perform such hack.

Solution
========

Well, how to prevent that from happening? In fact, knowing how Javet works and following the Javet way, that won't happen.

Event unhandledRejection
------------------------

Node.js provides a standard `solution <https://nodejs.org/docs/latest/api/process.html>`_. Application can listen to event ``unhandledRejection`` to prevent that event from hitting Javet so that the segfaults will not take place.

.. code-block:: javascript

    import process from 'process';
    process.on('unhandledRejection', (reason, promise) => {
      // Do whatever you want to do
    });

NodeRuntime.await()
-------------------

Application may call ``NodeRuntime.await()`` before closing the ``NodeRuntime``. This call explicitly tells ``NodeRuntime`` to activate the event loop.

NodeRuntime.lowMemoryNotification()
-----------------------------------

Application may call ``NodeRuntime.lowMemoryNotification()`` before closing the ``NodeRuntime``. This call forces ``NodeRuntime`` to perform garbage collection. During the garbage collection, the callbacks can be safely handled by Javet without segfaults.
