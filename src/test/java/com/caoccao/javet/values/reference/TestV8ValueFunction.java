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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.exceptions.JavetV8ValueAlreadyClosedException;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.utils.V8CallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueFunction extends BaseTestJavetRuntime {
    @Test
    public void testArrayPush() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.execute("const a = []; a;")) {
            assertNotNull(v8ValueArray);
            try (V8ValueFunction v8ValueFunctionPush = v8ValueArray.get("push")) {
                assertNotNull(v8ValueFunctionPush);
                assertEquals("function push() { [native code] }", v8ValueFunctionPush.toString());
                assertEquals(1, v8ValueFunctionPush.callInteger(v8ValueArray, new V8ValueString("x")));
            }
            assertEquals(1, v8ValueArray.getLength());
            assertEquals("x", v8ValueArray.toString());
        }
    }

    @Test
    public void testCallbackBlankWithoutThis() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("blank"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext);
        assertTrue(v8CallbackContext.getHandle() > 0L);
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            a.set("blank", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            v8Runtime.executeVoid("a.blank();");
            assertTrue(mockCallbackReceiver.isCalled());
            v8ValueFunction.setWeak();
            a.delete("blank");
            globalObject.delete("a");
        }
        assertEquals(1, v8Runtime.getReferenceCount());
        v8Runtime.requestGarbageCollectionForTesting(true);
        assertEquals(0, v8Runtime.getReferenceCount());
        assertThrows(JavetV8ValueAlreadyClosedException.class, () -> v8ValueFunction.close());
    }

    @Test
    public void testCallbackBlankWithThis() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThis", true), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext);
        assertTrue(v8CallbackContext.getHandle() > 0L);
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            a.set("x", new V8ValueString("1"));
            a.set("echoThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            assertEquals("{\"x\":\"1\"}", v8Runtime.executeString("a.echoThis();"));
            assertTrue(mockCallbackReceiver.isCalled());
            v8ValueFunction.setWeak();
            a.delete("echoThis");
            globalObject.delete("a");
        }
        assertEquals(1, v8Runtime.getReferenceCount());
        v8Runtime.requestGarbageCollectionForTesting(true);
        assertEquals(0, v8Runtime.getReferenceCount());
        assertThrows(JavetV8ValueAlreadyClosedException.class, () -> v8ValueFunction.close());
    }

    @Test
    public void testCallbackEchoLILOWithoutThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethodVarargs("echo"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("echo", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueArray v8ValueArray = v8Runtime.execute("var a = echo(1, '2', 3n); a;")) {
                assertEquals(3, v8ValueArray.getLength());
                assertEquals(1, v8ValueArray.getInteger(0));
                assertEquals("2", v8ValueArray.getString(1));
                assertEquals(3L, v8ValueArray.getLong(2));
            }
            assertTrue(globalObject.hasOwnProperty("a"));
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("echo");
        }
        assertEquals(0, v8Runtime.getReferenceCount());
    }

    @Test
    public void testCallbackEchoLILOWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethodVarargs("echoThis", true), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("x", new V8ValueString("1"));
            globalObject.set("echoThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueArray v8ValueArray = v8Runtime.execute("var a = echoThis(1, '2', 3n); a;")) {
                assertEquals(2, v8Runtime.getReferenceCount());
                assertEquals(4, v8ValueArray.getLength());
                try (V8ValueObject v8ValueObject = v8ValueArray.get(0)) {
                    assertEquals("1", v8ValueObject.getString("x"));
                }
                assertEquals(1, v8ValueArray.getInteger(1));
                assertEquals("2", v8ValueArray.getString(2));
                assertEquals(3L, v8ValueArray.getLong(3));
            }
            assertTrue(globalObject.hasOwnProperty("a"));
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("echoThis");
        }
        assertEquals(0, v8Runtime.getReferenceCount());
    }

    @Test
    public void testCallbackEchoVIVOWithoutThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echo", V8Value.class));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("echo", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueInteger v8ValueInteger = v8Runtime.execute("const a = echo(1); a;")) {
                assertEquals(1, v8ValueInteger.getValue());
            }
            globalObject.delete("echo");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        assertTrue(globalObject.get("a") instanceof V8ValueUndefined);
        assertEquals(0, v8Runtime.getReferenceCount());
    }

    @Test
    public void testCallbackEchoVIVOWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThis", V8Value.class, V8Value.class), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
                a.set("x", new V8ValueString("1"));
                a.set("echoThis", v8ValueFunction);
                globalObject.set("a", a);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueString v8ValueString = v8Runtime.execute("const x = a.echoThis('123'); x;")) {
                assertEquals("[{\"x\":\"1\"},\"123\"]", v8ValueString.getValue());
            }
        }
        assertTrue(mockCallbackReceiver.isCalled());
        assertEquals(0, v8Runtime.getReferenceCount());
    }

    @Test
    public void testCallbackError() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        V8CallbackContext v8CallbackContext = new V8CallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("error"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(v8CallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("testError", v8ValueFunction);
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
            globalObject.delete("testError");
        }
        mockCallbackReceiver.setCalled(false);
        assertEquals(0, v8Runtime.getReferenceCount());
    }
}
