package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueMap extends V8ValueSet implements IV8ValueMap {

    public V8ValueMap(long handle) {
        super(handle);
    }

    @Override
    public <T extends V8Value> T get(int key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, key);
    }

    @Override
    public <T extends V8Value> T get(String key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, key);
    }

    @Override
    public IV8ValueCollection getKeys() throws JavetException {
        return null;
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Map;
    }

    @Override
    public IV8ValueCollection getValues() throws JavetException {
        return null;
    }
}
