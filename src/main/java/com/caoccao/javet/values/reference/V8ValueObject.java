package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueType;

public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    public V8ValueObject(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueType.Object;
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
    public <T extends V8Value> T getValue(String key)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getValue(this, key);
    }
}
