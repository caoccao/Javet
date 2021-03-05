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

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Inspector extends BaseTestJavet {
    protected AtomicInteger atomicInteger;

    public TestV8Inspector() {
        atomicInteger = new AtomicInteger();
    }

    @BeforeEach
    public void beforeEach() {
        atomicInteger.set(0);
    }

    @Test
    public void testEvaluateValue() throws JavetException, TimeoutException, InterruptedException {
        V8Host v8Host = V8Host.getInstance();
        V8Inspector v8Inspector;
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Inspector = v8Runtime.getV8Inspector();
            assertNotNull(v8Inspector);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future future = executorService.submit(() -> {
                try {
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Profiler.enable\"}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.enable\"}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Debugger.enable\",\"params\":{\"maxScriptsCacheSize\":10000000}}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Debugger.setPauseOnExceptions\",\"params\":{\"state\":\"uncaught\"}}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Debugger.setAsyncCallStackDepth\",\"params\":{\"maxDepth\":32}}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.getIsolateId\"}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Debugger.setBlackboxPatterns\",\"params\":{\"patterns\":[]}}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.runIfWaitingForDebugger\"}");
                    v8Runtime.getExecutor("const a = 1;").executeVoid();
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Debugger.resume\"}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.evaluate\",\"params\":{\"expression\":\"a\",\"objectGroup\":\"console\",\"includeCommandLineAPI\":true,\"silent\":false,\"returnByValue\":false,\"generatePreview\":true,\"userGesture\":true,\"awaitPromise\":false,\"replMode\":true,\"allowUnsafeEvalBlockedByCSP\":false}}");
                    v8Runtime.getExecutor("const b = 1;").executeVoid();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("V8 inspector should not throw exception.");
                }
            });
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            assertTrue(future.isDone());
            runAndWait(5000, () -> atomicInteger.get() == v8Inspector.getResponses().size());
        }
        assertEquals(atomicInteger.get(), v8Inspector.getResponses().size());
    }
}
