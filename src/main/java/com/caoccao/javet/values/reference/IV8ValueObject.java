package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

@SuppressWarnings("unchecked")
public interface IV8ValueObject extends IV8ValueReference {
    IV8ValueCollection getPropertyNames() throws JavetException;

    IV8ValueCollection getOwnPropertyNames() throws JavetException;

    <T extends V8Value> T getValue(String key) throws JavetException;

    default Boolean getValueBoolean(String key) throws JavetException {
        return getValueObject(key);
    }

    default Integer getValueInteger(String key) throws JavetException {
        return getValueObject(key);
    }

    default Long getValueLong(String key) throws JavetException {
        return getValueObject(key);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R getValueObject(String key)
            throws JavetException {
        V8Value v8Value = getValue(key);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default String getValueString(String key) throws JavetException {
        return getValueObject(key);
    }
}
