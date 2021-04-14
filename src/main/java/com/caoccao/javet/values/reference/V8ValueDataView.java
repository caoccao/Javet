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
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueDouble;

public class V8ValueDataView extends V8ValueObject {
    /**
     * The constant PROPERTY_BYTE_LENGTH.
     */
    protected static final String PROPERTY_BYTE_LENGTH = "byteLength";
    /**
     * The constant PROPERTY_BUFFER.
     */
    protected static final String PROPERTY_BUFFER = "buffer";
    /**
     * The constant PROPERTY_BYTE_OFFSET.
     */
    protected static final String PROPERTY_BYTE_OFFSET = "byteOffset";
    protected static final String FUNCTION_GET_BIG_INT_64 = "getBigInt64";
    protected static final String FUNCTION_GET_FLOAT_32 = "getFloat32";
    protected static final String FUNCTION_GET_FLOAT_64 = "getFloat64";
    protected static final String FUNCTION_GET_INT_8 = "getInt8";
    protected static final String FUNCTION_GET_INT_16 = "getInt16";
    protected static final String FUNCTION_GET_INT_32 = "getInt32";
    protected static final String FUNCTION_SET_BIG_INT_64 = "setBigInt64";
    protected static final String FUNCTION_SET_FLOAT_32 = "setFloat32";
    protected static final String FUNCTION_SET_FLOAT_64 = "setFloat64";
    protected static final String FUNCTION_SET_INT_8 = "setInt8";
    protected static final String FUNCTION_SET_INT_16 = "setInt16";
    protected static final String FUNCTION_SET_INT_32 = "setInt32";

    V8ValueDataView(long handle) {
        super(handle);
    }

    public long getBigInt64(int byteOffset) throws JavetException {
        return getBigInt64(byteOffset, true);
    }

    public long getBigInt64(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeLong(
                FUNCTION_GET_BIG_INT_64,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueBoolean(littleEndian)).longValue();
    }

    public V8ValueArrayBuffer getBuffer() throws JavetException {
        return get(PROPERTY_BUFFER);
    }

    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    public int getByteOffset() throws JavetException {
        return getInteger(PROPERTY_BYTE_OFFSET);
    }

    public float getFloat32(int byteOffset) throws JavetException {
        return getFloat32(byteOffset, true);
    }

    public float getFloat32(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeDouble(
                FUNCTION_GET_FLOAT_32,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueBoolean(littleEndian)).floatValue();
    }

    public double getFloat64(int byteOffset) throws JavetException {
        return getFloat64(byteOffset, true);
    }

    public double getFloat64(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeDouble(
                FUNCTION_GET_FLOAT_64,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueBoolean(littleEndian)).doubleValue();
    }

    public short getInt16(int byteOffset) throws JavetException {
        return getInt16(byteOffset, true);
    }

    public short getInt16(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeInteger(
                FUNCTION_GET_INT_16,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueBoolean(littleEndian)).shortValue();
    }

    public int getInt32(int byteOffset) throws JavetException {
        return getInt32(byteOffset, true);
    }

    public int getInt32(int byteOffset, boolean littleEndian) throws JavetException {
        return invokeInteger(
                FUNCTION_GET_INT_32,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueBoolean(littleEndian)).intValue();
    }

    public byte getInt8(int byteOffset) throws JavetException {
        return invokeInteger(
                FUNCTION_GET_INT_8,
                v8Runtime.createV8ValueInteger(byteOffset)).byteValue();
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.DataView;
    }

    public void setBigInt64(int byteOffset, long value) throws JavetException {
        setBigInt64(byteOffset, value, true);
    }

    public void setBigInt64(int byteOffset, long value, boolean littleEndian) throws JavetException {
        invokeVoid(
                FUNCTION_SET_BIG_INT_64,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueLong(value),
                v8Runtime.createV8ValueBoolean(littleEndian));
    }

    public void setFloat32(int byteOffset, float value) throws JavetException {
        setFloat32(byteOffset, value, true);
    }

    public void setFloat32(int byteOffset, float value, boolean littleEndian) throws JavetException {
        invokeVoid(
                FUNCTION_SET_FLOAT_32,
                v8Runtime.createV8ValueInteger(byteOffset),
                new V8ValueDouble(value),
                v8Runtime.createV8ValueBoolean(littleEndian));
    }

    public void setFloat64(int byteOffset, double value) throws JavetException {
        setFloat64(byteOffset, value, true);
    }

    public void setFloat64(int byteOffset, double value, boolean littleEndian) throws JavetException {
        invokeVoid(
                FUNCTION_SET_FLOAT_64,
                v8Runtime.createV8ValueInteger(byteOffset),
                new V8ValueDouble(value),
                v8Runtime.createV8ValueBoolean(littleEndian));
    }

    public void setInt32(int byteOffset, int value) throws JavetException {
        setInt32(byteOffset, value, true);
    }

    public void setInt32(int byteOffset, int value, boolean littleEndian) throws JavetException {
        invokeVoid(
                FUNCTION_SET_INT_32,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueInteger(value),
                v8Runtime.createV8ValueBoolean(littleEndian));
    }

    public void setInt16(int byteOffset, short value) throws JavetException {
        setInt16(byteOffset, value, true);
    }

    public void setInt16(int byteOffset, short value, boolean littleEndian) throws JavetException {
        invokeVoid(
                FUNCTION_SET_INT_16,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueInteger(value),
                v8Runtime.createV8ValueBoolean(littleEndian));
    }

    public void setInt8(int byteOffset, byte value) throws JavetException {
        invokeVoid(
                FUNCTION_SET_INT_8,
                v8Runtime.createV8ValueInteger(byteOffset),
                v8Runtime.createV8ValueInteger(value));
    }
}
