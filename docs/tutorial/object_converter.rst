================
Object Converter
================

Javet has a built-in object converter with the following features.

* It covers primitive types + Set + Map + Array.
* It is completely open to subclass.
* It minimizes the performance overhead.

So, Javet doesn't natively support converting POJO objects because a POJO converter has to deal with reflection which is so slow that Javet leaves that to applications.

Design a POJO Converter
=======================

The following sample code runs in JDK 11. It's easy to tweak few API for JDK 8.

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

.. code-block:: json

    [ { name: 'Tom', value: 'CEO' }, { name: 'Jerry', value: 'CFO' } ]

This process is transparent and fully automated once the converter is set to ``V8Runtime``.

Null Safety
===========

What if the object converter meets ``null`` or ``undefined`` when target type is primitive? This is a quite famous topic in Java because converting null to primitive type results in ``java.lang.NullPointerException``. Luckily, Javet object converter is null safe by injecting default primitive values and the default primitive values can be overridden. Please check out ``com.caoccao.javet.interop.converters.IJavetConverter#getDefault*`` for detail.

Functions and Objects
=====================

There are few challenges in the object conversion.

* V8 functions cannot be easily represented by Java objects.
* V8 objects and maps cannot be easily differentiated in Java.
* Sometimes unexpected functions from object conversion may break applications.

So, Javet introduced ``IJavetEntityFunction`` and ``IJavetEntityMap`` so that V8 functions and V8 maps can be precisely represented in Java.

Also, ``JavetObjectConverter`` exposes ``setSkipFunctionInObject(boolean)`` and ``setExtractFunctionSourceCode(boolean)`` to give application the opportunity to skip functions in objects or extract source code of functions.

If the source code is provided to a user defined function, Javet object converter will inject that function from the source code automatically. That makes sure Java object from V8 object can be smoothly converted back to V8 object at both property and function levels.

Circular Structure
==================

It is inefficient and inconvenient for Javet to substantially detect circular structure during object conversion. Instead, Javet converter keeps increasing the depth of recursion and throws ``JavetConverterException`` when maximum depth is reach. Maximum depth can be changed before object conversion is started. This is a cheap operation with high performance.

Please avoid setting maximum depth to an unrealistic number because JVM will throw ``StackOverflowError`` which brings considerable performance overhead. The thing worse than that is there will be memory leak because resource recycling logic written in ``finally`` block sometimes won't be called when stack overflow occurs. Attackers may easily drain the server resource in minutes by sending tiny circular structure data.

Final Note
==========

The built-in converter supports bi-directional conversion. The sample above shows the way of how to convert Java objects to V8 values. The opposite way follows the same pattern.

Please refer to `source code <../../src/test/java/com/caoccao/javet/interop/converters/TestJavetCustomConverter.java>`_ for detail.

[`Home <../../README.rst>`_] [`Javet Tutorial <index.rst>`_]
