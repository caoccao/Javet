================
Bridge Converter
================

As ``JavetProxyConverter`` skips creating proxies for primitive types, ``List``, ``Set``, ``Map``, etc., sometimes it may be annoying that a variable is converted to a native JavaScript object by accident. So, how to fix that? ``JavetBridgeConverter`` is the one because it creates proxies for all Java types including ``Integer``, ``Long``, ``String``, etc. except that only Java array is converted to JavaScript array.

Usage
=====

The usage is identical to the one in ``JavetProxyConverter``. 

Preparation
-----------

.. code-block:: java

    // Step 1: Create an instance of JavetBridgeConverter.
    JavetBridgeConverter javetBridgeConverter = new JavetBridgeConverter();
    // Step 2: Set the V8Runtime converter to JavetBridgeConverter.
    v8Runtime.setConverter(javetBridgeConverter);

How to Cast Java Objects to JavaScript Objects?
===============================================

There are 2 ways of casting the Java objects to the JavaScript objects.

* **Implicit** - In JavaScript, sometimes the engine performs implicit type conversion. E.g. Given ``x`` is a Java string ``b``, ``'a' + x`` gives ``'ab'`` because ``x`` is implicitly cast to a JavaScript string by the engine.
* **Explicit** - In JavaScript, the built-in ways of casting a variable to a primitive type or array are ``[Symbol.toPrimitive]()`` or ``[Symbol.iterator]()``. Besides, Javet provides ``toV8Value()`` to allow the explicit type conversion.
