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

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.observers.IV8RuntimeObserver;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetEnginePool extends BaseTestJavet {
    public static final int TEST_POOL_DAEMON_CHECK_INTERVAL_MILLIS = 1;
    public static final int TEST_MAX_TIMEOUT = 1000;
    protected JavetEngineConfig javetEngineConfig;
    protected JavetEnginePool<?> javetEnginePool;

    @AfterEach
    public void afterEach() throws JavetException {
        assertEquals(0, javetEnginePool.getAverageCallbackContextCount());
        assertEquals(0, javetEnginePool.getAverageReferenceCount());
        assertEquals(0, javetEnginePool.getAverageV8ModuleCount());
        javetEnginePool.close();
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertEquals(javetEnginePool.getConfig().getPoolMaxSize(), javetEnginePool.getReleasedEngineCount());
        assertFalse(javetEnginePool.isActive());
        assertTrue(javetEnginePool.isClosed());
        assertEquals(0, v8Host.getV8RuntimeCount());
    }

    protected void assertStatistics() {
        assertNotNull(javetEnginePool.getAverageV8HeapStatistics().toString());
        assertNotNull(javetEnginePool.getV8SharedMemoryStatistics().toString());
        for (V8AllocationSpace v8AllocationSpace : V8AllocationSpace.getDistinctValues()) {
            V8HeapSpaceStatistics v8HeapSpaceStatistics =
                    javetEnginePool.getAverageV8HeapSpaceStatistics(v8AllocationSpace);
            assertSame(v8AllocationSpace, v8HeapSpaceStatistics.getAllocationSpace());
            assertNotNull(v8HeapSpaceStatistics.toString());
        }
    }

    @BeforeEach
    public void beforeEach() {
        javetEnginePool = new JavetEnginePool<>();
        assertTrue(javetEnginePool.isActive());
        assertFalse(javetEnginePool.isClosed());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        assertEquals(0, javetEnginePool.getIdleEngineCount());
        assertEquals(javetEnginePool.getConfig().getPoolMaxSize(), javetEnginePool.getReleasedEngineCount());
        javetEngineConfig = javetEnginePool.getConfig();
        javetEngineConfig.setPoolDaemonCheckIntervalMillis(TEST_POOL_DAEMON_CHECK_INTERVAL_MILLIS);
        javetEngineConfig.setJSRuntimeType(v8Host.getJSRuntimeType());
        assertEquals(0, javetEnginePool.getAverageCallbackContextCount());
        assertEquals(0, javetEnginePool.getAverageReferenceCount());
        assertEquals(0, javetEnginePool.getAverageV8ModuleCount());
    }

    @Test
    @Tag("performance")
    public void testDaemonThread() throws InterruptedException {
        javetEngineConfig.setWaitForEngineMaxRetryCount(5);
        IJavetLogger javetLogger = javetEngineConfig.getJavetLogger();
        final Random random = new Random();
        final int threadCount = 100;
        final int semaphorePermits = 10;
        final int loopCount = 10;
        final boolean testEmbeddedEngine = true;
        final Semaphore semaphore = new Semaphore(semaphorePermits);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public void run() {
                while (!semaphore.tryAcquire()) {
                    try {
                        long sleepTimeInMillis = random.nextInt(2) + 2;
                        TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
                    } catch (InterruptedException e) {
                        javetLogger.logError("[{0}] is interrupted.", Thread.currentThread().getId());
                        return;
                    }
                }
                long sleepTimeInMillis = random.nextInt(4) + 2;
                javetLogger.logInfo(
                        "[{0}] availablePermits is {1}, sleep {2}ms.",
                        Thread.currentThread().getId(),
                        semaphore.availablePermits(),
                        Long.toString(sleepTimeInMillis));
                if (testEmbeddedEngine) {
                    try (IJavetEngine<?> javetEngine = javetEnginePool.getEngine()) {
                        V8Runtime v8Runtime = javetEngine.getV8Runtime();
                        v8Runtime.getExecutor("1+1").executeVoid();
                        javetLogger.logInfo("[{0}] Execution is successful.", Thread.currentThread().getId());
                    } catch (JavetException e) {
                        javetLogger.logError("[{0}] {1}", Thread.currentThread().getId(), e.getMessage());
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepTimeInMillis);
                } catch (InterruptedException e) {
                    javetLogger.logError("[{0}] is interrupted.", Thread.currentThread().getId());
                }
                semaphore.release();
            }
        };
        for (int i = 0; i < threadCount; ++i) {
            executorService.submit(() -> {
                for (int j = 0; j < loopCount; ++j) {
                    try (IJavetEngine<?> javetEngine = javetEnginePool.getEngine()) {
                        V8Runtime v8Runtime = javetEngine.getV8Runtime();
                        v8Runtime.getGlobalObject().bind(anonymous);
                        v8Runtime.getExecutor("run();").executeVoid();
                        v8Runtime.getGlobalObject().unbind(anonymous);
                    } catch (JavetException e) {
                        javetLogger.logError("[{0}] {1}", Thread.currentThread().getId(), e.getMessage());
                    }
                }
            });
        }
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.MINUTES));
        javetLogger.logInfo("Completed.");
    }

    @Test
    public void testMultiThreadedExecutionBelowMaxSize() throws Exception {
        final int threadCount = javetEngineConfig.getPoolMaxSize() - javetEngineConfig.getPoolMinSize();
        Thread[] threads = new Thread[threadCount];
        Object lockObject = new Object();
        AtomicInteger runningCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        synchronized (lockObject) {
            IntStream.range(0, threadCount).forEach(j -> {
                Thread thread = new Thread(() -> {
                    try (IJavetEngine<?> engine = javetEnginePool.getEngine()) {
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
        javetEnginePool.wakeUpDaemon();
        runAndWait(TEST_MAX_TIMEOUT, () -> threadCount == javetEnginePool.getIdleEngineCount());
        assertEquals(0, failureCount.get());
        assertEquals(threadCount, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        assertStatistics();
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
                    try (IJavetEngine<?> engine = javetEnginePool.getEngine()) {
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
        javetEnginePool.wakeUpDaemon();
        runAndWait(TEST_MAX_TIMEOUT, () -> javetEngineConfig.getPoolMaxSize() == javetEnginePool.getIdleEngineCount());
        assertEquals(0, failureCount.get());
        runAndWait(TEST_MAX_TIMEOUT, () -> 0 == javetEnginePool.getActiveEngineCount());
        assertStatistics();
    }

    @Test
    public void testSingleThreadedExecution() throws Exception {
        List<V8HeapStatistics> v8HeapStatisticsList = new ArrayList<>();
        IV8RuntimeObserver<?> observer =
                v8Runtime -> v8HeapStatisticsList.add(v8Runtime.getV8HeapStatistics());
        assertEquals(0, javetEnginePool.observe(observer));
        try (IJavetEngine<?> engine = javetEnginePool.getEngine()) {
            assertEquals(0, javetEnginePool.getIdleEngineCount());
            assertEquals(1, javetEnginePool.getActiveEngineCount());
            assertEquals(javetEnginePool.getConfig().getPoolMaxSize() - 1, javetEnginePool.getReleasedEngineCount());
            V8Runtime v8Runtime = engine.getV8Runtime();
            assertTrue(v8Runtime.isPooled());
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.close(); // close() doesn't take effect because the V8 runtime is managed by pool
            assertEquals(4, v8Runtime.getExecutor("2 + 2").executeInteger());
            assertThrows(
                    JavetExecutionException.class,
                    () -> v8Runtime.getExecutor("eval('1');").executeVoid(),
                    "By default, the engine pool should disallow eval().");
        }
        runAndWait(TEST_MAX_TIMEOUT, () -> javetEnginePool.getIdleEngineCount() == 1);
        assertEquals(1, javetEnginePool.getIdleEngineCount());
        assertEquals(0, javetEnginePool.getActiveEngineCount());
        assertEquals(javetEnginePool.getConfig().getPoolMaxSize() - 1, javetEnginePool.getReleasedEngineCount());
        assertEquals(1, javetEnginePool.observe(observer));
        assertEquals(1, v8HeapStatisticsList.size());
        assertNotNull(v8HeapStatisticsList.stream().map(V8HeapStatistics::toString).collect(Collectors.joining()));
    }

    @Test
    public void testStatistics() throws Exception {
        final int size = Math.min(4, javetEnginePool.getConfig().getPoolMaxSize());
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public void test() {
            }
        };
        List<IJavetEngine<?>> engines = new ArrayList<>();
        List<V8ValueObject> v8ValueObjects = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            IJavetEngine<?> engine = javetEnginePool.getEngine();
            engines.add(engine);
            v8ValueObjects.add(engine.getV8Runtime().createV8ValueObject());
        }
        assertEquals(0, javetEnginePool.getAverageCallbackContextCount());
        assertEquals(1, javetEnginePool.getAverageReferenceCount());
        for (V8ValueObject v8ValueObject : v8ValueObjects) {
            v8ValueObject.bind(anonymous);
        }
        assertEquals(1, javetEnginePool.getAverageCallbackContextCount());
        assertEquals(1, javetEnginePool.getAverageReferenceCount());
        JavetResourceUtils.safeClose(v8ValueObjects);
        assertEquals(1, javetEnginePool.getAverageCallbackContextCount());
        assertEquals(0, javetEnginePool.getAverageReferenceCount());
        JavetResourceUtils.safeClose(engines);
    }
}
