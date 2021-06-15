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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetBiIndexedConsumer;
import com.caoccao.javet.interfaces.IJavetUniConsumer;
import com.caoccao.javet.interfaces.IJavetUniIndexedConsumer;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * The interface V8 value object.
 */
@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
    /**
     * Bind both functions via @V8Function and properties via @V8Property.
     *
     * @param callbackReceiver the callback receiver
     * @return the list of callback context
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    List<JavetCallbackContext> bind(Object callbackReceiver) throws JavetException;

    /**
     * Binds function by name and callback context.
     * <p>
     * It is for creating a Java code based function in V8.
     *
     * @param functionName         the function name
     * @param javetCallbackContext the javet callback context
     * @return true : function is bind, false: function is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    boolean bindFunction(String functionName, JavetCallbackContext javetCallbackContext) throws JavetException;

    /**
     * Binds function by name and string.
     * <p>
     * It is for creating a string based function in V8.
     * <p>
     * JS equivalent:
     * <code>
     * obj.func = function(arg1, arg2) { ... };
     * </code>
     *
     * @param functionName the function name
     * @param codeString   the code string
     * @return true : function is bind, false: function is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    boolean bindFunction(String functionName, String codeString) throws JavetException;

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @return true if the property is bind, false if the property is not bind
     * @throws JavetException the javet exception
     */
    default boolean bindProperty(
            String propertyName,
            JavetCallbackContext javetCallbackContextGetter) throws JavetException {
        return bindProperty(propertyName, javetCallbackContextGetter, null);
    }

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @param javetCallbackContextSetter the javet callback context setter
     * @return true if the property is bind, false if the property is not bind
     * @throws JavetException the javet exception
     */
    boolean bindProperty(
            String propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException;

    /**
     * Delete boolean.
     *
     * @param key the key
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean delete(Object key) throws JavetException;

    /**
     * Delete null boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default boolean deleteNull() throws JavetException {
        return delete(getV8Runtime().createV8ValueNull());
    }

    /**
     * Delete undefined boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default boolean deleteUndefined() throws JavetException {
        return delete(getV8Runtime().createV8ValueUndefined());
    }

    /**
     * For each.
     *
     * @param <Key>    the type of key
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the exception
     */
    <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer) throws JavetException, E;

    /**
     * For each.
     *
     * @param <Key>    the type of key
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the exception
     */
    <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer) throws JavetException, E;

    /**
     * For each.
     *
     * @param <Key>    the type of key
     * @param <Value>  the type of value
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the exception
     */
    <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer) throws JavetException, E;

    /**
     * For each.
     *
     * @param <Key>    the type of key
     * @param <Value>  the type of value
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the exception
     */
    <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer) throws JavetException, E;

    /**
     * Get t.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    <T extends V8Value> T get(Object key) throws JavetException;

    /**
     * Gets boolean.
     *
     * @param key the key
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default Boolean getBoolean(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets double.
     *
     * @param key the key
     * @return the double
     * @throws JavetException the javet exception
     */
    default Double getDouble(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets float.
     *
     * @param key the key
     * @return the float
     * @throws JavetException the javet exception
     */
    default Float getFloat(Object key) throws JavetException {
        Double result = getDouble(key);
        return result == null ? null : result.floatValue();
    }

    /**
     * Returns the identity hash for this object. The current implementation
     * uses an inline property on the object to store the identity hash.
     * <p>
     * The return value will never be 0. Also, it is not guaranteed to be
     * unique.
     *
     * @return the identity hash
     * @throws JavetException the javet exception
     */
    int getIdentityHash() throws JavetException;

    /**
     * Gets integer.
     *
     * @param key the key
     * @return the integer
     * @throws JavetException the javet exception
     */
    default Integer getInteger(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets long.
     *
     * @param key the key
     * @return the long
     * @throws JavetException the javet exception
     */
    default Long getLong(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets null.
     *
     * @param key the key
     * @return the null
     * @throws JavetException the javet exception
     */
    default V8ValueNull getNull(Object key) throws JavetException {
        return get(key);
    }

    /**
     * Gets object.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the object
     * @throws JavetException the javet exception
     */
    default <T> T getObject(Object key) throws JavetException {
        try {
            return getV8Runtime().toObject(get(key), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Gets own property names.
     *
     * @return the own property names
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    IV8ValueArray getOwnPropertyNames() throws JavetException;

    /**
     * Gets primitive.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @param key the key
     * @return the primitive
     * @throws JavetException the javet exception
     */
    default <R, T extends V8ValuePrimitive<R>> R getPrimitive(Object key) throws JavetException {
        try (V8Value v8Value = get(key)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets property.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    <T extends V8Value> T getProperty(Object key) throws JavetException;

    /**
     * Gets property boolean.
     *
     * @param key the key
     * @return the property boolean
     */
    default Boolean getPropertyBoolean(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property double.
     *
     * @param key the key
     * @return the property double
     */
    default Double getPropertyDouble(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property float.
     *
     * @param key the key
     * @return the property float
     */
    default Float getPropertyFloat(Object key) {
        Double result = getPropertyDouble(key);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets property integer.
     *
     * @param key the key
     * @return the property integer
     */
    default Integer getPropertyInteger(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property long.
     *
     * @param key the key
     * @return the property long
     */
    default Long getPropertyLong(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property names.
     *
     * @return the property names
     * @throws JavetException the javet exception
     */
    IV8ValueArray getPropertyNames() throws JavetException;

    /**
     * Gets property object.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property object
     * @throws JavetException the javet exception
     */
    default <T> T getPropertyObject(Object key) throws JavetException {
        try {
            return getV8Runtime().toObject(getProperty(key), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Gets property primitive.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @param key the key
     * @return the property primitive
     */
    default <R, T extends V8ValuePrimitive<R>> R getPropertyPrimitive(Object key) {
        try (V8Value v8Value = getProperty(key)) {
            return ((T) v8Value).getValue();
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets property string.
     *
     * @param key the key
     * @return the property string
     */
    default String getPropertyString(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property zoned date time.
     *
     * @param key the key
     * @return the property zoned date time
     */
    default ZonedDateTime getPropertyZonedDateTime(Object key) {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     * @throws JavetException the javet exception
     */
    default String getString(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets undefined.
     *
     * @param key the key
     * @return the undefined
     * @throws JavetException the javet exception
     */
    default V8ValueUndefined getUndefined(Object key) throws JavetException {
        return get(key);
    }

    /**
     * Gets zoned date time.
     *
     * @param key the key
     * @return the zoned date time
     * @throws JavetException the javet exception
     */
    default ZonedDateTime getZonedDateTime(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Has boolean.
     *
     * @param value the value
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean has(Object value) throws JavetException;

    /**
     * Has null boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default boolean hasNull() throws JavetException {
        return has(getV8Runtime().createV8ValueNull());
    }

    /**
     * Has own property boolean.
     *
     * @param key the key
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean hasOwnProperty(Object key) throws JavetException;

    /**
     * Has undefined boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default boolean hasUndefined() throws JavetException {
        return has(getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Invoke t.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    default <T extends V8Value> T invoke(String functionName, Object... objects) throws JavetException {
        return invokeExtended(functionName, true, objects);
    }

    /**
     * Invoke t.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param v8Values     the v 8 values
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    default <T extends V8Value> T invoke(String functionName, V8Value... v8Values) throws JavetException {
        return invokeExtended(functionName, true, v8Values);
    }

    /**
     * Invoke boolean boolean.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default Boolean invokeBoolean(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke double double.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the double
     * @throws JavetException the javet exception
     */
    default Double invokeDouble(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke extended and return V8 value which must be consumed,
     * otherwise memory leak may occur.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param returnResult the return result
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, Object... objects) throws JavetException;

    /**
     * Invoke extended and return V8 value which must be consumed,
     * otherwise memory leak may occur.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param returnResult the return result
     * @param v8Values     the v 8 values
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values) throws JavetException;

    /**
     * Invoke float float.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the float
     * @throws JavetException the javet exception
     */
    default Float invokeFloat(String functionName, Object... objects) throws JavetException {
        Double result = invokeDouble(functionName, objects);
        return result == null ? null : result.floatValue();
    }

    /**
     * Invoke integer integer.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the integer
     * @throws JavetException the javet exception
     */
    default Integer invokeInteger(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke long long.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the long
     * @throws JavetException the javet exception
     */
    default Long invokeLong(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke object t.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     */
    default <T> T invokeObject(String functionName, Object... objects) throws JavetException {
        try {
            return getV8Runtime().toObject(invokeExtended(functionName, true, objects), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Invoke primitive r.
     *
     * @param <R>          the type parameter
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the primitive value
     * @throws JavetException the javet exception
     */
    default <R, T extends V8ValuePrimitive<R>> R invokePrimitive(
            String functionName, Object... objects) throws JavetException {
        try (V8Value v8Value = invokeExtended(functionName, true, objects)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Invoke string string.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the string
     * @throws JavetException the javet exception
     */
    default String invokeString(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke void.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void invokeVoid(String functionName, Object... objects) throws JavetException {
        invokeExtended(functionName, false, objects);
    }

    /**
     * Invoke void.
     *
     * @param functionName the function name
     * @param v8Values     the v 8 values
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void invokeVoid(String functionName, V8Value... v8Values) throws JavetException {
        invokeExtended(functionName, false, v8Values);
    }

    /**
     * Set boolean.
     *
     * @param key   the key
     * @param value the value
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean set(Object key, Object value) throws JavetException;

    /**
     * Sets null.
     *
     * @param key the key
     * @return the null
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean setNull(Object key) throws JavetException {
        return set(key, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets property.
     *
     * @param key   the key
     * @param value the value
     * @return the property
     * @throws JavetException the javet exception
     */
    boolean setProperty(Object key, Object value) throws JavetException;

    /**
     * Sets property null.
     *
     * @param key the key
     * @return the property null
     * @throws JavetException the javet exception
     */
    default boolean setPropertyNull(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets property undefined.
     *
     * @param key the key
     * @return the property undefined
     * @throws JavetException the javet exception
     */
    default boolean setPropertyUndefined(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Sets undefined.
     *
     * @param key the key
     * @return the undefined
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean setUndefined(Object key) throws JavetException {
        return set(key, getV8Runtime().createV8ValueUndefined());
    }

    /**
     * To json string.
     * <p>
     * JS equivalent:
     * JSON.stringify(obj);
     *
     * @return the string
     */
    String toJsonString();

    /**
     * To proto string string.
     *
     * @return the string
     */
    String toProtoString();
}
