/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

/**
 * The type V8 value ArrayBuffer, representing a JavaScript ArrayBuffer object.
 */
public class V8ValueArrayBuffer extends V8ValueObject {
    /**
     * The constant BYTE_LENGTH_1 for 1-byte shift (short).
     */
    protected static final int BYTE_LENGTH_1 = 1;
    /**
     * The constant BYTE_LENGTH_2 for 2-byte shift (int/float).
     */
    protected static final int BYTE_LENGTH_2 = 2;
    /**
     * The constant BYTE_LENGTH_3 for 3-byte shift (long/double).
     */
    protected static final int BYTE_LENGTH_3 = 3;
    /**
     * The constant PROPERTY_BYTE_LENGTH.
     */
    protected static final String PROPERTY_BYTE_LENGTH = "byteLength";
    /**
     * The byte buffer backing this ArrayBuffer.
     */
    protected ByteBuffer byteBuffer;
    /**
     * The byte order used for multi-byte conversions.
     */
    protected ByteOrder byteOrder;

    V8ValueArrayBuffer(V8Runtime v8Runtime, long handle, ByteBuffer byteBuffer) throws JavetException {
        super(v8Runtime, handle);
        this.byteBuffer = byteBuffer;
        byteOrder = ByteOrder.nativeOrder();
    }

    /**
     * Copies the given byte array into this ArrayBuffer.
     *
     * @param bytes the byte array to copy from
     * @return true if the bytes were successfully copied, false otherwise
     */
    public boolean fromBytes(byte[] bytes) {
        if (bytes != null && bytes.length > 0 && bytes.length == byteBuffer.capacity()) {
            byteBuffer.put(bytes);
            return true;
        }
        return false;
    }

    /**
     * Copies the given double array into this ArrayBuffer.
     *
     * @param doubles the double array to copy from
     * @return true if the doubles were successfully copied, false otherwise
     */
    public boolean fromDoubles(double[] doubles) {
        if (doubles != null && doubles.length > 0 && doubles.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(byteOrder).asDoubleBuffer().put(doubles);
            return true;
        }
        return false;
    }

    /**
     * Copies the given float array into this ArrayBuffer.
     *
     * @param floats the float array to copy from
     * @return true if the floats were successfully copied, false otherwise
     */
    public boolean fromFloats(float[] floats) {
        if (floats != null && floats.length > 0 && floats.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(byteOrder).asFloatBuffer().put(floats);
            return true;
        }
        return false;
    }

    /**
     * Copies the given integer array into this ArrayBuffer.
     *
     * @param integers the integer array to copy from
     * @return true if the integers were successfully copied, false otherwise
     */
    public boolean fromIntegers(int[] integers) {
        if (integers != null && integers.length > 0 && integers.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(byteOrder).asIntBuffer().put(integers);
            return true;
        }
        return false;
    }

    /**
     * Copies the given long array into this ArrayBuffer.
     *
     * @param longs the long array to copy from
     * @return true if the longs were successfully copied, false otherwise
     */
    public boolean fromLongs(long[] longs) {
        if (longs != null && longs.length > 0 && longs.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(byteOrder).asLongBuffer().put(longs);
            return true;
        }
        return false;
    }

    /**
     * Copies the given short array into this ArrayBuffer.
     *
     * @param shorts the short array to copy from
     * @return true if the shorts were successfully copied, false otherwise
     */
    public boolean fromShorts(short[] shorts) {
        if (shorts != null && shorts.length > 0 && shorts.length == byteBuffer.capacity() >> BYTE_LENGTH_1) {
            byteBuffer.order(byteOrder).asShortBuffer().put(shorts);
            return true;
        }
        return false;
    }

    /**
     * Gets the underlying byte buffer.
     *
     * @return the byte buffer
     */
    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    /**
     * Gets the byte length of this ArrayBuffer.
     *
     * @return the byte length
     * @throws JavetException the javet exception
     */
    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    /**
     * Gets the byte order used for multi-byte conversions.
     *
     * @return the byte order
     */
    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.ArrayBuffer;
    }

    /**
     * Sets the byte order used for multi-byte conversions.
     *
     * @param byteOrder the byte order
     */
    public void setByteOrder(ByteOrder byteOrder) {
        Objects.requireNonNull(byteOrder);
        this.byteOrder = byteOrder;
    }

    /**
     * Converts the ArrayBuffer contents to a byte array.
     *
     * @return the byte array
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        return bytes;
    }

    /**
     * Converts the ArrayBuffer contents to a double array.
     *
     * @return the double array
     */
    public double[] toDoubles() {
        double[] doubles = new double[byteBuffer.capacity() >> BYTE_LENGTH_3];
        byteBuffer.order(byteOrder).asDoubleBuffer().get(doubles);
        return doubles;
    }

    /**
     * Converts the ArrayBuffer contents to a float array.
     *
     * @return the float array
     */
    public float[] toFloats() {
        float[] floats = new float[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(byteOrder).asFloatBuffer().get(floats);
        return floats;
    }

    /**
     * Converts the ArrayBuffer contents to an integer array.
     *
     * @return the integer array
     */
    public int[] toIntegers() {
        int[] integers = new int[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(byteOrder).asIntBuffer().get(integers);
        return integers;
    }

    /**
     * Converts the ArrayBuffer contents to a long array.
     *
     * @return the long array
     */
    public long[] toLongs() {
        long[] longs = new long[byteBuffer.capacity() >> BYTE_LENGTH_3];
        byteBuffer.order(byteOrder).asLongBuffer().get(longs);
        return longs;
    }

    /**
     * Converts the ArrayBuffer contents to a short array.
     *
     * @return the short array
     */
    public short[] toShorts() {
        short[] shorts = new short[byteBuffer.capacity() >> BYTE_LENGTH_1];
        byteBuffer.order(byteOrder).asShortBuffer().get(shorts);
        return shorts;
    }
}
