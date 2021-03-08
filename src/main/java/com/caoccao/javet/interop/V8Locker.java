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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetV8LockConflictException;
import com.caoccao.javet.interfaces.IJavetClosable;

import java.util.Objects;

/**
 * The type V8 locker.
 * It's designed for performance sensitive scenarios.
 */
public final class V8Locker implements IJavetClosable {
    private long threadId;
    private V8Runtime v8Runtime;

    /**
     * Instantiates a new V8 locker.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetV8LockConflictException the javet V8 lock conflict exception
     */
    V8Locker(V8Runtime v8Runtime) throws JavetV8LockConflictException {
        Objects.requireNonNull(v8Runtime);
        threadId = Thread.currentThread().getId();
        this.v8Runtime = v8Runtime;
        V8Native.lockV8Runtime(v8Runtime.getHandle());
    }

    @Override
    public void close() throws JavetException {
        final long currentThreadId = Thread.currentThread().getId();
        if (threadId != currentThreadId) {
            throw JavetV8LockConflictException.threadIdMismatch(threadId, currentThreadId);
        }
        V8Native.unlockV8Runtime(v8Runtime.getHandle());
    }
}
