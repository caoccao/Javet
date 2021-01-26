package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetV8RuntimeAlreadyRegisteredException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

public abstract class V8ValueReference extends V8Value implements IV8ValueReference {
    protected long handle;

    public V8ValueReference(long handle) {
        super();
        this.handle = handle;
    }

    @Override
    protected void releaseReference() throws JavetV8RuntimeLockConflictException {
        v8Runtime.removeReference(this);
    }

    @Override
    public void setV8Runtime(V8Runtime v8Runtime) throws JavetV8RuntimeAlreadyRegisteredException, JavetV8RuntimeLockConflictException {
        super.setV8Runtime(v8Runtime);
        v8Runtime.addReference(this);
    }

    @Override
    public abstract int getType();

    @Override
    public long getHandle() {
        return handle;
    }
}
