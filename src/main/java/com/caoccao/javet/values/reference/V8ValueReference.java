package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.*;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

public abstract class V8ValueReference extends V8Value implements IV8ValueReference {
    protected long handle;

    public V8ValueReference(long handle) {
        super();
        this.handle = handle;
    }

    @Override
    protected void releaseReference() throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        v8Runtime.removeReference(this);
    }

    @Override
    public void setV8Runtime(V8Runtime v8Runtime) throws
            JavetV8RuntimeAlreadyRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException {
        super.setV8Runtime(v8Runtime);
        v8Runtime.addReference(this);
    }

    @Override
    public abstract int getType();

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public void checkV8Runtime() throws
            JavetV8RuntimeNotRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException, JavetV8ValueAlreadyClosedException {
        if (handle == 0L) {
            throw new JavetV8ValueAlreadyClosedException();
        }
        super.checkV8Runtime();
    }

    @Override
    public void close() throws
            JavetV8RuntimeNotRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException, JavetV8ValueAlreadyClosedException {
        super.close();
        handle = 0L;
    }

    @Override
    public String toString() {
        try {
            checkV8Runtime();
            return v8Runtime.toString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
