===================
Primitive Converter
===================

``JavetPrimitiveConverter`` is the basic converter that only takes care of primitive values.

From Java to JavaScript
=======================

=============== ===============
Java            JavaScript
=============== ===============
boolean/Boolean boolean
byte/Byte       int
char/Character  string
double/Double   number
float/Float     number
int/Integer     int
long/Long       bigint
short/Short     int
String          string
ZonedDateTime   Date
=============== ===============

.. note::

    ``Optional``, ``OptionalInt``, ``OptionalDouble``, ``OptionalLong`` are supported except for Android.

From JavaScript to Java
=======================

=============== ===============
JavaScript      Java
=============== ===============
boolean         boolean
int             int
bigint          long
number          double
string          String
Date            ZonedDateTime
=============== ===============

.. caution::

    ``JavetPrimitiveConverter`` is not supposed to be used in any cases.
