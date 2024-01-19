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

package com.caoccao.javet.interop;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;

/**
 * The interface V8 creatable.
 *
 * @since 0.7.0
 */
public interface IV8Creatable {
    /**
     * Create a V8 synthetic module.
     *
     * @param moduleName     the module name
     * @param iV8ValueObject the V8 value object to be exported
     * @return the V8 module
     * @throws JavetException the javet exception
     * @since 3.0.1
     */
    @CheckReturnValue
    V8Module createV8Module(String moduleName, IV8ValueObject iV8ValueObject) throws JavetException;

    /**
     * Create V8 value array.
     *
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    V8ValueArray createV8ValueArray() throws JavetException;

    /**
     * Create V8 value array buffer from a given length.
     *
     * @param length the length
     * @return the V8 value array buffer
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException;

    /**
     * Create V8 value array buffer from a native byte buffer.
     *
     * @param byteBuffer the byte buffer
     * @return the V8 value array buffer
     * @throws JavetException the javet exception
     * @since 1.1.1
     */
    @CheckReturnValue
    V8ValueArrayBuffer createV8ValueArrayBuffer(ByteBuffer byteBuffer) throws JavetException;

    /**
     * Create V8 value big integer.
     *
     * @param bigInteger the big integer
     * @return the V8 value big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    V8ValueBigInteger createV8ValueBigInteger(BigInteger bigInteger) throws JavetException;

    /**
     * Create V8 value big integer.
     *
     * @param bigIntegerValue the big integer value
     * @return the V8 value big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    V8ValueBigInteger createV8ValueBigInteger(String bigIntegerValue) throws JavetException;

    /**
     * Create V8 value boolean.
     *
     * @param booleanValue the boolean value
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.7.4
     */
    V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException;

    /**
     * Create V8 value data view.
     *
     * @param v8ValueArrayBuffer the V8 value array buffer
     * @return the V8 value data view
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException;

    /**
     * Create V8 value double.
     *
     * @param doubleValue the double value
     * @return the V8 value double
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    V8ValueDouble createV8ValueDouble(double doubleValue) throws JavetException;

    /**
     * Create V8 value function.
     *
     * @param javetCallbackContext the javet callback context
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 0.7.1
     */
    @CheckReturnValue
    V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException;

    /**
     * Create V8 value function.
     *
     * @param codeString the code string
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    @CheckReturnValue
    V8ValueFunction createV8ValueFunction(String codeString) throws JavetException;

    /**
     * Create V8 value integer.
     *
     * @param integerValue the integer value
     * @return the V8 value integer
     * @throws JavetException the javet exception
     * @since 0.7.4
     */
    V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException;

    /**
     * Create V8 value long.
     *
     * @param longValue the long value
     * @return the V8 value long
     * @throws JavetException the javet exception
     * @since 0.7.4
     */
    V8ValueLong createV8ValueLong(long longValue) throws JavetException;

    /**
     * Create V8 value map.
     *
     * @return the V8 value map
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    V8ValueMap createV8ValueMap() throws JavetException;

    /**
     * Create V8 value null.
     *
     * @return the V8 value null
     * @since 0.7.2
     */
    V8ValueNull createV8ValueNull();

    /**
     * Create V8 value object.
     *
     * @return the V8 value object
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    V8ValueObject createV8ValueObject() throws JavetException;

    /**
     * Create V8 value promise.
     *
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    @CheckReturnValue
    V8ValuePromise createV8ValuePromise() throws JavetException;

    /**
     * Create V8 value proxy.
     *
     * @return the V8 value proxy
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    @CheckReturnValue
    default V8ValueProxy createV8ValueProxy() throws JavetException {
        return createV8ValueProxy(null);
    }

    /**
     * Create V8 value proxy.
     *
     * @param v8ValueObject the V8 value object
     * @return the V8 value proxy
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    @CheckReturnValue
    V8ValueProxy createV8ValueProxy(V8ValueObject v8ValueObject) throws JavetException;

    /**
     * Create V8 value set.
     *
     * @return the V8 value set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    V8ValueSet createV8ValueSet() throws JavetException;

    /**
     * Create V8 value string.
     *
     * @param str the str
     * @return the V8 value string
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    V8ValueString createV8ValueString(String str) throws JavetException;

    /**
     * Create V8 value symbol.
     *
     * @param description the description
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    default V8ValueSymbol createV8ValueSymbol(String description) throws JavetException {
        return createV8ValueSymbol(description, false);
    }

    /**
     * Create V8 value symbol.
     *
     * @param description the description
     * @param global      the global
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    V8ValueSymbol createV8ValueSymbol(String description, boolean global) throws JavetException;

    /**
     * Create V8 value typed array.
     *
     * @param type   the type
     * @param length the length
     * @return the V8 value typed array
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    @CheckReturnValue
    V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException;

    /**
     * Create V8 value undefined.
     *
     * @return the V8 value undefined
     * @since 0.7.2
     */
    V8ValueUndefined createV8ValueUndefined();

    /**
     * Create V8 value zoned date time.
     *
     * @param jsTimestamp the js timestamp
     * @return the V8 value zoned date time
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    V8ValueZonedDateTime createV8ValueZonedDateTime(long jsTimestamp) throws JavetException;

    /**
     * Create V8 value zoned date time.
     *
     * @param zonedDateTime the zoned date time
     * @return the V8 value zoned date time
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    V8ValueZonedDateTime createV8ValueZonedDateTime(ZonedDateTime zonedDateTime) throws JavetException;
}
