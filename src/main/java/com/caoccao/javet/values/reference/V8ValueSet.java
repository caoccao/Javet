package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueSet extends V8ValueObject implements IV8ValueSet {

    public V8ValueSet(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Set;
    }

    @Override
    public IV8ValueCollection getKeys() throws JavetException {
        return null;
    }

    @Override
    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

    @Override
    public boolean has(V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.has(this, value);
    }
}
