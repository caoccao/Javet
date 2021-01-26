package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueType;

public class V8ValueArray extends V8ValueReference implements IV8ValueCollection {
    public V8ValueArray(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueType.Array;
    }

    @Override
    public int getLength()
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }

    @Override
    public <T extends V8Value> T getValue(int index)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getValue(this, index);
    }
}
