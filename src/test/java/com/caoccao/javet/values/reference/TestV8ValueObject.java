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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueObject extends BaseTestJavetRuntime {
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
            assertTrue(b instanceof V8ValueObject);
        }
    }

    @Test
    public void testForEach() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "const a = {'A0': 0, 'A1': 1, 'A2': 2}; a;").execute()) {
            AtomicInteger count = new AtomicInteger(0);
            assertEquals(3, v8ValueObject.forEach((V8ValueString key) -> {
                assertEquals("A" + Integer.toString(count.getAndIncrement()), key.getValue());
            }));
            count.set(0);
            assertEquals(3, v8ValueObject.forEach((V8ValueString key, V8ValueInteger value) -> {
                assertEquals("A" + Integer.toString(count.get()), key.getValue());
                assertEquals(count.getAndIncrement(), value.getValue());
            }));
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
    public void testGetOwnPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;").execute()) {
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(7, iV8ValueArray.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueArray.getPropertyString(0));
                assertEquals("b", iV8ValueArray.getPropertyString(1));
                assertEquals("c", iV8ValueArray.getPropertyString(2));
                assertEquals("d", iV8ValueArray.getPropertyString(3));
                assertEquals("e", iV8ValueArray.getPropertyString(4));
                assertEquals("g", iV8ValueArray.getPropertyString(5));
                assertEquals("中文", iV8ValueArray.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;").execute()) {
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(7, iV8ValueArray.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueArray.getPropertyString(0));
                assertEquals("b", iV8ValueArray.getPropertyString(1));
                assertEquals("c", iV8ValueArray.getPropertyString(2));
                assertEquals("d", iV8ValueArray.getPropertyString(3));
                assertEquals("e", iV8ValueArray.getPropertyString(4));
                assertEquals("g", iV8ValueArray.getPropertyString(5));
                assertEquals("中文", iV8ValueArray.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetSetDelete() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const a = {}; a;").execute()) {
            assertTrue(v8ValueObject.set("a", v8Runtime.createV8ValueInteger(1)));
            assertTrue(v8ValueObject.set("b", v8Runtime.createV8ValueString("2")));
            assertEquals(1, v8ValueObject.getInteger("a"));
            assertEquals("2", v8ValueObject.getString("b"));
            assertTrue(v8ValueObject.delete("x"));
            assertTrue(v8ValueObject.delete("b"));
            V8Value v8Value = v8ValueObject.getUndefined("b");
            assertNotNull(v8Value);
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
    public void testIdentityHash() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const a = {}; a;").execute()) {
            assertTrue(v8ValueObject.getIdentityHash() > 0);
        }
    }

    @Test
    public void testInvoke() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = [1, 2, 3]; a;").execute()) {
            assertEquals(3, v8ValueArray.getLength());
            v8ValueArray.invoke("push", v8Runtime.createV8ValueInteger(4));
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
                innerObject.set("a", v8Runtime.createV8ValueString("1"));
                outerObject.set("x", innerObject);
            }
            assertEquals("{\"x\":{\"a\":\"1\"}}", outerObject.toJsonString());
        }
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
            v8ValueObject.setProperty("a", v8Runtime.createV8ValueString("1"));
            v8ValueObject.setProperty(v8Runtime.createV8ValueString("b"), v8Runtime.createV8ValueInteger(2));
            v8ValueObject.setProperty(v8Runtime.createV8ValueString("c"), v8Runtime.createV8ValueLong(3));
            v8ValueObject.setProperty(v8Runtime.createV8ValueString("d"), v8Runtime.createV8ValueZonedDateTime(now));
            v8ValueObject.setProperty(v8Runtime.createV8ValueString("e"), v8Runtime.createV8ValueDouble(1.23));
            v8ValueObject.setProperty(v8Runtime.createV8ValueString("f"), v8Runtime.createV8ValueBoolean(true));
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
    public void testToClone() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {}; x;").execute()) {
            v8ValueObject.setProperty("a", v8Runtime.createV8ValueString("1"));
            assertEquals("{\"a\":\"1\"}", v8ValueObject.toJsonString());
            try (V8ValueObject clonedV8ValueObject = v8ValueObject.toClone()) {
                assertEquals("{\"a\":\"1\"}", clonedV8ValueObject.toJsonString());
                assertNotEquals(v8ValueObject.getHandle(), clonedV8ValueObject.getHandle());
                assertEquals(v8Runtime, clonedV8ValueObject.getV8Runtime());
            }
        }
    }

    @Test
    public void testToJsonString() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {}; x;").execute()) {
            v8ValueObject.setProperty("a", v8Runtime.createV8ValueString("1"));
            v8ValueObject.setProperty("b", v8Runtime.createV8ValueInteger(2));
            v8ValueObject.setProperty("c", v8Runtime.createV8ValueDouble(1.23));
            assertEquals("{\"a\":\"1\",\"b\":2,\"c\":1.23}", v8ValueObject.toJsonString());
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
        v8Runtime.requestGarbageCollectionForTesting(true);
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
        v8Runtime.requestGarbageCollectionForTesting(true);
    }
}
