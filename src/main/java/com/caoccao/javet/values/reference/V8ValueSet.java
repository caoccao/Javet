package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetNotSupportedException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

public class V8ValueSet extends V8ValueObject {

    public static final String SET_GET = "Set.get()";

    public V8ValueSet(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Set;
    }

    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

    @Override
    public <T extends V8Value> T getValue(int index) throws JavetException {
        throw new JavetNotSupportedException(SET_GET);
    }

    @Override
    public <T extends V8Value> T getValue(String key) throws JavetException {
        throw new JavetNotSupportedException(SET_GET);
    }
}
