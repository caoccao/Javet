package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;

@SuppressWarnings("unchecked")
public interface IV8ValueSet extends IV8ValueObject {
    IV8ValueCollection getKeys() throws JavetException;

    int getSize() throws JavetException;

    default boolean has(int value) throws JavetException {
        return has(new V8ValueInteger(value));
    }

    default boolean has(String value) throws JavetException {
        return has(new V8ValueString(value));
    }

    boolean has(V8Value value) throws JavetException;
}
