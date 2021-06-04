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
        public V8Value toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
            V8Value v8Value = super.toV8Value(v8Runtime, object);
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
                        try (V8Value v8ValueTemp = toV8Value(v8Runtime, method.invoke(object))) {
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

Circular Structure
==================

It is inefficient and inconvenient for Javet to detect circular structure during object conversion. So, how to detect that in application? The recommended approach is to catch ``java.lang.StackOverflowError``. It only takes 10-30ms for the object converter to throw this exception. The actual time depends on how complex the object is. You are welcome proposing a better approach.

Final Note
==========

The built-in converter supports bi-directional conversion. The sample above shows the way of how to convert Java objects to V8 values. The opposite way follows the same pattern.

Please refer to `source code <../../src/test/java/com/caoccao/javet/interop/converters/TestJavetCustomConverter.java>`_ for detail.

[`Home <../../README.rst>`_] [`Javet Tutorial <index.rst>`_]
