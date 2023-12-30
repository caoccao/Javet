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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualValue;

import java.util.Objects;

/**
 * The type V8 value typed array.
 * The typical way of manipulating the typed array is as following.
 * 1. Get array buffer and apply try-with-resource.
 * 2. Create the value array by length.
 * 3. Fill the value array.
 * <p>
 * E.g.:
 * <pre>
 * try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
 *     long[] longs = new long[v8ValueArrayBuffer.getLength()];
 *     v8ValueArrayBuffer.toLongs(longs);
 *     // ...
 * }
 * </pre>
 * <p>
 * Or, play with ByteBuffer directly.
 * 1. Get array buffer and apply try-with-resource.
 * 2. Get native ByteBuffer.
 * 3. Set order of the native ByteBuffer to native order.
 * 4. Get typed buffer by type.
 * 5. Read from or write to the typed buffer.
 * <p>
 * E.g:
 * <pre>
 * try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
 *     LongBuffer longBuffer = v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.nativeOrder()).asLongBuffer();
 *     // ...
 * }
 * </pre>
 *
 * @since 0.7.2
 */
public class V8ValueTypedArray extends V8ValueObject implements IV8ValueTypedArray {

    /**
     * The constant PROPERTY_BYTE_LENGTH.
     *
     * @since 0.7.2
     */
    public static final String PROPERTY_BYTE_LENGTH = "byteLength";
    /**
     * The constant PROPERTY_BUFFER.
     *
     * @since 0.7.2
     */
    public static final String PROPERTY_BUFFER = "buffer";
    /**
     * The constant PROPERTY_BYTE_OFFSET.
     *
     * @since 0.7.2
     */
    public static final String PROPERTY_BYTE_OFFSET = "byteOffset";
    /**
     * The constant PROPERTY_NAME.
     *
     * @since 0.7.2
     */
    public static final String PROPERTY_NAME = "Name";
    /**
     * The constant ONE_BYTE_PER_VALUE.
     *
     * @since 0.7.2
     */
    public static final int ONE_BYTE_PER_VALUE = 1;
    /**
     * The constant TWO_BYTES_PER_VALUE.
     *
     * @since 0.7.2
     */
    public static final int TWO_BYTES_PER_VALUE = 2;
    /**
     * The constant FOUR_BYTES_PER_VALUE.
     *
     * @since 0.7.2
     */
    public static final int FOUR_BYTES_PER_VALUE = 4;
    /**
     * The constant EIGHT_BYTES_PER_VALUE.
     *
     * @since 0.7.2
     */
    public static final int EIGHT_BYTES_PER_VALUE = 8;
    /**
     * The constant ZERO_BYTE_PER_VALUE.
     *
     * @since 0.7.2
     */
    public static final int ZERO_BYTE_PER_VALUE = 0;
    /**
     * The Size in bytes.
     *
     * @since 0.7.2
     */
    protected int sizeInBytes;
    /**
     * The Type.
     *
     * @since 0.8.4
     */
    protected V8ValueReferenceType type;

    /**
     * Instantiates a new V8 value typed array.
     *
     * @param v8Runtime the v 8 runtime
     * @param handle    the handle
     * @param type      the type
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    V8ValueTypedArray(V8Runtime v8Runtime, long handle, int type) throws JavetException {
        super(v8Runtime, handle);
        setType(V8ValueReferenceType.parse(type));
    }

    /**
     * From byte array.
     *
     * @param bytes the byte array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromBytes(byte[] bytes) throws JavetException {
        Objects.requireNonNull(bytes);
        if (getType() == V8ValueReferenceType.Int8Array ||
                getType() == V8ValueReferenceType.Uint8Array ||
                getType() == V8ValueReferenceType.Uint8ClampedArray) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromBytes(bytes);
            }
        }
        return false;
    }

    /**
     * From double array.
     *
     * @param doubles the double array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromDoubles(double[] doubles) throws JavetException {
        Objects.requireNonNull(doubles);
        if (getType() == V8ValueReferenceType.Float64Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromDoubles(doubles);
            }
        }
        return false;
    }

    /**
     * From float array.
     *
     * @param floats the float array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromFloats(float[] floats) throws JavetException {
        Objects.requireNonNull(floats);
        if (getType() == V8ValueReferenceType.Float32Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromFloats(floats);
            }
        }
        return false;
    }

    /**
     * From integer array.
     *
     * @param integers the integer array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromIntegers(int[] integers) throws JavetException {
        Objects.requireNonNull(integers);
        if (getType() == V8ValueReferenceType.Int32Array ||
                getType() == V8ValueReferenceType.Uint32Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromIntegers(integers);
            }
        }
        return false;
    }

    /**
     * From long array.
     *
     * @param longs the long array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromLongs(long[] longs) throws JavetException {
        Objects.requireNonNull(longs);
        if (getType() == V8ValueReferenceType.BigInt64Array ||
                getType() == V8ValueReferenceType.BigUint64Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromLongs(longs);
            }
        }
        return false;
    }

    /**
     * From short array.
     *
     * @param shorts the short array
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public boolean fromShorts(short[] shorts) throws JavetException {
        Objects.requireNonNull(shorts);
        if (getType() == V8ValueReferenceType.Int16Array ||
                getType() == V8ValueReferenceType.Uint16Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.fromShorts(shorts);
            }
        }
        return false;
    }

    @Override
    public <T extends V8Value> T get(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGet(this, virtualKey.get());
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueArrayBuffer getBuffer() throws JavetException {
        return get(PROPERTY_BUFFER);
    }

    @Override
    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    @Override
    public int getByteOffset() throws JavetException {
        return getInteger(PROPERTY_BYTE_OFFSET);
    }

    @Override
    public int getLength() throws JavetException {
        return checkV8Runtime().getV8Internal().arrayGetLength(this);
    }

    @Override
    public int getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public V8ValueReferenceType getType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return type != V8ValueReferenceType.Invalid;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @since 0.9.1
     */
    protected void setType(V8ValueReferenceType type) {
        switch (type) {
            case Int8Array:
            case Uint8Array:
            case Uint8ClampedArray:
                sizeInBytes = ONE_BYTE_PER_VALUE;
                break;
            case Int16Array:
            case Uint16Array:
                sizeInBytes = TWO_BYTES_PER_VALUE;
                break;
            case Int32Array:
            case Uint32Array:
            case Float32Array:
                sizeInBytes = FOUR_BYTES_PER_VALUE;
                break;
            case Float64Array:
            case BigInt64Array:
            case BigUint64Array:
                sizeInBytes = EIGHT_BYTES_PER_VALUE;
                break;
            default:
                type = V8ValueReferenceType.Invalid;
                sizeInBytes = ZERO_BYTE_PER_VALUE;
                break;
        }
        this.type = type;
    }

    /**
     * To byte array.
     *
     * @return the byte array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public byte[] toBytes() throws JavetException {
        if (getType() == V8ValueReferenceType.Int8Array ||
                getType() == V8ValueReferenceType.Uint8Array ||
                getType() == V8ValueReferenceType.Uint8ClampedArray) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toBytes();
            }
        }
        return null;
    }

    /**
     * To double array.
     *
     * @return the double array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public double[] toDoubles() throws JavetException {
        if (getType() == V8ValueReferenceType.Float64Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toDoubles();
            }
        }
        return null;
    }

    /**
     * To float array.
     *
     * @return the float array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public float[] toFloats() throws JavetException {
        if (getType() == V8ValueReferenceType.Float32Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toFloats();
            }
        }
        return null;
    }

    /**
     * To int array.
     *
     * @return the int array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public int[] toIntegers() throws JavetException {
        if (getType() == V8ValueReferenceType.Int32Array ||
                getType() == V8ValueReferenceType.Uint32Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toIntegers();
            }
        }
        return null;
    }

    /**
     * To long array.
     *
     * @return the long array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public long[] toLongs() throws JavetException {
        if (getType() == V8ValueReferenceType.BigInt64Array ||
                getType() == V8ValueReferenceType.BigUint64Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toLongs();
            }
        }
        return null;
    }

    /**
     * To short array.
     *
     * @return the short array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public short[] toShorts() throws JavetException {
        if (getType() == V8ValueReferenceType.Int16Array ||
                getType() == V8ValueReferenceType.Uint16Array) {
            try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                return v8ValueArrayBuffer.toShorts();
            }
        }
        return null;
    }
}
