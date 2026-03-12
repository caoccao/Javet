================
Bridge Converter
================

As ``JavetProxyConverter`` skips creating proxies for primitive types, ``List``, ``Set``, ``Map``, etc., sometimes it may be annoying that a variable is converted to a native JavaScript object by accident. So, how to fix that? ``JavetBridgeConverter`` is the one because it creates proxies for all Java types including ``Integer``, ``Long``, ``String``, ``int[]``, ``long[]``, ``Object[]``, etc.

How It Differs from JavetProxyConverter
=======================================

``JavetBridgeConverter`` extends ``JavetProxyConverter`` with two key differences:

1. **All 6 built-in proxy plugins are enabled by default**: ``JavetProxyPluginMap``, ``JavetProxyPluginSet``, ``JavetProxyPluginList``, ``JavetProxyPluginArray``, ``JavetProxyPluginClass``, and ``JavetProxyPluginDefault``.
2. **All non-primitive, non-null Java objects are always proxied**: Unlike ``JavetProxyConverter`` which falls back to ``JavetObjectConverter`` when no plugin matches, ``JavetBridgeConverter`` always creates a proxy. This ensures that Java objects are never accidentally converted to native JS objects.

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
    // All 6 proxy plugins are enabled by default.
    assertEquals(6, javetBridgeConverter.getConfig().getProxyPlugins().size());

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

Number Bridge API
=================

Bridged ``Integer``, ``Double``, ``Float``, ``Byte``, ``Short``, and ``Long`` expose JavaScript Number/BigInt prototype methods:

.. code-block:: java

    // Integer: toExponential, toFixed, toPrecision, toLocaleString
    v8Runtime.getGlobalObject().set("i", 12345);
    assertEquals("1.2345e+4", v8Runtime.getExecutor("i.toExponential()").executeString());
    assertEquals("12345.00", v8Runtime.getExecutor("i.toFixed(2)").executeString());
    assertEquals("1.23e+4", v8Runtime.getExecutor("i.toPrecision(3)").executeString());

    // Double: same Number API
    v8Runtime.getGlobalObject().set("d", 1.23);
    assertEquals("1.23", v8Runtime.getExecutor("d.toFixed(2)").executeString());

    // Long: BigInt arithmetic
    v8Runtime.getGlobalObject().set("l", 100L);
    assertEquals(101L, v8Runtime.getExecutor("1n + l").executeLong());

    // Arithmetic works via implicit conversion
    assertEquals(12346, v8Runtime.getExecutor("1 + i").executeInteger());

String Bridge API
=================

Bridged ``String`` objects expose the full JavaScript String prototype API:

.. code-block:: java

    v8Runtime.getGlobalObject().set("s", "test string");

    // Indexing and length
    assertEquals("t", v8Runtime.getExecutor("s[0]").executeString());
    assertEquals(11, (Integer) v8Runtime.getExecutor("s.length").executeObject());

    // String methods
    assertEquals("TEST STRING", v8Runtime.getExecutor("s.toUpperCase()").executeString());
    assertTrue(v8Runtime.getExecutor("s.startsWith('test')").executeBoolean());
    assertEquals("test", v8Runtime.getExecutor("s.slice(0, 4)").executeString());
    assertEquals("est strin", v8Runtime.getExecutor("s.substring(1, 10)").executeString());

    // Java methods are also available
    int hashCode = (Integer) v8Runtime.getExecutor("s.hashCode()").executeObject();

    // Implicit conversion and spread syntax
    assertEquals("abc test string", v8Runtime.getExecutor("'abc ' + s").executeString());
    // JSON serialization
    assertEquals("\"test string\"", v8Runtime.getExecutor("JSON.stringify(s)").executeString());

Supported String methods include: ``at()``, ``charAt()``, ``charCodeAt()``, ``codePointAt()``, ``concat()``, ``endsWith()``, ``includes()``, ``indexOf()``, ``lastIndexOf()``, ``match()``, ``matchAll()``, ``normalize()``, ``padEnd()``, ``padStart()``, ``repeat()``, ``replace()``, ``replaceAll()``, ``search()``, ``slice()``, ``split()``, ``startsWith()``, ``substring()``, ``toLocaleLowerCase()``, ``toLocaleUpperCase()``, ``toLowerCase()``, ``toUpperCase()``, ``trim()``, ``trimEnd()``, ``trimStart()``, ``isWellFormed()``, ``toWellFormed()``.

ZonedDateTime Bridge API
========================

Bridged ``ZonedDateTime`` objects expose the full JavaScript Date prototype API:

.. code-block:: java

    ZonedDateTime zdt = ZonedDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneId.of("UTC"));
    v8Runtime.getGlobalObject().set("d", zdt);

    // All Date getters work
    assertEquals(2024, v8Runtime.getExecutor("d.getFullYear()").executeInteger());
    assertEquals(0, v8Runtime.getExecutor("d.getMonth()").executeInteger());  // 0-based
    assertEquals(15, v8Runtime.getExecutor("d.getDate()").executeInteger());
    assertEquals(10, v8Runtime.getExecutor("d.getHours()").executeInteger());

    // Date setters throw TypeError (ZonedDateTime is immutable)
    // v8Runtime.getExecutor("d.setFullYear(2025)").executeVoid(); // throws TypeError

    // String representations
    v8Runtime.getExecutor("d.toISOString()").executeString();
    v8Runtime.getExecutor("d.toJSON()").executeString();

.. note::

    All Date setter methods (``setDate()``, ``setFullYear()``, ``setHours()``, etc.) throw ``TypeError`` because ``ZonedDateTime`` is immutable.

How to Cast Java Objects to JavaScript Objects?
===============================================

There are 2 ways of casting the Java objects to the JavaScript objects.

* **Implicit** - In JavaScript, sometimes the engine performs implicit type conversion. E.g. Given ``x`` is a Java string ``b``, ``'a' + x`` gives ``'ab'`` because ``x`` is implicitly cast to a JavaScript string by the engine.
* **Explicit** - In JavaScript, the built-in ways of casting a variable to a primitive type or array are ``[Symbol.toPrimitive]()`` or ``[Symbol.iterator]()``. Besides, Javet provides ``toV8Value()`` to allow the explicit type conversion.

The ``Symbol.toPrimitive`` implementation supports all four hint types (``"number"``, ``"string"``, ``"boolean"``, ``"default"``) with type-appropriate conversions for each Java type.
