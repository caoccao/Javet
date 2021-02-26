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

/**
 * The type V8 value typed array.
 * The typical way of manipulating the typed array is as following.
 * 1. Get array buffer and apply try-with-resource.
 * 2. Get native ByteBuffer.
 * 3. Set order of the native ByteBuffer to little endian.
 * 4. Get typed buffer by type.
 * 5. Read from or write to the typed buffer.
 * E.g:
 * <code>
 * try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueTypedArray.getArrayBuffer()) {
 * LongBuffer longBuffer = v8ValueArrayBuffer.getByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer();
 * // ...
 * }
 * </code>
 */
public class V8ValueTypedArray extends V8ValueObject implements IV8ValueTypedArray {

    /**
     * The constant NAME_INT_8_ARRAY.
     */
    public static final String NAME_INT_8_ARRAY = "Int8Array";
    /**
     * The constant NAME_UINT_8_ARRAY.
     */
    public static final String NAME_UINT_8_ARRAY = "Uint8Array";
    /**
     * The constant NAME_UINT_8_CLAMPED_ARRAY.
     */
    public static final String NAME_UINT_8_CLAMPED_ARRAY = "Uint8ClampedArray";
    /**
     * The constant NAME_INT_16_ARRAY.
     */
    public static final String NAME_INT_16_ARRAY = "Int16Array";
    /**
     * The constant NAME_UINT_16_ARRAY.
     */
    public static final String NAME_UINT_16_ARRAY = "Uint16Array";
    /**
     * The constant NAME_INT_32_ARRAY.
     */
    public static final String NAME_INT_32_ARRAY = "Int32Array";
    /**
     * The constant NAME_UINT_32_ARRAY.
     */
    public static final String NAME_UINT_32_ARRAY = "Uint32Array";
    /**
     * The constant NAME_FLOAT_32_ARRAY.
     */
    public static final String NAME_FLOAT_32_ARRAY = "Float32Array";
    /**
     * The constant NAME_FLOAT_64_ARRAY.
     */
    public static final String NAME_FLOAT_64_ARRAY = "Float64Array";
    /**
     * The constant NAME_BIG_INT_64_ARRAY.
     */
    public static final String NAME_BIG_INT_64_ARRAY = "BigInt64Array";
    /**
     * The constant NAME_BIG_UINT_64_ARRAY.
     */
    public static final String NAME_BIG_UINT_64_ARRAY = "BigUint64Array";

    /**
     * The constant PROPERTY_BYTE_LENGTH.
     */
    public static final String PROPERTY_BYTE_LENGTH = "byteLength";
    /**
     * The constant PROPERTY_BUFFER.
     */
    public static final String PROPERTY_BUFFER = "buffer";
    /**
     * The constant PROPERTY_BYTE_OFFSET.
     */
    public static final String PROPERTY_BYTE_OFFSET = "byteOffset";
    public static final String PROPERTY_NAME = "Name";

    /**
     * The Size in bytes.
     */
    protected int sizeInBytes;
    /**
     * The Type.
     */
    protected int type;

    /**
     * Instantiates a new V8 value typed array.
     *
     * @param handle the handle
     * @param type   the type
     */
    V8ValueTypedArray(long handle, int type) {
        super(handle);
        setType(type);
    }

    /**
     * Gets JS constructor name by type.
     *
     * @param type the type
     * @return the name
     */
    public static String getName(int type) {
        switch (type) {
            case V8ValueReferenceType.Int8Array:
                return NAME_INT_8_ARRAY;
            case V8ValueReferenceType.Uint8Array:
                return NAME_UINT_8_ARRAY;
            case V8ValueReferenceType.Uint8ClampedArray:
                return NAME_UINT_8_CLAMPED_ARRAY;
            case V8ValueReferenceType.Int16Array:
                return NAME_INT_16_ARRAY;
            case V8ValueReferenceType.Uint16Array:
                return NAME_UINT_16_ARRAY;
            case V8ValueReferenceType.Int32Array:
                return NAME_INT_32_ARRAY;
            case V8ValueReferenceType.Uint32Array:
                return NAME_UINT_32_ARRAY;
            case V8ValueReferenceType.Float32Array:
                return NAME_FLOAT_32_ARRAY;
            case V8ValueReferenceType.Float64Array:
                return NAME_FLOAT_64_ARRAY;
            case V8ValueReferenceType.BigInt64Array:
                return NAME_BIG_INT_64_ARRAY;
            case V8ValueReferenceType.BigUint64Array:
                return NAME_BIG_UINT_64_ARRAY;
            default:
                return null;
        }
    }

    @Override
    public V8ValueArrayBuffer getArrayBuffer() throws JavetException {
        checkV8Runtime();
        return get(PROPERTY_BUFFER);
    }

    @Override
    public int getByteLength() throws JavetException {
        checkV8Runtime();
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    @Override
    public int getByteOffset() throws JavetException {
        checkV8Runtime();
        return getInteger(PROPERTY_BYTE_OFFSET);
    }

    @Override
    public int getLength() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }

    @Override
    public int getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    protected void setType(int type) {
        switch (type) {
            case V8ValueReferenceType.Int8Array:
            case V8ValueReferenceType.Uint8Array:
            case V8ValueReferenceType.Uint8ClampedArray:
                sizeInBytes = 1;
                break;
            case V8ValueReferenceType.Int16Array:
            case V8ValueReferenceType.Uint16Array:
                sizeInBytes = 2;
                break;
            case V8ValueReferenceType.Int32Array:
            case V8ValueReferenceType.Uint32Array:
            case V8ValueReferenceType.Float32Array:
                sizeInBytes = 4;
                break;
            case V8ValueReferenceType.Float64Array:
            case V8ValueReferenceType.BigInt64Array:
            case V8ValueReferenceType.BigUint64Array:
                sizeInBytes = 8;
                break;
            default:
                type = V8ValueReferenceType.Invalid;
                sizeInBytes = 0;
                break;
        }
        this.type = type;
    }

    @Override
    public boolean isValid() {
        return type != V8ValueReferenceType.Invalid;
    }
}
