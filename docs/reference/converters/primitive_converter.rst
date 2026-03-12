===================
Primitive Converter
===================

``JavetPrimitiveConverter`` is the basic converter that only takes care of primitive values. It is the base class of all other converters and handles the most common type conversions. It is optimized for performance with type checks ordered by statistical frequency.

From Java to JavaScript
=======================

======================= ===============
Java                    JavaScript
======================= ===============
boolean/Boolean         boolean
byte/Byte               int
char/Character          string
double/Double           number
float/Float             number
int/Integer             int
long/Long/BigInteger    bigint
short/Short             int
String                  string
ZonedDateTime           Date
null                    null
======================= ===============

Any unrecognized Java type is converted to ``undefined``.

Optional Types
--------------

``Optional``, ``OptionalInt``, ``OptionalDouble``, and ``OptionalLong`` are supported. If the optional is present, the inner value is converted recursively. If empty, the result is ``null``.

.. code-block:: java

    // Optional with value → converted inner value
    converter.toV8Value(v8Runtime, Optional.of(123));       // → V8ValueInteger(123)
    converter.toV8Value(v8Runtime, OptionalLong.of(100L));  // → V8ValueLong(100)

    // Empty optional → null
    converter.toV8Value(v8Runtime, Optional.empty());       // → V8ValueNull
    converter.toV8Value(v8Runtime, OptionalInt.empty());    // → V8ValueNull

From JavaScript to Java
=======================

=============== ===============
JavaScript      Java
=============== ===============
boolean         boolean
int             int
bigint          long/BigInteger
number          double
string          String
Date            ZonedDateTime
null            null
undefined       null
=============== ===============

Any unrecognized V8 value type (objects, arrays, functions, etc.) is returned as the raw ``V8Value`` instance.

.. note::

    * ``JavetPrimitiveConverter`` is typically not used directly. Use ``JavetObjectConverter`` (the default) for most use cases.
    * If ``bigint`` falls in the range of ``long``, it is converted to ``long``. Otherwise, it is converted to ``BigInteger``.
    * Both ``null`` and ``undefined`` from JavaScript are converted to Java ``null``.

Numeric Widening and Narrowing
------------------------------

When a JavaScript value is passed to a Java callback method, Javet performs automatic type coercion:

* ``int`` and ``bigint`` can be narrowed to ``byte``, ``short``, or widened to ``long``, ``float``, ``double``.
* A single-character ``string`` can be converted to ``char``. Multi-character strings are silently truncated to the first character.
* Both boxed (``Integer``) and unboxed (``int``) parameter types are supported in callbacks.
