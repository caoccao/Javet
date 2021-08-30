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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.util.Objects;

public class JavetEngine<R extends V8Runtime> implements IJavetEngine<R> {
    protected volatile boolean active;
    protected IJavetEnginePool<R> iJavetEnginePool;
    protected JavetEngineUsage usage;
    protected R v8Runtime;

    public JavetEngine(IJavetEnginePool<R> iJavetEnginePool, R v8Runtime) {
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
        setActive(false);
        if (forceClose) {
            if (iJavetEnginePool.getConfig().isGcBeforeEngineClose()) {
                v8Runtime.lowMemoryNotification();
            }
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
    @CheckReturnValue
    public IJavetEngineGuard getGuard() {
        return getGuard(iJavetEnginePool.getConfig().getDefaultEngineGuardTimeoutMillis());
    }

    @Override
    @CheckReturnValue
    public IJavetEngineGuard getGuard(long timeoutMillis) {
        return new JavetEngineGuard(this, v8Runtime, timeoutMillis);
    }

    /**
     * Gets utc now. It's designed for mocking the time in test scenario.
     *
     * @return the utc now
     */
    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    protected JavetEngineUsage getUsage() {
        return usage;
    }

    @Override
    public R getV8Runtime() throws JavetException {
        setActive(true);
        return v8Runtime;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isClosed() {
        return v8Runtime == null || v8Runtime.isClosed();
    }

    @Override
    public void resetContext() throws JavetException {
        v8Runtime.resetContext();
        usage.reset();
    }

    @Override
    public void resetIsolate() throws JavetException {
        v8Runtime.resetIsolate();
        usage.reset();
    }

    @Override
    public void sendGCNotification() {
        v8Runtime.lowMemoryNotification();
    }

    protected void setActive(boolean active) {
        this.active = active;
        touchLastActiveZonedDateTime();
    }

    protected void touchLastActiveZonedDateTime() {
        usage.setLastActiveZonedDatetime(getUTCNow());
    }

}
