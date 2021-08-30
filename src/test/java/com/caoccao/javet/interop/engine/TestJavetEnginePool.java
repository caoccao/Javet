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

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.executors.IV8Executor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class TestJavetEnginePool extends BaseTestJavet {
    public static final int TEST_POOL_DAEMON_CHECK_INTERVAL_MILLIS = 1;
    public static final int TEST_MAX_TIMEOUT = 1000;
    private JavetEngineConfig javetEngineConfig;
    private JavetEnginePool javetEnginePool;

    @AfterEach
    private void afterEach() throws JavetException {
        javetEnginePool.close();
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertFalse(javetEnginePool.isActive());
        assertTrue(javetEnginePool.isClosed());
        assertEquals(0, v8Host.getV8RuntimeCount());
    }

    @BeforeEach
    private void beforeEach() {
        javetEnginePool = new JavetEnginePool();
        assertTrue(javetEnginePool.isActive());
        assertFalse(javetEnginePool.isClosed());
        javetEngineConfig = javetEnginePool.getConfig();
        javetEngineConfig.setPoolDaemonCheckIntervalMillis(TEST_POOL_DAEMON_CHECK_INTERVAL_MILLIS);
        javetEngineConfig.setJSRuntimeType(v8Host.getJSRuntimeType());
    }

    @Test
    public void testMultiThreadedExecutionBelowMaxSize() throws Exception {
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        final int threadCount = javetEngineConfig.getPoolMaxSize() - javetEngineConfig.getPoolMinSize();
        Thread[] threads = new Thread[threadCount];
        Object lockObject = new Object();
        AtomicInteger runningCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        synchronized (lockObject) {
            IntStream.range(0, threadCount).forEach(j -> {
                Thread thread = new Thread(() -> {
                    try (IJavetEngine engine = javetEnginePool.getEngine()) {
                        runningCount.incrementAndGet();
                        V8Runtime v8Runtime = engine.getV8Runtime();
                        IV8Executor iV8Executor;
                        synchronized (lockObject) {
                            iV8Executor = v8Runtime.getExecutor("1+1");
                        }
                        assertEquals(2, iV8Executor.executeInteger());
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        logger.logError("Failed to execute. Error: {0}.", e.getMessage());
                    }
                });
                thread.start();
                threads[j] = thread;
            });
            runAndWait(TEST_MAX_TIMEOUT, () -> runningCount.get() == threadCount);
            runAndWait(TEST_MAX_TIMEOUT, () -> 0 == javetEnginePool.getIdleEngineCount());
            runAndWait(TEST_MAX_TIMEOUT, () -> threadCount == javetEnginePool.getActiveEngineCount());
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.logError("Failed to join the worker thread. Error: {0}.", e.getMessage());
            }
        }
        javetEnginePool.releaseEngine(null);
        runAndWait(TEST_MAX_TIMEOUT, () -> threadCount == javetEnginePool.getIdleEngineCount());
        assertEquals(0, failureCount.get());
        assertEquals(threadCount, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
    }

    @Test
    public void testMultiThreadedExecutionExceedsMaxSize() throws Exception {
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        final int threadCount = javetEngineConfig.getPoolMaxSize() + javetEngineConfig.getPoolMinSize();
        Thread[] threads = new Thread[threadCount];
        Object lockObject = new Object();
        AtomicInteger runningCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        synchronized (lockObject) {
            IntStream.range(0, threadCount).forEach(j -> {
                Thread thread = new Thread(() -> {
                    try (IJavetEngine engine = javetEnginePool.getEngine()) {
                        runningCount.incrementAndGet();
                        IV8Executor iV8Executor;
                        synchronized (lockObject) {
                            iV8Executor = engine.getV8Runtime().getExecutor("1 + 1");
                        }
                        assertEquals(2, iV8Executor.executeInteger());
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        logger.logError(e, "Failed to execute. Error: {0}.", e.getMessage());
                    }
                });
                thread.start();
                threads[j] = thread;
            });
            runAndWait(TEST_MAX_TIMEOUT, () -> runningCount.get() <= threadCount);
            // There shouldn't be any idle engines.
            runAndWait(TEST_MAX_TIMEOUT, () -> 0 == javetEnginePool.getIdleEngineCount());
            // Due to concurrent issue, actual engine count may be greater than max pool size.
            runAndWait(TEST_MAX_TIMEOUT, () -> javetEngineConfig.getPoolMaxSize() <= javetEnginePool.getActiveEngineCount());
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.logError("Failed to join the worker thread. Error: {0}.", e.getMessage());
            }
        }
        javetEnginePool.releaseEngine(null);
        runAndWait(TEST_MAX_TIMEOUT, () -> javetEngineConfig.getPoolMaxSize() == javetEnginePool.getIdleEngineCount());
        assertEquals(0, failureCount.get());
        runAndWait(TEST_MAX_TIMEOUT, () -> 0 == javetEnginePool.getActiveEngineCount());
    }

    @Test
    public void testSingleThreadedExecution() throws Exception {
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        try (IJavetEngine engine = javetEnginePool.getEngine()) {
            assertEquals(0, javetEnginePool.getIdleEngineCount());
            assertEquals(1, javetEnginePool.getActiveEngineCount());
            V8Runtime v8Runtime = engine.getV8Runtime();
            assertTrue(v8Runtime.isPooled());
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.close(); // close() doesn't take effect because the V8 runtime is managed by pool
            assertEquals(4, v8Runtime.getExecutor("2 + 2").executeInteger());
            assertThrows(JavetExecutionException.class, () -> {
                v8Runtime.getExecutor("eval('1');").executeVoid();
            }, "By default, the engine pool should disallow eval().");
        }
        runAndWait(TEST_MAX_TIMEOUT, () -> javetEnginePool.getIdleEngineCount() == 1);
        assertEquals(1, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
    }
}
