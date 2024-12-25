=========
V8 Values
=========

V8 values represent a family of V8 primitive or reference values in JVM.

======================== =========================== ===========================
Feature                  V8 Primitive Values         V8 Reference Values
======================== =========================== ===========================
Decoupled with V8        Yes                         No
Immutable                Yes                         No
Performance              High                        Medium
======================== =========================== ===========================

V8 Primitive Values
===================

=========================== ======================
Java Type                   JavaScript Type
=========================== ======================
V8ValueBigInteger           bigint
V8ValueBoolean              bool
V8ValueDouble               number
V8ValueInteger              number
V8ValueLong                 bigint
V8ValueNull                 null
V8ValueString               string
V8ValueUndefined            undefined
V8ValueZonedDateTime        Date
=========================== ======================

V8 Reference Values
===================

.. toctree::
    :maxdepth: 1

    v8_collection
    v8_function
    v8_promise
    v8_typed_array
