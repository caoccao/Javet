package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
    boolean hasOwnProperty(int key) throws JavetException;

    boolean hasOwnProperty(String key) throws JavetException;

    IV8ValueCollection getPropertyNames() throws JavetException;

    IV8ValueCollection getOwnPropertyNames() throws JavetException;

    <T extends V8Value> T getProperty(int index) throws JavetException;

    <T extends V8Value> T getProperty(String key) throws JavetException;

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
}
