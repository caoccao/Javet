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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetConsumer;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
    boolean delete(Object key) throws JavetException;

    default boolean deleteNull() throws JavetException {
        return delete(getV8Runtime().createV8ValueNull());
    }

    default boolean deleteUndefined() throws JavetException {
        return delete(getV8Runtime().createV8ValueUndefined());
    }

    <Key extends V8Value> int forEach(IJavetConsumer<Key> consumer) throws JavetException;

    <Key extends V8Value, Value extends V8Value> int forEach(IJavetBiConsumer<Key, Value> consumer) throws JavetException;

    <T extends V8Value> T get(Object key) throws JavetException;

    default Boolean getBoolean(Object key) throws JavetException {
        return getPrimitive(key);
    }

    default Double getDouble(Object key) throws JavetException {
        return getPrimitive(key);
    }

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

    default Integer getInteger(Object key) throws JavetException {
        return getPrimitive(key);
    }

    default Long getLong(Object key) throws JavetException {
        return getPrimitive(key);
    }

    default V8ValueNull getNull(Object key) throws JavetException {
        return get(key);
    }

    default <T extends Object> T getObject(Object key) throws JavetException {
        try {
            return getV8Runtime().toObject(get(key), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    IV8ValueArray getOwnPropertyNames() throws JavetException;

    default <R extends Object, T extends V8ValuePrimitive<R>> R getPrimitive(Object key)
            throws JavetException {
        V8Value v8Value = get(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    <T extends V8Value> T getProperty(Object key) throws JavetException;

    default Boolean getPropertyBoolean(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    default Double getPropertyDouble(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    default Float getPropertyFloat(Object key) throws JavetException {
        Double result = getPropertyDouble(key);
        return result == null ? null : result.floatValue();
    }

    default Integer getPropertyInteger(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    default Long getPropertyLong(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    IV8ValueArray getPropertyNames() throws JavetException;

    default <T extends Object> T getPropertyObject(Object key) throws JavetException {
        try {
            return getV8Runtime().toObject(getProperty(key), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getPropertyPrimitive(Object key)
            throws JavetException {
        V8Value v8Value = getProperty(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default String getPropertyString(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    default ZonedDateTime getPropertyZonedDateTime(Object key) throws JavetException {
        return getPropertyPrimitive(key);
    }

    default String getString(Object key) throws JavetException {
        return getPrimitive(key);
    }

    default V8ValueUndefined getUndefined(Object key) throws JavetException {
        return get(key);
    }

    default ZonedDateTime getZonedDateTime(Object key) throws JavetException {
        return getPrimitive(key);
    }

    boolean has(Object value) throws JavetException;

    default boolean hasNull() throws JavetException {
        return has(getV8Runtime().createV8ValueNull());
    }

    boolean hasOwnProperty(Object key) throws JavetException;

    default boolean hasUndefined() throws JavetException {
        return has(getV8Runtime().createV8ValueUndefined());
    }

    default <T extends V8Value> T invoke(String functionName, Object... objects) throws JavetException {
        return invokeExtended(functionName, true, objects);
    }

    default <T extends V8Value> T invoke(String functionName, V8Value... v8Values) throws JavetException {
        return invokeExtended(functionName, true, v8Values);
    }

    default Boolean invokeBoolean(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, Object... objects) throws JavetException;

    <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values) throws JavetException;

    default Double invokeDouble(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    default Float invokeFloat(String functionName, Object... objects) throws JavetException {
        Double result = invokeDouble(functionName, objects);
        return result == null ? null : result.floatValue();
    }

    default Integer invokeInteger(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    default Long invokeLong(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    default <T extends Object> T invokeObject(String functionName, Object... objects) throws JavetException {
        try {
            return getV8Runtime().toObject(invokeExtended(functionName, true, objects), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R invokePrimitive(
            String functionName, Object... objects) throws JavetException {
        try (V8Value v8Value = invokeExtended(functionName, true, objects)) {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
            return null;
        }
    }

    default String invokeString(String functionName, Object... objects) throws JavetException {
        return invokePrimitive(functionName, objects);
    }

    default void invokeVoid(String functionName, Object... objects) throws JavetException {
        invokeExtended(functionName, false, objects);
    }

    default void invokeVoid(String functionName, V8Value... v8Values) throws JavetException {
        invokeExtended(functionName, false, v8Values);
    }

    boolean set(Object key, Object value) throws JavetException;

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
        try (V8ValueFunction v8ValueFunction = getV8Runtime().createV8ValueFunction(javetCallbackContext)) {
            return set(functionName, v8ValueFunction);
        }
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
        try (V8ValueFunction v8ValueFunction = getV8Runtime().getExecutor(codeString).execute()) {
            return set(functionName, v8ValueFunction);
        }
    }

    default List<JavetCallbackContext> setFunctions(
            Object functionCallbackReceiver)
            throws JavetException {
        return setFunctions(functionCallbackReceiver, false);
    }

    default List<JavetCallbackContext> setFunctions(
            Object functionCallbackReceiver,
            boolean thisObjectRequired)
            throws JavetException {
        Map<String, Method> functionMap = new HashMap<>();
        for (Method method : functionCallbackReceiver.getClass().getMethods()) {
            if (method.isAnnotationPresent(V8Function.class)) {
                V8Function v8Function = method.getAnnotation(V8Function.class);
                String functionName = v8Function.name();
                if (functionName == null || functionName.length() == 0) {
                    functionName = method.getName();
                }
                // Duplicated functions will be dropped.
                if (!functionMap.containsKey(functionName)) {
                    functionMap.put(functionName, method);
                }
            } else if (method.isAnnotationPresent(V8RuntimeSetter.class)) {
                if (method.getParameterCount() != 1) {
                    throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                            SimpleMap.of(
                                    JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                    JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 1,
                                    JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, method.getParameterCount()));
                }
                if (!V8Runtime.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new JavetException(
                            JavetError.CallbackSignatureParameterTypeMismatch,
                            SimpleMap.of(
                                    JavetError.PARAMETER_EXPECTED_PARAMETER_TYPE, V8Runtime.class,
                                    JavetError.PARAMETER_ACTUAL_PARAMETER_TYPE, method.getParameterTypes()[0]));
                }
                try {
                    method.invoke(functionCallbackReceiver, getV8Runtime());
                } catch (Exception e) {
                    throw new JavetException(
                            JavetError.CallbackInjectionFailure,
                            SimpleMap.of(JavetError.PARAMETER_MESSAGE, e.getMessage()),
                            e);
                }
            }
        }
        List<JavetCallbackContext> javetCallbackContexts = new ArrayList<>();
        if (!functionMap.isEmpty()) {
            for (Map.Entry<String, Method> entry : functionMap.entrySet()) {
                final Method method = entry.getValue();
                try {
                    // Static method needs to be identified.
                    JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                            Modifier.isStatic(method.getModifiers()) ? null : functionCallbackReceiver,
                            method, thisObjectRequired);
                    setFunction(entry.getKey(), javetCallbackContext);
                    javetCallbackContexts.add(javetCallbackContext);
                } catch (Exception e) {
                    throw new JavetException(
                            JavetError.CallbackRegistrationFailure,
                            SimpleMap.of(
                                    JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                    JavetError.PARAMETER_MESSAGE, e.getMessage()),
                            e);
                }
            }
        }
        return javetCallbackContexts;
    }

    default boolean setNull(Object key) throws JavetException {
        return set(key, getV8Runtime().createV8ValueNull());
    }

    boolean setProperty(Object key, Object value) throws JavetException;

    default boolean setPropertyNull(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueNull());
    }

    default boolean setPropertyUndefined(Object key) throws JavetException {
        return setProperty(key, getV8Runtime().createV8ValueUndefined());
    }

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

    String toProtoString();
}
