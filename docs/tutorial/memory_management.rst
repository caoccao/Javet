=================
Memory Management
=================

3 Challenges
============

JVM GC
------

JVM is known to have a GC that manages memory automatically. However, that doesn't cover the objects in JNI native implementation. Once ``NewGlobalRef(javaObject)`` is called, that ``javaObject`` lives forever in JVM until ``DeleteGlobalRef(javaObject)`` is called.

C++ Runtime
-----------

Smart pointers in C++ cannot easily work across JNI to JVM, in other words, raw pointers are directly referenced in JVM as ``long``. C++ runtime has no idea when to free the memory of those raw pointers unless JVM tells C++ runtime to release via JNI.

V8 GC
-----

V8 generally categorizes objects in memory to 3 types.

1. ``v8::Local`` - It lives within the local scope of a C++ function call.
2. ``v8::Persistent`` - Its lifecycle is managed by V8 GC.
3. ``v8::External`` - V8 GC treats it as root object so that it lives as long as the V8 isolate lives.

Solution: Weak Reference
========================

Javet directly borrows the way V8 manages objects in JVM. The rule is simple in the following 2 patterns.

Manually Manage V8 Objects
--------------------------

.. code-block:: java

    // Create an object and wrap it with try resource.
    try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
        // Do whatever you want to do with this object
        // v8ValueObject.close() is called automatically at the end of the block.
    }
    // Outside the code block, this object is no longer valid.

Automatically Manage V8 Objects
-------------------------------

.. code-block:: java

    // Create an object.
    V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
    // Do whatever you want to do with this object
    v8ValueObject.setWeak();
    // Do whatever you want to do with this object
    /*
     v8ValueObject.close() is called automatically via V8 GC callback.
     So, there is no need to close the V8 object explicitly.
     This is quite useful when the lifecycle is not determined, E.g. V8 function.
     */

Note: V8 does not recycle objects that are referenced by other objects. Please make sure the object chain is broken so that GC can work as expected. ``com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor`` is a good sample showing how to deal with that.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
