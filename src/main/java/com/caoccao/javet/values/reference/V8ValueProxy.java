package com.caoccao.javet.values.reference;

import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueProxy extends V8ValueObject {

    public V8ValueProxy(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Proxy;
    }
}
