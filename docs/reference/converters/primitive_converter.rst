===================
Primitive Converter
===================

``JavetPrimitiveConverter`` is the basic converter that only takes care of primitive values.

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
======================= ===============

.. note::

    ``Optional``, ``OptionalInt``, ``OptionalDouble``, ``OptionalLong`` are also supported.

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
=============== ===============

.. note::

    * ``JavetPrimitiveConverter`` is not supposed to be used in any cases.
    * If ``bigint`` falls in the range of ``long``, it is converted to ``long``. Otherwise, it is converted to ``BigInteger``.
