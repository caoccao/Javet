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
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.mock.MockAnnotationBasedCallbackReceiver;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
            assertEquals(18, javetCallbackContexts.size());
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
            assertTrue(v8Runtime.getExecutor("a.self() === a").executeBoolean());
            v8Runtime.getGlobalObject().delete("a");
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
    }

    @Test
    public void testAnnotationBasedFunctionsAndProperties() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            MockAnnotationBasedCallbackReceiver mockAnnotationBasedCallbackReceiver =
                    new MockAnnotationBasedCallbackReceiver();
            List<JavetCallbackContext> javetCallbackContexts =
                    v8ValueObject.bind(mockAnnotationBasedCallbackReceiver);
            assertEquals(18, javetCallbackContexts.size());
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
            v8Runtime.getGlobalObject().delete("a");
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        }
        v8Runtime.getGlobalObject().bindFunction("a", codeString);
        assertEquals("123測試", v8Runtime.getExecutor("a();").executeString());
        v8Runtime.getGlobalObject().bindFunction("b", "(x) => x + 1;");
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
                assertTrue(v8ValueFunctionPush.getJSFunctionType().isNative());
                assertNotNull(v8ValueFunctionPush);
                assertEquals("function push() { [native code] }", v8ValueFunctionPush.toString());
                assertEquals(1, v8ValueFunctionPush.callInteger(v8ValueArray, "x"));
            }
            assertEquals(1, v8ValueArray.getLength());
            assertEquals("x", v8ValueArray.toString());
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
        assertEquals(0, v8Runtime.getReferenceCount());
        try {
            v8ValueFunction.close();
        } catch (JavetException e) {
            assertEquals(JavetError.RuntimeAlreadyClosed, e.getError());
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
        v8Runtime.requestGarbageCollectionForTesting(true);
        assertEquals(0, v8Runtime.getReferenceCount());
        try {
            v8ValueFunction.close();
        } catch (JavetException e) {
            assertEquals(JavetError.RuntimeAlreadyClosed, e.getError());
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
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
    public void testContextScope() throws JavetException {
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
            MockAnnotationBasedCallbackReceiver mockAnnotationBasedCallbackReceiver =
                    new MockAnnotationBasedCallbackReceiver();
            v8ValueObject.bind(mockAnnotationBasedCallbackReceiver);
            assertEquals(3, iV8Executor.executeInteger());
            v8ValueObject.forEach((key) -> v8ValueObject.delete(key));
        } catch (JavetExecutionException e) {
            e.printStackTrace();
            fail(e.getScriptingError().toString());
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
    }

    /**
     * V8 stores source code in either one byte or two bytes in internal storage.
     * This test is to validate both of them.
     *
     * @throws JavetException the javet exception
     */
    @Test
    public void testGetAndSetSourceCode() throws JavetException {
        final int functionCount = 5;
        String functionStatementTemplate = "var {0} = {1};\n";
        String functionNameTemplate = "f{0}";
        String[][] functionBodyTemplates = new String[][]{
                new String[]{
                        "() => /* One Byte */ a[{0}]",
                        "() => /* One Byte */ a[{0}] + 1",
                },
                new String[]{
                        "() => /* Two Bytes 简体 繁體 にほんご français Español */ a[{0}]",
                        "() => /* Two Bytes 简体 繁體 にほんご français Español */ a[{0}] + 1",
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
                try (V8ValueFunction v8ValueFunction = v8Runtime.getGlobalObject().get(functionNames.get(i))) {
                    assertTrue(v8ValueFunction.getJSFunctionType().isUserDefined());
                    assertTrue(v8ValueFunction.getJSScopeType().isClass());
                    assertEquals(functionBodies.get(i), v8ValueFunction.getSourceCode());
                    assertTrue(v8ValueFunction.getJSScopeType().isClass());
                    assertEquals(i, v8ValueFunction.callInteger(null));
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                    assertTrue(v8ValueFunction.setSourceCode(MessageFormat.format(functionBodyTemplate[1], i)));
                    assertEquals(i + 1, v8ValueFunction.callInteger(null));
                    // Restore the original script, otherwise next round will fail.
                    assertTrue(v8ValueFunction.setSourceCode(functionBodies.get(i)));
                    assertTrue(v8ValueFunction.getJSScopeType().isFunction());
                }
            }
            v8Runtime.resetContext();
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
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        }
        v8Runtime.requestGarbageCollectionForTesting(true);
    }
}
