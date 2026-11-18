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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualValue;

import java.util.Objects;

/**
 * The type V8 value typed array.
 * <p>
 * The {@code from*()} and {@code to*()} methods are the automatic data extraction with the default rules,
 * which are also applied by the object converter. They copy the whole array buffer of the typed array,
 * regardless of the byte offset and the byte length of the typed array.
 * The {@code from*()} methods return false if the length of the given array
 * doesn't match the array buffer.
 * <p>
 * E.g.:
 * <pre>
 * try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getBuffer()) {
 *     long[] longs = v8ValueArrayBuffer.toLongs();
 *     // ...
 *     v8ValueArrayBuffer.fromLongs(longs);
 * }
 * </pre>
 * <p>
 * Other needs, e.g. a typed array that only covers a part of its array buffer
 * (created by {@code subarray()} or a small Node.js {@code Buffer} in a shared pool),
 * go to the manual data extraction via the native ByteBuffer.
 * <ol>
 * <li>Get array buffer and apply try-with-resource.</li>
 * <li>Duplicate the native ByteBuffer, which covers the whole array buffer.</li>
 * <li>Set the position and the limit of the duplicated ByteBuffer by the byte offset and the byte length.</li>
 * <li>Slice the duplicated ByteBuffer and set its order to native order.</li>
 * <li>Get typed buffer by type.</li>
 * <li>Read from or write to the typed buffer.</li>
 * </ol>
 * <p>
 * E.g:
 * <pre>
 * try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getBuffer()) {
 *     ByteBuffer byteBuffer = v8ValueArrayBuffer.getByteBuffer().duplicate();
 *     byteBuffer.position(v8ValueTypedArray.getByteOffset());
 *     byteBuffer.limit(v8ValueTypedArray.getByteOffset() + v8ValueTypedArray.getByteLength());
 *     LongBuffer longBuffer = byteBuffer.slice().order(ByteOrder.nativeOrder()).asLongBuffer();
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
    protected V8ValueType type;

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
        setType(V8ValueType.parse(type));
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
        switch (getType()) {
            case Int8Array:
            case Uint8Array:
            case Uint8ClampedArray:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.fromBytes(bytes);
                }
            default:
                return false;
        }
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
        if (getType() == V8ValueType.Float64Array) {
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
        if (getType() == V8ValueType.Float32Array) {
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
        switch (getType()) {
            case Int32Array:
            case Uint32Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.fromIntegers(integers);
                }
            default:
                return false;
        }
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
        switch (getType()) {
            case BigInt64Array:
            case BigUint64Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.fromLongs(longs);
                }
            default:
                return false;
        }
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
        switch (getType()) {
            case Int16Array:
            case Uint16Array:
            case Float16Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.fromShorts(shorts);
                }
            default:
                return false;
        }
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
    public V8ValueType getType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return type != V8ValueType.Invalid;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @since 0.9.1
     */
    protected void setType(V8ValueType type) {
        switch (type) {
            case Int8Array:
            case Uint8Array:
            case Uint8ClampedArray:
                sizeInBytes = ONE_BYTE_PER_VALUE;
                break;
            case Int16Array:
            case Uint16Array:
            case Float16Array:
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
                type = V8ValueType.Invalid;
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
        switch (getType()) {
            case Int8Array:
            case Uint8Array:
            case Uint8ClampedArray:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.toBytes();
                }
            default:
                return null;
        }
    }

    /**
     * To double array.
     *
     * @return the double array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public double[] toDoubles() throws JavetException {
        if (getType() == V8ValueType.Float64Array) {
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
        if (getType() == V8ValueType.Float32Array) {
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
        switch (getType()) {
            case Int32Array:
            case Uint32Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.toIntegers();
                }
            default:
                return null;
        }
    }

    /**
     * To long array.
     *
     * @return the long array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public long[] toLongs() throws JavetException {
        switch (getType()) {
            case BigInt64Array:
            case BigUint64Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.toLongs();
                }
            default:
                return null;
        }
    }

    /**
     * To short array.
     *
     * @return the short array
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    public short[] toShorts() throws JavetException {
        switch (getType()) {
            case Int16Array:
            case Uint16Array:
            case Float16Array:
                try (V8ValueArrayBuffer v8ValueArrayBuffer = getBuffer()) {
                    return v8ValueArrayBuffer.toShorts();
                }
            default:
                return null;
        }
    }
}
