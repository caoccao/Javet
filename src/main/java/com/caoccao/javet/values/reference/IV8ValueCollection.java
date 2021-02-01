package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8ValueCollection extends IV8ValueObject {
    int getLength() throws JavetException;

    <T extends V8Value> T get(int index) throws JavetException;

    default Boolean getBoolean(int index) throws JavetException {
        return getObject(index);
    }

    default Double getDouble(int index) throws JavetException {
        return getObject(index);
    }

    default Integer getInteger(int index) throws JavetException {
        return getObject(index);
    }

    default Long getLong(int index) throws JavetException {
        return getObject(index);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getObject(int index)
            throws JavetException {
        V8Value v8Value = get(index);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default String getString(int index) throws JavetException {
        return getObject(index);
    }

    default ZonedDateTime getZonedDateTime(int index) throws JavetException {
        return getObject(index);
    }

    int push(V8Value v8Value) throws JavetException;
}
