================
Object Converter
================

Javet has a built-in ``JavetObjectConverter`` which extends ``JavetPrimitiveConverter`` with the following features.

* It covers primitive types + Set + Map + Array + TypedArray + Stream.
* It is completely open to subclass.
* It minimizes the performance overhead via configurable batch processing.
* It allows registering custom objects.

From Java to JavaScript
=======================

======================== ===============
Java                     JavaScript
======================== ===============
boolean[]                Array
byte[]                   Int8Array
char[]                   Array
double[]                 Float64Array
float[]                  Float32Array
int[]                    Int32Array
long[]                   BigInt64Array
short[]                  Int16Array
Object[]                 Array
Map                      object
Set                      Set
Collection               Array
Stream                   Array
IntStream                Array
DoubleStream             Array
LongStream               Array
IJavetEntityFunction     Function
IJavetEntityMap          Map
IJavetEntityError        Error
JavetEntitySymbol        Symbol
IJavetMappable           Any
======================== ===============

.. note::

    * ``Map`` converts to a plain JS ``object``, whereas ``IJavetEntityMap`` (a ``LinkedHashMap`` subclass) converts to a JS ``Map``. Use ``JavetEntityMap`` when you need a JavaScript ``Map`` instead of a plain object.
    * All ``BaseStream`` types (``Stream``, ``IntStream``, ``DoubleStream``, ``LongStream``) are supported. The stream is consumed (terminated) during conversion.
    * Large collections use batch processing (configurable via ``config.setBatchSize()``, default 100, minimum 10) for performance.

From JavaScript to Java
=======================

===================== ===============================
JavaScript            Java
===================== ===============================
Array                 ArrayList<Object>
Set                   HashSet<Object>
Map                   HashMap<Object, Object>
Int8Array             byte[]
Uint8Array            byte[]
Uint8ClampedArray     byte[]
Int16Array            short[]
Uint16Array           short[]
Int32Array            int[]
Uint32Array           int[]
Float32Array          float[]
Float64Array          double[]
BigInt32Array         long[]
BigInt64Array         long[]
Function              IJavetEntityFunction
Symbol                JavetEntitySymbol
Proxy                 Any
Object                HashMap<Object, Object> or Any
===================== ===============================

.. note::

    * ``IJavetEntityError`` round-trips JavaScript errors with their type, message, stack trace, and any extra enumerable properties (accessible via ``getContext()``).
    * A ``V8ValueProxy`` is automatically unwrapped back to its original Java object if it was created by ``JavetProxyConverter``.

So, Javet doesn't natively support converting POJO objects because a POJO converter has to deal with reflection which is so slow that Javet leaves that to applications. However, if the POJO objects are owned by the application, it is possible to register custom objects with the built-in ``JavetObjectConverter``. Otherwise, designing a POJO converter is the alternative solution.

Configuration Options
=====================

``JavetObjectConverter`` inherits ``JavetConverterConfig`` which provides the following options:

============================== =========== ========== ==========================================================
Option                         Type        Default    Description
============================== =========== ========== ==========================================================
``maxDepth``                   int         20         Maximum recursion depth for circular structure detection.
``batchSize``                  int         100        Batch size for pushing elements to arrays (minimum 10).
``skipFunctionInObject``       boolean     true       Skip function-valued properties during object conversion.
``extractFunctionSourceCode``  boolean     false      Extract function source code into ``JavetEntityFunction``.
``sealedEnabled``              boolean     false      Convert sealed JS arrays to ``Object[]`` instead of ``List``.
============================== =========== ========== ==========================================================

.. code-block:: java

    JavetObjectConverter converter = new JavetObjectConverter();

    // Include function properties in object conversion
    converter.getConfig().setSkipFunctionInObject(false);

    // Extract function source code for round-trip fidelity
    converter.getConfig().setExtractFunctionSourceCode(true);

    // Convert sealed arrays to Object[] instead of ArrayList
    converter.getConfig().setSealedEnabled(true);

    // Increase batch size for large array conversion
    converter.getConfig().setBatchSize(500);

Null Safety
-----------

What if the object converter meets ``null`` or ``undefined`` when the target type is a primitive? Javet is null safe by injecting default primitive values into ``JavetConverterConfig``. These defaults can be overridden:

.. code-block:: java

    converter.getConfig().setDefaultInt(-1);       // Default: 0
    converter.getConfig().setDefaultBoolean(true);  // Default: false
    converter.getConfig().setDefaultDouble(0.0);    // Default: 0D

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
