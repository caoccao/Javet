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
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueFunction extends BaseTestJavetRuntime {
    @Test
    public void testAnonymousFunction() throws JavetException {
        String codeString = "() => '123測試'";
        try (V8Value v8Value = v8Runtime.getExecutor(codeString).execute()) {
            assertNotNull(v8Value);
            assertTrue(v8Value instanceof V8ValueFunction);
            V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Value;
            assertEquals(codeString, v8ValueFunction.toString());
            assertEquals("123測試", v8ValueFunction.callString(null));
        }
        v8Runtime.getGlobalObject().setFunction("a", codeString);
        assertEquals("123測試", v8Runtime.getExecutor("a();").executeString());
        v8Runtime.getGlobalObject().setFunction("b", "(x) => x + 1;");
        assertEquals(2, v8Runtime.getExecutor("b(1);").executeInteger());
        v8Runtime.getGlobalObject().delete("a");
        v8Runtime.getGlobalObject().delete("b");
        v8Runtime.requestGarbageCollectionForTesting(true);
    }

    @Test
    public void testArrayPush() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = []; a;").execute()) {
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
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("blank"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext);
        assertTrue(javetCallbackContext.getHandle() > 0L);
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            a.set("blank", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            v8Runtime.getExecutor("a.blank();").executeVoid();
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
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThis", true), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext);
        assertTrue(javetCallbackContext.getHandle() > 0L);
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            a.set("x", new V8ValueString("1"));
            a.set("echoThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            assertEquals("{\"x\":\"1\"}", v8Runtime.getExecutor("a.echoThis();").executeString());
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
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethodVarargs("echo"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("echo", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("var a = echo(1, '2', 3n); a;").execute()) {
                assertEquals(3, v8ValueArray.getLength());
                assertEquals(1, v8ValueArray.getInteger(0));
                assertEquals("2", v8ValueArray.getString(1));
                assertEquals(3L, v8ValueArray.getLong(2));
            }
            assertTrue(globalObject.hasOwnProperty("a"));
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("echo");
        }
    }

    @Test
    public void testCallbackEchoLILOWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethodVarargs("echoThis", true), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("x", new V8ValueString("1"));
            globalObject.set("echoThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("var a = echoThis(1, '2', 3n); a;").execute()) {
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
    }

    @Test
    public void testCallbackEchoStringWithoutThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoString", String.class));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("echoString", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            assertEquals("abc", v8Runtime.getExecutor("const a = echoString('abc'); a;").executeString());
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }

    @Test
    public void testCallbackEchoStringWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThisString", V8Value.class, String.class), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
                a.set("x", new V8ValueString("1"));
                a.set("echoThisString", v8ValueFunction);
                globalObject.set("a", a);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueString v8ValueString = v8Runtime.getExecutor(
                    "const x = a.echoThisString('123'); x;").execute()) {
                assertEquals("[{\"x\":\"1\"},\"123\"]", v8ValueString.getValue());
            }
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }

    @Test
    public void testCallbackEchoVIVOWithoutThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echo", V8Value.class));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("echo", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueInteger v8ValueInteger = v8Runtime.getExecutor("const a = echo(1); a;").execute()) {
                assertEquals(1, v8ValueInteger.getValue());
            }
            globalObject.delete("echo");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        assertTrue(globalObject.get("a").isUndefined());
    }

    @Test
    public void testCallbackEchoVIVOWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThis", V8Value.class, V8Value.class), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
                a.set("x", new V8ValueString("1"));
                a.set("echoThis", v8ValueFunction);
                globalObject.set("a", a);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueString v8ValueString = v8Runtime.getExecutor("const x = a.echoThis('123'); x;").execute()) {
                assertEquals("[{\"x\":\"1\"},\"123\"]", v8ValueString.getValue());
            }
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }

    @Test
    public void testCallbackError() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("error"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        globalObject.setFunction("testError", javetCallbackContext);
        assertEquals(1, v8Runtime.getReferenceCount());
        assertFalse(mockCallbackReceiver.isCalled());
        try {
            v8Runtime.getExecutor("testError();").executeVoid();
            fail("Failed to report Java error.");
        } catch (JavetExecutionException e) {
            assertEquals("Error: Mock error", e.getMessage());
        }
        assertTrue(mockCallbackReceiver.isCalled());
        try (V8ValueError v8ValueError = v8Runtime.getExecutor(
                "let a; try { testError(); } catch (error) { a = error; } a;").execute()) {
            assertNotNull(v8ValueError);
            assertEquals("Mock error", v8ValueError.getMessage());
        }
        globalObject.delete("testError");
        mockCallbackReceiver.setCalled(false);
        v8Runtime.requestGarbageCollectionForTesting(true);
    }

    @Test
    public void testCallbackJoinWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver,
                mockCallbackReceiver.getMethod("joinWithThis",
                        V8ValueObject.class, Boolean.class, Double.class, Integer.class, Long.class,
                        String.class, ZonedDateTime.class, V8ValueString.class),
                true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject x = v8Runtime.createV8ValueObject()) {
                globalObject.set("x", x);
                x.set("p", new V8ValueString("q"));
                x.set("joinWithThis", v8ValueFunction);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = x.joinWithThis(true, 1.23, 2, 3n, '4', new Date(1611710223719), '6'); a;").executeString();
            assertEquals("{\"p\":\"q\"},true,1.23,2,3,4,2021-01-27T01:17:03.719Z[UTC],6", resultString);
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }

    @Test
    public void testCallbackJoinIntegerArrayWithThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver,
                mockCallbackReceiver.getMethod(
                        "joinIntegerArrayWithThis", V8ValueObject.class, String.class, Integer[].class),
                true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject x = v8Runtime.createV8ValueObject()) {
                globalObject.set("x", x);
                x.set("p", new V8ValueString("q"));
                x.set("joinIntegerArrayWithThis", v8ValueFunction);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = x.joinIntegerArrayWithThis('x', 1, 2, 3); a;").executeString();
            assertEquals("{\"p\":\"q\"},x,1,2,3", resultString);
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }

    @Test
    public void testCallbackJoinWithoutThis() throws JavetException, NoSuchMethodException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("joinWithoutThis",
                Boolean.class, Double.class, Integer.class, Long.class, String.class, ZonedDateTime.class,
                V8ValueString.class));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext)) {
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("joinWithoutThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = joinWithoutThis(true, 1.23, 2, 3n, '4', new Date(1611710223719), '6'); a;").executeString();
            assertEquals("true,1.23,2,3,4,2021-01-27T01:17:03.719Z[UTC],6", resultString);
        }
        assertTrue(mockCallbackReceiver.isCalled());
    }
}
