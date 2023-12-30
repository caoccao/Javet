/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

public abstract class V8ValueReference extends V8Value implements IV8ValueReference {
    public static final long INVALID_HANDLE = 0L;
    protected long handle;
    protected boolean weak;

    V8ValueReference(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime);
        this.handle = handle;
        weak = false;
        addReference();
    }

    protected void addReference() throws JavetException {
        checkV8Runtime().getV8Internal().addReference(this);
    }

    @Override
    public V8Runtime checkV8Runtime() throws JavetException {
        if (isClosed()) {
            throw new JavetException(JavetError.RuntimeAlreadyClosed);
        }
        return super.checkV8Runtime();
    }

    @Override
    public void clearWeak() throws JavetException {
        checkV8Runtime().getV8Internal().clearWeak(this);
        weak = false;
    }

    @Override
    public void close() throws JavetException {
        close(false);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (isClosed()) {
            throw new JavetException(JavetError.RuntimeAlreadyClosed);
        }
        if (forceClose || !isWeak()) {
            removeReference();
            handle = INVALID_HANDLE;
            v8Runtime = null;
            weak = false;
        }
    }

    @Override
    public boolean equals(V8Value v8Value) throws JavetException {
        if (!(v8Value instanceof V8ValueReference)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueReference v8ValueReference = (V8ValueReference) v8Value;
        if (getHandle() == v8ValueReference.getHandle()) {
            return true;
        }
        return checkV8Runtime().getV8Internal().equals(this, v8ValueReference);
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public abstract V8ValueReferenceType getType();

    @Override
    public boolean isClosed() {
        return handle == INVALID_HANDLE || super.isClosed();
    }

    @Override
    public boolean isWeak() {
        return weak;
    }

    @Override
    public boolean isWeak(boolean force) throws JavetException {
        if (force) {
            weak = checkV8Runtime().getV8Internal().isWeak(this);
        }
        return weak;
    }

    protected void removeReference() throws JavetException {
        checkV8Runtime().getV8Internal().removeReference(this);
    }

    @Override
    public boolean sameValue(V8Value v8Value) throws JavetException {
        if (!(v8Value instanceof V8ValueReference)) {
            return false;
        }
        return ((V8ValueReference) v8Value).getHandle() == getHandle();
    }

    @Override
    public void setWeak() throws JavetException {
        checkV8Runtime().getV8Internal().setWeak(this);
        weak = true;
    }

    @Override
    public boolean strictEquals(V8Value v8Value) throws JavetException {
        return sameValue(v8Value);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T toClone(boolean referenceCopy) throws JavetException {
        return checkV8Runtime().getV8Internal().cloneV8Value(this, referenceCopy);
    }

    @Override
    public String toString() {
        try {
            return checkV8Runtime().getV8Internal().toString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
