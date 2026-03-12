===============
Proxy Converter
===============

Can I inject arbitrary Java objects and call all the API in JavaScript? Yes, ``JavetProxyConverter`` is designed for that. In general, the user experience is very much close to the one provided by GraalJS. As ``JavetProxyConverter`` opens almost the whole JVM to V8, it is very dangerous to allow end users to touch that V8 runtime, so ``JavetProxyConverter`` is not enabled by default. Here are the steps on how to enable that.

Usage
=====

Preparation
-----------

.. code-block:: java

    // Step 1: Create an instance of JavetProxyConverter.
    JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
    // Step 2: Set the V8Runtime converter to JavetProxyConverter.
    v8Runtime.setConverter(javetProxyConverter);

Instance: File
--------------

.. code-block:: java

    File file = new File("/tmp/i-am-not-accessible");
    v8Runtime.getGlobalObject().set("file", file);
    assertEquals(file, v8Runtime.getGlobalObject().getObject("file"));
    assertEquals(file.exists(), v8Runtime.getExecutor("file.exists()").executeBoolean());
    assertEquals(file.isFile(), v8Runtime.getExecutor("file.isFile()").executeBoolean());
    assertEquals(file.isDirectory(), v8Runtime.getExecutor("file.isDirectory()").executeBoolean());
    assertEquals(file.canRead(), v8Runtime.getExecutor("file.canRead()").executeBoolean());
    assertEquals(file.canWrite(), v8Runtime.getExecutor("file.canWrite()").executeBoolean());
    assertEquals(file.canExecute(), v8Runtime.getExecutor("file.canExecute()").executeBoolean());
    v8Runtime.getGlobalObject().delete("file");
    v8Runtime.lowMemoryNotification();

Instance: List
--------------

.. code-block:: java

    javetProxyConverter.getConfig().setProxyListEnabled(true);
    List<String> list = SimpleList.of("x", "y");
    v8Runtime.getGlobalObject().set("list", list);
    assertSame(list, v8Runtime.getGlobalObject().getObject("list"));
    // contains
    assertTrue(v8Runtime.getExecutor("list.contains('x')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("list.contains('y')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("list.contains('z')").executeBoolean());
    // includes
    assertTrue(v8Runtime.getExecutor("list.includes('x')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("list.includes('x', 1)").executeBoolean());
    assertTrue(v8Runtime.getExecutor("list.includes('y', 1)").executeBoolean());
    // push
    assertEquals(4, v8Runtime.getExecutor("list.push('z', '1')").executeInteger());
    assertTrue(v8Runtime.getExecutor("list.includes('z')").executeBoolean());
    // pop
    assertEquals("1", v8Runtime.getExecutor("list.pop()").executeString());
    // toJSON
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(list);").executeString());
    // Symbol.iterator
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify([...list]);").executeString());
    // unshift
    assertEquals(5, v8Runtime.getExecutor("list.unshift('1', '2')").executeInteger());
    // shift
    assertEquals("1", v8Runtime.getExecutor("list.shift()").executeString());
    assertEquals("2", v8Runtime.getExecutor("list.shift()").executeString());
    // delete
    assertTrue(v8Runtime.getExecutor("delete list[2]").executeBoolean());
    assertEquals(2, v8Runtime.getExecutor("list.size()").executeInteger());
    // length
    assertEquals(2, v8Runtime.getExecutor("list.length").executeInteger());
    v8Runtime.getGlobalObject().delete("list");
    v8Runtime.lowMemoryNotification();
    javetProxyConverter.getConfig().setProxyListEnabled(false);

Instance: Map
-------------

.. code-block:: java

    javetProxyConverter.getConfig().setProxyMapEnabled(true);
    Map<String, Object> map = SimpleMap.of("x", 1, "y", "2");
    v8Runtime.getGlobalObject().set("map", map);
    assertTrue(map == v8Runtime.getGlobalObject().getObject("map"));
    assertEquals(1, v8Runtime.getExecutor("map['x']").executeInteger());
    assertEquals("2", v8Runtime.getExecutor("map['y']").executeString());
    assertEquals(1, v8Runtime.getExecutor("map.x").executeInteger());
    assertEquals("2", v8Runtime.getExecutor("map.y").executeString());
    assertEquals("3", v8Runtime.getExecutor("map['z'] = '3'; map.z;").executeString());
    assertEquals("3", map.get("z"));
    assertEquals("4", v8Runtime.getExecutor("map.z = '4'; map.z;").executeString());
    assertEquals("4", map.get("z"));
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(map));").executeString());
    assertTrue(v8Runtime.getExecutor("delete map['x']").executeBoolean());
    assertFalse(map.containsKey("x"));
    assertTrue(v8Runtime.getExecutor("delete map['y']").executeBoolean());
    assertFalse(map.containsKey("y"));
    assertEquals(
            "{\"z\":\"z\"}",
            v8Runtime.getExecutor("JSON.stringify(map);").executeString());
    v8Runtime.getGlobalObject().delete("map");
    v8Runtime.lowMemoryNotification();
    javetProxyConverter.getConfig().setProxyMapEnabled(false);

Instance: Path
--------------

.. code-block:: java

    Path path = new File("/tmp/i-am-not-accessible").toPath();
    v8Runtime.getGlobalObject().set("path", path);
    assertEquals(path, v8Runtime.getGlobalObject().getObject("path"));
    assertEquals(path.toString(), v8Runtime.getExecutor("path.toString()").executeString());
    Path newPath = v8Runtime.toObject(v8Runtime.getExecutor("path.resolve('abc')").execute(), true);
    assertNotNull(newPath);
    assertEquals(path.resolve("abc").toString(), newPath.toString());
    assertEquals(path.resolve("abc").toString(), v8Runtime.getExecutor("path.resolve('abc').toString()").executeString());
    v8Runtime.getGlobalObject().delete("path");
    v8Runtime.lowMemoryNotification();

Instance: Set
-------------

.. code-block:: java

    javetProxyConverter.getConfig().setProxySetEnabled(true);
    Set<String> set = SimpleSet.of("x", "y");
    v8Runtime.getGlobalObject().set("set", set);
    assertSame(set, v8Runtime.getGlobalObject().getObject("set"));
    assertTrue(v8Runtime.getExecutor("set.contains('x')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.contains('y')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.add('z')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
    assertTrue(v8Runtime.getExecutor("set.has('z')").executeBoolean());
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set));").executeString());
    assertEquals(
            "[\"x\",\"y\",\"z\"]",
            v8Runtime.getExecutor("const keys = []; for (let key of set.keys()) { keys.push(key); } JSON.stringify(keys);").executeString());
    assertTrue(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
    assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
    v8Runtime.getGlobalObject().delete("set");
    v8Runtime.getGlobalObject().delete("set");
    v8Runtime.lowMemoryNotification();
    javetProxyConverter.getConfig().setProxySetEnabled(false);

Static: StringBuilder
---------------------

.. code-block:: java

    v8Runtime.getGlobalObject().set("StringBuilder", StringBuilder.class);
    assertEquals("abc def", v8Runtime.getExecutor(
            "function main() {\n" +
                    "  return new StringBuilder().append('abc').append(' ').append('def').toString();\n" +
                    "}\n" +
                    "main();").executeString());
    v8Runtime.getGlobalObject().delete("StringBuilder");
    v8Runtime.lowMemoryNotification();

Static: Pattern
---------------

.. code-block:: java

    v8Runtime.getGlobalObject().set("Pattern", Pattern.class);
    assertTrue(v8Runtime.getExecutor("let p = Pattern.compile('^\\\\d+$'); p;").executeObject() instanceof Pattern);
    assertTrue(v8Runtime.getExecutor("p.matcher('123').matches();").executeBoolean());
    assertFalse(v8Runtime.getExecutor("p.matcher('a123').matches();").executeBoolean());
    v8Runtime.getGlobalObject().delete("Pattern");
    v8Runtime.getExecutor("p = undefined;").executeVoid();
    v8Runtime.lowMemoryNotification();

Static: Enum
------------

Static class usually does not have an instance. The dynamic proxy based converter is smart enough to handle that.

.. code-block:: java

    v8Runtime.getGlobalObject().set("JavetErrorType", JavetErrorType.class);
    assertEquals(JavetErrorType.Converter, v8Runtime.getExecutor("JavetErrorType.Converter").executeObject());
    assertThrows(
            JavetExecutionException.class,
            () -> v8Runtime.getExecutor("JavetErrorType.Converter = 1;").executeVoid(),
            "Public final field should not be writable.");
    v8Runtime.getGlobalObject().delete("JavetErrorType");
    v8Runtime.getGlobalObject().set("Converter", JavetErrorType.Converter);
    assertEquals(JavetErrorType.Converter, v8Runtime.getGlobalObject().getObject("Converter"));
    v8Runtime.getGlobalObject().delete("Converter");
    v8Runtime.lowMemoryNotification();

Static: Interface
-----------------

Sometimes an interface or annotation class can be injected for enabling Java reflection in V8.

.. code-block:: java

    v8Runtime.getGlobalObject().set("AutoCloseable", AutoCloseable.class);
    v8Runtime.getGlobalObject().set("IJavetClosable", IJavetClosable.class);
    assertTrue(AutoCloseable.class.isAssignableFrom(IJavetClosable.class));
    assertTrue(v8Runtime.getExecutor("AutoCloseable.isAssignableFrom(IJavetClosable);").executeBoolean());
    assertEquals(AutoCloseable.class, v8Runtime.getExecutor("AutoCloseable").executeObject());
    assertEquals(IJavetClosable.class, v8Runtime.getExecutor("IJavetClosable").executeObject());
    v8Runtime.getGlobalObject().delete("AutoCloseable");
    v8Runtime.getGlobalObject().delete("IJavetClosable");
    v8Runtime.lowMemoryNotification();

Dynamic: Anonymous Function
---------------------------

This feature is quite special as it allows implementing Java interfaces in JavaScript via anonymous functions, also known as lambda expressions.

1. Define a simple interface ``IStringJoiner`` for joining two strings.

.. code-block:: java

    interface IStringJoiner extends AutoCloseable {
        String join(String a, String b);
    }

2. Define a simple class ``StringJoiner`` which holds the interface ``IStringJoiner``.

.. code-block:: java

    public class StringJoiner implements AutoCloseable {
        private IStringJoiner joiner;

        public StringJoiner() {
            joiner = null;
        }

        @Override
        public void close() throws Exception {
            if (joiner != null) {
                joiner.close();
                joiner = null;
            }
        }

        public IStringJoiner getJoiner() {
            return joiner;
        }

        public void setJoiner(IStringJoiner joiner) {
            this.joiner = joiner;
        }
    }

3. Inject the implementation from JavaScript.

.. code-block:: java

    try (StringJoiner stringJoiner = new StringJoiner()) {
        v8Runtime.getGlobalObject().set("stringJoiner", stringJoiner);
        v8Runtime.getExecutor("stringJoiner.setJoiner((a, b) => a + ',' + b);").executeVoid();
        IStringJoiner joiner = stringJoiner.getJoiner();
        assertEquals("a,b", joiner.join("a", "b"));
        assertEquals("a,b,c", joiner.join(joiner.join("a", "b"), "c"));
        v8Runtime.getGlobalObject().delete("stringJoiner");
    }
    v8Runtime.lowMemoryNotification();

Voilà! It works.

.. note::

    The JavaScript implementation is backed up by ``V8ValueFunction`` which is an orphan object. After its internal ``V8Runtime`` is closed, it will no longer callable. It's recommended to have the interface implement ``AutoCloseable`` as the sample shows so that the orphan ``V8ValueFunction`` can be recycled explicitly. If you don't own the interface, Javet will force the recycle of the orphan ``V8ValueFunction`` when the ``V8Runtime`` is being closed. Be careful, if you keep the application running for long while without recycling them in time, ``OutOfMemoryError`` may occur.

Dynamic: Anonymous Object for Interface
---------------------------------------

This feature is similar to the dynamic anonymous function, but is an enhanced version because it allows implementing all methods exposed by a Java interface.

1. Define a simple interface ``IStringUtils`` for joining two strings.

.. code-block:: java

    interface IStringUtils extends AutoCloseable {
        String hello();
        String join(String separator, String... strings);
        List<String> split(String separator, String string);
    }

2. Define a simple class ``StringUtils`` which holds the interface ``IStringUtils``.

.. code-block:: java

    public class StringUtils implements AutoCloseable {
        private IStringUtils utils;

        public StringUtils() {
            utils = null;
        }

        @Override
        public void close() throws Exception {
            if (utils != null) {
                utils.close();
                utils = null;
            }
        }

        public IStringUtils getUtils() {
            return utils;
        }

        public void setUtils(IStringUtils utils) {
            this.utils = utils;
        }
    }

3. Inject the implementation from JavaScript.

.. code-block:: java

    try (StringUtils stringUtils = new StringUtils()) {
        v8Runtime.getGlobalObject().set("stringUtils", stringUtils);
        v8Runtime.getExecutor(
                "stringUtils.setUtils({\n" +
                "  hello: () => 'hello',\n" +
                "  join: (separator, ...strings) => [...strings].join(separator),\n" +
                "  split: (separator, str) => str.split(separator),\n" +
                "});"
        ).executeVoid();
        IStringUtils utils = stringUtils.getUtils();
        assertEquals("hello", utils.hello());
        assertEquals("a,b,c", utils.join(",", "a", "b", "c"));
        assertArrayEquals(
                new String[]{"a", "b", "c"},
                utils.split(",", "a,b,c").toArray(new String[0]));
        v8Runtime.getGlobalObject().delete("stringUtils");
    }
    v8Runtime.lowMemoryNotification();

Voilà aussi! It works again.

Dynamic: Anonymous Object for Class
-----------------------------------

This feature is similar to the dynamic anonymous object for interface, but it allows implementing all methods exposed by a non-final Java class.

1. Add ``ByteBuddy`` and ``JavetBuddy`` to the dependency. Please refer to `JavetBuddy <https://github.com/caoccao/JavetBuddy>`_ for detail.

2. Define a simple class ``DynamicClass`` for adding two integers.

.. code-block:: java

    public class DynamicClass {
        public int add(int a, int b) {
            return 0;
        }
    }

3. Create an instance of a class which takes an instance of the ``DynamicClass``.

.. code-block:: java

    IJavetAnonymous anonymous = new IJavetAnonymous() {
        @V8Function
        public void test(DynamicClass dynamicClass) throws Exception {
            assertEquals(3, dynamicClass.add(1, 2), "Add should work.");
            ((AutoCloseable) dynamicClass).close();
        }
    };

4. Inject the implementation from JavaScript. Please note that dynamic object support is disabled by default and ``JavetReflectionObjectFactory`` needs to be set to the converter config for ``JavetProxyConverter`` to enable this feature.

.. code-block:: java

    try {
        javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = "a.test({\n" +
                "  add: (a, b) => a + b,\n" +
                "});";
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    } finally {
        javetProxyConverter.getConfig().setReflectionObjectFactory(null);
        v8Runtime.lowMemoryNotification();
    }

Voilà aussi! It works again.

How to handle custom constructor? Well, it's easy. Just specify a property ``$`` as an array of arguments for the constructor as follows.

.. code-block:: java

    IJavetAnonymous anonymous = new IJavetAnonymous() {
        @V8Function
        public void test(File file) throws Exception {
            assertTrue(file.exists());
            ((AutoCloseable) file).close();
        }
    };
    v8Runtime.getGlobalObject().set("a", anonymous);
    String codeString = "a.test({\n" +
            "  $: ['/tmp/not-exist-file'],\n" +
            "  exists: () => true,\n" +
            "});";
    v8Runtime.getExecutor(codeString).executeVoid();
    v8Runtime.getGlobalObject().delete("a");

One of the constructors of ``java.io.File`` needs a ``String`` as the path. So ``$: ['/tmp/not-exist-file']`` is passed for JavetBuddy to construct ``File`` correctly. Obviously that file doesn't exists, but the return value of ``exists()`` is ``true`` because it comes from the derived class created by ByteBuddy.

.. note::

    The JavaScript implementation is backed up by ``V8ValueObject`` which is an orphan object. After its internal ``V8Runtime`` is closed, it will no longer be callable. It's recommended to have the interface or the object implement ``AutoCloseable`` as the sample shows so that the orphan ``V8ValueObject`` can be recycled explicitly.
    
    If you don't own the interface or the object, there are 2 ways of recycling it to avoid memory leak.
    
    1. Manually calling ``System.gc(); System.runFinalization();`` will recycle the orphan ``V8ValueObject`` via the Java garbage collector.

    2. Javet will force the recycle of the orphan ``V8ValueObject`` when the ``V8Runtime`` is being closed. Be careful, if you keep the application running for long time without recycling them in time, ``OutOfMemoryError`` may occur. Of course, that is less likely going to happen because the Java garbage collector runs periodically.

Features
========

* Any Java objects generated inside V8 are automatically handled by the converter.
* Getters and setters (``get``, ``is``, ``set`` and ``put``) are smartly handled.
* Overloaded methods and varargs methods are identified well.
* Primitive types, Set, Map, List, Array are not handled by default. They can be enabled via proxy plugins.
* Java interfaces can be implemented by anonymous functions in JavaScript.
* Annotations can be applied to classes or methods to alter the default behaviors.
* Classes implementing ``IJavetNonProxy`` are never proxied — they return ``undefined`` instead.

========================= ============================= ===============================================================================
Annotation                Type                          Description
========================= ============================= ===============================================================================
``@V8Convert``            Class                         Sets the conversion mode and proxy mode for the annotated class.
``@V8Allow``              Constructor / Field / Method  Marks the member as visible in ``AllowOnly`` mode.
``@V8Block``              Constructor / Field / Method  Marks the member as blocked in ``BlockOnly`` mode.
``@V8Property``           Field / Method                Binds the field or method as a JS property with optional alias name.
``@V8Function``           Method                        Binds the method as a JS function with optional alias name.
``@V8Getter``             Method                        Binds the method as a generic getter (receives the property key).
``@V8Setter``             Method                        Binds the method as a generic setter (receives key and value).
``@V8BindingEnabler``     Method                        Dynamic control: the method returns a boolean to enable/disable bindings.
``@V8RuntimeSetter``      Method                        Dependency injection: Javet calls this method to inject the ``V8Runtime``.
``@V8ProxyFunctionApply`` Method                        Marks the apply handler for ``V8ProxyMode.Function`` proxies.
========================= ============================= ===============================================================================

@V8Convert::mode
----------------

It tells the converter how to treat the annotated class.

* Transparent - Transparent mode maps the Java objects directly to V8 and ignores any annotations. It is the default mode.
* AllowOnly - AllowOnly mode only maps the API with ``@V8Allow``.
* BlockOnly - BlockOnly mode only ignores the API with ``@V8Block``.

@V8Property::name
-----------------

It tells the converter to bind the property to an alias name.

@V8Function::name
-----------------

It tells the converter to bind the function to an alias name.

@V8Function::thisObjectRequired
-------------------------------

When set to ``true``, the JavaScript ``this`` object is passed as the first argument to the Java method. This is useful for methods that need to modify or inspect the calling JS object.

.. code-block:: java

    @V8Function(thisObjectRequired = true)
    public V8Value self(V8ValueObject thisObject) {
        return thisObject;
    }

@V8Property::symbolType
-----------------------

Binds a property to a JavaScript ``Symbol`` instead of a string key. For example, ``@V8Property(name = "toPrimitive", symbolType = V8ValueSymbolType.BuiltIn)`` binds to ``Symbol.toPrimitive``.

@V8BindingEnabler
-----------------

A method annotated with ``@V8BindingEnabler`` receives a method name (``String``) and returns a ``boolean`` indicating whether that binding should be active. This allows dynamic enabling/disabling of functions at runtime.

.. code-block:: java

    private final Set<String> disabledFunctionSet = new HashSet<>();

    @V8BindingEnabler
    public boolean isV8BindingEnabled(String methodName) {
        return !disabledFunctionSet.contains(methodName);
    }

@V8RuntimeSetter
----------------

A method annotated with ``@V8RuntimeSetter`` is called by Javet to inject the ``V8Runtime`` into the callback receiver object. This is useful when the Java object needs to create V8 values.

.. code-block:: java

    private V8Runtime v8Runtime;

    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

@V8ProxyFunctionApply
---------------------

Marks a method as the ``apply`` trap handler for proxies in ``V8ProxyMode.Function`` mode. A class can have multiple ``@V8ProxyFunctionApply`` methods with different parameter signatures for overload resolution.

.. code-block:: java

    @V8Convert(proxyMode = V8ProxyMode.Function)
    public class Calculator {
        @V8ProxyFunctionApply
        public int apply(int a, int b) { return a + b; }

        @V8ProxyFunctionApply
        public String apply(String a, String b) { return a + b; }
    }

    // In JavaScript: calculator(1, 2) → 3, calculator('a', 'b') → 'ab'

@V8Getter and @V8Setter with V8Value Parameters
------------------------------------------------

``@V8Getter`` and ``@V8Setter`` methods can receive raw ``V8Value`` subtypes instead of Java types. This allows inspecting the property key as a V8 value.

.. code-block:: java

    @V8Getter
    public V8Value getter(V8ValueString v8ValueKey) throws JavetException {
        return v8ValueKey.getV8Runtime().createV8ValueString("value for " + v8ValueKey.getValue());
    }

    @V8Setter
    public void setter(V8ValueString v8ValueKey, V8ValueString v8ValueValue) {
        // Handle raw V8 values directly
    }

IJavetDirectProxyHandler
========================

For maximum control over proxy behavior, implement ``IJavetDirectProxyHandler<E>``. This interface provides direct access to all JavaScript Proxy handler traps without reflection overhead.

.. code-block:: java

    public class MyHandler implements IJavetDirectProxyHandler<RuntimeException> {
        private V8Runtime v8Runtime;
        private Map<String, Object> data = new HashMap<>();

        @Override
        public V8Runtime getV8Runtime() { return v8Runtime; }

        @Override
        public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver)
                throws JavetException {
            String key = property.asString();
            if (data.containsKey(key)) {
                return v8Runtime.createV8ValueString(data.get(key).toString());
            }
            return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
        }
    }

The interface provides getter/setter maps for efficient property interception:

* ``proxyGetStringGetterMap()`` — returns ``Map<String, Function>`` for string property getters
* ``proxyGetStringSetterMap()`` — returns ``Map<String, BiFunction>`` for string property setters
* ``proxyGetSymbolGetterMap()`` — returns symbol property getters

Helper methods ``registerStringGetter()``, ``registerStringGetterFunction()``, and ``registerStringSetter()`` simplify registration.

IJavetNonProxy
==============

Classes implementing ``IJavetNonProxy`` are excluded from all proxy conversion. When the converter encounters such an object, it returns ``V8ValueUndefined`` instead of creating a proxy.

How does JavetProxyConverter Work?
==================================

``JavetProxyConverter`` creates a JavaScript proxy per Java object. For now, the proxy intercepts ``get``, ``has`` and ``set`` to achieve the complete virtualization of Java objects in JavaScript runtime.

How to Customize JavetProxyConverter?
=====================================

It is recommended to subclass ``JavetProxyConverter`` and override few internal API to achieve complete customization.

How to Adjust the Get and Set Priorities?
==========================================

When accessing or setting properties on proxied Java objects from JavaScript, Javet follows a priority order to resolve which Java mechanism (field, method, getter, setter, etc.) should handle the operation. These priorities can be customized to alter the default resolution behavior.

Get Priorities
--------------

Get priorities determine the order in which Javet attempts to resolve property access from JavaScript. The default get priority order is:

1. **Index** - Array-like indexed access (e.g., ``obj[0]``)
2. **Field** - Public fields matching the property name
3. **Method** - Methods matching the property name
4. **GetMethod** - Getter methods (e.g., ``getValue()`` for property ``value``)
5. **BuiltInMethod** - Built-in methods like ``toV8Value()``
6. **GenericGetter** - Methods annotated with ``@V8Getter``
7. **Polyfill** - JavaScript polyfill methods

When accessing a property from JavaScript, Javet iterates through these priorities in order and returns the first match.

Set Priorities
--------------

Set priorities determine the order in which Javet attempts to resolve property assignment from JavaScript. The default set priority order is:

1. **Index** - Array-like indexed assignment (e.g., ``obj[0] = value``)
2. **Field** - Public fields matching the property name
3. **GenericSetter** - Methods annotated with ``@V8Setter``
4. **SetMethod** - Setter methods (e.g., ``setValue()`` for property ``value``)

When setting a property from JavaScript, Javet iterates through these priorities in order and uses the first match.

Example: Default Priority Behavior
-----------------------------------

Consider this Java class:

.. code-block:: java

    public class PriorityExample {
        public String data;

        public String getData() {
            return "from-method";
        }

        public void setData(String value) {
            this.data = "method:" + value;
        }

        @V8Getter
        public String xGetter(String key) {
            if ("info".equals(key)) {
                return "from-generic-getter";
            }
            return null;
        }

        @V8Setter
        public boolean xSetter(String key, String value) {
            if ("info".equals(key)) {
                this.data = "setter:" + value;
                return true;
            }
            return false;
        }
    }

With default priorities:

.. code-block:: java

    PriorityExample obj = new PriorityExample();
    v8Runtime.getGlobalObject().set("obj", obj);

    // Accessing 'data' - Field priority comes before Method
    // Returns the field value, not "from-method"
    String value = v8Runtime.getExecutor("obj.data").executeString();

    // Accessing 'info' - Only GenericGetter handles this
    assertEquals("from-generic-getter",
        v8Runtime.getExecutor("obj.info").executeString());

    // Setting 'data' - Field priority comes before SetMethod
    // Sets the field directly, setData() is not called
    v8Runtime.getExecutor("obj.data = 'test'").executeVoid();
    assertEquals("test", obj.data);  // Direct field assignment

How to Customize Priorities?
----------------------------

You can customize the get and set priorities by modifying the ``ClassDescriptor`` for your class. Because ``ClassDescriptor`` is cached in ``ClassDescriptorStore`` globally, changes will affect all instances of that class in the current JVM. And for the first time, you need to inject an instance of the class to let ``JavetProxyConverter`` create the ``ClassDescriptor``.

.. code-block:: java

    TestPriorityClass testObject = new TestPriorityClass();
    v8Runtime.getGlobalObject().set("a", testObject);
    ClassDescriptor classDescriptor = ClassDescriptorStore.getObjectMap().get(TestPriorityClass.class);
    assertNotNull(classDescriptor);
    // Change the get priority
    classDescriptor.getGetPriorities().clear();
    classDescriptor.getGetPriorities().addAll(List.of(
            ClassDescriptor.GetPriority.Index,
            ClassDescriptor.GetPriority.GetMethod,
            ClassDescriptor.GetPriority.Method,
            ClassDescriptor.GetPriority.BuiltInMethod,
            ClassDescriptor.GetPriority.GenericGetter,
            ClassDescriptor.GetPriority.Field,
            ClassDescriptor.GetPriority.Polyfill));
    // Change the set priority
    classDescriptor.getSetPriorities().clear();
    classDescriptor.getSetPriorities().addAll(List.of(
            ClassDescriptor.SetPriority.SetMethod,
            ClassDescriptor.SetPriority.GenericSetter,
            ClassDescriptor.SetPriority.Field,
            ClassDescriptor.SetPriority.Index));
