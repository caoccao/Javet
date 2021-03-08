======================
Manipulate V8 Function
======================

Lifecycle
=========

Know the Implication
--------------------

Lifecycle of a function is recommended to be managed by V8. This is a bit different from the common usage of other V8 value objects.

Why? Because in order to keep track of the callback capability, Javet needs to persist few tiny objects in JVM as well as in V8. Those persisted objects get released immediately when ``close()`` is explicitly called and ``isWeak()`` is ``false``. However, once a function is set to a certain object, it is typically no longer needed. If closing that function explicitly really recycles it, the following callback will cause memory corruption.

The solution is to set the function to weak by ``setWeak()`` so that the lifecycle management is handed over to V8. V8 decides when to recycle the function and notifies Javet to recycle those persisted objects.

Option 1: The Common Way
------------------------

.. code-block:: java

    // Create a function and wrap it with try resource.
    try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
        // Do whatever you want to do with this function
    }
    // Outside the code block, this function is no longer valid. Calling this function in V8 will result in memory corruption.

Option 2: The Recommended Way
-----------------------------

.. code-block:: java

    V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext);
    // Set this function to the certain V8 value objects.
    v8ValueFunction.setWeak();
    // Once this function is set to weak, its lifecycle is automatically managed by Javet + V8.
    // There is no need to call close() any more.

    // Alternatively, setFunction() makes that easy with only one line.
    v8ValueObject.setFunction("test", javetCallbackContext);
    // An instance of V8ValueFunction is created and set to weak internally.

Automatic Type Conversion
=========================

Javet is capable of automatically converting its internal ``V8Value`` to primitive types by inspecting the function signature. So, the following 4 functions are all the same and valid.

.. code-block:: java

    // Option 1
    public String echo(String str) {
        return str;
    }

    // Option 2
    public String echo(V8Value arg) {
        return arg == null ? null : arg.toString();
    }

    // Option 3
    public V8Value echo(String str) {
        return new V8ValueString(str);
    }

    // Option 4
    public V8Value echo(V8Value arg) throws JavetException {
        return arg.toClone();
    }

    // All 4 functions above can be handled in Javet as the following function
    echo("123");

Note: Primitive types must be in their object form in the method signature. E.g. ``boolean`` must be set to ``Boolean``, ``int`` must be set to ``Integer``, etc. Why? Because the converted value could be ``null`` which would cause JDK to complain with an exception.

Call vs Invoke
==============

In one sentence, ``call()`` belongs to function and ``invoke()`` belongs to object.

Call
----

``call()`` is almost equivalent to ``Function.prototype.call()``. It allows the caller to specify receiver. Besides, Javet combines ``Function.prototype.call()`` and ``Function.prototype.apply()`` because Java is friendly to varargs.

.. code-block:: java

    func.call(object, false, a, b, c); // func.call(object, a, b, c); without result
    func.call(object, true, a, b, c); // func.call(object, a, b, c); with result
    func.call(object, a, b, c); // func.call(object, a, b, c); with result
    func.callVoid(object, a, b, c); // func.call(object, a, b, c); without result
    func.callAsConstructor(a, b, c); // new func(a, b, c);

Invoke
------

``invoke()`` takes function name and arguments, but not receiver because the object itself is the receiver. So the API is almost identical to ``call()`` except for the first argument.

.. code-block:: java

    object.invoke("func", false, a, b, c); // object.func(a, b, c); without result
    object.invoke("func", true, a, b, c); // object.func(a, b, c); with result
    object.invoke("func", a, b, c); // object.func(a, b, c); with result
    object.invokeVoid("func", a, b, c); // object.func(a, b, c); without result

``invoke()`` is heavily used in Javet so that the JNI implementation can be dramatically simplified. In few extreme cases, V8 doesn't expose its C++ API and ``invoke()`` appears to be the only way. So, feel free to invoke all kinds of JS API despite of the deficit of Javet built-in API.

How about Bind?
---------------

``Function.prototype.bind()`` is simply a ``set()`` in Javet.

.. code-block:: java

    object.set("func", func); object.invoke("func", false, a, b, c); // func.bind(object); func(a, b, c); without result
    object.set("func", func); object.invoke("func", true, a, b, c); // func.bind(object); func(a, b, c); with result
    object.set("func", func); object.invoke("func", a, b, c); // func.bind(object); func(a, b, c); with result
    object.set("func", func); object.invokeVoid("func", a, b, c); // func.bind(object); func(a, b, c); without result

Please review `test cases <../../src/test/java/com/caoccao/javet/values/reference/TestV8ValueFunction.java>`_ for more detail.

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
