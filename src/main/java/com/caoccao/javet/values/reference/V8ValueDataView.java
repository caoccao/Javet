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

/**
 * The type V8 value DataView, representing a JavaScript DataView object.
 */
public class V8ValueDataView extends V8ValueObject {
    /**
     * The constant FUNCTION_GET_BIG_INT_64.
     */
    protected static final String FUNCTION_GET_BIG_INT_64 = "getBigInt64";
    /**
     * The constant FUNCTION_GET_FLOAT_32.
     */
    protected static final String FUNCTION_GET_FLOAT_32 = "getFloat32";
    /**
     * The constant FUNCTION_GET_FLOAT_64.
     */
    protected static final String FUNCTION_GET_FLOAT_64 = "getFloat64";
    /**
     * The constant FUNCTION_GET_INT_16.
     */
    protected static final String FUNCTION_GET_INT_16 = "getInt16";
    /**
     * The constant FUNCTION_GET_INT_32.
     */
    protected static final String FUNCTION_GET_INT_32 = "getInt32";
    /**
     * The constant FUNCTION_GET_INT_8.
     */
    protected static final String FUNCTION_GET_INT_8 = "getInt8";
    /**
     * The constant FUNCTION_SET_BIG_INT_64.
     */
    protected static final String FUNCTION_SET_BIG_INT_64 = "setBigInt64";
    /**
     * The constant FUNCTION_SET_FLOAT_32.
     */
    protected static final String FUNCTION_SET_FLOAT_32 = "setFloat32";
    /**
     * The constant FUNCTION_SET_FLOAT_64.
     */
    protected static final String FUNCTION_SET_FLOAT_64 = "setFloat64";
    /**
     * The constant FUNCTION_SET_INT_16.
     */
    protected static final String FUNCTION_SET_INT_16 = "setInt16";
    /**
     * The constant FUNCTION_SET_INT_32.
     */
    protected static final String FUNCTION_SET_INT_32 = "setInt32";
    /**
     * The constant FUNCTION_SET_INT_8.
     */
    protected static final String FUNCTION_SET_INT_8 = "setInt8";
    /**
     * The constant PROPERTY_BUFFER.
     */
    protected static final String PROPERTY_BUFFER = "buffer";
    /**
     * The constant PROPERTY_BYTE_LENGTH.
     */
    protected static final String PROPERTY_BYTE_LENGTH = "byteLength";
    /**
     * The constant PROPERTY_BYTE_OFFSET.
     */
    protected static final String PROPERTY_BYTE_OFFSET = "byteOffset";

    V8ValueDataView(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Gets a signed 64-bit integer (BigInt) at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to read from
     * @return the BigInt64 value
     * @throws JavetException the javet exception
     */
    public long getBigInt64(int byteOffset) throws JavetException {
        return getBigInt64(byteOffset, true);
    }

    /**
     * Gets a signed 64-bit integer (BigInt) at the specified byte offset.
     *
     * @param byteOffset   the byte offset to read from
     * @param littleEndian whether the value is stored in little-endian format
     * @return the BigInt64 value
     * @throws JavetException the javet exception
     */
    public long getBigInt64(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeLong(FUNCTION_GET_BIG_INT_64, byteOffset, littleEndian);
    }

    /**
     * Gets the underlying ArrayBuffer referenced by this DataView.
     *
     * @return the V8 value array buffer
     * @throws JavetException the javet exception
     */
    public V8ValueArrayBuffer getBuffer() throws JavetException {
        return get(PROPERTY_BUFFER);
    }

    /**
     * Gets the byte length of this DataView.
     *
     * @return the byte length
     * @throws JavetException the javet exception
     */
    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    /**
     * Gets the byte offset of this DataView from the start of its ArrayBuffer.
     *
     * @return the byte offset
     * @throws JavetException the javet exception
     */
    public int getByteOffset() throws JavetException {
        return getInteger(PROPERTY_BYTE_OFFSET);
    }

    /**
     * Gets a 32-bit float at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to read from
     * @return the float value
     * @throws JavetException the javet exception
     */
    public float getFloat32(int byteOffset) throws JavetException {
        return getFloat32(byteOffset, true);
    }

    /**
     * Gets a 32-bit float at the specified byte offset.
     *
     * @param byteOffset   the byte offset to read from
     * @param littleEndian whether the value is stored in little-endian format
     * @return the float value
     * @throws JavetException the javet exception
     */
    public float getFloat32(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeDouble(FUNCTION_GET_FLOAT_32, byteOffset, littleEndian).floatValue();
    }

    /**
     * Gets a 64-bit float at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to read from
     * @return the double value
     * @throws JavetException the javet exception
     */
    public double getFloat64(int byteOffset) throws JavetException {
        return getFloat64(byteOffset, true);
    }

    /**
     * Gets a 64-bit float at the specified byte offset.
     *
     * @param byteOffset   the byte offset to read from
     * @param littleEndian whether the value is stored in little-endian format
     * @return the double value
     * @throws JavetException the javet exception
     */
    public double getFloat64(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeDouble(FUNCTION_GET_FLOAT_64, byteOffset, littleEndian);
    }

    /**
     * Gets a signed 16-bit integer at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to read from
     * @return the short value
     * @throws JavetException the javet exception
     */
    public short getInt16(int byteOffset) throws JavetException {
        return getInt16(byteOffset, true);
    }

    /**
     * Gets a signed 16-bit integer at the specified byte offset.
     *
     * @param byteOffset   the byte offset to read from
     * @param littleEndian whether the value is stored in little-endian format
     * @return the short value
     * @throws JavetException the javet exception
     */
    public short getInt16(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeInteger(FUNCTION_GET_INT_16, byteOffset, littleEndian).shortValue();
    }

    /**
     * Gets a signed 32-bit integer at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to read from
     * @return the int value
     * @throws JavetException the javet exception
     */
    public int getInt32(int byteOffset) throws JavetException {
        return getInt32(byteOffset, true);
    }

    /**
     * Gets a signed 32-bit integer at the specified byte offset.
     *
     * @param byteOffset   the byte offset to read from
     * @param littleEndian whether the value is stored in little-endian format
     * @return the int value
     * @throws JavetException the javet exception
     */
    public int getInt32(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeInteger(FUNCTION_GET_INT_32, byteOffset, littleEndian);
    }

    /**
     * Gets a signed 8-bit integer at the specified byte offset.
     *
     * @param byteOffset the byte offset to read from
     * @return the byte value
     * @throws JavetException the javet exception
     */
    public byte getInt8(int byteOffset) throws JavetException {
        return invokeInteger(FUNCTION_GET_INT_8, byteOffset).byteValue();
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.DataView;
    }

    /**
     * Sets a signed 64-bit integer (BigInt) at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the BigInt64 value to set
     * @throws JavetException the javet exception
     */
    public void setBigInt64(int byteOffset, long value) throws JavetException {
        setBigInt64(byteOffset, value, true);
    }

    /**
     * Sets a signed 64-bit integer (BigInt) at the specified byte offset.
     *
     * @param byteOffset   the byte offset to write to
     * @param value        the BigInt64 value to set
     * @param littleEndian whether to store the value in little-endian format
     * @throws JavetException the javet exception
     */
    public void setBigInt64(int byteOffset, long value, boolean littleEndian) throws JavetException {
        invokeVoid(FUNCTION_SET_BIG_INT_64, byteOffset, value, littleEndian);
    }

    /**
     * Sets a 32-bit float at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the float value to set
     * @throws JavetException the javet exception
     */
    public void setFloat32(int byteOffset, float value) throws JavetException {
        setFloat32(byteOffset, value, true);
    }

    /**
     * Sets a 32-bit float at the specified byte offset.
     *
     * @param byteOffset   the byte offset to write to
     * @param value        the float value to set
     * @param littleEndian whether to store the value in little-endian format
     * @throws JavetException the javet exception
     */
    public void setFloat32(int byteOffset, float value, boolean littleEndian) throws JavetException {
        invokeVoid(FUNCTION_SET_FLOAT_32, byteOffset, value, littleEndian);
    }

    /**
     * Sets a 64-bit float at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the double value to set
     * @throws JavetException the javet exception
     */
    public void setFloat64(int byteOffset, double value) throws JavetException {
        setFloat64(byteOffset, value, true);
    }

    /**
     * Sets a 64-bit float at the specified byte offset.
     *
     * @param byteOffset   the byte offset to write to
     * @param value        the double value to set
     * @param littleEndian whether to store the value in little-endian format
     * @throws JavetException the javet exception
     */
    public void setFloat64(int byteOffset, double value, boolean littleEndian) throws JavetException {
        invokeVoid(FUNCTION_SET_FLOAT_64, byteOffset, value, littleEndian);
    }

    /**
     * Sets a signed 16-bit integer at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the short value to set
     * @throws JavetException the javet exception
     */
    public void setInt16(int byteOffset, short value) throws JavetException {
        setInt16(byteOffset, value, true);
    }

    /**
     * Sets a signed 16-bit integer at the specified byte offset.
     *
     * @param byteOffset   the byte offset to write to
     * @param value        the short value to set
     * @param littleEndian whether to store the value in little-endian format
     * @throws JavetException the javet exception
     */
    public void setInt16(int byteOffset, short value, boolean littleEndian) throws JavetException {
        invokeVoid(FUNCTION_SET_INT_16, byteOffset, value, littleEndian);
    }

    /**
     * Sets a signed 32-bit integer at the specified byte offset using little-endian byte order.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the int value to set
     * @throws JavetException the javet exception
     */
    public void setInt32(int byteOffset, int value) throws JavetException {
        setInt32(byteOffset, value, true);
    }

    /**
     * Sets a signed 32-bit integer at the specified byte offset.
     *
     * @param byteOffset   the byte offset to write to
     * @param value        the int value to set
     * @param littleEndian whether to store the value in little-endian format
     * @throws JavetException the javet exception
     */
    public void setInt32(int byteOffset, int value, boolean littleEndian) throws JavetException {
        invokeVoid(FUNCTION_SET_INT_32, byteOffset, value, littleEndian);
    }

    /**
     * Sets a signed 8-bit integer at the specified byte offset.
     *
     * @param byteOffset the byte offset to write to
     * @param value      the byte value to set
     * @throws JavetException the javet exception
     */
    public void setInt8(int byteOffset, byte value) throws JavetException {
        invokeVoid(FUNCTION_SET_INT_8, byteOffset, value);
    }
}
