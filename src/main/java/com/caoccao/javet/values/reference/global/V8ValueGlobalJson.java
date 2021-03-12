package com.caoccao.javet.values.reference.global;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueGlobalJson extends V8ValueObject {

    public static final String FUNCTION_STRINGIFY = "stringify";

    V8ValueGlobalJson(long handle) {
        super(handle);
    }

    public String stringify(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeString(FUNCTION_STRINGIFY, v8Value);
    }

    @Override
    public V8ValueGlobalJson toClone() throws JavetException {
        return this;
    }
}
