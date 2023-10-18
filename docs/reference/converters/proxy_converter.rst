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

1. Add ``ByteBuddy`` to the dependency. You may skip this step if your project has already referenced ``ByteBuddy``.

.. code-block:: xml

    <!-- Maven -->
    <dependency>
        <groupId>net.bytebuddy</groupId>
        <artifactId>byte-buddy</artifactId>
        <version>1.12.17</version>
    </dependency>

.. code-block:: kotlin

    // Gradle Kotlin DSL
    implementation("net.bytebuddy:byte-buddy:1.12.17")

.. code-block:: groovy

    // Gradle Groovy DSL
    implementation 'net.bytebuddy:byte-buddy:1.12.17'

2. Copy :extsource3:`JavetReflectionObjectFactory.java <../../../src/test/java/com/caoccao/javet/interop/proxy/JavetReflectionObjectFactory.java>` to your project. As Javet doesn't reference ``ByteBuddy`` directly, ``JavetReflectionObjectFactory`` has to stay at the test project.

3. Define a simple class ``DynamicClass`` for adding two integers.

.. code-block:: java

    public class DynamicClass {
        public int add(int a, int b) {
            return 0;
        }
    }

4. Create an instance of a class which takes an instance of the ``DynamicClass``.

.. code-block:: java

    IJavetAnonymous anonymous = new IJavetAnonymous() {
        @V8Function
        public void test(DynamicClass dynamicClass) throws Exception {
            assertEquals(3, dynamicClass.add(1, 2), "Add should work.");
            ((AutoCloseable) dynamicClass).close();
        }
    };

5. Inject the implementation from JavaScript. Please note that dynamic object support is disabled by default and ``JavetReflectionObjectFactory`` needs to be set to the converter config for ``JavetProxyConverter`` to enable this feature.

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

.. note::

    The JavaScript implementation is backed up by ``V8ValueObject`` which is an orphan object. After its internal ``V8Runtime`` is closed, it will no longer callable. It's recommended to have the interface implement ``AutoCloseable`` as the sample shows so that the orphan ``V8ValueObject`` can be recycled explicitly. If you don't own the interface, Javet will force the recycle of the orphan ``V8ValueObject`` when the ``V8Runtime`` is being closed. Be careful, if you keep the application running for long while without recycling them in time, ``OutOfMemoryError`` may occur.

Features
========

* Any Java objects generated inside V8 are automatically handled by the converter.
* Getters and setters (``get``, ``is``, ``set`` and ``put``) are smartly handled.
* Overloaded methods and varargs methods are identified well.
* Primitive types, Set, Map, List, Array are not handled. Map is special because it can be enabled.
* Java interfaces can be implemented by anonymous functions in JavaScript.
* Annotations can be applied to classes or methods to alter the default behaviors.

============= ============================= =====================================================================
Annotation    Type                          Description
============= ============================= =====================================================================
@V8Convert    Class                         It tells the converter which mode to be applied to the annotated class.
@V8Allow      Constructor / Field / Method  It tells the converter to bind the constructor / field / method.
@V8Block      Constructor / Field / Method  It tells the converter to ignore the constructor / field / method.
@V8Property   Field                         It tells the converter to bind the field.
@V8Function   Method                        It tells the converter to bind the method.
@V8Getter     Method                        It tells the converter to bind the method as getter.
@V8Setter     Method                        It tells the converter to bind the method as setter.
============= ============================= =====================================================================

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

How does JavetProxyConverter Work?
==================================

``JavetProxyConverter`` creates a JavaScript proxy per Java object. For now, the proxy intercepts ``get``, ``has`` and ``set`` to achieve the complete virtualization of Java objects in JavaScript runtime.

How to Customize JavetProxyConverter?
=====================================

It is recommended to subclass ``JavetProxyConverter`` and override few internal API to achieve complete customization.
