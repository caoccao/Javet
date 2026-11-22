/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.interop.monitoring.V8StatisticsFuture;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.values.reference.V8ValueFunction;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Host extends BaseTestJavet {
    private static final long TIMEOUT_MILLIS = 10_000;

    @Test
    public void testAllRuntimes() {
        IntStream.range(0, 2).forEach(i -> Stream.of(JSRuntimeType.values())
                .filter(type -> {
                    File libFile = BaseTestJavet.getLibFile(type);
                    return libFile != null && libFile.exists();
                })
                .forEach(type -> {
                    try (V8Runtime v8Runtime = V8Host.getInstance(type).createV8Runtime()) {
                        assertNotNull(v8Runtime);
                        assertEquals(type, v8Runtime.getJSRuntimeType());
                        assertEquals(2, v8Runtime.getExecutor("1+1").executeInteger());
                    } catch (JavetException e) {
                        fail(e);
                    }
                }));
    }

    @Test
    public void testCreateV8RuntimeWithGlobalName() throws JavetException {
        if (isV8()) {
            V8RuntimeOptions runtimeOptions = v8Host.getJSRuntimeType().getRuntimeOptions();
            runtimeOptions.setGlobalName("window");
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(runtimeOptions)) {
                assertNotNull(v8Runtime);
                assertTrue(v8Host.isIsolateCreated());
            }
        }
    }

    @Test
    public void testCreateV8RuntimeWithoutGlobalName() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertNotNull(v8Runtime);
            assertTrue(v8Host.isIsolateCreated());
        }
    }

    @Test
    public void testLogJSRuntimeType() {
        JSRuntimeType jsRuntimeType = v8Host.getJSRuntimeType();
        logger.logInfo("JS runtime type is {0}, version is {1}.",
                jsRuntimeType.getName(), jsRuntimeType.getVersion());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPurgeV8StatisticsFutureBeforeInterrupt() throws Exception {
        final CountDownLatch blockedLatch = new CountDownLatch(1);
        final CountDownLatch releaseLatch = new CountDownLatch(1);
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            JavetCallbackContext callbackContext = new JavetCallbackContext(
                    "block", JavetCallbackType.DirectCallNoThisAndNoResult,
                    (IJavetDirectCallable.NoThisAndNoResult<InterruptedException>) (v8Values) -> {
                        blockedLatch.countDown();
                        assertTrue(releaseLatch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                    });
            try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(callbackContext)) {
                v8Runtime.getGlobalObject().set("block", v8ValueFunction);
            }
            final AtomicReference<Throwable> executionThrowable = new AtomicReference<>();
            Thread executionThread = new Thread(() -> {
                try {
                    // The interrupt is delivered when the function is called after block() returns.
                    v8Runtime.getExecutor("block(); (() => {})();").executeVoid();
                } catch (Throwable t) {
                    executionThrowable.set(t);
                }
            });
            executionThread.start();
            final V8StatisticsFuture<V8HeapStatistics> v8StatisticsFuture;
            try {
                assertTrue(blockedLatch.await(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                // The isolate is in use, so the request waits for the interrupt.
                v8StatisticsFuture = (V8StatisticsFuture<V8HeapStatistics>) v8Runtime.getV8HeapStatistics();
                assertFalse(v8StatisticsFuture.isDone());
                assertNotEquals(V8StatisticsFuture.INVALID_HANDLE, v8StatisticsFuture.getHandle());
                final AtomicReference<Throwable> joinThrowable = new AtomicReference<>();
                Thread joinThread = new Thread(() -> {
                    try {
                        v8StatisticsFuture.join();
                    } catch (Throwable t) {
                        joinThrowable.set(t);
                    }
                });
                joinThread.setDaemon(true);
                joinThread.start();
                runAndWait(TIMEOUT_MILLIS, () -> joinThread.getState() == Thread.State.WAITING);
                // The purge claims the request before the interrupt is delivered.
                V8Host.purgeV8StatisticsFuture(v8StatisticsFuture, v8Host.getV8Native());
                joinThread.join(TIMEOUT_MILLIS);
                assertFalse(joinThread.isAlive(), "The caller waiting in join() should be released.");
                assertInstanceOf(CancellationException.class, joinThrowable.get());
                assertTrue(v8StatisticsFuture.isCancelled());
            } finally {
                releaseLatch.countDown();
                executionThread.join(TIMEOUT_MILLIS);
            }
            assertFalse(executionThread.isAlive());
            assertNull(executionThrowable.get());
            // The late interrupt releases the native request, but doesn't complete the future.
            assertEquals(V8StatisticsFuture.INVALID_HANDLE, v8StatisticsFuture.getHandle());
            assertTrue(v8StatisticsFuture.isCancelled());
            v8Runtime.getGlobalObject().delete("block");
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
        }
    }
}
