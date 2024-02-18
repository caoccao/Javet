=============
Proxy Plugins
=============

The proxy converter and its derived converters support proxy plugins that have the following features.

* Conversion for the particular Java types can be easily turned on or off by the plugins.
* The plugins can extend the capabilities of the particular Java types to simulate the native JS types.
* The public Java fields and methods are available as well.

Built-in Proxy Plugins
======================

The built-in proxy plugins provide the basic support. They are turned off by default in the ``JavetProxyConverter``, and turned on by default in the ``JavetBridgeConverter``. They can be easily turned on or off once the converter is created to allow fine-grained control over the conversion.

========================= ================================================================================================================================================
Plugin                    Types
========================= ================================================================================================================================================
JavetProxyPluginDefault   ``BigInteger``, ``Boolean``, ``Byte``, ``Character``, ``Double``, ``Float``, ``Integer``, ``Long``, ``Short``, ``String``, ``ZonedDateTime``.
JavetProxyPluginArray     ``boolean[]``, ``byte[]``, ``char[]``, ``double[]``, ``float[]``, ``int[]``, ``long[]``, ``short[]``, ``Object[]``.
JavetProxyPluginList      ``List<?>``
JavetProxyPluginMap       ``Map<?, ?>``
JavetProxyPluginSet       ``Set<?>``
========================= ================================================================================================================================================

JavetProxyPluginDefault
-----------------------

The default plugin maps all primitive types, ``BigInteger``, ``String`` and ``ZonedDateTime`` to their corresponding JS types.

=================== ====================
Java Type           JS Type
=================== ====================
``BigInteger``      ``bigint``
``Boolean``         ``boolean``
``Byte``            ``number``
``Character``       ``string``
``Double``          ``number``
``Float``           ``number``
``Integer``         ``number``
``Long``            ``bigint``
``Short``           ``number``
``String``          ``string``
``ZonedDateTime``   ``Date``
=================== ====================

.. code-block:: java

    // 2n**65n
    BigInteger bigInteger = new BigInteger("36893488147419103232");
    v8Runtime.getGlobalObject().set("l", bigInteger);
    assertEquals(
        bigInteger.add(new BigInteger("1")),
        v8Runtime.getExecutor("1n + l").executeBigInteger());
    v8Runtime.getGlobalObject().delete("l");

JavetProxyPluginArray
---------------------

The array plugin maps all primitive arrays and object arrays to the JS ``array``.

.. code-block:: java

    int[] intArray = new int[]{1, 2};
    v8Runtime.getGlobalObject().set("intArray", intArray);
    assertSame(intArray, v8Runtime.getGlobalObject().getObject("intArray"));
    assertTrue(v8Runtime.getExecutor("Array.isArray(intArray)").executeBoolean());
    assertTrue(v8Runtime.getExecutor("0 in intArray").executeBoolean());
    assertFalse(v8Runtime.getExecutor("2 in intArray").executeBoolean());
    assertEquals("Array", v8Runtime.getExecutor("intArray.constructor.name").executeString());
    assertEquals(
            "[1,2]",
            v8Runtime.getExecutor("JSON.stringify(intArray)").executeString());
    assertEquals(
            "[[0,1],[1,2]]",
            v8Runtime.getExecutor("JSON.stringify([...intArray.entries()])").executeString());
    assertEquals(3, v8Runtime.getExecutor("intArray[0] = 3; intArray[0]").executeInteger());
    assertEquals(1, v8Runtime.getExecutor("intArray[0] = 1; intArray[0]").executeInteger());
    assertEquals(2, v8Runtime.getExecutor("intArray.length").executeInteger());
    v8Runtime.getGlobalObject().delete("intArray");

JavetProxyPluginList
--------------------

The list plugin maps ``List<?>`` to the JS ``array``.

.. code-block:: java

    List<Object> list = SimpleList.of("x", "y");
    v8Runtime.getGlobalObject().set("list", list);
    assertTrue(v8Runtime.getExecutor("Array.isArray(list)").executeBoolean());
    assertEquals("Array", v8Runtime.getExecutor("list.constructor.name").executeString());
    assertTrue(v8Runtime.getExecutor("0 in list").executeBoolean());
    assertFalse(v8Runtime.getExecutor("2 in list").executeBoolean());
    assertTrue(v8Runtime.getExecutor("list.contains('x')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("list.contains('y')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("list.contains('z')").executeBoolean());
    assertEquals(4, v8Runtime.getExecutor("list.push('z', '1')").executeInteger());
    assertEquals("1", v8Runtime.getExecutor("list.pop()").executeString());
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(list)").executeString());
    v8Runtime.getGlobalObject().delete("list");

JavetProxyPluginMap
-------------------

The map plugin maps ``Map<?, ?>`` to the JS ``object``.

.. code-block:: java

    Map<String, Object> map = SimpleMap.of("x", 1, "y", "2");
    v8Runtime.getGlobalObject().set("map", map);
    assertTrue(v8Runtime.getExecutor("map.containsKey('x')").executeBoolean());
    assertEquals("Object", v8Runtime.getExecutor("map.constructor.name").executeString());
    assertTrue(v8Runtime.getExecutor("'x' in map").executeBoolean());
    assertFalse(v8Runtime.getExecutor("1 in map").executeBoolean());
    assertEquals(1, v8Runtime.getExecutor("map['x']").executeInteger());
    assertEquals("2", v8Runtime.getExecutor("map['y']").executeString());
    assertEquals(1, v8Runtime.getExecutor("map.x").executeInteger());
    assertEquals("2", v8Runtime.getExecutor("map.y").executeString());
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(map).sort());").executeString());
    assertEquals(
            "[[\"x\",1],[\"y\",\"2\"],[\"z\",\"4\"]]",
            v8Runtime.getExecutor("JSON.stringify([...map.entries()].sort((a,b)=>a[0]-b[0]));").executeString());
    v8Runtime.getGlobalObject().delete("map");

JavetProxyPluginSet
-------------------

The set plugin maps ``Set<?>`` to the JS ``Set``.

.. code-block:: java

    Set<String> set = SimpleSet.of("x", "y");
    v8Runtime.getGlobalObject().set("set", set);
    assertEquals("Set", v8Runtime.getExecutor("set.constructor.name").executeString());
    assertTrue(v8Runtime.getExecutor("'x' in set").executeBoolean());
    assertFalse(v8Runtime.getExecutor("1 in set").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.contains('x')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.contains('y')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
    assertEquals(2, v8Runtime.getExecutor("set.size").executeInteger());
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set).sort());").executeString());
    v8Runtime.getGlobalObject().delete("set");

Add or Remove Proxy Plugins
===========================

The proxy plugins are stored in the ``JavetConverterConfig``.

.. code-block:: java

    // Add a proxy plugin
    converter.getConfig().getProxyPlugins().add(0, JavetProxyPluginList.getInstance());
    // Remove a proxy plugin
    converter.getConfig().getProxyPlugins().removeIf(p -> p instanceof JavetProxyPluginList);

Create a New Proxy Plugin
=========================

There are typically two ways to create a new proxy plugin.

* Implement ``IClassProxyPlugin`` from scratch.
* Subclass an existing proxy plugin.

It's recommended to review the source code of the built-in proxy plugins to learn how to create your own proxy plugin.
