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
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The interface V8 value object.
 *
 * @since 0.7.0
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
    default boolean bindFunction(String functionName, String codeString) throws JavetException {
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().createV8ValueFunction(codeString)) {
            return set(functionName, v8ValueFunction);
        }
    }

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @return true if the property is bind, false if the property is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
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
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean bindProperty(
            String propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        Objects.requireNonNull(propertyName);
        return bindProperty(
                getV8Runtime().createV8ValueString(propertyName),
                javetCallbackContextGetter,
                javetCallbackContextSetter);
    }

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean bindProperty(
            V8ValueString propertyName,
            JavetCallbackContext javetCallbackContextGetter) throws JavetException {
        return bindProperty(propertyName, javetCallbackContextGetter, null);
    }

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean bindProperty(
            V8ValueSymbol propertyName,
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
     * @since 0.8.9
     */
    boolean bindProperty(
            V8ValueString propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException;

    /**
     * Bind property.
     *
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @param javetCallbackContextSetter the javet callback context setter
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    boolean bindProperty(
            V8ValueSymbol propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException;

    /**
     * Delete boolean.
     *
     * @param key the key
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean delete(Object key) throws JavetException;

    /**
     * Delete null boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default boolean deleteNull() throws JavetException {
        return delete(getV8Runtime().createV8ValueNull());
    }

    /**
     * Delete private property boolean.
     *
     * @param propertyName the property name
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean deletePrivateProperty(String propertyName) throws JavetException;

    /**
     * Delete undefined boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @since 0.8.10
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
     * @since 0.8.10
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
     * @since 0.8.9
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
     * @since 0.8.10
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
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T get(Object key) throws JavetException;

    /**
     * Gets boolean.
     *
     * @param key the key
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.8.0
     */
    int getIdentityHash() throws JavetException;

    /**
     * Gets integer.
     *
     * @param key the key
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
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
     * Gets private property.
     *
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    @CheckReturnValue
    <T extends V8Value> T getPrivateProperty(String propertyName) throws JavetException;

    /**
     * Gets private property boolean.
     *
     * @param propertyName the property name
     * @return the private property boolean
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Boolean getPrivatePropertyBoolean(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property double.
     *
     * @param propertyName the property name
     * @return the private property double
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Double getPrivatePropertyDouble(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property float.
     *
     * @param propertyName the property name
     * @return the private property float
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Float getPrivatePropertyFloat(String propertyName) throws JavetException {
        Double result = getPrivatePropertyDouble(propertyName);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets private property integer.
     *
     * @param propertyName the property name
     * @return the private property integer
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Integer getPrivatePropertyInteger(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property long.
     *
     * @param propertyName the property name
     * @return the private property long
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Long getPrivatePropertyLong(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property null.
     *
     * @param propertyName the property name
     * @return the private property null
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default V8ValueNull getPrivatePropertyNull(String propertyName) throws JavetException {
        return getPrivateProperty(propertyName);
    }

    /**
     * Gets private property object.
     *
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property object
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default <T> T getPrivatePropertyObject(String propertyName) throws JavetException {
        try {
            return getV8Runtime().toObject(getPrivateProperty(propertyName), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Gets private property primitive.
     *
     * @param <R>          the type parameter
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property primitive
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default <R, T extends V8ValuePrimitive<R>> R getPrivatePropertyPrimitive(String propertyName)
            throws JavetException {
        try (V8Value v8Value = getPrivateProperty(propertyName)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets private property string.
     *
     * @param propertyName the property name
     * @return the private property string
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default String getPrivatePropertyString(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property undefined.
     *
     * @param propertyName the property name
     * @return the private property undefined
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default V8ValueUndefined getPrivatePropertyUndefined(String propertyName) throws JavetException {
        return getPrivateProperty(propertyName);
    }

    /**
     * Gets private property zoned date time.
     *
     * @param propertyName the property name
     * @return the private property zoned date time
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default ZonedDateTime getPrivatePropertyZonedDateTime(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets property.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T getProperty(Object key) throws JavetException;

    /**
     * Gets property boolean.
     *
     * @param key the key
     * @return the property boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Boolean getPropertyBoolean(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property double.
     *
     * @param key the key
     * @return the property double
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Double getPropertyDouble(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property float.
     *
     * @param key the key
     * @return the property float
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Float getPropertyFloat(Object key) throws JavetException {
        Double result = getPropertyDouble(key);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets property integer.
     *
     * @param key the key
     * @return the property integer
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Integer getPropertyInteger(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property long.
     *
     * @param key the key
     * @return the property long
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Long getPropertyLong(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property names.
     *
     * @return the property names
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    IV8ValueArray getPropertyNames() throws JavetException;

    /**
     * Gets property object.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property object
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default <R, T extends V8ValuePrimitive<R>> R getPropertyPrimitive(Object key) throws JavetException {
        try (V8Value v8Value = getProperty(key)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets property string.
     *
     * @param key the key
     * @return the property string
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default String getPropertyString(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property zoned date time.
     *
     * @param key the key
     * @return the property zoned date time
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default ZonedDateTime getPropertyZonedDateTime(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets prototype.
     *
     * @param <T> the type parameter
     * @return the prototype
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    @CheckReturnValue
    <T extends IV8ValueObject> T getPrototype() throws JavetException;

    /**
     * Gets string.
     *
     * @param key the key
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @since 0.7.0
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
     * @since 0.7.0
     */
    default ZonedDateTime getZonedDateTime(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Has object property key.
     *
     * @param value the value
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean has(Object value) throws JavetException;

    /**
     * Has null property key.
     *
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.3
     */
    default boolean hasNull() throws JavetException {
        return has(getV8Runtime().createV8ValueNull());
    }

    /**
     * Has own property key.
     *
     * @param key the key
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean hasOwnProperty(Object key) throws JavetException;

    /**
     * Has private property boolean.
     *
     * @param propertyName the property name
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean hasPrivateProperty(String propertyName) throws JavetException;

    /**
     * Has undefined property key.
     *
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.3
     */
    default boolean hasUndefined() throws JavetException {
        return has(getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Invoke function with return V8 value by name and objects as arguments.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    default <T extends V8Value> T invoke(String functionName, Object... objects) throws JavetException {
        return invokeExtended(functionName, true, objects);
    }

    /**
     * Invoke function with return V8 value by name and V8 values as arguments.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param v8Values     the V8 values
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    default <T extends V8Value> T invoke(String functionName, V8Value... v8Values) throws JavetException {
        return invokeExtended(functionName, true, v8Values);
    }

    /**
     * Invoke function with return value boolean by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Boolean invokeBoolean(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke function with return value double by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the double
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.5
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
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values) throws JavetException;

    /**
     * Invoke function with return value float by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the float
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Float invokeFloat(String functionName, Object... objects) throws JavetException {
        Double result = invokeDouble(functionName, objects);
        return result == null ? null : result.floatValue();
    }

    /**
     * Invoke function with return value integer by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Integer invokeInteger(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke function with return value long by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the long
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Long invokeLong(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke function with return value object by name and objects as arguments.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * Invoke function with return value primitive by name and objects as arguments.
     *
     * @param <R>          the type parameter
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param objects      the objects
     * @return the primitive value
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * Invoke function with return value string by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default String invokeString(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    /**
     * Invoke function without return value by name and objects as arguments.
     *
     * @param functionName the function name
     * @param objects      the objects
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void invokeVoid(String functionName, Object... objects) throws JavetException {
        invokeExtended(functionName, false, objects);
    }

    /**
     * Invoke function without return value by name and V8 values as arguments.
     *
     * @param functionName the function name
     * @param v8Values     the V8 values
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean set(Object key, Object value) throws JavetException;

    /**
     * Sets null.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean setNull(Object key) throws JavetException {
        return set(key, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets private property.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean setPrivateProperty(String propertyName, Object propertyValue) throws JavetException;

    /**
     * Sets private property null.
     *
     * @param propertyKey the property key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default boolean setPrivatePropertyNull(String propertyKey) throws JavetException {
        return setPrivateProperty(propertyKey, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets private property undefined.
     *
     * @param propertyKey the property key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default boolean setPrivatePropertyUndefined(String propertyKey) throws JavetException {
        return setPrivateProperty(propertyKey, getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Sets property.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean setProperty(Object key, Object value) throws JavetException;

    /**
     * Sets property null.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default boolean setPropertyNull(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets property undefined.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default boolean setPropertyUndefined(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Sets prototype.
     *
     * @param v8ValueObject the V8 value object
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    boolean setPrototype(IV8ValueObject v8ValueObject) throws JavetException;

    /**
     * Sets undefined.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
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
     * @since 0.7.0
     */
    String toJsonString();

    /**
     * To proto string.
     *
     * @return the string
     * @since 0.8.0
     */
    String toProtoString();

    /**
     * Unbind the interception.
     *
     * @param callbackReceiver the callback receiver
     * @return the count
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    int unbind(Object callbackReceiver) throws JavetException;

    /**
     * Unbind function.
     *
     * @param functionName the function name
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    boolean unbindFunction(String functionName) throws JavetException;

    /**
     * Unbind property.
     *
     * @param propertyName the property name
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean unbindProperty(String propertyName) throws JavetException {
        Objects.requireNonNull(propertyName);
        return unbindProperty(getV8Runtime().createV8ValueString(propertyName));
    }

    /**
     * Unbind property boolean.
     *
     * @param propertyName the property name
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean unbindProperty(V8ValueString propertyName) throws JavetException;

    /**
     * Unbind property boolean.
     *
     * @param propertyName the property name
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean unbindProperty(V8ValueSymbol propertyName) throws JavetException;
}
