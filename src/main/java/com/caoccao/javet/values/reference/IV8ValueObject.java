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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
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

    default boolean delete(int key) throws JavetException {
        return delete(new V8ValueInteger(key));
    }

    default boolean delete(long key) throws JavetException {
        return delete(new V8ValueLong(key));
    }

    default boolean delete(String key) throws JavetException {
        return delete(new V8ValueString(key));
    }

    boolean delete(V8Value key) throws JavetException;

    default boolean deleteNull() throws JavetException {
        return delete(new V8ValueNull());
    }

    default boolean deleteUndefined() throws JavetException {
        return delete(new V8ValueUndefined());
    }

    default <T extends V8Value> T get(int key) throws JavetException {
        return get(new V8ValueInteger(key));
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
        return getProperty(new V8ValueInteger(index));
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

    default boolean hasOwnProperty(int key) throws JavetException {
        return hasOwnProperty(new V8ValueInteger(key));
    }

    default boolean hasOwnProperty(String key) throws JavetException {
        return hasOwnProperty(new V8ValueString(key));
    }

    boolean hasOwnProperty(V8Value key) throws JavetException;

    default boolean set(int key, V8Value value) throws JavetException {
        return set(new V8ValueInteger(key), value);
    }

    default boolean set(String key, V8Value value) throws JavetException {
        return set(new V8ValueString(key), value);
    }

    boolean set(V8Value key, V8Value value) throws JavetException;

    default boolean setNull(int key) throws JavetException {
        return set(new V8ValueInteger(key), new V8ValueNull());
    }

    default boolean setNull(String key) throws JavetException {
        return set(new V8ValueString(key), new V8ValueNull());
    }

    default boolean setProperty(int key, V8Value value) throws JavetException {
        return setProperty(new V8ValueInteger(key), value);
    }

    default boolean setProperty(String key, V8Value value) throws JavetException {
        return setProperty(new V8ValueString(key), value);
    }

    boolean setProperty(V8Value key, V8Value value) throws JavetException;

    default boolean setPropertyNull(int key) throws JavetException {
        return setProperty(new V8ValueInteger(key), new V8ValueNull());
    }

    default boolean setPropertyNull(String key) throws JavetException {
        return setProperty(new V8ValueString(key), new V8ValueNull());
    }

    default boolean setPropertyUndefined(int key) throws JavetException {
        return setProperty(new V8ValueInteger(key), new V8ValueUndefined());
    }

    default boolean setPropertyUndefined(String key) throws JavetException {
        return setProperty(new V8ValueString(key), new V8ValueUndefined());
    }

    default boolean setUndefined(int key) throws JavetException {
        return set(new V8ValueInteger(key), new V8ValueUndefined());
    }

    default boolean setUndefined(String key) throws JavetException {
        return set(new V8ValueString(key), new V8ValueUndefined());
    }

    String toJsonString();
}
