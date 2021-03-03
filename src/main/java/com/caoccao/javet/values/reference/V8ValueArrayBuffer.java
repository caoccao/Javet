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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueArrayBuffer extends V8ValueObject {
    protected static final String PROPERTY_BYTE_LENGTH = "byteLength";
    protected static final int BYTE_LENGTH_1 = 1;
    protected static final int BYTE_LENGTH_2 = 2;
    protected static final int BYTE_LENGTH_3 = 3;

    protected ByteBuffer byteBuffer;
    protected ByteOrder byteOrder;

    V8ValueArrayBuffer(long handle, ByteBuffer byteBuffer) {
        super(handle);
        this.byteBuffer = byteBuffer;
        byteOrder = ByteOrder.nativeOrder();
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        Objects.requireNonNull(byteOrder);
        this.byteOrder = byteOrder;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.ArrayBuffer;
    }

    public boolean fromBytes(byte[] bytes) {
        Objects.requireNonNull(bytes);
        if (bytes != null && bytes.length > 0 && bytes.length == byteBuffer.capacity()) {
            byteBuffer.put(bytes);
            return true;
        }
        return false;
    }

    public boolean fromDoubles(double[] doubles) {
        Objects.requireNonNull(doubles);
        if (doubles != null && doubles.length > 0 && doubles.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(byteOrder).asDoubleBuffer().put(doubles);
            return true;
        }
        return false;
    }

    public boolean fromFloats(float[] floats) {
        Objects.requireNonNull(floats);
        if (floats != null && floats.length > 0 && floats.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(byteOrder).asFloatBuffer().put(floats);
            return true;
        }
        return false;
    }

    public boolean fromIntegers(int[] integers) {
        Objects.requireNonNull(integers);
        if (integers != null && integers.length > 0 && integers.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(byteOrder).asIntBuffer().put(integers);
            return true;
        }
        return false;
    }

    public boolean fromLongs(long[] longs) {
        Objects.requireNonNull(longs);
        if (longs != null && longs.length > 0 && longs.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(byteOrder).asLongBuffer().put(longs);
            return true;
        }
        return false;
    }

    public boolean fromShorts(short[] shorts) {
        Objects.requireNonNull(shorts);
        if (shorts != null && shorts.length > 0 && shorts.length == byteBuffer.capacity() >> BYTE_LENGTH_1) {
            byteBuffer.order(byteOrder).asShortBuffer().put(shorts);
            return true;
        }
        return false;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[byteBuffer.capacity()];
        byteBuffer.get(bytes);
        return bytes;
    }

    public double[] toDoubles() {
        double[] doubles = new double[byteBuffer.capacity() >> BYTE_LENGTH_3];
        byteBuffer.order(byteOrder).asDoubleBuffer().get(doubles);
        return doubles;
    }

    public float[] toFloats() {
        float[] floats = new float[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(byteOrder).asFloatBuffer().get(floats);
        return floats;
    }

    public int[] toIntegers() {
        int[] integers = new int[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(byteOrder).asIntBuffer().get(integers);
        return integers;
    }

    public long[] toLongs() {
        long[] longs = new long[byteBuffer.capacity() >> BYTE_LENGTH_3];
        byteBuffer.order(byteOrder).asLongBuffer().get(longs);
        return longs;
    }

    public short[] toShorts() {
        short[] shorts = new short[byteBuffer.capacity() >> BYTE_LENGTH_1];
        byteBuffer.order(byteOrder).asShortBuffer().get(shorts);
        return shorts;
    }
}
