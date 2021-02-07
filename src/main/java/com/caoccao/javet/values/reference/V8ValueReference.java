/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

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
    protected void addReference() throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException {
        v8Runtime.addReference(this);
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
    public void clearWeak() throws JavetException {
        v8Runtime.clearWeak(this);
    }

    @Override
    public void close() throws JavetException {
        close(false);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (forceClose || !isWeak()) {
            super.close();
            handle = 0L;
        }
    }

    @Override
    public abstract int getType();

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public boolean isWeak() throws JavetException {
        return v8Runtime.isWeak(this);
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
        addReference();
    }

    @Override
    public void setWeak() throws JavetException {
        v8Runtime.setWeak(this);
    }

    @Override
    public String toProtoString() {
        try {
            checkV8Runtime();
            return v8Runtime.toProtoString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
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
