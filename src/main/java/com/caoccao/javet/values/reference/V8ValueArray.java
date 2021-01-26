package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.exceptions.JavetV8RuntimeNotRegisteredException;
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
    public int getLength() throws JavetV8RuntimeLockConflictException, JavetV8RuntimeNotRegisteredException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }
}
