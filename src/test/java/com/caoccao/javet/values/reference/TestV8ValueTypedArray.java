/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestV8ValueTypedArray extends BaseTestJavetRuntime {

    @Test
    public void testByte() throws JavetException {
        final int length = 16;
        V8ValueReferenceType[] types = new V8ValueReferenceType[]{
                V8ValueReferenceType.Int8Array,
                V8ValueReferenceType.Uint8Array,
                V8ValueReferenceType.Uint8ClampedArray};
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) (bytes[i] & 0x7F);
        }
        for (int i = 0; i < types.length; ++i) {
            final V8ValueReferenceType type = types[i];
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(1, v8ValueTypedArray.getSizeInBytes());
                assertEquals(length, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getBuffer()) {
                    v8ValueArrayBuffer.fromBytes(bytes);
                }
                for (int j = 0; j < length; j++) {
                    assertEquals(bytes[j], v8Runtime.getExecutor("a[" + j + "];").executeInteger());
                }
            }
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(1, v8ValueTypedArray.getSizeInBytes());
                assertEquals(length, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getBuffer()) {
                    v8ValueArrayBuffer.fromBytes(bytes);
                }
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    assertEquals(bytes[j], v8Runtime.getExecutor("b[" + j + "];").executeInteger());
                }
            }
            resetContext();
        }
    }

    @Test
    public void testDouble() throws JavetException {
        final int length = 16;
        final int size = 8;
        final V8ValueReferenceType type = V8ValueReferenceType.Float64Array;
        final int byteLength = length * size;
        final double[] doubles = new Random().doubles(length).toArray();
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            assertTrue(v8ValueTypedArray.fromDoubles(doubles));
            for (int i = 0; i < length; i++) {
                assertEquals(doubles[i], v8Runtime.getExecutor("a[" + i + "];").executeDouble(), 0.001D);
            }
        }
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            assertTrue(v8ValueTypedArray.fromDoubles(doubles));
            v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
            for (int i = 0; i < length; i++) {
                assertEquals(doubles[i], v8Runtime.getExecutor("b[" + i + "];").executeDouble(), 0.001D);
            }
        }
    }

    @Test
    public void testFloat() throws JavetException {
        final int length = 16;
        final int size = 4;
        final V8ValueReferenceType type = V8ValueReferenceType.Float32Array;
        final int byteLength = length * size;
        final float[] floats = new float[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            floats[i] = random.nextFloat();
        }
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            assertTrue(v8ValueTypedArray.fromFloats(floats));
            for (int i = 0; i < length; i++) {
                assertEquals(floats[i], v8Runtime.getExecutor("a[" + i + "];").executeDouble().floatValue(), 0.001F);
            }
        }
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            assertTrue(v8ValueTypedArray.fromFloats(floats));
            v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
            for (int i = 0; i < length; i++) {
                assertEquals(floats[i], v8Runtime.getExecutor("b[" + i + "];").executeDouble().floatValue(), 0.001F);
            }
        }
    }

    @Test
    public void testInteger() throws JavetException {
        final int length = 16;
        int[] sizes = new int[]{4, 4};
        V8ValueReferenceType[] types = new V8ValueReferenceType[]{V8ValueReferenceType.Int32Array, V8ValueReferenceType.Uint32Array};
        final int[] integers = new Random().ints(length).toArray();
        for (int i = 0; i < length; i++) {
            if (integers[i] < 0) {
                integers[i] = 0 - integers[i];
            }
        }
        for (int i = 0; i < types.length; ++i) {
            final int size = sizes[i];
            final V8ValueReferenceType type = types[i];
            final int byteLength = length * size;
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromIntegers(integers));
                for (int j = 0; j < length; j++) {
                    assertEquals(integers[j], v8Runtime.getExecutor("a[" + j + "];").executeInteger());
                }
            }
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromIntegers(integers));
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    assertEquals(integers[j], v8Runtime.getExecutor("b[" + j + "];").executeInteger());
                }
            }
            resetContext();
        }
    }

    @Test
    public void testLong() throws JavetException {
        final int length = 16;
        int[] sizes = new int[]{8, 8};
        V8ValueReferenceType[] types = new V8ValueReferenceType[]{V8ValueReferenceType.BigInt64Array, V8ValueReferenceType.BigUint64Array};
        final long[] longs = new Random().longs(length).toArray();
        for (int i = 0; i < length; i++) {
            if (longs[i] < 0) {
                longs[i] = 0 - longs[i];
            }
        }
        for (int i = 0; i < types.length; ++i) {
            final int size = sizes[i];
            final V8ValueReferenceType type = types[i];
            final int byteLength = length * size;
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromLongs(longs));
                for (int j = 0; j < length; j++) {
                    assertEquals(longs[j], v8Runtime.getExecutor("a[" + j + "];").executeLong());
                }
            }
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromLongs(longs));
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    assertEquals(longs[j], v8Runtime.getExecutor("b[" + j + "];").executeLong());
                }
            }
            resetContext();
        }
    }

    @Test
    public void testShort() throws JavetException {
        final int length = 16;
        int[] sizes = new int[]{2, 2};
        V8ValueReferenceType[] types = new V8ValueReferenceType[]{V8ValueReferenceType.Int16Array, V8ValueReferenceType.Uint16Array};
        final short[] shorts = new short[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            shorts[i] = (short) (random.nextInt() & 0x7FFF);
        }
        for (int i = 0; i < types.length; ++i) {
            final int size = sizes[i];
            final V8ValueReferenceType type = types[i];
            final int byteLength = length * size;
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + type.getName() + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromShorts(shorts));
                for (int j = 0; j < length; j++) {
                    assertEquals(shorts[j], v8Runtime.getExecutor("a[" + j + "];").executeInteger());
                }
            }
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                assertTrue(v8ValueTypedArray.fromShorts(shorts));
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    assertEquals(shorts[j], v8Runtime.getExecutor("b[" + j + "];").executeInteger());
                }
            }
            resetContext();
        }
    }
}
