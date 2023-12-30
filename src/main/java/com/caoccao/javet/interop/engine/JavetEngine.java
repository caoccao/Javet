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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The type Javet engine.
 *
 * @param <R> the type parameter
 * @since 0.7.0
 */
public class JavetEngine<R extends V8Runtime> implements IJavetEngine<R> {
    /**
     * The Active.
     *
     * @since 0.7.0
     */
    protected volatile boolean active;
    /**
     * The Javet engine pool.
     *
     * @since 0.7.0
     */
    protected IJavetEnginePool<R> iJavetEnginePool;
    /**
     * The Index.
     *
     * @since 1.0.5
     */
    protected int index;
    /**
     * The Usage.
     *
     * @since 0.7.0
     */
    protected JavetEngineUsage usage;
    /**
     * The V8 runtime.
     *
     * @since 0.7.0
     */
    protected R v8Runtime;

    /**
     * Instantiates a new Javet engine.
     *
     * @param iJavetEnginePool the javet engine pool
     * @param v8Runtime        the V8 runtime
     * @since 0.7.0
     */
    public JavetEngine(IJavetEnginePool<R> iJavetEnginePool, R v8Runtime) {
        this.iJavetEnginePool = Objects.requireNonNull(iJavetEnginePool);
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
        usage = new JavetEngineUsage();
        setActive(false);
    }

    @Override
    public void close() throws JavetException {
        close(false);
    }

    /**
     * Close.
     *
     * @param forceClose the force close
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    protected void close(boolean forceClose) throws JavetException {
        setActive(false);
        if (forceClose) {
            if (iJavetEnginePool.getConfig().isGCBeforeEngineClose()) {
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
     * Gets index.
     *
     * @return the index
     * @since 1.0.5
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets UTC now. It's designed for mocking the time in test scenario.
     *
     * @return the UTC now
     * @since 0.9.1
     */
    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    /**
     * Gets usage.
     *
     * @return the usage
     * @since 0.7.0
     */
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

    /**
     * Sets active.
     *
     * @param active the active
     * @since 0.7.0
     */
    protected void setActive(boolean active) {
        this.active = active;
        touchLastActiveZonedDateTime();
    }

    /**
     * Sets index.
     *
     * @param index the index
     * @since 1.0.5
     */
    void setIndex(int index) {
        assert index >= 0 : "Engine index must be no less than 0.";
        assert index < iJavetEnginePool.getConfig().getPoolMaxSize()
                : "Engine index must be less than " + iJavetEnginePool.getConfig().getPoolMaxSize() + ".";
        this.index = index;
    }

    /**
     * Touch last active zoned date time.
     *
     * @since 0.9.1
     */
    protected void touchLastActiveZonedDateTime() {
        usage.setLastActiveZonedDatetime(getUTCNow());
    }

}
