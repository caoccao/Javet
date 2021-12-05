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
int/Integer     int
long/Long       bigint
float/Float     number
double/Double   number
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
