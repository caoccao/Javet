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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.util.Objects;

public class JavetEngine implements IJavetEngine {
    protected boolean active;
    protected IJavetEnginePool iJavetEnginePool;
    protected JavetEngineUsage usage;
    protected V8Runtime v8Runtime;

    public JavetEngine(IJavetEnginePool iJavetEnginePool, V8Runtime v8Runtime) {
        Objects.requireNonNull(v8Runtime);
        this.iJavetEnginePool = iJavetEnginePool;
        this.v8Runtime = v8Runtime;
        usage = new JavetEngineUsage();
        setActive(false);
    }

    @Override
    public void close() throws JavetException {
        close(false);
    }

    protected void close(boolean forceClose) throws JavetException {
        if (v8Runtime.isLocked()) {
            v8Runtime.unlock();
        }
        setActive(false);
        if (forceClose) {
            v8Runtime.close(true);
        } else {
            iJavetEnginePool.releaseEngine(this);
        }
    }

    @Override
    public JavetEngineConfig getConfig() {
        return iJavetEnginePool.getConfig();
    }

    @Override
    public IJavetEngineGuard getGuard() {
        return getGuard(iJavetEnginePool.getConfig().getDefaultEngineGuardTimeoutMillis());
    }

    @Override
    public IJavetEngineGuard getGuard(long timeoutMillis) {
        return new JavetEngineGuard(this, v8Runtime, timeoutMillis);
    }

    protected JavetEngineUsage getUsage() {
        return usage;
    }

    @Override
    public V8Runtime getV8Runtime() throws JavetException {
        setActive(true);
        if (!v8Runtime.isLocked()) {
            v8Runtime.lock();
        }
        return v8Runtime;
    }

    /**
     * Gets utc now. It's designed for mocking the time in test scenario.
     *
     * @return the utc now
     */
    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    @Override
    public void resetContext() throws JavetException {
        resetContext(false);
    }

    @Override
    public void resetContext(boolean skipLock) throws JavetException {
        if (!skipLock) {
            v8Runtime.unlock();
        }
        v8Runtime.resetContext();
        usage.reset();
        if (!skipLock) {
            v8Runtime.lock();
        }
    }

    protected void resetIsolate() throws JavetException {
        resetIsolate(false);
    }

    protected void resetIsolate(boolean skipLock) throws JavetException {
        if (!skipLock) {
            v8Runtime.unlock();
        }
        v8Runtime.resetIsolate();
        usage.reset();
        if (!skipLock) {
            v8Runtime.lock();
        }
    }

    protected void touchLastActiveZonedDateTime() {
        usage.setLastActiveZonedDatetime(getUTCNow());
    }

    @Override
    public boolean isActive() {
        return active;
    }

    protected void setActive(boolean active) {
        this.active = active;
        touchLastActiveZonedDateTime();
    }

}
