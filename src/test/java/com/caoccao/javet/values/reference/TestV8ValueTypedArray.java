/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;
import org.junit.jupiter.api.Test;

import java.nio.ByteOrder;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8ValueTypedArray extends BaseTestJavetRuntime {
    @Test
    public void testDouble() throws JavetException {
        final int length = 16;
        final int size = 8;
        final int type = V8ValueReferenceType.Float64Array;
        final int byteLength = length * size;
        final double[] doubles = new Random().doubles(length).toArray();
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                "const a = new " + V8ValueTypedArray.getName(type) + "(" + length + "); a;").execute()) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(doubles);
            }
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
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(doubles);
            }
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
        final int type = V8ValueReferenceType.Float32Array;
        final int byteLength = length * size;
        final float[] floats = new float[length];
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            floats[i] = random.nextFloat();
        }
        try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                "const a = new " + V8ValueTypedArray.getName(type) + "(" + length + "); a;").execute()) {
            assertEquals(length, v8ValueTypedArray.getLength());
            assertEquals(size, v8ValueTypedArray.getSizeInBytes());
            assertEquals(byteLength, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertEquals(type, v8ValueTypedArray.getType());
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floats);
            }
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
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floats);
            }
            v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
            for (int i = 0; i < length; i++) {
                assertEquals(floats[i], v8Runtime.getExecutor("b[" + i + "];").executeDouble().floatValue(), 0.001F);
            }
        }
    }

    @Test
    public void testInteger() throws JavetException {
        final int length = 16;
        int[] sizes = new int[]{1, 1, 1, 2, 2, 4, 4};
        int[] types = new int[]{
                V8ValueReferenceType.Int8Array,
                V8ValueReferenceType.Uint8Array,
                V8ValueReferenceType.Uint8ClampedArray,
                V8ValueReferenceType.Int16Array,
                V8ValueReferenceType.Uint16Array,
                V8ValueReferenceType.Int32Array,
                V8ValueReferenceType.Uint32Array};
        for (int i = 0; i < types.length; ++i) {
            final int size = sizes[i];
            final int type = types[i];
            final int byteLength = length * size;
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + V8ValueTypedArray.getName(type) + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                    for (int j = 0; j < byteLength; j++) {
                        v8ValueArrayBuffer.getByteBuffer().put(j, (byte) j);
                    }
                }
                for (int j = 0; j < length; j++) {
                    int expectedInt = 0;
                    for (int k = 0; k < size; k++) {
                        expectedInt += (j * size + k) << (k << 3);
                    }
                    assertEquals(expectedInt, v8Runtime.getExecutor("a[" + j + "];").executeInteger());
                }
            }
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(type, length)) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                    for (int j = 0; j < byteLength; j++) {
                        v8ValueArrayBuffer.getByteBuffer().put(j, (byte) j);
                    }
                }
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    int expectedInt = 0;
                    for (int k = 0; k < size; k++) {
                        expectedInt += (j * size + k) << (k << 3);
                    }
                    assertEquals(expectedInt, v8Runtime.getExecutor("b[" + j + "];").executeInteger());
                }
            }
            v8Runtime.unlock().resetContext().lock();
        }
    }

    @Test
    public void testLong() throws JavetException {
        final int length = 16;
        int[] sizes = new int[]{8, 8};
        int[] types = new int[]{V8ValueReferenceType.BigInt64Array, V8ValueReferenceType.BigUint64Array};
        final long[] longs = new Random().longs(length).toArray();
        for (int i = 0; i < types.length; ++i) {
            final int size = sizes[i];
            final int type = types[i];
            final int byteLength = length * size;
            try (V8ValueTypedArray v8ValueTypedArray = v8Runtime.getExecutor(
                    "const a = new " + V8ValueTypedArray.getName(type) + "(" + length + "); a;").execute()) {
                assertEquals(length, v8ValueTypedArray.getLength());
                assertEquals(size, v8ValueTypedArray.getSizeInBytes());
                assertEquals(byteLength, v8ValueTypedArray.getByteLength());
                assertEquals(0, v8ValueTypedArray.getByteOffset());
                assertEquals(type, v8ValueTypedArray.getType());
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                    v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(longs);
                }
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
                try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
                    v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(longs);
                }
                v8Runtime.getGlobalObject().set("b", v8ValueTypedArray);
                for (int j = 0; j < length; j++) {
                    assertEquals(longs[j], v8Runtime.getExecutor("b[" + j + "];").executeLong());
                }
            }
            v8Runtime.unlock().resetContext().lock();
        }
    }
}
