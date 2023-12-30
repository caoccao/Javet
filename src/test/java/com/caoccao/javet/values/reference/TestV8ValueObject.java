/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.entities.JavetEntityFunction;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.mock.MockAnnotationBasedCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueDouble;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class TestV8ValueObject extends BaseTestJavetRuntime {
    @Test
    public void testAnnotationBasedProperties() throws JavetException {
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
            assertEquals(123, v8Runtime.getExecutor("a['integerValue']").executeInteger());
            assertEquals(2, mockAnnotationBasedCallbackReceiver.getCount());
            v8Runtime.getExecutor("a.stringValue = 'abc';").executeVoid();
            assertEquals(3, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", v8Runtime.getExecutor("a.stringValue").executeString());
            assertEquals(4, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", v8Runtime.getExecutor("a['stringValue']").executeString());
            assertEquals(5, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", v8Runtime.getExecutor("a['stringValueWithThis']").executeString());
            assertEquals(7, mockAnnotationBasedCallbackReceiver.getCount());
            v8Runtime.getExecutor("a.stringValueWithThis = 'def';").executeVoid();
            assertEquals(9, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("def", v8Runtime.getExecutor("a['stringValue']").executeString());
            assertEquals(10, mockAnnotationBasedCallbackReceiver.getCount());
            assertNull(v8Runtime.getExecutor("a[Symbol.for('symbolValue')]").executeString());
            assertEquals(11, mockAnnotationBasedCallbackReceiver.getCount());
            v8Runtime.getExecutor("a[Symbol.for('symbolValue')] = 'abc';").executeVoid();
            assertEquals(12, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", mockAnnotationBasedCallbackReceiver.getSymbolValue());
            assertEquals(13, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals("abc", v8Runtime.getExecutor("a[Symbol.for('symbolValue')]").executeString());
            assertEquals(14, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(1000, v8Runtime.getExecutor("a[Symbol.toPrimitive]").executeInteger());
            assertEquals(15, mockAnnotationBasedCallbackReceiver.getCount());
            assertEquals(19, v8ValueObject.unbind(mockAnnotationBasedCallbackReceiver));
            assertNull(v8Runtime.getExecutor("a['stringValue']").executeString());
            assertEquals(15, mockAnnotationBasedCallbackReceiver.getCount());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testAnnotationBasedPropertyAndFunction() throws JavetException {
        // 2 JS functions => 1 Java function
        IJavetAnonymous iJavetAnonymous1 = new IJavetAnonymous() {
            @V8Function(name = "testFunction")
            @V8Property(name = "testProperty")
            public String test() {
                return "abc";
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(iJavetAnonymous1);
            assertEquals("abc", v8Runtime.getExecutor("a['testProperty']").executeString());
            assertEquals("abc", v8Runtime.getExecutor("a.testFunction()").executeString());
            assertEquals("abc", v8Runtime.getExecutor("a.testFunction(123)").executeString(), "Redundant parameters should be dropped.");
            v8ValueObject.unbind(iJavetAnonymous1);
        } finally {
            v8Runtime.lowMemoryNotification();
        }
        // 1 JS function => 2 Java functions
        IJavetAnonymous iJavetAnonymous2 = new IJavetAnonymous() {
            @V8Function(name = "test")
            public String testFunction() {
                return "abc";
            }

            @V8Property(name = "test")
            public JavetEntityFunction testProperty() {
                return new JavetEntityFunction("() => 'abc'", JSFunctionType.UserDefined);
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8ValueObject.bind(iJavetAnonymous2);
            assertEquals("abc", v8Runtime.getExecutor("a['test']()").executeString());
            assertEquals("abc", v8Runtime.getExecutor("a.test()").executeString());
            assertEquals("abc", v8Runtime.getExecutor("a.test(123)").executeString(), "Redundant parameters should be dropped.");
            v8ValueObject.unbind(iJavetAnonymous2);
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testClearWeak() throws JavetException {
        V8ValueObject a = v8Runtime.createV8ValueObject();
        V8ValueGlobalObject globalObject = v8Runtime.getGlobalObject();
        globalObject.set("a", a);
        a.setWeak();
        assertTrue(a.isWeak());
        assertEquals(1, v8Runtime.getReferenceCount());
        a.close();
        assertEquals(1, v8Runtime.getReferenceCount(),
                "Close() should not work because 'a' is weak.");
        a.clearWeak();
        assertFalse(a.isWeak());
        assertEquals(1, v8Runtime.getReferenceCount());
        a.close();
        assertEquals(0, v8Runtime.getReferenceCount());
        assertEquals(0L, a.getHandle());
        try (V8ValueObject b = globalObject.get("a")) {
            assertNotNull(b);
        }
    }

    @Test
    public void testEquals() throws JavetException {
        try (V8ValueObject v8ValueObject1 = v8Runtime.getExecutor(
                "const a = {'x': '1'}; a;").execute()) {
            assertFalse(v8ValueObject1.equals(null));
            assertFalse(v8ValueObject1.sameValue(null));
            assertFalse(v8ValueObject1.strictEquals(null));
            assertFalse(v8ValueObject1.equals(v8Runtime.createV8ValueNull()));
            assertFalse(v8ValueObject1.sameValue(v8Runtime.createV8ValueNull()));
            assertFalse(v8ValueObject1.strictEquals(v8Runtime.createV8ValueNull()));
            assertTrue(v8ValueObject1.equals(v8ValueObject1));
            assertTrue(v8ValueObject1.sameValue(v8ValueObject1));
            assertTrue(v8ValueObject1.strictEquals(v8ValueObject1));
            try (V8ValueObject v8ValueObject2 = v8Runtime.getExecutor(
                    "const b = {'x': '1'}; b;").execute()) {
                assertFalse(v8ValueObject1.equals(v8ValueObject2));
                assertFalse(v8ValueObject1.sameValue(v8ValueObject2));
                assertFalse(v8ValueObject1.strictEquals(v8ValueObject2));
            }
        }
    }

    @Test
    public void testForEach() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "const a = {'A0': 0, 'A1': 1, 'A2': 2}; a;").execute()) {
            AtomicInteger count = new AtomicInteger(0);
            assertEquals(3, v8ValueObject.forEach((V8ValueString key) -> {
                assertEquals("A" + count.getAndIncrement(), key.getValue());
            }));
            count.set(0);
            assertEquals(3, v8ValueObject.forEach((V8ValueString key, V8ValueInteger value) -> {
                assertEquals("A" + count.get(), key.getValue());
                assertEquals(count.getAndIncrement(), value.getValue());
            }));
            assertEquals(3, v8ValueObject.forEach((int index, V8ValueString key) -> {
                assertEquals("A" + index, key.getValue());
            }));
            assertEquals(3, v8ValueObject.forEach((int index, V8ValueString key, V8ValueInteger value) -> {
                assertEquals("A" + index, key.getValue());
                assertEquals(index, value.getValue());
            }));
        }
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "const b = {'2147483648': '2**31'}; b;").execute()) {
            assertEquals(1, v8ValueObject.forEach((V8Value key, V8Value value) -> {
                assertInstanceOf(V8ValueDouble.class, key);
                assertEquals("2147483648", key.toString());
            }));
        }
    }

    @Test
    public void testGeneratorObject() throws JavetException {
        try (V8ValueIterator<V8ValueInteger> v8ValueIterator = v8Runtime.getExecutor(
                "function* generator() {\n" +
                        "  yield 1;\n" +
                        "  yield 2;\n" +
                        "}; generator();").execute()) {
            assertNotNull(v8ValueIterator);
            assertTrue(v8ValueIterator.isGeneratorObject());
            try (V8ValueFunction v8ValueFunction = v8Runtime.getGlobalObject().get("generator")) {
                assertTrue(v8ValueFunction.isGeneratorFunction());
            }
            assertEquals(1, v8ValueIterator.getNext().getValue());
            assertEquals(2, v8ValueIterator.getNext().getValue());
            assertNull(v8ValueIterator.getNext());
        }
    }

    @Test
    public void testGetOwnPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, " +
                        "'中文': '測試', '1234567890': '1234567890'}; x;").execute()) {
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(8, iV8ValueArray.getLength());
                // Order is preserved since ES2015.
                assertEquals(1234567890, iV8ValueArray.getPropertyInteger(0));
                assertEquals("a", iV8ValueArray.getPropertyString(1));
                assertEquals("b", iV8ValueArray.getPropertyString(2));
                assertEquals("c", iV8ValueArray.getPropertyString(3));
                assertEquals("d", iV8ValueArray.getPropertyString(4));
                assertEquals("e", iV8ValueArray.getPropertyString(5));
                assertEquals("g", iV8ValueArray.getPropertyString(6));
                assertEquals("中文", iV8ValueArray.getPropertyString(7));
            }
        }
    }

    @Test
    public void testGetProperty() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1, 3: 'x'}, '中文': '測試'};"
                        + "x['i'] = true;x['j'] = 1.23;x['k'] = new Date(1611710223719);"
                        + "x;").execute()) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            assertEquals(1, ((V8ValueInteger) v8ValueObject.getProperty("a")).getValue());
            assertEquals(1, v8ValueObject.getPropertyInteger("a"));
            assertEquals("2", ((V8ValueString) v8ValueObject.getProperty("b")).getValue());
            assertEquals("2", v8ValueObject.getPropertyString("b"));
            assertEquals(3L, ((V8ValueLong) v8ValueObject.getProperty("c")).getValue());
            assertEquals(3L, v8ValueObject.getPropertyLong("c"));
            assertEquals(1, v8ValueObject.getPropertyInteger("d"));
            assertTrue(v8ValueObject.getProperty("e").isNull());
            assertEquals("測試", v8ValueObject.getPropertyString("中文"));
            assertTrue(v8ValueObject.getProperty("$").isUndefined());
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject childV8ValueObject = v8ValueObject.getProperty("g")) {
                assertNotNull(childV8ValueObject);
                assertEquals(v8Runtime, childV8ValueObject.getV8Runtime());
                assertEquals(1, childV8ValueObject.getPropertyInteger("h"));
                assertEquals("x", childV8ValueObject.getPropertyString(3));
                assertTrue(childV8ValueObject.hasOwnProperty("h"));
                assertTrue(childV8ValueObject.hasOwnProperty(3));
                assertFalse(childV8ValueObject.hasOwnProperty("p"));
                assertFalse(childV8ValueObject.hasOwnProperty(1));
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertTrue(v8ValueObject.getPropertyBoolean("i"));
            assertEquals(1.23, v8ValueObject.getPropertyDouble("j"), 0.001);
            assertEquals(
                    "2021-01-27T01:17:03.719Z[UTC]",
                    v8ValueObject.getPropertyZonedDateTime("k").withZoneSameInstant(ZoneId.of("UTC")).toString());
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }

    @Test
    public void testGetPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, " +
                        "'中文': '測試', '1234567890': '1234567890'}; x;").execute()) {
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(8, iV8ValueArray.getLength());
                // Order is preserved since ES2015.
                assertEquals(1234567890, iV8ValueArray.getPropertyInteger(0));
                assertEquals("a", iV8ValueArray.getPropertyString(1));
                assertEquals("b", iV8ValueArray.getPropertyString(2));
                assertEquals("c", iV8ValueArray.getPropertyString(3));
                assertEquals("d", iV8ValueArray.getPropertyString(4));
                assertEquals("e", iV8ValueArray.getPropertyString(5));
                assertEquals("g", iV8ValueArray.getPropertyString(6));
                assertEquals("中文", iV8ValueArray.getPropertyString(7));
            }
        }
    }

    @Test
    public void testGetSetDeletePrivate() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            assertFalse(v8ValueObject.hasPrivateProperty("x"));
            assertTrue(v8ValueObject.getPrivateProperty("x").isUndefined());
            assertTrue(v8ValueObject.setPrivateProperty("x", 1));
            assertTrue(v8ValueObject.hasPrivateProperty("x"));
            assertEquals(1, v8ValueObject.getPrivatePropertyInteger("x"));
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                assertEquals("[]", iV8ValueArray.toJsonString());
            }
            assertTrue(v8ValueObject.deletePrivateProperty("x"));
            assertTrue(v8ValueObject.getPrivateProperty("x").isUndefined());
        }
    }

    @Test
    public void testGetSetDeletePublic() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const a = {}; a;").execute()) {
            assertTrue(v8ValueObject.set("a", 1));
            assertTrue(v8ValueObject.set("b", "2"));
            assertTrue(v8ValueObject.set("c", new String[]{"x", "y"}));
            assertEquals(1, v8ValueObject.getInteger("a"));
            assertEquals("2", v8ValueObject.getString("b"));
            assertArrayEquals(
                    new String[]{"x", "y"},
                    ((List<String>) v8Runtime.toObject(v8ValueObject.get("c"), true)).toArray(new String[0]));
            assertTrue(v8ValueObject.delete("x"));
            assertTrue(v8ValueObject.delete("b"));
            V8Value v8Value = v8ValueObject.getUndefined("b");
            assertNotNull(v8Value);
            v8ValueObject.setString("d", "1");
            assertEquals("1", v8ValueObject.getString("d"));
            assertTrue(v8ValueObject.has("d"));
            v8ValueObject.setString("d", null);
            assertNull(v8ValueObject.getString("d"));
            assertTrue(v8ValueObject.has("d"));
            Object[] keysAndValues = new Object[]{"x", 1, "y", 2, "z", 3};
            assertTrue(v8ValueObject.set(keysAndValues));
            for (int i = 0; i < keysAndValues.length / 2; i += 2) {
                assertTrue(v8ValueObject.has(keysAndValues[i * 2]));
                assertEquals(keysAndValues[i * 2 + 1], v8ValueObject.getInteger(keysAndValues[i * 2]));
            }
        }
        String key = "a";
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            for (Boolean value : new Boolean[]{true, false, null}) {
                assertTrue(v8ValueObject.setBoolean(key, value));
                assertTrue(v8ValueObject.has(key));
                assertEquals(value, v8ValueObject.getBoolean(key));
            }
        }
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            for (Double value : new Double[]{0.1D, 1.234D, -1.234D, Double.MIN_VALUE, Double.MAX_VALUE}) {
                assertTrue(v8ValueObject.setDouble(key, value));
                assertTrue(v8ValueObject.has(key));
                assertEquals(value, v8ValueObject.getDouble(key), 0.001D);
            }
            assertTrue(v8ValueObject.setDouble(key, null));
            assertTrue(v8ValueObject.has(key));
            assertNull(v8ValueObject.getDouble(key));
        }
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            for (Integer value : new Integer[]{0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, null}) {
                assertTrue(v8ValueObject.setInteger(key, value));
                assertTrue(v8ValueObject.has(key));
                assertEquals(value, v8ValueObject.getInteger(key));
            }
        }
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            for (Long value : new Long[]{0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, null}) {
                assertTrue(v8ValueObject.setLong(key, value));
                assertTrue(v8ValueObject.has(key));
                assertEquals(value, v8ValueObject.getLong(key));
            }
        }
        v8Runtime.getExecutor("var test = { get a(){return b;}};").executeVoid();
        try {
            v8Runtime.getGlobalObject().getObject("test");
            fail("Failed to report ReferenceError.");
        } catch (JavetExecutionException e) {
            assertEquals("ReferenceError: b is not defined", e.getMessage());
            assertEquals(
                    "ReferenceError: b is not defined\n" +
                            "Resource: undefined\n" +
                            "Source Code: var test = { get a(){return b;}};\n" +
                            "Line Number: 1\n" +
                            "Column: 21, 22\n" +
                            "Position: 21, 22",
                    e.getScriptingError().toString());
        }
    }

    @Test
    public void testHarmonyScoping() throws JavetException {
        v8Runtime.getExecutor("let a = 1;").executeVoid();
        v8Runtime.getExecutor("let b = 2;").executeVoid();
        assertEquals(3, v8Runtime.getExecutor("a + b").executeInteger(),
                "Variables should be visible in the scope.");
    }

    @Test
    public void testIdentityHash() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const a = {}; a;").execute()) {
            assertTrue(v8ValueObject.getIdentityHash() > 0);
        }
    }

    @Test
    public void testInvokeObject() throws JavetException {
        v8Runtime.getExecutor("function a(b) { return [1,2,3].concat(b);}").executeVoid();
        List<Integer> result = v8Runtime.getGlobalObject().invokeObject("a", (Object) (new Integer[]{4, 5, 6}));
        assertArrayEquals(
                new Integer[]{1, 2, 3, 4, 5, 6},
                result.toArray(new Integer[0]),
                "invokeObject() should work transparently without resource leak");
    }

    @Test
    public void testInvokeVoid() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = [1, 2, 3]; a;").execute()) {
            assertEquals(3, v8ValueArray.getLength());
            v8ValueArray.invokeVoid("push", 4);
            assertEquals(4, v8ValueArray.getLength());
            assertEquals(4, v8ValueArray.getInteger(3));
            assertEquals("1,2,3,4", v8ValueArray.toString());
        }
    }

    @Test
    public void testNestedObject() throws JavetException {
        try (V8ValueObject outerObject = v8Runtime.getExecutor("const o = {}; o;").execute()) {
            assertEquals("{}", outerObject.toJsonString());
            try (V8ValueObject innerObject = v8Runtime.createV8ValueObject()) {
                innerObject.set("a", "1");
                outerObject.set("x", innerObject);
            }
            assertEquals("{\"x\":{\"a\":\"1\"}}", outerObject.toJsonString());
        }
    }

    @Test
    @Tag("performance")
    public void testPerformanceSet() throws JavetException {
        final int pairCount = 1000;
        final int iterations = 1000;
        // Test set one by one.
        {
            final long startTime = System.currentTimeMillis();
            int successCount = 0;
            for (int i = 0; i < iterations; i++) {
                try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                    for (int j = 0; j < pairCount; j++) {
                        if (v8ValueObject.set("a", "a")) {
                            successCount++;
                        }
                    }
                }
            }
            final long stopTime = System.currentTimeMillis();
            final long tps = pairCount * iterations * 1000 / (stopTime - startTime);
            logger.logInfo("Object set one by one: {0} tps.", tps);
            assertEquals(pairCount * iterations, successCount);
        }
        // Test set by batch.
        {
            final long startTime = System.currentTimeMillis();
            int successCount = 0;
            Object[] items = new Object[pairCount * 2];
            Arrays.fill(items, "a");
            for (int i = 0; i < iterations; i++) {
                try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                    if (v8ValueObject.set(items)) {
                        successCount++;
                    }
                }
            }
            final long stopTime = System.currentTimeMillis();
            final long tps = pairCount * iterations * 1000 / (stopTime - startTime);
            logger.logInfo("Object set in a batch: {0} tps.", tps);
            assertEquals(iterations, successCount);
        }
    }

    @Test
    public void testPrototype() throws JavetException {
        v8Runtime.getExecutor("function A() {}; A.prototype.b = () => 2;").executeVoid();
        v8Runtime.getExecutor("function B() {}").executeVoid();
        try (V8ValueObject v8ValueObjectA = v8Runtime.getExecutor("A;").execute()) {
            try (V8ValueObject v8ValueObjectPrototypeA = v8ValueObjectA.getPrototype()) {
                assertTrue(v8ValueObjectPrototypeA.hasOwnProperty("b"));
                assertFalse(v8ValueObjectPrototypeA.hasOwnProperty("a"));
                v8ValueObjectPrototypeA.bindFunction("a", "() => 1");
                assertTrue(v8ValueObjectPrototypeA.hasOwnProperty("a"));
                try (V8ValueObject v8ValueObjectB = v8Runtime.getExecutor("B;").execute()) {
                    try (V8ValueObject v8ValueObjectPrototypeB = v8ValueObjectB.getPrototype()) {
                        assertFalse(v8ValueObjectPrototypeB.hasOwnProperty("b"));
                        assertFalse(v8ValueObjectPrototypeB.hasOwnProperty("a"));
                    }
                    v8ValueObjectB.setPrototype(v8ValueObjectPrototypeA);
                    try (V8ValueObject v8ValueObjectPrototypeB = v8ValueObjectB.getPrototype()) {
                        assertTrue(v8ValueObjectPrototypeB.hasOwnProperty("b"));
                        assertTrue(v8ValueObjectPrototypeB.hasOwnProperty("a"));
                    }
                }
            }
        }
        assertEquals(1, v8Runtime.getExecutor("const a = new A(); a.a();").executeInteger());
        assertEquals(1, v8Runtime.getExecutor("const b = new B(); b.a();").executeInteger());
        assertEquals(2, v8Runtime.getExecutor("b.b();").executeInteger());
    }

    @Test
    public void testSetProperty() throws JavetException {
        ZonedDateTime now = ZonedDateTime.now();
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {}; x;").execute()) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                assertEquals(0, iV8ValueArray.getLength());
            }
            v8ValueObject.setProperty("a", "1");
            v8ValueObject.setProperty("b", 2);
            v8ValueObject.setProperty("c", 3L);
            v8ValueObject.setProperty("d", now);
            v8ValueObject.setProperty("e", 1.23D);
            v8ValueObject.setProperty("f", true);
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                assertEquals(6, iV8ValueArray.getLength());
                assertEquals("a", iV8ValueArray.getString(0));
                assertEquals("b", iV8ValueArray.getString(1));
                assertEquals("c", iV8ValueArray.getString(2));
                assertEquals("d", iV8ValueArray.getString(3));
                assertEquals("e", iV8ValueArray.getString(4));
                assertEquals("f", iV8ValueArray.getString(5));
            }
            assertEquals("1", v8ValueObject.getPropertyString("a"));
            assertEquals(2, v8ValueObject.getPropertyInteger("b"));
            assertEquals(3L, v8ValueObject.getPropertyLong("c"));
            assertEquals(
                    now.toInstant().toEpochMilli(),
                    v8ValueObject.getPropertyZonedDateTime("d").toInstant().toEpochMilli());
            assertEquals(1.23, v8ValueObject.getPropertyDouble("e"), 0.001);
            assertTrue(v8ValueObject.getPropertyBoolean("f"));
            assertEquals("[object Object]", v8ValueObject.toString());
            assertEquals("[object Object]", v8ValueObject.toProtoString());
        }
    }

    @Test
    public void testSetWeakDirectDescendant() throws JavetException {
        V8ValueObject a = v8Runtime.createV8ValueObject();
        V8ValueGlobalObject globalObject = v8Runtime.getGlobalObject();
        globalObject.set("a", a);
        a.setWeak();
        assertTrue(a.isWeak());
        assertEquals(1, v8Runtime.getReferenceCount());
        a.close();
        assertEquals(1, v8Runtime.getReferenceCount(),
                "Close() should not work because 'a' is weak.");
        a.clearWeak();
        assertFalse(a.isWeak());
        assertEquals(1, v8Runtime.getReferenceCount());
        a.setWeak();
        globalObject.delete("a");
        v8Runtime.lowMemoryNotification();
        assertEquals(0, v8Runtime.getReferenceCount());
        assertEquals(0L, a.getHandle());
        assertTrue(globalObject.get("a").isUndefined());
    }

    @Test
    public void testSetWeakIndirectDescendant() throws JavetException {
        V8ValueGlobalObject globalObject = v8Runtime.getGlobalObject();
        try (V8ValueObject a = v8Runtime.createV8ValueObject()) {
            globalObject.set("a", a);
            V8ValueObject b = v8Runtime.createV8ValueObject();
            a.set("b", b);
            b.setWeak();
        }
        assertEquals(1, v8Runtime.getReferenceCount());
        globalObject.delete("a");
        v8Runtime.lowMemoryNotification();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testToClone(boolean referenceCopy) throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {}; x;").execute()) {
            assertTrue(v8ValueObject.set("a", "1"));
            assertEquals("{\"a\":\"1\"}", v8ValueObject.toJsonString());
            try (V8ValueObject clonedV8ValueObject = v8ValueObject.toClone(referenceCopy)) {
                assertEquals("{\"a\":\"1\"}", clonedV8ValueObject.toJsonString());
                assertNotEquals(v8ValueObject.getHandle(), clonedV8ValueObject.getHandle());
                if (referenceCopy) {
                    assertTrue(clonedV8ValueObject.strictEquals(v8ValueObject));
                } else {
                    assertFalse(clonedV8ValueObject.strictEquals(v8ValueObject));
                }
                assertTrue(clonedV8ValueObject.set("a", "2"));
                assertEquals("{\"a\":\"2\"}", clonedV8ValueObject.toJsonString());
                if (referenceCopy) {
                    assertEquals("{\"a\":\"2\"}", v8ValueObject.toJsonString());
                } else {
                    assertEquals("{\"a\":\"1\"}", v8ValueObject.toJsonString());
                }
                assertEquals(v8Runtime, clonedV8ValueObject.getV8Runtime());
            }
        }
    }

    @Test
    public void testToJsonString() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {}; x;").execute()) {
            v8ValueObject.setProperty("a", "1");
            v8ValueObject.setProperty("b", 2);
            v8ValueObject.setProperty("c", 1.23);
            assertEquals("{\"a\":\"1\",\"b\":2,\"c\":1.23}", v8ValueObject.toJsonString());
        }
    }
}
