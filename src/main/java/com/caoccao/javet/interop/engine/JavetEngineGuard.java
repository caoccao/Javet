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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The type Javet engine guard.
 *
 * @since 0.7.2
 */
public class JavetEngineGuard implements IJavetEngineGuard {
    /**
     * The constant IS_IN_DEBUG_MODE.
     *
     * @since 0.8.9
     */
    protected static final boolean IS_IN_DEBUG_MODE =
            /* if defined ANDROID
            false;
            /* end if */
            /* if not defined ANDROID */
            ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
    /* end if */

    /**
     * The Closed.
     *
     * @since 0.9.10
     */
    protected boolean closed;
    /**
     * The Future.
     *
     * @since 0.8.10
     */
    protected Future<?> future;
    /**
     * The Javet engine.
     *
     * @since 0.8.10
     */
    protected IJavetEngine<?> iJavetEngine;
    /**
     * The Quitting.
     *
     * @since 0.8.10
     */
    protected volatile boolean quitting;
    /**
     * The Skip in debug mode.
     *
     * @since 0.8.9
     */
    protected boolean skipInDebugMode;
    /**
     * The Timeout millis.
     *
     * @since 0.8.9
     */
    protected long timeoutMillis;
    /**
     * The V8 runtime.
     *
     * @since 0.7.2
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new Javet engine guard.
     *
     * @param iJavetEngine the javet engine
     * @param v8Runtime    the V8 runtime
     * @param timeoutMills the timeout mills
     * @since 0.7.2
     */
    public JavetEngineGuard(IJavetEngine<?> iJavetEngine, V8Runtime v8Runtime, long timeoutMills) {
        Objects.requireNonNull(iJavetEngine);
        closed = false;
        this.iJavetEngine = iJavetEngine;
        quitting = false;
        skipInDebugMode = true;
        this.timeoutMillis = timeoutMills;
        this.v8Runtime = v8Runtime;
        future = this.iJavetEngine.getConfig().getExecutorService().submit(this);
    }

    @Override
    public void cancel() {
        quitting = true;
    }

    @Override
    public void close() throws JavetException {
        cancel();
        if (!future.isDone() && !future.isCancelled()) {
            future.cancel(true);
        }
        closed = true;
    }

    @Override
    public void disableInDebugMode() {
        skipInDebugMode = true;
    }

    @Override
    public void enableInDebugMode() {
        skipInDebugMode = false;
    }

    @Override
    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * Gets UTC now.
     *
     * @return the UTC now
     * @since 0.9.1
     */
    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    @Override
    public boolean isClosed() {
        return closed || v8Runtime == null || v8Runtime.isClosed();
    }

    /**
     * Is quitting.
     *
     * @return true : quitting, false : not quitting
     * @since 0.7.2
     */
    public boolean isQuitting() {
        return quitting;
    }

    @Override
    public void run() {
        JavetEngineConfig config = iJavetEngine.getConfig();
        IJavetLogger logger = config.getJavetLogger();
        ZonedDateTime startZonedDateTime = getUTCNow();
        while (!isQuitting() && iJavetEngine.isActive()) {
            if (skipInDebugMode && IS_IN_DEBUG_MODE) {
                break;
            }
            ZonedDateTime currentZonedDateTime = getUTCNow();
            if (startZonedDateTime.plusNanos(TimeUnit.MILLISECONDS.toNanos(timeoutMillis))
                    .isBefore(currentZonedDateTime)) {
                try {
                    if (v8Runtime.isInUse()) {
                        // Javet only terminates the execution when V8 runtime is in use.
                        v8Runtime.terminateExecution();
                        Duration duration = Duration.between(startZonedDateTime, currentZonedDateTime);
                        logger.logWarn("Execution was terminated after {0}ms.", duration.toMillis());
                    }
                } catch (Throwable t) {
                    logger.error(t.getMessage(), t);
                }
                break;
            } else {
                try {
                    //noinspection BusyWait
                    Thread.sleep(config.getEngineGuardCheckIntervalMillis());
                } catch (Throwable t) {
                    // It's closed.
                }
            }
        }
        quitting = true;
    }

    @Override
    public void setTimeoutMillis(long timeoutSeconds) {
        this.timeoutMillis = timeoutSeconds;
    }
}
