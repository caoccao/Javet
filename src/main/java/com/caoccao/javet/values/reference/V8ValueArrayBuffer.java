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

@SuppressWarnings("unchecked")
public class V8ValueArrayBuffer extends V8ValueObject {

    public static final String PROPERTY_BYTE_LENGTH = "byteLength";
    public static final int BYTE_LENGTH_1 = 1;
    public static final int BYTE_LENGTH_2 = 2;
    public static final int BYTE_LENGTH_3 = 3;

    protected ByteBuffer byteBuffer;

    protected V8ValueArrayBuffer(long handle, ByteBuffer byteBuffer) {
        super(handle);
        this.byteBuffer = byteBuffer;
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
        if (bytes != null && bytes.length > 0 && bytes.length == byteBuffer.capacity()) {
            byteBuffer.put(bytes);
            return true;
        }
        return false;
    }

    public boolean fromDoubles(double[] doubles) {
        if (doubles != null && doubles.length > 0 && doubles.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(doubles);
            return true;
        }
        return false;
    }

    public boolean fromFloats(float[] floats) {
        if (floats != null && floats.length > 0 && floats.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floats);
            return true;
        }
        return false;
    }

    public boolean fromIntegers(int[] integers) {
        if (integers != null && integers.length > 0 && integers.length == byteBuffer.capacity() >> BYTE_LENGTH_2) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(integers);
            return true;
        }
        return false;
    }

    public boolean fromLongs(long[] longs) {
        if (longs != null && longs.length > 0 && longs.length == byteBuffer.capacity() >> BYTE_LENGTH_3) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(longs);
            return true;
        }
        return false;
    }

    public boolean fromShorts(short[] shorts) {
        if (shorts != null && shorts.length > 0 && shorts.length == byteBuffer.capacity() >> BYTE_LENGTH_1) {
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);
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
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(doubles);
        return doubles;
    }

    public float[] toFloats() {
        float[] floats = new float[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floats);
        return floats;
    }

    public int[] toIntegers() {
        int[] integers = new int[byteBuffer.capacity() >> BYTE_LENGTH_2];
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(integers);
        return integers;
    }

    public long[] toLongs() {
        long[] longs = new long[byteBuffer.capacity() >> BYTE_LENGTH_3];
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(longs);
        return longs;
    }

    public short[] toShorts() {
        short[] shorts = new short[byteBuffer.capacity() >> BYTE_LENGTH_1];
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }
}
