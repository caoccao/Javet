================
Object Converter
================

Javet has a built-in ``JavetObjectConverter`` with the following features.

* It covers primitive types + Set + Map + Array.
* It is completely open to subclass.
* It minimizes the performance overhead.
* It allows registering custom objects.

So, Javet doesn't natively support converting POJO objects because a POJO converter has to deal with reflection which is so slow that Javet leaves that to applications. However, if the POJO objects are owned by the application, it is possible to register custom objects with the built-in ``JavetObjectConverter``. Otherwise, designing a POJO converter is the alternative solution.

What if the application wants to inject arbitrary Java objects? No worry, the ``JavetProxyConverter`` is designed for that purpose.

Register Custom Objects
=======================

``JavetObjectConverter`` exposes ``registerCustomObject()`` for alien objects which match the following conditions.

* Default constructor without arguments
* Method with signature ``void fromMap(Map<String, Object> map)``
* Method with signature ``Map<String, Object> toMap()``

Enhance the Custom Object
-------------------------

* Create a default constructor without arguments.
* Add a method with signature ``void fromMap(Map<String, Object> map)``
* Add a method with signature ``Map<String, Object> toMap()``

Can the method names be changed? Yes, they can have arbitrary names.

.. code-block:: java

    public final class CustomObject {
        private String name;
        private int value;

        public CustomObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public CustomObject() {
            this(null, 0);
        }

        public void fromMap(Map<String, Object> map) {
            setName((String) map.get("name"));
            setValue((Integer) map.get("value"));
        }

        public Map<String, Object> toMap() {
            return new HashMap<String, Object>() {
                {
                    put("name", getName());
                    put("value", getValue());
                }
            };
        }

        // getters and setters
    }

Register the Custom Object
--------------------------

As the default converter is ``JavetObjectConverter``, just follow the code snippet below to register a custom object.

.. code-block:: java

    JavetObjectConverter converter = (JavetObjectConverter)v8Runtime.getConverter();
    converter.registerCustomObject(CustomObject.class);

If the method names are different from the default ones, just provide the names upon registration as the following.

.. code-block:: java

    converter.registerCustomObject(CustomObject.class, "customFromMap", "customToMap");

Usage
-----

After the registration is completed, there is no additional steps any more. Just follow the regular pattern.

.. code-block:: java

    CustomObject[] customObjects = new CustomObject[]{
            new CustomObject("x", 1),
            new CustomObject("y", 2),
    };
    v8Runtime.getGlobalObject().set("a", customObjects);
    assertEquals(2, v8Runtime.getExecutor("a.length").executeInteger());
    List<CustomObject> v8CustomObjects = v8Runtime.getGlobalObject().getObject("a");
    assertNotNull(v8CustomObjects);
    assertEquals(2, v8CustomObjects.size());
    for (int i = 0; i < customObjects.length; i++) {
        assertEquals(customObjects[i].getName(), v8Runtime.getExecutor("a[" + i + "].name").executeString());
        assertEquals(customObjects[i].getValue(), v8Runtime.getExecutor("a[" + i + "].value").executeInteger());
        assertEquals(customObjects[i].getName(), v8CustomObjects.get(i).getName());
        assertEquals(customObjects[i].getValue(), v8CustomObjects.get(i).getValue());
    }

Highlights
----------

* PROS: This is a built-in feature so there is no need to deal with a POJO converter.
* CONS: This is a little bit intrusive to the custom objects.

How does It Work?
-----------------

As V8 supports private properties, ``JavetObjectConverter`` sets the custom object class name to the V8 object in ``toMap()`` and gets the name from the V8 object in ``fromMap()``. So it is the V8 object that carries the type information all the time and ``JavetObjectConverter`` is free from memorizing the complicated relationship between the Java objects and V8 objects.

Design a POJO Converter
=======================

A POJO converter usually is designed for the Java objects that are now owned by the application. So, it has to deal with reflection heavily. The following sample code runs in JDK 11. It's easy to tweak few API for JDK 8.

Define POJO Object
------------------

Let's say you have a Pojo that allows you to define a name-value pair.

.. code-block:: java

    public class Pojo {
        private String name;
        private String value;

        public Pojo() {
            this(null, null);
        }

        public Pojo(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

Create PojoConverter
--------------------

Then, create a generic PojoConverter.

* It is generic enough to cover all kinds of Pojo objects in a recursive way.
* There is no need to deal with primitive types because the parent converter handles that.
* Always override the methods with depth as argument for circular structure detection.
* Always increment the depth in recursive call.

.. code-block:: java

    @SuppressWarnings("unchecked")
    public class PojoConverter extends JavetObjectConverter {
        public static final String METHOD_PREFIX_GET = "get";
        public static final String METHOD_PREFIX_IS = "is";
        protected static final Set<String> EXCLUDED_METHODS;

        static {
            EXCLUDED_METHODS = new HashSet<>();
            for (Method method : Object.class.getMethods()) {
                if (method.getParameterCount() == 0) {
                    String methodName = method.getName();
                    if (methodName.startsWith(METHOD_PREFIX_IS) || methodName.startsWith(METHOD_PREFIX_GET)) {
                        EXCLUDED_METHODS.add(methodName);
                    }
                }
            }
        }

        @Override
        protected V8Value toV8Value(
                V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
            V8Value v8Value = super.toV8Value(v8Runtime, object, depth);
            if (v8Value != null && !(v8Value.isUndefined())) {
                return v8Value;
            }
            Class objectClass = object.getClass();
            V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
            for (Method method : objectClass.getMethods()) {
                if (method.getParameterCount() == 0 && method.canAccess(object)) {
                    String methodName = method.getName();
                    String propertyName = null;
                    if (methodName.startsWith(METHOD_PREFIX_IS) && !EXCLUDED_METHODS.contains(methodName)
                            && methodName.length() > METHOD_PREFIX_IS.length()) {
                        propertyName = methodName.substring(METHOD_PREFIX_IS.length(), METHOD_PREFIX_IS.length() + 1).toLowerCase(Locale.ROOT)
                                + methodName.substring(METHOD_PREFIX_IS.length() + 1);
                    } else if (methodName.startsWith(METHOD_PREFIX_GET) && !EXCLUDED_METHODS.contains(methodName)
                            && methodName.length() > METHOD_PREFIX_GET.length()) {
                        propertyName = methodName.substring(METHOD_PREFIX_GET.length(), METHOD_PREFIX_GET.length() + 1).toLowerCase(Locale.ROOT)
                                + methodName.substring(METHOD_PREFIX_GET.length() + 1);
                    }
                    if (propertyName != null) {
                        try (V8Value v8ValueTemp = toV8Value(v8Runtime, method.invoke(object), depth + 1)) {
                            v8ValueObject.set(propertyName, v8ValueTemp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            v8Value = v8ValueObject;
            return v8Runtime.decorateV8Value(v8Value);
        }
    }

Ready! Go!
----------

Just write few lines of code to interact with Javet.

.. code-block:: java

    public class TestPojo {
        public static void main(String[] args) throws JavetException {
            Pojo[] pojoArray = new Pojo[]{
                    new Pojo("Tom", "CEO"),
                    new Pojo("Jerry", "CFO")};
            try (V8Runtime v8Runtime = V8Host.getNodeInstance().createV8Runtime()) {
                v8Runtime.setConverter(new PojoConverter());
                v8Runtime.getGlobalObject().set("pojoArray", pojoArray);
                v8Runtime.getExecutor("console.log(pojoArray);").executeVoid();
            }
        }
    }

The console output is:

.. code-block:: js

    [ { name: 'Tom', value: 'CEO' }, { name: 'Jerry', value: 'CFO' } ]

This process is transparent and fully automated once the converter is set to ``V8Runtime``.

Universal Converter
===================

Can I inject arbitrary Java objects and call all the API in JavaScript? Yes, ``JavetProxyConverter`` is designed for that. In general, the user experience is very much close to the one provided by GraalJS. As ``JavetProxyConverter`` opens almost the whole JVM to V8, it is very dangerous to allow end users to touch that V8 runtime, so ``JavetProxyConverter`` is not enabled by default. Here are the steps on how to enable that.

Preparation
-----------

.. code-block:: java

    // Step 1: Create an instance of JavetProxyConverter.
    JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
    // Step 2: Set the V8Runtime converter to JavetProxyConverter.
    v8Runtime.setConverter(javetProxyConverter);
    // Please feel free to inject arbitrary Java objects.

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

Instance: Map
-------------

.. code-block:: java

    javetProxyConverter.getConfig().setProxyMapEnabled(true);
    Map<String, Object> map = new HashMap<String, Object>() {{
        put("x", 1);
        put("y", "2");
    }};
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

Static class usually does not have an instance. The universal proxy based converter is smart enough to handle that.

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

Note: The JavaScript implementation is backed up by ``V8ValueFunction`` which is an orphan object. After its internal ``V8Runtime`` is closed, it will no longer callable. It's recommended to have the interface implement ``AutoClosable`` as the sample shows so that the orphan ``V8ValueFunction`` can be recycled explicitly. If you don't own the interface, Javet will force the recycle of the orphan ``V8ValueFunction`` when the ``V8Runtime`` is being closed. Be careful, if you keep the application running for long while without recycling them in time, ``OutOfMemoryError`` may occur.

Dynamic: Anonymous Object
-------------------------

This feature is similar to the dynamic anonymous function, but is an enhanced version because it allows implementing all methods exposed by the Java interface.

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

Note: The JavaScript implementation is backed up by ``V8ValueObject`` which is an orphan object. After its internal ``V8Runtime`` is closed, it will no longer callable. It's recommended to have the interface implement ``AutoClosable`` as the sample shows so that the orphan ``V8ValueObject`` can be recycled explicitly. If you don't own the interface, Javet will force the recycle of the orphan ``V8ValueObject`` when the ``V8Runtime`` is being closed. Be careful, if you keep the application running for long while without recycling them in time, ``OutOfMemoryError`` may occur.

Features
--------

* Any Java objects generated inside V8 are automatically handled by the converter.
* Getters and setters (``get``, ``is``, ``set`` and ``put``) are smartly handled.
* Overloaded methods and varargs methods are identified well.
* Primitive types, Set, Map, List, Array are not handled. Map is special because it can be enabled.
* Java interfaces can be implemented by anonymous functions in JavaScript.

How does JavetProxyConverter Work?
----------------------------------

``JavetProxyConverter`` creates a JavaScript proxy per Java object. For now, the proxy intercepts ``get``, ``has`` and ``set`` to achieve the complete virtualization of Java objects in JavaScript runtime.

How to Customize JavetProxyConverter?
-------------------------------------

It is recommended to subclass ``JavetProxyConverter`` and override few internal API to achieve complete customization.

Null Safety
===========

What if the object converter meets ``null`` or ``undefined`` when target type is primitive? This is a quite famous topic in Java because converting null to primitive type results in ``java.lang.NullPointerException``. Luckily, Javet object converter is null safe by injecting default primitive values to ``JavetConverterConfig`` and these default primitive values can be overridden.

Functions and Objects
=====================

There are few challenges in the object conversion.

* V8 functions cannot be easily represented by Java objects.
* V8 objects and maps cannot be easily differentiated in Java.
* Sometimes unexpected functions from object conversion may break applications.

So, Javet introduced ``IJavetEntityFunction`` and ``IJavetEntityMap`` so that V8 functions and V8 maps can be precisely represented in Java.

Also, ``JavetConverterConfig`` exposes ``setSkipFunctionInObject(boolean)`` and ``setExtractFunctionSourceCode(boolean)`` to give application the opportunity to skip functions in objects or extract source code of functions.

If the source code is provided to a user defined function, Javet object converter will inject that function from the source code automatically. That makes sure Java object from V8 object can be smoothly converted back to V8 object at both property and function levels.

Circular Structure
==================

It is inefficient and inconvenient for Javet to substantially detect circular structure during object conversion. Instead, Javet converter keeps increasing the depth of recursion and throws ``JavetConverterException`` when maximum depth is reach. Maximum depth can be changed before object conversion is started. This is a cheap operation with high performance.

Please avoid setting maximum depth to an unrealistic number because JVM will throw ``StackOverflowError`` which brings considerable performance overhead. The thing worse than that is there will be memory leak because resource recycling logic written in ``finally`` block sometimes won't be called when stack overflow occurs. Attackers may easily drain the server resource in minutes by sending tiny circular structure data.

Final Note
==========

The built-in converter supports bi-directional conversion. The sample above shows the way of how to convert Java objects to V8 values. The opposite way follows the same pattern.

Please refer to :extsource3:`source code <../../../src/test/java/com/caoccao/javet/interop/converters/TestJavetCustomConverter.java>` for detail.
