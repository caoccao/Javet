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
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.mock.MockAnnotationBasedCallbackReceiver;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class TestV8ValueFunction extends BaseTestJavetRuntime {
    @Test
    public void testAnnotationBasedFunctions() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            MockAnnotationBasedCallbackReceiver mockAnnotationBasedCallbackReceiver =
                    new MockAnnotationBasedCallbackReceiver();
            List<JavetCallbackContext> javetCallbackContexts =
                    v8ValueObject.bind(mockAnnotationBasedCallbackReceiver);
            assertEquals(22, javetCallbackContexts.size());
            assertEquals(0, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("test", v8Runtime.getExecutor("a.echo('test')").executeString());
            assertEquals(1, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.add(1, 2)").executeInteger());
            assertEquals(2, mockAnnotationBasedCallbackReceiver.getCount());
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(
                    "a.generateArrayWithConverter()").execute()) {
                assertEquals("[\"a\",1]", v8ValueArray.toJsonString());
            }
            assertEquals(3, mockAnnotationBasedCallbackReceiver.getCount());
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(
                    "a.generateArrayWithoutConverter()").execute()) {
                assertEquals("[\"a\",1]", v8ValueArray.toJsonString());
            }
            assertEquals(4, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("static", v8Runtime.getExecutor("a.staticEcho('static')").executeString());
            assertEquals(4, mockAnnotationBasedCallbackReceiver.getCount());
            // Primitive test
            assertTrue(v8Runtime.getExecutor("a.primitiveRevertBoolean(false)").executeBoolean());
            assertEquals(5, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddByte(1, 2)").executeInteger());
            assertEquals(6, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3.57, v8Runtime.getExecutor("a.primitiveAddDouble(1.23, 2.34)").executeDouble(), 0.01);
            assertEquals(7, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3.57, v8Runtime.getExecutor("a.primitiveAddFloat(1.23, 2.34)").executeDouble(), 0.01);
            assertEquals(8, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddInt(1, 2)").executeInteger());
            assertEquals(9, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddInt(1n, 2n)").executeInteger());
            assertEquals(10, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddLong(1n, 2n)").executeLong());
            assertEquals(11, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddLong(1, 2)").executeLong());
            assertEquals(12, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddShort(1, 2)").executeInteger());
            assertEquals(13, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(3, v8Runtime.getExecutor("a.primitiveAddShort(1n, 2n)").executeInteger());
            assertEquals(14, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("b", v8Runtime.getExecutor("a.primitiveIncreaseChar('a')").executeString());
            assertEquals(15, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("c", v8Runtime.getExecutor("a.primitiveIncreaseChar('bye')").executeString());
            assertEquals(16, mockAnnotationBasedCallbackReceiver.getCount());
            assertTrue(v8Runtime.getExecutor("a.self() === a").executeBoolean());
            assertEquals(17, mockAnnotationBasedCallbackReceiver.getCount());
            // Null safety test
            assertTrue(v8Runtime.getExecutor("a.primitiveRevertBoolean(null)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.primitiveRevertBoolean(undefined)").executeBoolean());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddByte(1, null)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddByte(1, undefined)").executeInteger());
            assertEquals(1.23, v8Runtime.getExecutor("a.primitiveAddDouble(1.23, null)").executeDouble(), 0.01);
            assertEquals(1.23, v8Runtime.getExecutor("a.primitiveAddDouble(1.23, undefined)").executeDouble(), 0.01);
            assertEquals(1.23, v8Runtime.getExecutor("a.primitiveAddFloat(1.23, null)").executeDouble(), 0.01);
            assertEquals(1.23, v8Runtime.getExecutor("a.primitiveAddFloat(1.23, undefined)").executeDouble(), 0.01);
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddInt(1, null)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddInt(1, undefined)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddLong(1n, null)").executeLong());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddLong(1n, undefined)").executeLong());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddShort(1, null)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("a.primitiveAddShort(1, undefined)").executeInteger());
            assertEquals(String.valueOf((char) 1), v8Runtime.getExecutor("a.primitiveIncreaseChar(null)").executeString());
            assertEquals(String.valueOf((char) 1), v8Runtime.getExecutor("a.primitiveIncreaseChar(undefined)").executeString());
            assertEquals(19, v8ValueObject.unbind(mockAnnotationBasedCallbackReceiver));
            try {
                v8Runtime.getExecutor("a.echo('test')").executeVoid();
                fail("Failed to throw an exception");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: a.echo is not a function", e.getMessage());
            }
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testAnnotationBasedFunctionsAndProperties() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            MockAnnotationBasedCallbackReceiver mockAnnotationBasedCallbackReceiver =
                    new MockAnnotationBasedCallbackReceiver();
            List<JavetCallbackContext> javetCallbackContexts =
                    v8ValueObject.bind(mockAnnotationBasedCallbackReceiver);
            assertEquals(22, javetCallbackContexts.size());
            assertEquals(0, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(123, v8Runtime.getExecutor("a.integerValue").executeInteger());
            assertEquals(1, mockAnnotationBasedCallbackReceiver.getCount());
            v8Runtime.getExecutor("a.stringValue = 'abc';").executeVoid();
            assertEquals(2, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", v8Runtime.getExecutor("a.stringValue").executeString());
            assertEquals(3, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("static", v8Runtime.getExecutor("a.staticEcho('static')").executeString());
            assertEquals(3, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("test", v8Runtime.getExecutor("a.echo('test')").executeString());
            assertEquals(4, mockAnnotationBasedCallbackReceiver.getCount());
            assertFalse(v8ValueObject.hasOwnProperty("disabledFunction"));
            assertThrows(JavetException.class, () -> {
                v8Runtime.getExecutor("a.disabledFunction()").executeString();
            });
            assertFalse(v8ValueObject.hasOwnProperty("disabledProperty"));
            try (V8Value v8Value = v8Runtime.getExecutor("a.disabledProperty").execute()) {
                assertTrue(v8Value.isUndefined());
            }
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testAnonymousFunction() throws JavetException {
        String codeString = "() => '123測試'";
        try (V8Value v8Value = v8Runtime.getExecutor(codeString).execute()) {
            assertNotNull(v8Value);
            assertTrue(v8Value instanceof V8ValueFunction);
            V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Value;
            assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined());
            assertEquals(codeString, v8ValueFunction.toString());
            assertEquals(codeString, v8ValueFunction.getSourceCode());
            assertEquals("123測試", v8ValueFunction.callString(null));
            v8Runtime.getGlobalObject().bindFunction("a", codeString);
            assertEquals("123測試", v8Runtime.getExecutor("a();").executeString());
            v8Runtime.getGlobalObject().bindFunction("b", "(x) => x + 1;");
            assertEquals(2, v8Runtime.getExecutor("b(1);").executeInteger());
            v8Runtime.getGlobalObject().delete("a");
            v8Runtime.getGlobalObject().delete("b");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testArrayPush() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = []; a;").execute()) {
            assertNotNull(v8ValueArray);
            try (V8ValueFunction v8ValueFunctionPush = v8ValueArray.get("push")) {
                assertTrue(v8ValueFunctionPush.getJSFunctionType().isNative());
                assertNotNull(v8ValueFunctionPush);
                assertEquals("function push() { [native code] }", v8ValueFunctionPush.toString());
                assertEquals(1, v8ValueFunctionPush.callInteger(v8ValueArray, "x"));
            }
            assertEquals(1, v8ValueArray.getLength());
            assertEquals("x", v8ValueArray.toString());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testAsyncFunction() throws JavetException {
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction("() => {}")) {
            assertFalse(v8ValueFunction.isAsyncFunction());
            assertFalse(v8ValueFunction.isGeneratorFunction());
        }
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction("async () => {Promise.resolve(0);}")) {
            assertTrue(v8ValueFunction.isAsyncFunction());
            assertFalse(v8ValueFunction.isGeneratorFunction());
        }
    }

    @Test
    public void testCallObject() throws JavetException {
        String prefixString = "const x = 1; ";
        String functionName = "function a";
        String suffixString = " const y = 2;";
        String codeString = "(b) { return [1,2,3].concat(b); }";
        v8Runtime.getExecutor(prefixString + functionName + codeString + suffixString).executeVoid();
        try (V8ValueFunction v8ValueFunction = v8Runtime.getGlobalObject().get("a")) {
            assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined());
            assertEquals(functionName + codeString, v8ValueFunction.toString());
            assertEquals(codeString, v8ValueFunction.getSourceCode());
            List<Integer> result = v8ValueFunction.callObject(null, (Object) (new Integer[]{4, 5, 6}));
            assertArrayEquals(
                    new Integer[]{1, 2, 3, 4, 5, 6},
                    result.toArray(new Integer[0]),
                    "callObject() should work transparently without resource leak");
        }
    }

    @Test
    public void testCallbackBlankWithThis() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoThis", true), true);
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext);
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            a.set("x", "1");
            a.set("echoThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            assertEquals("{\"x\":\"1\"}", v8Runtime.getExecutor("a.echoThis();").executeString());
            assertTrue(mockCallbackReceiver.isCalled());
            v8ValueFunction.setWeak();
            a.delete("echoThis");
            globalObject.delete("a");
        }
        assertEquals(1, v8Runtime.getReferenceCount());
        v8Runtime.lowMemoryNotification();
        assertEquals(0, v8Runtime.getReferenceCount());
        try {
            v8ValueFunction.close();
        } catch (JavetException e) {
            assertEquals(JavetError.RuntimeAlreadyClosed, e.getError());
        }
    }

    @Test
    public void testCallbackBlankWithoutThis() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("blank"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(javetCallbackContext);
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
        v8Runtime.lowMemoryNotification();
        assertEquals(0, v8Runtime.getReferenceCount());
        try {
            v8ValueFunction.close();
        } catch (JavetException e) {
            assertEquals(JavetError.RuntimeAlreadyClosed, e.getError());
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
            globalObject.set("x", "1");
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
        } finally {
            v8Runtime.lowMemoryNotification();
        }
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
        } finally {
            v8Runtime.lowMemoryNotification();
        }
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
                a.set("x", "1");
                a.set("echoThisString", v8ValueFunction);
                globalObject.set("a", a);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueString v8ValueString = v8Runtime.getExecutor(
                    "const x = a.echoThisString('123'); x;").execute()) {
                assertEquals("[{\"x\":\"1\"},\"123\"]", v8ValueString.getValue());
            }
            globalObject.delete("a");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        v8Runtime.lowMemoryNotification();
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
        }
        assertFalse(mockCallbackReceiver.isCalled());
        assertEquals("abc", v8Runtime.getExecutor("echoString('abc')").executeString());
        assertTrue(mockCallbackReceiver.isCalled());
        globalObject.delete("echoString");
        javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("echoString", V8Value[].class));
        globalObject.bindFunction("echoString", javetCallbackContext);
        assertEquals("abc", v8Runtime.getExecutor("echoString('abc')").executeString());
        assertEquals("abc,def", v8Runtime.getExecutor("echoString('abc', 'def')").executeString());
        assertEquals("", v8Runtime.getExecutor("echoString()").executeString());
        globalObject.delete("echoString");
        v8Runtime.lowMemoryNotification();
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
                a.set("x", "1");
                a.set("echoThis", v8ValueFunction);
                globalObject.set("a", a);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            try (V8ValueString v8ValueString = v8Runtime.getExecutor("const x = a.echoThis('123'); x;").execute()) {
                assertEquals("[{\"x\":\"1\"},\"123\"]", v8ValueString.getValue());
            }
            globalObject.delete("a");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        v8Runtime.lowMemoryNotification();
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
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testCallbackError() throws JavetException, NoSuchMethodException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("error"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        globalObject.bindFunction("testError", javetCallbackContext);
        assertEquals(0, v8Runtime.getReferenceCount());
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
        v8Runtime.lowMemoryNotification();
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
                x.set("p", "q");
                x.set("joinIntegerArrayWithThis", v8ValueFunction);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = x.joinIntegerArrayWithThis('x', 1, 2, 3); a;").executeString();
            assertEquals("{\"p\":\"q\"},x,1,2,3", resultString);
            globalObject.delete("x");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        v8Runtime.lowMemoryNotification();
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
                x.set("p", "q");
                x.set("joinWithThis", v8ValueFunction);
            }
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = x.joinWithThis(true, 1.23, 2, 3n, '4', new Date(1611710223719), '6'); a;").executeString();
            assertEquals("{\"p\":\"q\"},true,1.23,2,3,4,2021-01-27T01:17:03.719Z[UTC],6", resultString);
            globalObject.delete("x");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        v8Runtime.lowMemoryNotification();
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
            assertTrue(v8ValueFunction.getJSFunctionType().isAPI());
            assertEquals(1, v8Runtime.getReferenceCount());
            globalObject.set("joinWithoutThis", v8ValueFunction);
            assertFalse(mockCallbackReceiver.isCalled());
            String resultString = v8Runtime.getExecutor(
                    "const a = joinWithoutThis(true, 1.23, 2, 3n, '4', new Date(1611710223719), '6'); a;").executeString();
            assertEquals("true,1.23,2,3,4,2021-01-27T01:17:03.719Z[UTC],6", resultString);
            globalObject.delete("joinWithoutThis");
        }
        assertTrue(mockCallbackReceiver.isCalled());
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testContextScope() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public Integer contextScope(V8ValueFunction v8ValueFunction) throws JavetException {
                assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined());
                assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                if (v8ValueFunction.setSourceCode("() => a + 2")) {
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                    return v8ValueFunction.callInteger(null);
                } else {
                    return 0;
                }
            }
        };
        v8Runtime.getExecutor("var c = {};" +
                "var x = {\n" +
                "  contextScope: function(anonymousFunction) {\n" +
                "    return anonymousFunction.call();\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "function outerFunction() {\n" +
                "  const a = 1;\n" +
                "  c['d'] = 3;\n" +
                "  return x.contextScope(() => a);\n" +
                "};").executeVoid();
        IV8Executor iV8Executor = v8Runtime.getExecutor("outerFunction()");
        assertEquals(1, iV8Executor.executeInteger());
        try (V8ValueObject v8ValueObject = v8Runtime.getGlobalObject().get("x")) {
            v8ValueObject.bind(anonymous);
            assertEquals(3, iV8Executor.executeInteger());
            v8ValueObject.unbind(anonymous);
        } catch (JavetExecutionException e) {
            e.printStackTrace();
            fail(e.getScriptingError().toString());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testDoubleStream() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public DoubleStream test(DoubleStream doubleStream) {
                return doubleStream.map(i -> i + 1);
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("a.test([1.23,2.34]);").execute()) {
                assertEquals(2, v8ValueArray.getLength());
                assertEquals(2.23D, v8ValueArray.getDouble(0), 0.001D);
                assertEquals(3.34D, v8ValueArray.getDouble(1), 0.001D);
            }
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    @Tag("performance")
    public void testFunctionCreationFailure() throws JavetException {
        final int size = 100;
        final int rounds = 100;
        final Random random = new Random();
        MockAnnotationBasedCallbackReceiver mockAnnotationBasedCallbackReceiver =
                new MockAnnotationBasedCallbackReceiver();
        String placeholder = "/* PlaceHolder */";
        StringBuilder sb = new StringBuilder("(x) => {\nconst a = [];\n");
        IntStream.range(0, size).forEach(i -> sb.append("a.push(").append(i).append(");\n"));
        sb.append("// ").append(placeholder).append("\nreturn a.length;\n}");
        String codeStringTemplate = sb.toString();
        final ExecutorService executorService = Executors.newCachedThreadPool();
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public int count(V8ValueFunction v8ValueFunction) throws JavetException {
                final V8Runtime v8Runtime = v8ValueFunction.getV8Runtime();
                if (v8ValueFunction.getJSScopeType().isClass()) {
                    try {
                        v8ValueFunction.callVoid(null);
                    } catch (JavetException e) {
                    }
                }
                String originalCodeString = v8ValueFunction.getSourceCode();
                String codeString = codeStringTemplate.replace(placeholder, Double.toString(random.nextDouble()));
                int result;
                try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                    executorService.submit(() -> {
                        if (random.nextDouble() < 0.01) {
                            try {
                                Thread.sleep(10 + random.nextInt(20));
                                v8Runtime.terminateExecution();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    v8Runtime.getGlobalObject().set("a", v8ValueObject);
                    v8ValueObject.bind(mockAnnotationBasedCallbackReceiver);
                    v8ValueFunction.setSourceCode(codeString);
                    result = v8ValueFunction.callInteger(null, v8ValueObject);
                    v8ValueFunction.setSourceCode(originalCodeString);
                    v8ValueObject.unbind(mockAnnotationBasedCallbackReceiver);
                    v8Runtime.getGlobalObject().delete("a");
                }
                return result;
            }
        };
        final AtomicInteger completedCount = new AtomicInteger(0);
        final AtomicBoolean failed = new AtomicBoolean(false);
        final int threadCount = 200;
        try (IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>()) {
            javetEnginePool.getConfig().setPoolMaxSize(threadCount);
            Thread[] threads = new Thread[threadCount];
            for (int i = 0; i < threads.length; ++i) {
                threads[i] = new Thread(() -> {
                    try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
                        V8Runtime v8Runtime = javetEngine.getV8Runtime();
                        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction("(x) => 0")) {
                            V8ValueObject v8ValueObject = v8Runtime.getGlobalObject();
                            for (int j = 0; j < rounds && !failed.get(); ++j) {
                                v8ValueObject.bind(anonymous);
                                try {
                                    assertEquals(size, v8ValueObject.invokeInteger("count", v8ValueFunction));
                                } catch (JavetExecutionException e) {
                                    assertEquals(e.getError().getCode(), JavetError.ExecutionFailure.getCode());
                                }
                                v8ValueObject.unbind(anonymous);
                            }
                        } finally {
                            v8Runtime.lowMemoryNotification();
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                        failed.set(true);
                        fail(t.getMessage());
                    } finally {
                        completedCount.incrementAndGet();
                    }
                });
            }
            Arrays.stream(threads).forEach(Thread::start);
            while (completedCount.get() < threadCount) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            assertFalse(failed.get());
        }
    }

    @Test
    public void testGeneratorFunction() throws JavetException {
        try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction("() => {}")) {
            assertFalse(v8ValueFunction.isGeneratorFunction());
            assertFalse(v8ValueFunction.isAsyncFunction());
        }
        try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                "function* a() {yield 0;}; a;").execute()) {
            assertTrue(v8ValueFunction.isGeneratorFunction());
            assertFalse(v8ValueFunction.isAsyncFunction());
        }
        try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                "async function* b() {\n" +
                        "  let i = 0;\n" +
                        "  while (i < 3) {\n" +
                        "    yield i++;\n" +
                        "  }\n" +
                        "}; b;").execute()) {
            assertTrue(v8ValueFunction.isGeneratorFunction());
            assertTrue(v8ValueFunction.isAsyncFunction());
        }
    }

    @Test
    public void testGetAndSetExtraLongSourceCode() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            private int callCount = 0;

            @V8Function
            public Integer intercept(V8ValueFunction v8ValueFunction) throws JavetException {
                assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined());
                if (v8ValueFunction.getJSScopeType().isClass()) {
                    v8ValueFunction.callInteger(null, 0);
                }
                assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                String originalCodeString = v8ValueFunction.getSourceCode();
                String newCodeString = originalCodeString + " /*\n測試\nI am longer\n*/ + 1";
                v8ValueFunction.setSourceCode(newCodeString);
                int result = v8ValueFunction.callInteger(null, 1);
                assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                v8ValueFunction.setSourceCode(originalCodeString);
                ++callCount;
                return result;
            }
        };
        String placeholder = "/*\n" + String.join("\n", ZoneId.getAvailableZoneIds()) + "\n*/";
        try {
            final int copyCount = 10;
            v8Runtime.getGlobalObject().bind(anonymous);
            StringBuilder sb = new StringBuilder();
            sb.append("// Header x\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("const a = [];\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("for (let i = 0; i < 5; ++i) {\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("  a.push(intercept( x => /* comment */ x + i + 0 /* comment */));\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("  a.push(intercept( x => /* comment */ x + i + 1 /* comment */));\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("  a.push(intercept( x => /* comment */ x + i + 2 /* comment */));\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("  a.push(intercept( x => /* comment */ x + i + 3 /* comment */));\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("  a.push(intercept( x => /* comment */ x + i + 4 /* comment */));\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("}\n");
            IntStream.range(0, copyCount).forEach(i -> sb.append(placeholder + "\n"));
            sb.append("// Footer");
            String sourceCode = sb.toString();
            v8Runtime.getExecutor(sourceCode).executeVoid();
            assertEquals(
                    "[2,3,4,5,6,3,4,5,6,7,4,5,6,7,8,5,6,7,8,9,6,7,8,9,10]",
                    v8Runtime.getExecutor("JSON.stringify(a);").executeString());
            v8Runtime.getGlobalObject().unbind(anonymous);
        } catch (JavetExecutionException e) {
            e.printStackTrace();
            fail(e.getScriptingError().toString());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testGetAndSetMalformedSourceCode() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public String test(V8ValueFunction v8ValueFunction) throws JavetException {
                v8ValueFunction.callString(null);
                String originalSourceCode = v8ValueFunction.getSourceCode();
                v8ValueFunction.setSourceCode("() => 'a' \n ;\n  ", true);
                String resultString = v8ValueFunction.callString(null);
                assertEquals("a", resultString);
                v8ValueFunction.setSourceCode(originalSourceCode);
                return resultString;
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(anonymous);
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8Runtime.getExecutor(
                    "// comment\n" +
                            "a.test(\n" +
                            "  () => \n'abc'              \n// comment\n);\n" +
                            "// comment\n" +
                            "   ").executeVoid();
            v8ValueObject.unbind(anonymous);
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    /**
     * V8 stores source code in either one byte or two bytes in internal storage.
     * This test is to validate both of them.
     *
     * @throws JavetException the javet exception
     */
    @Test
    public void testGetAndSetRegularSourceCode() throws JavetException {
        final int functionCount = 5;
        String functionStatementTemplate = "var {0} = {1};\n";
        String functionNameTemplate = "f{0}";
        String[][] functionBodyTemplates = new String[][]{
                // One Byte (0): Increase the length
                new String[]{
                        "() => /* One Byte (0) */ a[{0}]",
                        "() => /* One Byte (0) */ a[{0}] + 1",
                },
                // One Byte (1): Decrease the length
                new String[]{
                        "() => /* One Byte (1) I am longer */ a[{0}]",
                        "() => /* One Byte (1) */ a[{0}] + 1",
                },
                // Two Bytes (2): Increase the length
                new String[]{
                        "() => /* Two Bytes (2) 简体 繁體 にほんご français Español I am longer */ a[{0}]",
                        "() => /* Two Bytes (2) 简体 繁體 にほんご français Español */ a[{0}] + 1",
                },
                // Two Bytes (3): Decrease the length
                new String[]{
                        "() => /* Two Bytes (3) 简体 繁體 にほんご français Español */ a[{0}]",
                        "() => /* Two Bytes (3) 简体 繁體 にほんご français Español */ a[{0}] + 1",
                },
                // One Byte => Two Bytes (4): Increase the length
                new String[]{
                        "() => /* One Byte => Two Bytes (4) */ a[{0}]",
                        "() => /* One Byte => Two Bytes (4) 简体 繁體 にほんご français Español */ a[{0}] + 1",
                },
                // One Byte => Two Bytes (5): Decrease the length
                new String[]{
                        "() => /* One Byte => Two Bytes (5) I am longer I am longer I am longer I am longer */ a[{0}]",
                        "() => /* One Byte => Two Bytes (5) 简体 繁體 にほんご français Español */ a[{0}] + 1",
                },
                // Two Bytes => One Byte (6): Increase the length
                new String[]{
                        "() => /* Two Bytes => One Byte (6) 简体 繁體 にほんご français Español */ a[{0}]",
                        "() => /* Two Bytes => One Byte (6) I am longer I am longer I am longer I am longer */ a[{0}] + 1",
                },
                // Two Bytes => One Byte (7): Decrease the length
                new String[]{
                        "() => /* Two Bytes => One Byte (7) 简体 繁體 にほんご français Español */ a[{0}]",
                        "() => /* Two Bytes => One Byte (7) */ a[{0}] + 1",
                },
        };
        for (String[] functionBodyTemplate : functionBodyTemplates) {
            List<String> functionNames = new ArrayList<>(functionCount);
            List<String> functionBodies = new ArrayList<>(functionCount);
            final StringBuilder sb = new StringBuilder();
            sb.append("const a = Array.from(Array(").append(functionCount).append(").keys());\n");
            IntStream.range(0, functionCount).forEach(i -> {
                String functionName = MessageFormat.format(functionNameTemplate, i);
                String functionBody = MessageFormat.format(functionBodyTemplate[0], i);
                functionNames.add(functionName);
                functionBodies.add(functionBody);
                String functionStatement = MessageFormat.format(functionStatementTemplate, functionName, functionBody);
                sb.append(functionStatement);
            });
            String codeString = sb.toString();
            v8Runtime.getExecutor(codeString).executeVoid();
            for (int i = 0; i < functionCount; ++i) {
                // Get, update and restore the source code.
                try (V8ValueFunction v8ValueFunction = v8Runtime.getGlobalObject().get(functionNames.get(i))) {
                    assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined(),
                            "Function type should be user defined.");
                    assertTrue(v8ValueFunction.getJSScopeType().isClass(),
                            "The cache is not ready and the scope type should be [Class].");
                    assertEquals(functionBodies.get(i), v8ValueFunction.getSourceCode(),
                            "The source code should match.");
                    assertTrue(v8ValueFunction.getJSScopeType().isClass(),
                            "The cache is not ready and the scope type should be [Class].");
                    assertEquals(i, v8ValueFunction.callInteger(null),
                            "Calling the function to build the cache and the result should match.");
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction(),
                            "The cache is ready and the scope type should be [Function].");
                    assertTrue(v8ValueFunction.setSourceCode(MessageFormat.format(functionBodyTemplate[1], i)),
                            "Updating the source code should pass.");
                    assertEquals(i + 1, v8ValueFunction.callInteger(null),
                            "Calling the new function and the result should match.");
                    assertTrue(v8ValueFunction.setSourceCode(functionBodies.get(i)),
                            "Restoring the source code should pass.");
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction(),
                            "The cache is refreshed and the scope type should be [Function].");
                }
                // Verify the cache.
                try (V8ValueFunction v8ValueFunction = v8Runtime.getGlobalObject().get(functionNames.get(i))) {
                    assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined(),
                            "Function type should be user defined.");
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction(),
                            "The cache is restored and the scope type should be [Function].");
                    assertEquals(i, v8ValueFunction.callInteger(null),
                            "Calling the function from the cache and the result should match.");
                }
            }
            v8Runtime.resetContext();
        }
    }

    @Test
    public void testIntStream() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public IntStream test(IntStream intStream) {
                return intStream.map(i -> i + 1);
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("a.test([1,2]);").execute()) {
                assertEquals(2, v8ValueArray.getLength());
                assertEquals(2, v8ValueArray.getInteger(0));
                assertEquals(3, v8ValueArray.getInteger(1));
            }
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testLongStream() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public LongStream test(LongStream longStream) {
                return longStream.map(i -> i + 1);
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("a.test([1n,2n]);").execute()) {
                assertEquals(2, v8ValueArray.getLength());
                assertEquals(2L, v8ValueArray.getLong(0));
                assertEquals(3L, v8ValueArray.getLong(1));
            }
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testOptional() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public Optional<String> test(Optional<String> optionalString) {
                if (optionalString.isPresent()) {
                    String str = optionalString.get();
                    if (str.length() > 0) {
                        return Optional.of(str.substring(1));
                    }
                }
                return Optional.empty();
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            assertTrue(v8Runtime.getExecutor("a.test('ab') == 'b';").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.test('a') == '';").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.test(null) === null;").executeBoolean());
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testOptionalDouble() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public OptionalDouble test(OptionalDouble optionalDouble) {
                if (optionalDouble.isPresent()) {
                    double d = optionalDouble.getAsDouble();
                    return OptionalDouble.of(d + 1);
                }
                return OptionalDouble.empty();
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            assertTrue(v8Runtime.getExecutor("Math.abs(a.test(1.2) - 2.2) < 0.001;").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.test(null) === null;").executeBoolean());
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testOptionalInt() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public OptionalInt test(OptionalInt optionalInt) {
                if (optionalInt.isPresent()) {
                    int i = optionalInt.getAsInt();
                    return OptionalInt.of(i + 1);
                }
                return OptionalInt.empty();
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            assertTrue(v8Runtime.getExecutor("a.test(1) == 2;").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.test(null) === null;").executeBoolean());
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testOptionalLong() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public OptionalLong test(OptionalLong optionalLong) {
                if (optionalLong.isPresent()) {
                    long l = optionalLong.getAsLong();
                    return OptionalLong.of(l + 1);
                }
                return OptionalLong.empty();
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            assertTrue(v8Runtime.getExecutor("a.test(1n) == 2n;").executeBoolean());
            assertTrue(v8Runtime.getExecutor("a.test(null) === null;").executeBoolean());
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testPropertyGetter() throws NoSuchMethodException, JavetException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContextGetter = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("getValue"));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", v8ValueObject);
            mockCallbackReceiver.setValue("abc");
            assertTrue(v8ValueObject.bindProperty("test", javetCallbackContextGetter));
            assertEquals(1, v8Runtime.getReferenceCount());
            assertEquals(1, v8Runtime.getCallbackContextCount());
            mockCallbackReceiver.setCalled(false);
            assertEquals("abc", v8Runtime.getExecutor("a.test").executeString());
            assertTrue(mockCallbackReceiver.isCalled());
            mockCallbackReceiver.setCalled(false);
            assertEquals("abc", v8Runtime.getExecutor("a['test']").executeString());
            assertTrue(mockCallbackReceiver.isCalled());
            mockCallbackReceiver.setCalled(false);
            assertEquals("{\"test\":\"abc\"}", v8ValueObject.toJsonString());
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testPropertyGetterAndSetter() throws NoSuchMethodException, JavetException {
        assertEquals(0, v8Runtime.getReferenceCount());
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        JavetCallbackContext javetCallbackContextGetter = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("getValue"));
        JavetCallbackContext javetCallbackContextSetter = new JavetCallbackContext(
                mockCallbackReceiver, mockCallbackReceiver.getMethod("setValue", String.class));
        V8ValueObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", v8ValueObject);
            assertNull(mockCallbackReceiver.getValue());
            assertTrue(v8ValueObject.bindProperty("test", javetCallbackContextGetter, javetCallbackContextSetter));
            assertEquals(1, v8Runtime.getReferenceCount());
            assertEquals(2, v8Runtime.getCallbackContextCount());
            mockCallbackReceiver.setCalled(false);
            assertFalse(mockCallbackReceiver.isCalled());
            v8Runtime.getExecutor("a.test = 'abc';").executeVoid();
            assertTrue(mockCallbackReceiver.isCalled());
            assertEquals("abc", mockCallbackReceiver.getValue());
            assertEquals("{\"test\":\"abc\"}", v8ValueObject.toJsonString());
            mockCallbackReceiver.setCalled(false);
            assertEquals("abc", v8Runtime.getExecutor("a.test").executeString());
            assertTrue(mockCallbackReceiver.isCalled());
            mockCallbackReceiver.setCalled(false);
            assertEquals("abc", v8Runtime.getExecutor("a['test']").executeString());
            assertTrue(mockCallbackReceiver.isCalled());
            mockCallbackReceiver.setCalled(false);
            assertEquals("{\"test\":\"abc\"}", v8ValueObject.toJsonString());
            assertTrue(mockCallbackReceiver.isCalled());
            globalObject.delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testStream() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public Stream test(Stream stream) {
                return stream.filter(o -> o instanceof String);
            }
        };

        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(anonymous);
            try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("a.test(['a',1,'b']);").execute()) {
                assertEquals(2, v8ValueArray.getLength());
                assertEquals("a", v8ValueArray.getString(0));
                assertEquals("b", v8ValueArray.getString(1));
            }
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }
}
