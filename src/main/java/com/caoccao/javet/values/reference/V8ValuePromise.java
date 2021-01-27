package com.caoccao.javet.values.reference;

import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValuePromise extends V8ValueObject {

    public V8ValuePromise(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Promise;
    }
}
