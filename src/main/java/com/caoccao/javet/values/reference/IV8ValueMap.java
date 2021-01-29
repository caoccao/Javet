package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueMap extends IV8ValueSet {
    <T extends V8Value> T get(int key) throws JavetException;

    <T extends V8Value> T get(String key) throws JavetException;

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

    default String getString(int key) throws JavetException {
        return getObject(key);
    }

    default String getString(String key) throws JavetException {
        return getObject(key);
    }

    IV8ValueCollection getValues() throws JavetException;

    default ZonedDateTime getZonedDateTime(int key) throws JavetException {
        return getObject(key);
    }

    default ZonedDateTime getZonedDateTime(String key) throws JavetException {
        return getObject(key);
    }
}
