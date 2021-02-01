package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueString;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
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

    IV8ValueCollection getOwnPropertyNames() throws JavetException;

    IV8ValueCollection getPropertyNames() throws JavetException;

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

    default boolean setProperty(int key, V8Value value) throws JavetException {
        return setProperty(new V8ValueInteger(key), value);
    }

    default boolean setProperty(String key, V8Value value) throws JavetException {
        return setProperty(new V8ValueString(key), value);
    }

    boolean setProperty(V8Value key, V8Value value) throws JavetException;
}
