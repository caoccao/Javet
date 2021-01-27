package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueError extends V8ValueObject {

    public static final String STACK = "stack";
    public static final String MESSAGE = "message";

    public V8ValueError(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Error;
    }

    public String getMessage() throws JavetException {
        return getValueString(MESSAGE);
    }

    public String getStack() throws JavetException {
        return getValueString(STACK);
    }
}
