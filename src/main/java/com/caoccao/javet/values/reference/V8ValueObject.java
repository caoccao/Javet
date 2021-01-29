package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    public V8ValueObject(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean hasOwnProperty(int key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.hasOwnProperty(this, key);
    }

    @Override
    public boolean hasOwnProperty(String key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.hasOwnProperty(this, key);
    }

    @Override
    public IV8ValueCollection getOwnPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getOwnPropertyNames(this);
    }

    @Override
    public IV8ValueCollection getPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getPropertyNames(this);
    }

    @Override
    public <T extends V8Value> T getProperty(int index)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getProperty(this, index);
    }

    @Override
    public <T extends V8Value> T getProperty(String key)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getProperty(this, key);
    }
}
