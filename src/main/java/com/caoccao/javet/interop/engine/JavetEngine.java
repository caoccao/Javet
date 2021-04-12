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

@SuppressWarnings("unchecked")
public class JavetEngine<R extends V8Runtime> implements IJavetEngine<R> {
    protected boolean active;
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
            v8Runtime.close(true);
        } else {
            iJavetEnginePool.releaseEngine(this);
        }
    }

    @Override
    public void sendGCNotification() {
        v8Runtime.lowMemoryNotification();
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
    public R getV8Runtime() throws JavetException {
        setActive(true);
        return (R) v8Runtime;
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
        v8Runtime.resetContext();
        usage.reset();
    }

    @Override
    public void resetIsolate() throws JavetException {
        v8Runtime.resetIsolate();
        usage.reset();
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
