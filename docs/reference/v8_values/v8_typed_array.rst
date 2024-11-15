==============
V8 Typed Array
==============

There are 12 typed array supported. They are as follows.

=================== =========================== =============== =========== =======================
Type                Value Range                 Size in bytes   Java Type   Web IDL type
=================== =========================== =============== =========== =======================
Int8Array           -128 to 127                 1               byte        byte
Uint8Array          0 to 255                    1               byte        octet
Uint8ClampedArray   0 to 255                    1               byte        octet
Int16Array          -32768 to 32767             2               short       short
Uint16Array         0 to 65535                  2               short       unsigned short
Int32Array          -2147483648 to 2147483647   4               int         long
Uint32Array         0 to 4294967295             4               int         unsigned long
Float16Array        -65504 to 65504             2               short       N/A
Float32Array        -3.4e38 to 3.4e38           4               int         unrestricted float
Float64Array        -1.8e308 to 1.8e308         8               double      unrestricted double
BigInt64Array       -2^63 to 2^63 - 1           8               long        bigint
BigUint64Array      0 to 2^64 - 1               8               long        bigint
=================== =========================== =============== =========== =======================

Play with V8ValueTypedArray
===========================

All typed array share the same V8 value type ``V8ValueTypedArray``. They can be differentiated by their ``getType()`` which is an enum indicating their actual types. There is a buffer associated with the ``V8ValueTypedArray``. That buffer is the actual shared memory between JVM and V8. In other words, there is zero memory copy between JVM and V8 if ``V8ValueTypedArray`` is used. Many performance sensitive applications usually exchange data via ``V8ValueTypedArray``. There are a set of ``from****`` and ``to****`` API for the data exchange.

.. code-block:: java

    try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
        V8ValueReferenceType.Int8Array, 4)) {
        assertEquals(4, v8ValueTypedArray.getLength());
        assertEquals(1, v8ValueTypedArray.getSizeInBytes());
        assertEquals(4, v8ValueTypedArray.getByteLength());
        assertEquals(0, v8ValueTypedArray.getByteOffset());
        assertEquals(V8ValueReferenceType.Int8Array, v8ValueTypedArray.getType());
        try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getBuffer()) {
            v8ValueArrayBuffer.fromBytes(new byte[]{ (byte) 1, (byte) 2, (byte) 3, (byte) 4,});
        }
    }

Play with Float16Array
======================

``Float16Array`` was `introduced to V8 in Mar, 2024 <https://blog.seokho.dev/development/2024/03/03/V8-Float16Array.html>`_ as an implementation of TC39 `proposal-float16array <https://github.com/tc39/proposal-float16array>`_. It's quite special because there is no float 16 in Java, so has to be mapped to ``short``.

Javet borrows ``Float16`` from the `Android FP16 implementation <https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/libcore/util/FP16.java>`_ (copyright preserved) to allow easy translation between ``short`` and ``float``. Just call ``short toHalf(float f)`` or ``float toFloat(short h)`` to complete the translation.

``Float16Array`` is not enabled by default. Please make sure the following code is executed before the first ``NodeRuntime`` or ``V8Runtime`` is created.

.. code-block:: java

    // Node.js mode
    NodeRuntimeOptions.NODE_FLAGS.setJsFloat16Array(true);

    // V8 mode
    V8RuntimeOptions.V8_FLAGS.setJsFloat16Array(true);

Please review the :extsource3:`test cases <../../../src/test/java/com/caoccao/javet/values/reference/TestV8ValueTypedArray.java>` for more detail.
