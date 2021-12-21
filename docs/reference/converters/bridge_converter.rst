================
Bridge Converter
================

As ``JavetProxyConverter`` skips creating proxies for primitive types, ``List``, ``Set``, ``Map``, etc., sometimes it may be annoying that a variable is converted to a native JavaScript object by accident. So, how to fix that? ``JavetBridgeConverter`` is the one because it creates proxies for all Java types including ``Integer``, ``Long``, ``String``, ``int[]``, ``long[]``, ``Object[]``, etc.

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

Boolean
-------

.. code-block:: java

    v8Runtime.getGlobalObject().set("bTrue", true);
    v8Runtime.getGlobalObject().set("bFalse", false);
    assertTrue((Boolean) v8Runtime.getExecutor("bTrue").executeObject());
    assertFalse((Boolean) v8Runtime.getExecutor("bFalse").executeObject());
    assertEquals(1, v8Runtime.getExecutor("bTrue.toV8Value()?1:0").executeInteger());
    assertEquals(0, v8Runtime.getExecutor("bFalse.toV8Value()?1:0").executeInteger());
    assertEquals(1, v8Runtime.getExecutor("bTrue[Symbol.toPrimitive]()?1:0").executeInteger());
    assertEquals(0, v8Runtime.getExecutor("bFalse[Symbol.toPrimitive]()?1:0").executeInteger());
    v8Runtime.getGlobalObject().delete("bTrue");
    v8Runtime.getGlobalObject().delete("bFalse");

int
---

.. code-block:: java

    v8Runtime.getGlobalObject().set("i", 12345);
    assertEquals(12345, (Integer) v8Runtime.getExecutor("i").executeObject());
    assertEquals(12345, v8Runtime.getExecutor("i.toV8Value()").executeInteger());
    assertEquals(12345, v8Runtime.getExecutor("i[Symbol.toPrimitive]()").executeInteger());
    assertEquals(12346, v8Runtime.getExecutor("1 + i").executeInteger());
    v8Runtime.getGlobalObject().delete("i");

int Array
---------

.. code-block:: java

    int[] intArray = new int[]{1, 2};
    v8Runtime.getGlobalObject().set("a", intArray);
    assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
    assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
    assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
    assertArrayEquals(intArray, v8Runtime.getExecutor("a[Symbol.toPrimitive]()").executeObject());
    assertArrayEquals(intArray, v8Runtime.getExecutor("a[Symbol.iterator]()").executeObject());
    assertArrayEquals(intArray, v8Runtime.getExecutor("a.toV8Value()").executeObject());
    v8Runtime.getGlobalObject().delete("a");

Integer List
------------

.. code-block:: java

    List<Integer> integerList = new ArrayList<>();
    integerList.add(1);
    integerList.add(2);
    v8Runtime.getGlobalObject().set("a", integerList);
    assertEquals(2, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
    assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
    assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
    v8Runtime.getExecutor("a.add(3);").executeVoid();
    assertEquals(3, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
    assertEquals(3, (Integer) v8Runtime.getExecutor("a[2]").executeObject());
    assertEquals(3, integerList.size());
    assertEquals(3, integerList.get(2));
    v8Runtime.getGlobalObject().delete("a");

Long Unmodifiable List
----------------------

.. code-block:: java

    List<Long> longList = Collections.unmodifiableList(Arrays.asList(1L, 2L));
    v8Runtime.getGlobalObject().set("a", longList);
    assertEquals(2, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
    assertEquals(1L, (Long) v8Runtime.getExecutor("a[0]").executeObject());
    assertEquals(2L, (Long) v8Runtime.getExecutor("a[1]").executeObject());
    v8Runtime.getGlobalObject().delete("a");

String Array
------------

.. code-block:: java

    v8Runtime.getGlobalObject().set("a", new String[]{"x", "y"});
    assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
    assertEquals("x", v8Runtime.getExecutor("a[0]").executeObject());
    assertEquals("y", v8Runtime.getExecutor("a[1]").executeObject());
    assertEquals(
            "[\"x\",\"y\"]",
            v8Runtime.getExecutor("JSON.stringify(a[Symbol.toPrimitive]())").executeString());
    assertEquals(
            "[\"x\",\"y\"]",
            v8Runtime.getExecutor("JSON.stringify(a[Symbol.iterator]())").executeString());
    assertEquals(
            "[\"x\",\"y\"]",
            v8Runtime.getExecutor("JSON.stringify(a.toV8Value())").executeString());
    v8Runtime.getGlobalObject().delete("a");

How to Cast Java Objects to JavaScript Objects?
===============================================

There are 2 ways of casting the Java objects to the JavaScript objects.

* **Implicit** - In JavaScript, sometimes the engine performs implicit type conversion. E.g. Given ``x`` is a Java string ``b``, ``'a' + x`` gives ``'ab'`` because ``x`` is implicitly cast to a JavaScript string by the engine.
* **Explicit** - In JavaScript, the built-in ways of casting a variable to a primitive type or array are ``[Symbol.toPrimitive]()`` or ``[Symbol.iterator]()``. Besides, Javet provides ``toV8Value()`` to allow the explicit type conversion.
