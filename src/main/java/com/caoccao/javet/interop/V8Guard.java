/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The type V8 guard.
 *
 * @since 3.1.3
 */
public final class V8Guard implements IJavetClosable, Delayed {
    /**
     * The constant DEFAULT_TIMEOUT_MILLIS.
     *
     * @since 3.1.3
     */
    public static final int DEFAULT_TIMEOUT_MILLIS = 30000;
    private final long startTimeMillis;
    private final V8Runtime v8Runtime;
    private volatile boolean closed;
    private volatile boolean debugModeEnabled;
    private volatile long endTimeMillis;
    // It is the key of the guard queue, so it is only changed while the guard is not queued.
    private volatile long nextCheckTimeMillis;
    // Protected by the runtime close lock. A scheduled guard is either queued or being checked by the daemon.
    private boolean scheduled;
    // Protected by the runtime close lock. It is set once the expired guard has terminated execution.
    private boolean terminated;

    /**
     * Instantiates a new V8 guard.
     *
     * @param v8Runtime the V8 runtime
     * @since 3.1.3
     */
    V8Guard(V8Runtime v8Runtime) {
        this(v8Runtime, DEFAULT_TIMEOUT_MILLIS, false);
    }

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
        setTimeoutMillis(timeoutMillis);
    }

    /**
     * Cancel.
     *
     * @since 3.1.3
     */
    public void cancel() {
        if (!isClosed()) {
            closed = true;
            ReentrantLock closeLock = v8Runtime.getCloseLock();
            closeLock.lock();
            try {
                DelayQueue<V8Guard> v8GuardQueue = v8Runtime.getV8Host().getV8GuardDaemon().getV8GuardQueue();
                boolean ignored = v8GuardQueue.remove(this);
            } finally {
                closeLock.unlock();
            }
        }
    }

    /**
     * Checks the guard and terminates execution or schedules the next check.
     * <p>
     * Called by the guard daemon after dequeuing this guard. It doesn't wait for the runtime
     * that is being reset or closed, so that the guards of the other runtimes are still checked.
     *
     * @since 6.0.2
     */
    void check() {
        V8Host.V8GuardDaemon v8GuardDaemon = v8Runtime.getV8Host().getV8GuardDaemon();
        ReentrantLock closeLock = v8Runtime.getCloseLock();
        // A reset or a close holds the lock till the native teardown is done, which may take
        // an unbounded time, e.g. while Node.js drains an event loop with an active timer.
        if (!closeLock.tryLock()) {
            // Check the guard again later instead of waiting for the lock. The daemon still owns the guard,
            // so it is the only one that may change the queue key. A closed guard is dropped.
            if (!isClosed()) {
                nextCheckTimeMillis = System.currentTimeMillis() + v8GuardDaemon.getSleepIntervalMillis();
                v8GuardDaemon.getV8GuardQueue().add(this);
            }
            return;
        }
        try {
            if (isClosed() || v8Runtime.isClosed()) {
                scheduled = false;
                return;
            }
            long now = System.currentTimeMillis();
            if (now >= endTimeMillis) {
                if ((debugModeEnabled || !v8GuardDaemon.isInDebugMode()) && v8Runtime.isInUse()) {
                    v8Runtime.terminateExecution();
                    if (!terminated) {
                        terminated = true;
                        v8Runtime.getLogger().logWarn(
                                "Execution was terminated after {0}ms.", now - startTimeMillis);
                    }
                }
                // An expired guard stays armed until it is closed, because the runtime may be idle
                // or in a call that doesn't run JavaScript. It must let other guards reach the head of the queue.
                nextCheckTimeMillis = now + v8GuardDaemon.getSleepIntervalMillis();
            } else {
                nextCheckTimeMillis = endTimeMillis;
            }
            v8GuardDaemon.getV8GuardQueue().add(this);
        } finally {
            closeLock.unlock();
        }
    }

    @Override
    public void close() throws JavetException {
        cancel();
    }

    @Override
    public int compareTo(Delayed delayed) {
        return Long.compare(nextCheckTimeMillis, ((V8Guard) delayed).nextCheckTimeMillis);
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(nextCheckTimeMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
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
        ReentrantLock closeLock = v8Runtime.getCloseLock();
        closeLock.lock();
        try {
            DelayQueue<V8Guard> v8GuardQueue = v8Runtime.getV8Host().getV8GuardDaemon().getV8GuardQueue();
            // Remove before changing the queue key. If the daemon owns this guard, it requeues the guard
            // and reads the new end time when it checks the guard.
            boolean removed = v8GuardQueue.remove(this);
            // Saturate a huge timeout so that the end time doesn't overflow into the past.
            endTimeMillis = timeoutMillis > Long.MAX_VALUE - startTimeMillis
                    ? Long.MAX_VALUE
                    : startTimeMillis + timeoutMillis;
            terminated = false;
            if (!isClosed() && (!scheduled || removed)) {
                nextCheckTimeMillis = endTimeMillis;
                scheduled = true;
                v8GuardQueue.add(this);
            }
        } finally {
            closeLock.unlock();
        }
    }
}
