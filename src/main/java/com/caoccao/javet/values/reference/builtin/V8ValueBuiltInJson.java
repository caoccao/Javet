package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInJson extends V8ValueObject {

    public static final String FUNCTION_STRINGIFY = "stringify";

    public V8ValueBuiltInJson(long handle) {
        super(handle);
    }

    public String stringify(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeString(FUNCTION_STRINGIFY, v8Value);
    }

    @Override
    public V8ValueBuiltInJson toClone() throws JavetException {
        return this;
    }
}
