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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueInternalType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

/**
 * The interface V8 value function.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public interface IV8ValueFunction extends IV8ValueObject {
    /**
     * Call function by objects and return V8 value.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param objects  the objects
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    default <T extends V8Value> T call(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callExtended(receiver, true, objects);
    }

    /**
     * Call function by V8 values and return V8 value.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    default <T extends V8Value> T call(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callExtended(receiver, true, v8Values);
    }

    /**
     * Call function as constructor by objects.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException;

    /**
     * Call function as constructor by V8 values.
     *
     * @param <T>      the type parameter
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException;

    /**
     * Call function by objects and return boolean.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Boolean callBoolean(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    /**
     * Call function by objects and return double.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the double
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Double callDouble(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    /**
     * Call function by objects and return V8 value.
     *
     * @param <T>          the type parameter
     * @param receiver     the receiver
     * @param returnResult the return result
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException;

    /**
     * Call function by V8 values and return V8 value.
     *
     * @param <T>          the type parameter
     * @param receiver     the receiver
     * @param returnResult the return result
     * @param v8Values     the v 8 values
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException;


    /**
     * Call function by objects and return float.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the float
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Float callFloat(IV8ValueObject receiver, Object... objects) throws JavetException {
        Double result = callDouble(receiver, objects);
        return result == null ? null : result.floatValue();
    }

    /**
     * Call function by objects and return integer.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Integer callInteger(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    /**
     * Call function by objects and return long.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the long
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Long callLong(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    /**
     * Call function by objects and return object.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param objects  the objects
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default <T> T callObject(IV8ValueObject receiver, Object... objects) throws JavetException {
        try {
            return getV8Runtime().toObject(callExtended(receiver, true, objects), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call function by objects and return primitive object.
     *
     * @param <R>      the type parameter
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param objects  the objects
     * @return the primitive object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default <R, T extends V8ValuePrimitive<R>> R callPrimitive(
            IV8ValueObject receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call function by objects and return string.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default String callString(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    /**
     * Call function by objects without return.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void callVoid(IV8ValueObject receiver, Object... objects) throws JavetException {
        callExtended(receiver, false, objects);
    }

    /**
     * Call function by V8 values without return.
     *
     * @param receiver the receiver
     * @param v8Values the V8 values
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void callVoid(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        callExtended(receiver, false, v8Values);
    }

    /**
     * Gets internal properties.
     *
     * @return the internal properties
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    @CheckReturnValue
    IV8ValueArray getInternalProperties() throws JavetException;

    /**
     * Gets JS function type.
     *
     * @return the JS function type
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    JSFunctionType getJSFunctionType() throws JavetException;

    /**
     * Gets JS scope type.
     *
     * @return the JS scope type
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    JSScopeType getJSScopeType() throws JavetException;

    /**
     * Gets source code.
     *
     * @return the source code
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    String getSourceCode() throws JavetException;

    /**
     * Is async function.
     *
     * @return true: yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    default boolean isAsyncFunction() throws JavetException {
        return hasInternalType(V8ValueInternalType.AsyncFunction);
    }

    /**
     * Is generator function.
     *
     * @return true: yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    default boolean isGeneratorFunction() throws JavetException {
        return hasInternalType(V8ValueInternalType.GeneratorFunction);
    }

    /**
     * Sets source code.
     * <p>
     * Note: The source is shared among all function objects.
     * So the caller is responsible for restoring the original source code,
     * otherwise the next function call will likely fail because the source code
     * of the next function call is incorrect.
     *
     * @param sourceCode the source code
     * @return the source code
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    boolean setSourceCode(String sourceCode) throws JavetException;
}
