/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values;

import com.caoccao.javet.exceptions.*;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;

public abstract class V8Value implements IJavetClosable, Cloneable {
    protected V8Runtime v8Runtime;

    public V8Value() {
        v8Runtime = null;
    }

    protected abstract void addReference() throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException;

    public void checkV8Runtime() throws
            JavetV8RuntimeNotRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException, JavetV8ValueAlreadyClosedException {
        if (v8Runtime == null) {
            throw new JavetV8RuntimeNotRegisteredException();
        }
        this.v8Runtime.checkLock();
    }

    @Override
    public void close() throws
            JavetV8RuntimeNotRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException, JavetV8ValueAlreadyClosedException {
        checkV8Runtime();
        releaseReference();
        v8Runtime = null;
    }

    public abstract V8Value clone();

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    public void setV8Runtime(V8Runtime v8Runtime) throws
            JavetV8RuntimeAlreadyRegisteredException, JavetV8RuntimeLockConflictException,
            JavetV8RuntimeAlreadyClosedException {
        if (this.v8Runtime != null) {
            throw new JavetV8RuntimeAlreadyRegisteredException();
        }
        this.v8Runtime = v8Runtime;
        this.v8Runtime.checkLock();
    }

    protected abstract void releaseReference() throws
            JavetV8RuntimeLockConflictException, JavetV8RuntimeAlreadyClosedException;
}
