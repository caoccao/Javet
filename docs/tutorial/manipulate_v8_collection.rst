========================
Manipulate V8 Collection
========================

Collection in V8
================

Javet provides decent support on manipulating V8 collection as following.

=================== =========================== ================ =========== ============ ======= =================================== =============== =============== =================== ============================
Collection          getLength() / getSize()     add() / set()    delete()    has()        get()   getKeys() / getOwnPropertyNames()   getValues()     getEntries()    forEach(Consumer)   forEach(BiConsumer)
=================== =========================== ================ =========== ============ ======= =================================== =============== =============== =================== ============================
Object              No                          **Yes**          **yes**     **Yes**      **Yes** **Yes**                             No              No              **Yes**             **Yes**
Array               **Yes**                     **Yes**          **yes**     **Yes**      **Yes** **Yes**                             No              No              **Yes**             No
Map                 **Yes**                     **Yes**          **yes**     **Yes**      **Yes** **Yes**                             **Yes**         **Yes**         **Yes**             **Yes**
Set                 **Yes**                     **Yes**          **yes**     **Yes**      **Yes** **Yes**                             No              No              **Yes**             No 
WeakMap             No                          **Yes**          **yes**     **Yes**      **Yes** No                                  No              No              No                  No
WeakSet             No                          **Yes**          **yes**     **Yes**      No      No                                  No              No              No                  No
=================== =========================== ================ =========== ============ ======= =================================== =============== =============== =================== ============================

Usage
=====

V8ValueArray
------------

.. code-block:: java

    try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = new Array(0,1,2); a;").execute()) {
        AtomicInteger count = new AtomicInteger(0);
        v8ValueArray.forEach((V8ValueInteger value) -> {
            assertEquals(count.getAndIncrement(), value.getValue());
        });
        assertEquals(4, v8ValueArray.push(3));
        assertEquals(3, v8ValueArray.popInteger());
    }

V8ValueSet
----------

.. code-block:: java

    try (V8ValueSet v8ValueSet = v8Runtime.getExecutor(
            "const a = new Set(); a.add('0'); a.add('1'); a.add('2'); a;").execute()) {
        // V8 feature: Order is preserved.
        AtomicInteger count = new AtomicInteger(0);
        assertEquals(3, v8ValueSet.forEach((V8ValueString key) -> {
            assertEquals(Integer.toString(count.getAndIncrement()), key.getValue());
        }));
        v8ValueSet.add("3");
        assertTrue(v8ValueSet.has("3"));
        assertTrue(v8ValueSet.delete("3"));
        assertFalse(v8ValueSet.has("3"));
    }

V8ValueMap
----------

.. code-block:: java

    try (V8ValueMap v8ValueMap = v8Runtime.getExecutor(
            "const a = new Map(); a.set('0', 0); a.set('1', 1); a.set('2', 2); a;").execute()) {
        // V8 feature: Order is preserved.
        AtomicInteger count = new AtomicInteger(0);
        assertEquals(3, v8ValueMap.forEach((V8ValueString key) -> {
            assertNotNull(key);
            assertEquals(Integer.toString(count.getAndIncrement()), key.getValue());
        }));
        count.set(0);
        assertEquals(3, v8ValueMap.forEach((V8ValueString key, V8ValueInteger value) -> {
            assertNotNull(key);
            assertNotNull(value);
            assertEquals(Integer.toString(count.get()), key.getValue());
            assertEquals(count.getAndIncrement(), value.getValue());
        }));
        v8ValueMap.set("a", v8Runtime.createV8ValueInteger(1));
        assertEquals(4, v8ValueMap.getSize());
        assertTrue(v8ValueMap.has("a"));
        assertTrue(v8ValueMap.delete("a"));
    }

[`Home <../../README.rst>`_] [`Tutorial <index.rst>`_]
