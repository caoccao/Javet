/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The type V8 guard.
 *
 * @since 3.1.3
 */
public final class V8Guard implements IJavetClosable {
    private final long startTimeMillis;
    private final V8Runtime v8Runtime;
    private boolean closed;
    private boolean debugModeEnabled;
    private long endTimeMillis;

    /**
     * Instantiates a new V8 guard.
     *
     * @param v8Runtime     the V8 runtime
     * @param timeoutMillis the timeout millis
     * @since 3.1.3
     */
    V8Guard(V8Runtime v8Runtime, long timeoutMillis) {
        this(v8Runtime, timeoutMillis, false);
    }

    /**
     * Instantiates a new V8 guard.
     *
     * @param v8Runtime        the V8 runtime
     * @param timeoutMillis    the timeout millis
     * @param debugModeEnabled the debug mode enabled
     * @since 3.1.3
     */
    V8Guard(V8Runtime v8Runtime, long timeoutMillis, boolean debugModeEnabled) {
        assert timeoutMillis > 0 : "timeoutMillis must be greater than 0";
        closed = false;
        this.debugModeEnabled = debugModeEnabled;
        startTimeMillis = System.currentTimeMillis();
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
        setTimeoutMillis(timeoutMillis, true);
    }

    /**
     * Cancel.
     */
    public void cancel() {
        if (!isClosed()) {
            PriorityBlockingQueue<V8Guard> v8GuardQueue = v8Runtime.getV8Host().getV8GuardDaemon().getV8GuardQueue();
            boolean ignored = v8GuardQueue.remove(this);
            closed = true;
        }
    }

    @Override
    public void close() throws JavetException {
        cancel();
    }

    /**
     * Gets end time millis.
     *
     * @return the end time millis
     * @since 3.1.3
     */
    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    /**
     * Gets start time millis.
     *
     * @return the start time millis
     * @since 3.1.3
     */
    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    /**
     * Gets timeout millis.
     *
     * @return the timeout millis
     * @since 3.1.3
     */
    public long getTimeoutMillis() {
        return endTimeMillis - startTimeMillis;
    }

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 3.1.3
     */
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Is debug mode enabled.
     *
     * @return true : yes, false : no
     * @since 3.1.3
     */
    public boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    /**
     * Sets debug mode enabled.
     *
     * @param debugModeEnabled the debug mode enabled
     * @since 3.1.3
     */
    public void setDebugModeEnabled(boolean debugModeEnabled) {
        this.debugModeEnabled = debugModeEnabled;
    }

    /**
     * Sets timeout millis.
     *
     * @param timeoutMillis the timeout millis
     * @since 3.1.3
     */
    public void setTimeoutMillis(long timeoutMillis) {
        setTimeoutMillis(timeoutMillis, false);
    }

    private void setTimeoutMillis(long timeoutMillis, boolean addOnly) {
        endTimeMillis = startTimeMillis + timeoutMillis;
        if (!isClosed()) {
            PriorityBlockingQueue<V8Guard> v8GuardQueue = v8Runtime.getV8Host().getV8GuardDaemon().getV8GuardQueue();
            if (!addOnly) {
                boolean ignored = v8GuardQueue.remove(this);
            }
            v8GuardQueue.add(this);
        }
    }
}
