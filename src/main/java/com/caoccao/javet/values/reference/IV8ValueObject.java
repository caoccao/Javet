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
import com.caoccao.javet.enums.V8ValueInternalType;
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

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
     * The constant DEFAULT_BATCH_SIZE is the default batch size for get a chunk of items.
     *
     * @since 2.2.0
     */
    int DEFAULT_BATCH_SIZE = 100;
    /**
     * The constant MIN_BATCH_SIZE.
     *
     * @since 2.2.0
     */
    int MIN_BATCH_SIZE = 1;

    /**
     * Batch get a range of values by keys.
     *
     * @param v8ValueKeys   the V8 value keys
     * @param v8ValueValues the V8 value values
     * @param length        the length
     * @return the actual item count
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("UnusedReturnValue")
    int batchGet(V8Value[] v8ValueKeys, V8Value[] v8ValueValues, int length) throws JavetException;

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
     * Binds function by name symbol and callback context.
     * <p>
     * It is for creating a Java code based function in V8.
     *
     * @param javetCallbackContext the javet callback context
     * @return true : the function is bind, false: the function is not bind
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean bindFunction(JavetCallbackContext javetCallbackContext) throws JavetException;

    /**
     * Binds function by name string and code string.
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
     * @return true : the function is bind, false: the function is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean bindFunction(String functionName, String codeString) throws JavetException {
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().createV8ValueFunction(codeString)) {
            return set(functionName, v8ValueFunction);
        }
    }

    /**
     * Binds function by name symbol and code string.
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
     * @return true : the function is bind, false: the function is not bind
     * @throws JavetException the javet exception
     * @since 1.0.0
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean bindFunction(V8ValueSymbol functionName, String codeString) throws JavetException {
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().createV8ValueFunction(codeString)) {
            return set(functionName, v8ValueFunction);
        }
    }

    /**
     * Bind property by name string and getter.
     *
     * @param javetCallbackContextGetter the javet callback context getter
     * @return true : the property is bind, false : the property is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    default boolean bindProperty(JavetCallbackContext javetCallbackContextGetter) throws JavetException {
        return bindProperty(javetCallbackContextGetter, null);
    }

    /**
     * Bind property by getter and setter.
     *
     * @param javetCallbackContextGetter the javet callback context getter
     * @param javetCallbackContextSetter the javet callback context setter
     * @return true : the property is bind, false : the property is not bind
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    boolean bindProperty(
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter)
            throws JavetException;

    /**
     * Delete property by key object.
     *
     * @param key the key
     * @return true : the property is deleted, false : the property is not deleted
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean delete(Object key) throws JavetException;

    /**
     * Delete property by null.
     *
     * @return true : the property is deleted, false : the property is not deleted
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default boolean deleteNull() throws JavetException {
        return delete(getV8Runtime().createV8ValueNull());
    }

    /**
     * Delete private property by name string.
     *
     * @param propertyName the property name
     * @return true : the private property is deleted, false : the private property is not deleted
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean deletePrivateProperty(String propertyName) throws JavetException;

    /**
     * Delete property by undefined.
     *
     * @return true : the property is deleted, false : the property is not deleted
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default boolean deleteUndefined() throws JavetException {
        return delete(getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Invoke the uni-consumer for each of the keys.
     *
     * @param <Key>    the type of key
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.10
     */
    default <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer)
            throws JavetException, E {
        return forEach(consumer, DEFAULT_BATCH_SIZE);
    }

    /**
     * Invoke the uni-consumer for each of the keys.
     *
     * @param <Key>     the type of key
     * @param <E>       the type of exception
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer,
            int batchSize)
            throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach(consumer, batchSize);
        }
    }

    /**
     * Invoke the uni-indexed-consumer for each of the keys.
     *
     * @param <Key>    the type of key
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.10
     */
    default <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer)
            throws JavetException, E {
        return forEach(consumer, DEFAULT_BATCH_SIZE);
    }

    /**
     * Invoke the uni-indexed-consumer for each of the keys.
     *
     * @param <Key>     the type of key
     * @param <E>       the type of exception
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer,
            int batchSize)
            throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach(consumer, batchSize);
        }
    }

    /**
     * Invoke the bi-consumer for each of the keys.
     *
     * @param <Key>    the type of key
     * @param <Value>  the type of value
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.9
     */
    default <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer)
            throws JavetException, E {
        return forEach(consumer, DEFAULT_BATCH_SIZE);
    }

    /**
     * Invoke the bi-consumer for each of the keys.
     *
     * @param <Key>     the type of key
     * @param <Value>   the type of value
     * @param <E>       the type of exception
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer,
            int batchSize)
            throws JavetException, E;

    /**
     * Invoke the bi-indexed-consumer for each of the keys.
     *
     * @param <Key>    the type of key
     * @param <Value>  the type of value
     * @param <E>      the type of exception
     * @param consumer the consumer
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.10
     */
    default <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer)
            throws JavetException, E {
        return forEach(consumer, DEFAULT_BATCH_SIZE);
    }

    /**
     * Invoke the bi-indexed-consumer for each of the keys.
     *
     * @param <Key>     the type of key
     * @param <Value>   the type of value
     * @param <E>       the type of exception
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the key count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.10
     */
    <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer,
            int batchSize)
            throws JavetException, E;

    /**
     * Get property value by key object.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @param <T> the type parameter
     * @param key the property key
     * @return the property value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T get(Object key) throws JavetException;

    /**
     * Gets property value as big integer by key object.
     *
     * @param key the key
     * @return the property value as big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default BigInteger getBigInteger(Object key) throws JavetException {
        return getPrimitive(key);
    }

    /**
     * Gets property value as boolean by key object.
     *
     * @param key the key
     * @return the property value as boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    Boolean getBoolean(Object key) throws JavetException;

    /**
     * Gets property value as double by key object.
     *
     * @param key the key
     * @return the property value as double
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    Double getDouble(Object key) throws JavetException;

    /**
     * Gets property value as float by key object.
     *
     * @param key the key
     * @return the property value as float
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
     * Gets property value as integer by key object.
     *
     * @param key the key
     * @return the property value as integer
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    Integer getInteger(Object key) throws JavetException;

    /**
     * Gets property value as long by key object.
     *
     * @param key the key
     * @return the property value as long
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    Long getLong(Object key) throws JavetException;

    /**
     * Gets property value as null by key object.
     *
     * @param key the key
     * @return the property value as null
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default V8ValueNull getNull(Object key) throws JavetException {
        return get(key);
    }

    /**
     * Gets property value as object by key object.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property value as object
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
     * Gets own property name strings.
     *
     * @return the own property name strings
     * @throws JavetException the javet exception
     * @since 2.0.2
     */
    default List<String> getOwnPropertyNameStrings() throws JavetException {
        List<String> ownPropertyNameStrings = new ArrayList<>();
        forEach(v8Value -> {
            if (v8Value instanceof V8ValueString) {
                ownPropertyNameStrings.add(((V8ValueString) v8Value).getValue());
            }
        });
        return ownPropertyNameStrings;
    }

    /**
     * Gets own property names.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @return the own property names
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    IV8ValueArray getOwnPropertyNames() throws JavetException;

    /**
     * Gets property value as primitive by key object.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @param key the key
     * @return the property value as primitive
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
     * Gets private property value by name string.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property value
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    @CheckReturnValue
    <T extends V8Value> T getPrivateProperty(String propertyName) throws JavetException;

    /**
     * Gets private property value as boolean by name string.
     *
     * @param propertyName the property name
     * @return the private property value as boolean
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Boolean getPrivatePropertyBoolean(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property value as double by name string.
     *
     * @param propertyName the property name
     * @return the private property value as double
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Double getPrivatePropertyDouble(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property value as float by name string.
     *
     * @param propertyName the property name
     * @return the private property value as float
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Float getPrivatePropertyFloat(String propertyName) throws JavetException {
        Double result = getPrivatePropertyDouble(propertyName);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets private property value as integer by name string.
     *
     * @param propertyName the property name
     * @return the private property value as integer
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Integer getPrivatePropertyInteger(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property value as long by name string.
     *
     * @param propertyName the property name
     * @return the private property value as long
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default Long getPrivatePropertyLong(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property value as null by name string.
     *
     * @param propertyName the property name
     * @return the private property value as null
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default V8ValueNull getPrivatePropertyNull(String propertyName) throws JavetException {
        return getPrivateProperty(propertyName);
    }

    /**
     * Gets private property value as object by name string.
     *
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property value as object
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
     * Gets private property value as primitive by name string.
     *
     * @param <R>          the type parameter
     * @param <T>          the type parameter
     * @param propertyName the property name
     * @return the private property value as primitive
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
     * Gets private property value as string by name string.
     *
     * @param propertyName the property name
     * @return the private property value as string
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default String getPrivatePropertyString(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets private property value as undefined by name string.
     *
     * @param propertyName the property name
     * @return the private property value as undefined
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default V8ValueUndefined getPrivatePropertyUndefined(String propertyName) throws JavetException {
        return getPrivateProperty(propertyName);
    }

    /**
     * Gets private property value as zoned date time by name string.
     *
     * @param propertyName the property name
     * @return the private property value as zoned date time
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default ZonedDateTime getPrivatePropertyZonedDateTime(String propertyName) throws JavetException {
        return getPrivatePropertyPrimitive(propertyName);
    }

    /**
     * Gets property value by key object.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T getProperty(Object key) throws JavetException;

    /**
     * Gets property value as boolean by key object.
     *
     * @param key the key
     * @return the property value as boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Boolean getPropertyBoolean(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property value as double by key object.
     *
     * @param key the key
     * @return the property value as double
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Double getPropertyDouble(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property value as float by key object.
     *
     * @param key the key
     * @return the property value as float
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Float getPropertyFloat(Object key) throws JavetException {
        Double result = getPropertyDouble(key);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets property value as integer by key object.
     *
     * @param key the key
     * @return the property value as integer
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Integer getPropertyInteger(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property value as long by key object.
     *
     * @param key the key
     * @return the property value as long
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Long getPropertyLong(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property names.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @return the property names
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    IV8ValueArray getPropertyNames() throws JavetException;

    /**
     * Gets property value as object by key object.
     *
     * @param <T> the type parameter
     * @param key the key
     * @return the property value as object
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
     * Gets property value as primitive by key object.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @param key the key
     * @return the property value as primitive
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
     * Gets property value as string by key object.
     *
     * @param key the key
     * @return the property value as string
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default String getPropertyString(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets property value as zoned date time by key object.
     *
     * @param key the key
     * @return the property value as zoned date time
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default ZonedDateTime getPropertyZonedDateTime(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    /**
     * Gets prototype.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @param <T> the type parameter
     * @return the prototype
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    @CheckReturnValue
    <T extends IV8ValueObject> T getPrototype() throws JavetException;

    /**
     * Gets string by key object.
     *
     * @param key the key
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    String getString(Object key) throws JavetException;

    /**
     * Gets undefined by key object.
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
     * Gets zoned date time by key object.
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
     * Has object property key by key object.
     *
     * @param value the value
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean has(Object value) throws JavetException;

    /**
     * Has internal type.
     * <p>
     * This API reveals the V8 internal implementation detail.
     * A typical JavaScript object may look the same in JavaScript (typeof),
     * but is very different internally.
     *
     * @param internalType the internal type
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    boolean hasInternalType(V8ValueInternalType internalType) throws JavetException;

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
     * Has own property key by key object.
     *
     * @param key the key
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean hasOwnProperty(Object key) throws JavetException;

    /**
     * Has private property by name string.
     *
     * @param propertyName the property name
     * @return true : yes, false: no
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
     * Invoke function and return a V8 value by function name and objects as arguments.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
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
     * Invoke function and return a V8 value by function name and V8 values as arguments.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
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
     * Invoke function and return a boolean by function name and objects as arguments.
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
     * Invoke function and return a double by function name and objects as arguments.
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
     * Invoke function and return a V8 value by function name, return result, objects as arguments.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
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
     * Invoke function and return a V8 value by function name, return result, V8 values as arguments.
     * <p>
     * The return value must be consumed, otherwise memory leak may occur.
     *
     * @param <T>          the type parameter
     * @param functionName the function name
     * @param returnResult the return result
     * @param v8Values     the V8 values
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values) throws JavetException;

    /**
     * Invoke function and return a float by function name and objects as arguments.
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
     * Invoke function and return a integer by function name and objects as arguments.
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
     * Invoke function and return a long by function name and objects as arguments.
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
     * Invoke function and return an object by function name and objects as arguments.
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
     * Invoke function and return a primitive by function name and objects as arguments.
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
     * Invoke function and return a string by function name and objects as arguments.
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
     * Invoke function without a return value by function name and objects as arguments.
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
     * Invoke function without a return value by function name and V8 values as arguments.
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
     * Is generator object.
     *
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    default boolean isGeneratorObject() throws JavetException {
        return hasInternalType(V8ValueInternalType.GeneratorObject);
    }

    /**
     * Set property by key object and value object.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean set(Object key, Object value) throws JavetException;

    /**
     * Set property by pairs of key object and value object.
     *
     * @param keysAndValues the keys and values
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     */
    boolean set(Object... keysAndValues) throws JavetException;

    /**
     * Set property by key object and value boolean.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setBoolean(Object key, Boolean value) throws JavetException;

    /**
     * Set property by key object and value double.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setDouble(Object key, Double value) throws JavetException;

    /**
     * Set property by key object and value integer.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setInteger(Object key, Integer value) throws JavetException;

    /**
     * Set property by key object and value long.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setLong(Object key, Long value) throws JavetException;

    /**
     * Set property to null by key object.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setNull(Object key) throws JavetException;

    /**
     * Set private property by name string and value object.
     *
     * @param propertyName  the property name
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean setPrivateProperty(String propertyName, Object propertyValue) throws JavetException;

    /**
     * Sets private property to null by name string.
     *
     * @param propertyName the property name
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default boolean setPrivatePropertyNull(String propertyName) throws JavetException {
        return setPrivateProperty(propertyName, getV8Runtime().createV8ValueNull());
    }

    /**
     * Sets private property to undefined by name string.
     *
     * @param propertyName the property name
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    default boolean setPrivatePropertyUndefined(String propertyName) throws JavetException {
        return setPrivateProperty(propertyName, getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Set property by key object and value object.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean setProperty(Object key, Object value) throws JavetException;

    /**
     * Set property to null by key object.
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
     * Set property to undefined by key object.
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
    @SuppressWarnings("UnusedReturnValue")
    boolean setPrototype(IV8ValueObject v8ValueObject) throws JavetException;

    /**
     * Set property by key object and value string.
     *
     * @param key   the key
     * @param value the value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setString(Object key, String value) throws JavetException;

    /**
     * Set property to undefined by key object.
     *
     * @param key the key
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setUndefined(Object key) throws JavetException;

    /**
     * To json string.
     * <p>
     * JS equivalent:
     * <pre>
     * JSON.stringify(obj);
     * </pre>
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
     * Unbind functions and properties by object.
     *
     * @param callbackReceiver the callback receiver
     * @return the unbind function and property count
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    int unbind(Object callbackReceiver) throws JavetException;

    /**
     * Unbind function by function name string.
     *
     * @param functionName the function name
     * @return true : the function is unbind, false: the function is not unbind
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean unbindFunction(String functionName) throws JavetException {
        return delete(functionName);
    }

    /**
     * Unbind function by function name string.
     *
     * @param functionName the function name
     * @return true : the function is unbind, false: the function is not unbind
     * @throws JavetException the javet exception
     * @since 1.0.0
     */
    default boolean unbindFunction(V8ValueString functionName) throws JavetException {
        return delete(functionName);
    }

    /**
     * Unbind function by function name symbol.
     *
     * @param functionName the function name
     * @return true : the function is unbind, false: the function is not unbind
     * @throws JavetException the javet exception
     * @since 1.0.0
     */
    @SuppressWarnings("UnusedReturnValue")
    default boolean unbindFunction(V8ValueSymbol functionName) throws JavetException {
        return delete(functionName);
    }

    /**
     * Unbind property by callback context.
     *
     * @param javetCallbackContext the javet callback context
     * @return true : the property is unbind, false: the property is not unbind
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    boolean unbindProperty(JavetCallbackContext javetCallbackContext) throws JavetException;

    /**
     * Unbind property by property name string.
     *
     * @param propertyName the property name
     * @return true : the property is unbind, false: the property is not unbind
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    default boolean unbindProperty(String propertyName) throws JavetException {
        return unbindProperty(getV8Runtime().createV8ValueString(Objects.requireNonNull(propertyName)));
    }

    /**
     * Unbind property by property name string.
     *
     * @param propertyName the property name
     * @return true : the property is unbind, false: the property is not unbind
     * @throws JavetException the javet exception
     */
    boolean unbindProperty(V8ValueString propertyName) throws JavetException;

    /**
     * Unbind property by property name symbol.
     *
     * @param propertyName the property name
     * @return true : the property is unbind, false: the property is not unbind
     * @throws JavetException the javet exception
     */
    boolean unbindProperty(V8ValueSymbol propertyName) throws JavetException;
}
