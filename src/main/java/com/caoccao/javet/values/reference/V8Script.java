package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

@SuppressWarnings("unchecked")
public class V8Script extends V8ValueReference {
    public V8Script(long handle) {
        super(handle);
    }

    @Override
    public V8Script toClone() throws JavetException {
        return this;
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Script;
    }
}
