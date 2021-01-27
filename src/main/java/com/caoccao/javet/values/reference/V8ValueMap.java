package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueMap extends V8ValueObject {

    public V8ValueMap(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Map;
    }

    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }
}
