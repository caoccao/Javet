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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.SimpleMap;

import java.util.Objects;

/**
 * The type V8 locker.
 * It's designed for performance sensitive scenarios.
 *
 * @since 0.7.3
 */
public final class V8Locker implements IJavetClosable {
    private final long threadId;
    private final IV8Native v8Native;
    private final V8Runtime v8Runtime;
    private volatile boolean locked;

    /**
     * Instantiates a new V8 locker.
     *
     * @param v8Runtime the V8 runtime
     * @param v8Native  the V8 native
     * @throws JavetException the javet exception
     * @since 0.7.3
     */
    V8Locker(V8Runtime v8Runtime, IV8Native v8Native) throws JavetException {
        Objects.requireNonNull(v8Runtime);
        threadId = Thread.currentThread().getId();
        this.v8Native = v8Native;
        this.v8Runtime = v8Runtime;
        if (!v8Native.lockV8Runtime(v8Runtime.getHandle())) {
            throw new JavetException(JavetError.LockAcquisitionFailure);
        }
        locked = true;
    }

    @Override
    public void close() throws JavetException {
        final long currentThreadId = Thread.currentThread().getId();
        if (threadId != currentThreadId) {
            throw new JavetException(JavetError.LockConflictThreadIdMismatch, SimpleMap.of(
                    JavetError.PARAMETER_LOCKED_THREAD_ID, Long.toString(threadId),
                    JavetError.PARAMETER_CURRENT_THREAD_ID, Long.toString(currentThreadId)));
        }
        if (!v8Native.unlockV8Runtime(v8Runtime.getHandle())) {
            throw new JavetException(JavetError.LockReleaseFailure);
        }
        locked = false;
    }

    @Override
    public boolean isClosed() {
        return !locked;
    }

    /**
     * Is locked.
     *
     * @return the boolean
     * @since 0.9.10
     */
    public boolean isLocked() {
        return locked;
    }
}
