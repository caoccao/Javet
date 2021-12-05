================
Object Converter
================

Javet has a built-in ``JavetObjectConverter`` with the following features.

* It covers primitive types + Set + Map + Array.
* It is completely open to subclass.
* It minimizes the performance overhead.
* It allows registering custom objects.

So, Javet doesn't natively support converting POJO objects because a POJO converter has to deal with reflection which is so slow that Javet leaves that to applications. However, if the POJO objects are owned by the application, it is possible to register custom objects with the built-in ``JavetObjectConverter``. Otherwise, designing a POJO converter is the alternative solution.

Register Custom Objects
=======================

``JavetObjectConverter`` exposes ``registerCustomObject()`` for alien objects which match the following conditions.

* Default constructor without arguments
* Method with signature ``void fromMap(Map<String, Object> map)``
* Method with signature ``Map<String, Object> toMap()``

.. note::

    If the target custom object is touchable, having it implement ``IJavetMappable`` can make things easier.

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
