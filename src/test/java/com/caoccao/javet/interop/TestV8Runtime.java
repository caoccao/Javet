/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.exceptions.JavetTerminatedException;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {
    @Test
    public void testAllowEval() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.allowEval(true);
            assertEquals(1, v8Runtime.getExecutor("const a = eval('1'); a;").executeInteger());
            v8Runtime.allowEval(false);
            try {
                v8Runtime.getExecutor("const b = eval('1'); b;").executeInteger();
                fail("Failed to disallow eval().");
            } catch (JavetExecutionException e) {
                assertEquals(
                        "EvalError: Code generation from strings disallowed for this context",
                        e.getError().getMessage());
            }
        }
    }

    @Test
    public void testClose() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
        }
    }

    @Test
    public void testExecuteScript() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
            try (V8ValueObject window = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("window", window);
                window.set("x", new V8ValueString("1"));
            }
            assertEquals("1", v8Runtime.getExecutor("window.x;").executeString());
        }
    }

    @Test
    public void testLowMemoryNotification() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testResetContext() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", new V8ValueString("1"));
            v8Runtime.resetContext();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a").isUndefined());
        }
    }

    @Test
    public void testResetIsolate() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", new V8ValueString("1"));
            v8Runtime.resetIsolate();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a").isUndefined());
        }
    }

    @Test
    public void testTerminateExecution() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("var count = 0;").executeVoid();
            V8ValueGlobalObject globalObject = v8Runtime.getGlobalObject();
            // Create a daemon thread monitoring the V8 runtime status.
            Thread daemonThread = new Thread(() -> {
                try {
                    // V8 runtime isInUse() does not require lock.
                    while (true) {
                        if (v8Runtime.isInUse() || globalObject.getInteger("count") > 0) {
                            break;
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
                assertFalse(e.isContinuable());
            }
            final int count = globalObject.getInteger("count");
            assertTrue(count > 0, "Count should be greater than 0.");
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                    "V8 runtime should still be able to execute script after being terminated.");
        }
    }
}
