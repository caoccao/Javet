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

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetTerminatedException;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Guard extends BaseTestJavet {
    @BeforeEach
    public void beforeEach() {
        v8Host.setSleepIntervalMillis(1);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testAutoTerminateExecution(boolean debugModeEnabled) throws JavetException {
        assertEquals(0, v8Host.getV8GuardDaemon().getV8GuardQueue().size());
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            try (V8Guard v8Guard = v8Runtime.getGuard(3)) {
                v8Guard.setDebugModeEnabled(debugModeEnabled);
                assertEquals(1, v8Host.getV8GuardDaemon().getV8GuardQueue().size());
                v8Runtime.getExecutor("var count = 0; while (true) { ++count; }").executeVoid();
                fail("Failed to terminate execution.");
            } catch (JavetException e) {
                assertInstanceOf(JavetTerminatedException.class, e);
                assertEquals(JavetError.ExecutionTerminated, e.getError());
                assertFalse(((JavetTerminatedException) e).isContinuable());
            }
            assertTrue(v8Runtime.getGlobalObject().getInteger("count") > 0);
        }
        assertEquals(0, v8Host.getV8GuardDaemon().getV8GuardQueue().size());
    }

    @Test
    public void testAutoTerminationMultiThreaded() throws InterruptedException {
        final int threadCount = 5;
        final List<Integer> expectedSequence = IntStream.range(0, threadCount).boxed().collect(Collectors.toList());
        final List<Integer> newSequence = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final Object lock = new Object();
        expectedSequence.forEach(i -> executorService.submit(() -> {
            try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
                countDownLatch.countDown();
                synchronized (lock) {
                    lock.wait();
                }
                try (V8Guard v8Guard = v8Runtime.getGuard(10L * (i + 1))) {
                    v8Guard.setDebugModeEnabled(true);
                    v8Runtime.getExecutor("while (true) {}").executeVoid();
                } catch (JavetTerminatedException e) {
                    assertFalse(e.isContinuable());
                }
                synchronized (newSequence) {
                    newSequence.add(i);
                }
            } catch (Exception e) {
                fail(e);
            }
        }));
        countDownLatch.await();
        synchronized (lock) {
            lock.notifyAll();
        }
        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10000, TimeUnit.MILLISECONDS));
        assertEquals(expectedSequence, newSequence);
    }

    @Test
    public void testManualTerminateExecution() throws JavetException {
        final int maxCycle = 3;
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("var count = 0;").executeVoid();
            V8ValueGlobalObject globalObject = v8Runtime.getGlobalObject();
            // Create a daemon thread monitoring the V8 runtime status.
            Thread daemonThread = new Thread(() -> {
                try {
                    int cycle = 0;
                    // V8 runtime isInUse() does not require lock.
                    while (true) {
                        if (v8Runtime.isInUse() || globalObject.getInteger("count") > 0) {
                            ++cycle;
                            if (cycle >= maxCycle) {
                                break;
                            }
                        } else {
                            cycle = 0;
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // V8 runtime terminateExecution() does not require lock.
                    v8Runtime.terminateExecution();
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            });
            daemonThread.start();
            try {
                v8Runtime.getExecutor("while (true) { ++count; }").executeVoid();
                fail("Failed to throw exception when execution is terminated.");
            } catch (JavetTerminatedException e) {
                assertEquals(JavetError.ExecutionTerminated, e.getError());
                assertFalse(e.isContinuable());
            }
            final int count = globalObject.getInteger("count");
            assertTrue(count > 0, "Count should be greater than 0.");
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                    "V8 runtime should still be able to execute script after being terminated.");
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testWithoutTermination(boolean debugModeEnabled) throws JavetException {
        final long timeoutMillis = 10000;
        long startTimeMillis = System.currentTimeMillis();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            try (V8Guard v8Guard = v8Runtime.getGuard(timeoutMillis)) {
                v8Guard.setDebugModeEnabled(debugModeEnabled);
                assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            }
        }
        long endTimeMillis = System.currentTimeMillis();
        assertTrue(endTimeMillis - startTimeMillis < timeoutMillis);
    }
}
