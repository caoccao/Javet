package com.caoccao.javet.values.reference;

import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueArguments extends V8ValueArray {
    public V8ValueArguments(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Arguments;
    }
}
