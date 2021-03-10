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
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetConsumer;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
    default boolean delete(int key) throws JavetException {
        return delete(getV8Runtime().createV8ValueInteger(key));
    }

    default boolean delete(long key) throws JavetException {
        return delete(getV8Runtime().createV8ValueLong(key));
    }

    boolean delete(V8Value key) throws JavetException;

    default boolean delete(String key) throws JavetException {
        return delete(new V8ValueString(key));
    }

    default boolean deleteNull() throws JavetException {
        return delete(getV8Runtime().createV8ValueNull());
    }

    default boolean deleteUndefined() throws JavetException {
        return delete(getV8Runtime().createV8ValueUndefined());
    }

    <Key extends V8Value> int forEach(IJavetConsumer<Key> consumer) throws JavetException;

    <Key extends V8Value, Value extends V8Value> int forEach(IJavetBiConsumer<Key, Value> consumer) throws JavetException;

    default <T extends V8Value> T get(int key) throws JavetException {
        return get(getV8Runtime().createV8ValueInteger(key));
    }

    default <T extends V8Value> T get(String key) throws JavetException {
        return get(new V8ValueString(key));
    }

    <T extends V8Value> T get(V8Value key) throws JavetException;

    default Boolean getBoolean(int key) throws JavetException {
        return getObject(key);
    }

    default Boolean getBoolean(String key) throws JavetException {
        return getObject(key);
    }

    default Double getDouble(int key) throws JavetException {
        return getObject(key);
    }

    default Double getDouble(String key) throws JavetException {
        return getObject(key);
    }

    default Float getFloat(int key) throws JavetException {
        Double result = getDouble(key);
        return result == null ? null : result.floatValue();
    }

    default Float getFloat(String key) throws JavetException {
        Double result = getDouble(key);
        return result == null ? null : result.floatValue();
    }

    default Integer getInteger(int key) throws JavetException {
        return getObject(key);
    }

    default Integer getInteger(String key) throws JavetException {
        return getObject(key);
    }

    default Long getLong(int key) throws JavetException {
        return getObject(key);
    }

    default Long getLong(String key) throws JavetException {
        return getObject(key);
    }

    default V8ValueNull getNull(int key) throws JavetException {
        return get(key);
    }

    default V8ValueNull getNull(String key) throws JavetException {
        return get(key);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getObject(int key)
            throws JavetException {
        V8Value v8Value = get(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getObject(String key)
            throws JavetException {
        V8Value v8Value = get(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    IV8ValueArray getOwnPropertyNames() throws JavetException;

    IV8ValueArray getPropertyNames() throws JavetException;

    default <T extends V8Value> T getProperty(int index) throws JavetException {
        return getProperty(getV8Runtime().createV8ValueInteger(index));
    }

    default <T extends V8Value> T getProperty(String key) throws JavetException {
        return getProperty(new V8ValueString(key));
    }

    <T extends V8Value> T getProperty(V8Value key) throws JavetException;

    default Boolean getPropertyBoolean(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default Boolean getPropertyBoolean(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default Double getPropertyDouble(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default Double getPropertyDouble(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default Float getPropertyFloat(int index) throws JavetException {
        Double result = getPropertyDouble(index);
        return result == null ? null : result.floatValue();
    }

    default Float getPropertyFloat(String key) throws JavetException {
        Double result = getPropertyDouble(key);
        return result == null ? null : result.floatValue();
    }

    default Integer getPropertyInteger(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default Integer getPropertyInteger(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default Long getPropertyLong(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default Long getPropertyLong(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getPropertyObject(int index)
            throws JavetException {
        V8Value v8Value = getProperty(index);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getPropertyObject(String key)
            throws JavetException {
        V8Value v8Value = getProperty(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default String getPropertyString(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default String getPropertyString(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default ZonedDateTime getPropertyZonedDateTime(int index) throws JavetException {
        return getPropertyObject(index);
    }

    default ZonedDateTime getPropertyZonedDateTime(String key) throws JavetException {
        return getPropertyObject(key);
    }

    default String getString(int key) throws JavetException {
        return getObject(key);
    }

    default String getString(String key) throws JavetException {
        return getObject(key);
    }

    default V8ValueUndefined getUndefined(int key) throws JavetException {
        return get(key);
    }

    default V8ValueUndefined getUndefined(String key) throws JavetException {
        return get(key);
    }

    default ZonedDateTime getZonedDateTime(int key) throws JavetException {
        return getObject(key);
    }

    default ZonedDateTime getZonedDateTime(String key) throws JavetException {
        return getObject(key);
    }

    default boolean has(int value) throws JavetException {
        return has(getV8Runtime().createV8ValueInteger(value));
    }

    default boolean has(long value) throws JavetException {
        return has(getV8Runtime().createV8ValueLong(value));
    }

    default boolean has(String value) throws JavetException {
        return has(new V8ValueString(value));
    }

    boolean has(V8Value value) throws JavetException;

    default boolean hasNull() throws JavetException {
        return has(getV8Runtime().createV8ValueNull());
    }

    default boolean hasOwnProperty(int key) throws JavetException {
        return hasOwnProperty(getV8Runtime().createV8ValueInteger(key));
    }

    default boolean hasOwnProperty(String key) throws JavetException {
        return hasOwnProperty(new V8ValueString(key));
    }

    boolean hasOwnProperty(V8Value key) throws JavetException;

    default boolean hasUndefined() throws JavetException {
        return has(getV8Runtime().createV8ValueUndefined());
    }

    <T extends V8Value> T invoke(String functionName, boolean returnResult, V8Value... v8Values) throws JavetException;

    default <T extends V8Value> T invoke(String functionName, V8Value... v8Values) throws JavetException {
        return invoke(functionName, true, v8Values);
    }

    default Boolean invokeBoolean(String functionName, V8Value... v8Values) throws JavetException {
        return invokeObject(functionName, v8Values);
    }

    default Double invokeDouble(String functionName, V8Value... v8Values) throws JavetException {
        return invokeObject(functionName, v8Values);
    }

    default Float invokeFloat(String functionName, V8Value... v8Values) throws JavetException {
        Double result = invokeDouble(functionName, v8Values);
        return result == null ? null : result.floatValue();
    }

    default Integer invokeInteger(String functionName, V8Value... v8Values) throws JavetException {
        return invokeObject(functionName, v8Values);
    }

    default Long invokeLong(String functionName, V8Value... v8Values) throws JavetException {
        return invokeObject(functionName, v8Values);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R invokeObject(
            String functionName, V8Value... v8Values) throws JavetException {
        try (V8Value v8Value = invoke(functionName, v8Values)) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String invokeString(String functionName, V8Value... v8Values) throws JavetException {
        return invokeObject(functionName, v8Values);
    }

    default void invokeVoid(String functionName, V8Value... v8Values) throws JavetException {
        invoke(functionName, false, v8Values);
    }

    default boolean set(int key, V8Value value) throws JavetException {
        return set(getV8Runtime().createV8ValueInteger(key), value);
    }

    default boolean set(String key, V8Value value) throws JavetException {
        return set(new V8ValueString(key), value);
    }

    boolean set(V8Value key, V8Value value) throws JavetException;

    /**
     * Sets function by name and callback context.
     * <p>
     * It is for creating a Java code based function in V8.
     *
     * @param functionName         the function name
     * @param javetCallbackContext the javet callback context
     * @return true: function is set, false: function is not set
     * @throws JavetException the javet exception
     */
    default boolean setFunction(String functionName, JavetCallbackContext javetCallbackContext) throws JavetException {
        V8ValueFunction v8ValueFunction = getV8Runtime().createV8ValueFunction(javetCallbackContext);
        boolean success = set(functionName, v8ValueFunction);
        v8ValueFunction.setWeak();
        return success;
    }

    /**
     * Sets function by name and string.
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
     * @return true: function is set, false: function is not set
     * @throws JavetException the javet exception
     */
    default boolean setFunction(String functionName, String codeString) throws JavetException {
        V8ValueFunction v8ValueFunction = getV8Runtime().getExecutor(codeString).execute();
        boolean success = set(functionName, v8ValueFunction);
        v8ValueFunction.setWeak();
        return success;
    }

    default boolean setNull(int key) throws JavetException {
        return set(getV8Runtime().createV8ValueInteger(key), getV8Runtime().createV8ValueNull());
    }

    default boolean setNull(String key) throws JavetException {
        return set(new V8ValueString(key), getV8Runtime().createV8ValueNull());
    }

    default boolean setProperty(int key, V8Value value) throws JavetException {
        return setProperty(getV8Runtime().createV8ValueInteger(key), value);
    }

    default boolean setProperty(String key, V8Value value) throws JavetException {
        return setProperty(new V8ValueString(key), value);
    }

    boolean setProperty(V8Value key, V8Value value) throws JavetException;

    default boolean setPropertyNull(int key) throws JavetException {
        return setProperty(getV8Runtime().createV8ValueInteger(key), getV8Runtime().createV8ValueNull());
    }

    default boolean setPropertyNull(String key) throws JavetException {
        return setProperty(new V8ValueString(key), getV8Runtime().createV8ValueNull());
    }

    default boolean setPropertyUndefined(int key) throws JavetException {
        return setProperty(getV8Runtime().createV8ValueInteger(key), getV8Runtime().createV8ValueUndefined());
    }

    default boolean setPropertyUndefined(String key) throws JavetException {
        return setProperty(new V8ValueString(key), getV8Runtime().createV8ValueUndefined());
    }

    default boolean setUndefined(int key) throws JavetException {
        return set(getV8Runtime().createV8ValueInteger(key), getV8Runtime().createV8ValueUndefined());
    }

    default boolean setUndefined(String key) throws JavetException {
        return set(new V8ValueString(key), getV8Runtime().createV8ValueUndefined());
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
}
