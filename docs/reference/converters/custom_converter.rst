================
Custom Converter
================

Sometimes the converters provided by Javet are not powerful enough. No problem. Javet allows defining custom converters. Here is a simple example about how to create a custom converter.

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
        protected <T extends V8Value> T toV8Value(
                V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
            T v8Value = super.toV8Value(v8Runtime, object, depth);
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
            v8Value = (T) v8ValueObject;
            return v8Value;
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
