package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueArguments extends V8ValueObject implements IV8ValueCollection {
    public V8ValueArguments(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Arguments;
    }

    @Override
    public int getLength()
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }
}
