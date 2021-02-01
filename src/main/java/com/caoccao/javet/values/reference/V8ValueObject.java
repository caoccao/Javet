package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    public V8ValueObject(long handle) {
        super(handle);
    }

    @Override
    public <T extends V8Value> T get(V8Value key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, key);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean hasOwnProperty(V8Value key) throws JavetException {
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
    public <T extends V8Value> T getProperty(V8Value key)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getProperty(this, key);
    }

    @Override
    public boolean set(V8Value key, V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.set(this, key, value);
    }

    @Override
    public boolean setProperty(V8Value key, V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.setProperty(this, key, value);
    }
}
