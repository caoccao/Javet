package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueInteger;

public class V8ValueArray extends V8ValueObject implements IV8ValueCollection {
    public V8ValueArray(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Array;
    }

    @Override
    public int getLength()
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }

    @Override
    public <T extends V8Value> T get(int index) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, new V8ValueInteger(index));
    }

    @Override
    public int push(V8Value v8Value) throws JavetException {
        checkV8Runtime();
//        return v8Runtime.push(this, v8Value);
        return 0;
    }
}
