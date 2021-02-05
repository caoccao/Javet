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
import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueError;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {
    @Test
    public void testCallbackBlank() throws JavetException, NoSuchMethodException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            assertEquals(0, v8Runtime.getCallbackContextCount());
            MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
            V8ValueObject globalObject = v8Runtime.getGlobalObject();
            V8CallbackContext v8CallbackContext = null;
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8CallbackContext = v8Runtime.createCallback(
                        v8ValueObject, "blank", mockCallbackReceiver,
                        mockCallbackReceiver.getMethod("blank"));
                globalObject.set("a", v8ValueObject);
            }
            assertEquals(1, v8Runtime.getCallbackContextCount());
            assertFalse(mockCallbackReceiver.isCalled());
            v8Runtime.executeVoid("a.blank();");
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("a");
            assertTrue(globalObject.get("a") instanceof V8ValueUndefined);
            v8Runtime.removeCallback(v8CallbackContext);
            assertEquals(0, v8Runtime.getCallbackContextCount());
        }
    }

    @Test
    public void testCallbackEchoLILO() throws JavetException, NoSuchMethodException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            assertEquals(0, v8Runtime.getCallbackContextCount());
            MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
            V8CallbackContext v8CallbackContext = v8Runtime.createCallback(
                    v8Runtime.getGlobalObject(), "echo", mockCallbackReceiver,
                    mockCallbackReceiver.getMethodVarargs("echo"));
            assertEquals(1, v8Runtime.getCallbackContextCount());
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueArray v8ValueArray = v8Runtime.execute("var a = echo(1, '2', 3n); a;")) {
                assertEquals(3, v8ValueArray.getLength());
                assertEquals(1, v8ValueArray.getInteger(0));
                assertEquals("2", v8ValueArray.getString(1));
                assertEquals(3L, v8ValueArray.getLong(2));
            }
            assertTrue(mockCallbackReceiver.isCalled());
            v8Runtime.removeCallback(v8CallbackContext);
            assertEquals(0, v8Runtime.getCallbackContextCount());
        }
    }

    @Test
    public void testCallbackEchoVIVO() throws JavetException, NoSuchMethodException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            assertEquals(0, v8Runtime.getCallbackContextCount());
            MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
            V8CallbackContext v8CallbackContext = v8Runtime.createCallback(
                    v8Runtime.getGlobalObject(), "echo", mockCallbackReceiver,
                    mockCallbackReceiver.getMethod("echo", V8Value.class));
            assertEquals(1, v8Runtime.getCallbackContextCount());
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueInteger v8ValueInteger = v8Runtime.execute("const a = echo(1); a;")) {
                assertEquals(1, v8ValueInteger.getValue());
            }
            assertTrue(mockCallbackReceiver.isCalled());
            v8Runtime.removeCallback(v8CallbackContext);
            assertEquals(0, v8Runtime.getCallbackContextCount());
        }
    }

    @Test
    public void testCallbackError() throws JavetException, NoSuchMethodException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
            V8CallbackContext v8CallbackContext = v8Runtime.createCallback(
                    v8Runtime.getGlobalObject(), "testError", mockCallbackReceiver,
                    mockCallbackReceiver.getMethod("error"));
            assertEquals(1, v8Runtime.getCallbackContextCount());
            assertFalse(mockCallbackReceiver.isCalled());
            try {
                v8Runtime.executeVoid("testError();");
                fail("Failed to report Java error.");
            } catch (JavetExecutionException e) {
                assertEquals("Error: Mock error", e.getMessage());
            }
            assertTrue(mockCallbackReceiver.isCalled());
            try (V8ValueError v8ValueError = v8Runtime.execute(
                    "let a; try { testError(); } catch (error) { a = error; } a;")) {
                assertNotNull(v8ValueError);
                assertEquals("Mock error", v8ValueError.getMessage());
            }
            mockCallbackReceiver.setCalled(false);
            v8Runtime.removeCallback(v8CallbackContext);
            assertEquals(0, v8Runtime.getCallbackContextCount());
        }
    }

    @Test
    public void testClose() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
        }
    }

    @Test
    public void testExecute() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            v8Runtime.executeVoid("var a = 1;");
            assertEquals(2, v8Runtime.executeInteger("a + 1"));
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.lock();
            v8Runtime.executeVoid("var a = 1;");
            assertEquals(2, v8Runtime.executeInteger("a + 1"));
        }
    }

    @Test
    public void testLockAndUnlock() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            final int iterations = 3;
            for (int i = 0; i < iterations; ++i) {
                assertThrows(JavetV8RuntimeLockConflictException.class, () -> {
                    v8Runtime.unlock();
                }, "It should not allow unlock before lock.");
                v8Runtime.lock();
                // It should not allow double lock.
                v8Runtime.lock();
                v8Runtime.unlock();
            }
        }
    }

    @Test
    public void testReset() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.reset();
        }
    }

}
