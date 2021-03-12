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
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8DataModule;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValuePromise;
import com.caoccao.javet.values.reference.global.V8ValueGlobalPromise;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {
    @Test
    public void testAllowEval() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
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
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
        }
    }

    @Test
    public void testExecuteModule() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            String codeString = "export default () => { return { a: 1 }; };";
            IV8Executor iV8Executor = v8Runtime.getExecutor(codeString);
            iV8Executor.getV8ScriptOrigin().setResourceName("./test.js").setModule(true);
            try (V8ValuePromise v8ValuePromise = iV8Executor.execute()) {
                assertNotNull(v8ValuePromise);
                assertTrue(v8ValuePromise.isFulfilled());
                try (V8ValuePromise childV8ValuePromise = v8ValuePromise.then(
                        "() => 1")) {
                    assertTrue(childV8ValuePromise.isPending());
                    try (V8Value v8Value = childV8ValuePromise.getResult()) {
                        assertTrue(v8Value.isUndefined());
                    }
                    try (V8ValueGlobalPromise v8ValueGlobalPromise = v8Runtime.getGlobalObject().getPromise()) {
                        try (V8ValuePromise v8ValuePromiseResolved = v8ValueGlobalPromise.resolve(childV8ValuePromise)) {
                            assertNotNull(v8ValuePromiseResolved);
                            assertEquals(1, v8ValuePromiseResolved.getResultInteger());
                        }
                    }
                }
                try (V8Value v8Value = v8ValuePromise.getResult()) {
                    assertTrue(v8Value.isUndefined());
                }
            }
            codeString = "import * as X from './test.js';";
            iV8Executor = v8Runtime.getExecutor(codeString);
            iV8Executor.getV8ScriptOrigin().setModule(true);
            try (V8Value v8ValuePromise = iV8Executor.execute()) {
                assertNotNull(v8ValuePromise);
            }
        }
    }

    @Test
    public void testExecuteScript() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
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
    public void testResetContext() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
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
        V8Host v8Host = V8Host.getInstance();
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
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            // Create a daemon thread monitoring the V8 runtime status.
            Thread daemonThread = new Thread(() -> {
                // V8 runtime isInUse() does not require lock.
                while (!v8Runtime.isInUse()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // V8 runtime terminateExecution() does not require lock.
                v8Runtime.terminateExecution();
            });
            daemonThread.start();
            try {
                v8Runtime.getExecutor(
                        "var count = 0; while (true) { ++count; }")
                        .executeVoid();
                fail("Failed to throw exception when execution is terminated.");
            } catch (JavetTerminatedException e) {
                assertFalse(e.isContinuable());
            }
            final int count = v8Runtime.getGlobalObject().getInteger("count");
            assertTrue(count > 0, "Count should be greater than 0.");
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                    "V8 runtime should still be able to execute script after being terminated.");
        }
    }
}
