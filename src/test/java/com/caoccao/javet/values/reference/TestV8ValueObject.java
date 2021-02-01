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
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueObject extends BaseTestJavetRuntime {
    @Test
    public void testGetOwnPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;")) {
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(7, iV8ValueCollection.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueCollection.getPropertyString(0));
                assertEquals("b", iV8ValueCollection.getPropertyString(1));
                assertEquals("c", iV8ValueCollection.getPropertyString(2));
                assertEquals("d", iV8ValueCollection.getPropertyString(3));
                assertEquals("e", iV8ValueCollection.getPropertyString(4));
                assertEquals("g", iV8ValueCollection.getPropertyString(5));
                assertEquals("中文", iV8ValueCollection.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;")) {
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(7, iV8ValueCollection.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueCollection.getPropertyString(0));
                assertEquals("b", iV8ValueCollection.getPropertyString(1));
                assertEquals("c", iV8ValueCollection.getPropertyString(2));
                assertEquals("d", iV8ValueCollection.getPropertyString(3));
                assertEquals("e", iV8ValueCollection.getPropertyString(4));
                assertEquals("g", iV8ValueCollection.getPropertyString(5));
                assertEquals("中文", iV8ValueCollection.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetSetDelete() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute("const a = {}; a;")) {
            assertTrue(v8ValueObject.set("a", new V8ValueInteger(1)));
            assertTrue(v8ValueObject.set("b", new V8ValueString("2")));
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
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1, 3: 'x'}, '中文': '測試'};"
                        + "x['i'] = true;x['j'] = 1.23;x['k'] = new Date(1611710223719);"
                        + "x;")) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            assertEquals(1, ((V8ValueInteger) v8ValueObject.getProperty("a")).getValue());
            assertEquals(1, v8ValueObject.getPropertyInteger("a"));
            assertEquals("2", ((V8ValueString) v8ValueObject.getProperty("b")).getValue());
            assertEquals("2", v8ValueObject.getPropertyString("b"));
            assertEquals(3L, ((V8ValueLong) v8ValueObject.getProperty("c")).getValue());
            assertEquals(3L, v8ValueObject.getPropertyLong("c"));
            assertEquals(1, v8ValueObject.getPropertyInteger("d"));
            assertTrue(v8ValueObject.getProperty("e") instanceof V8ValueNull);
            assertEquals("測試", v8ValueObject.getPropertyString("中文"));
            assertTrue(v8ValueObject.getProperty("$") instanceof V8ValueUndefined);
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
    public void testSetProperty() throws JavetException {
        ZonedDateTime now = ZonedDateTime.now();
        try (V8ValueObject v8ValueObject = v8Runtime.execute("const x = {}; x;")) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getOwnPropertyNames()) {
                assertEquals(0, iV8ValueCollection.getLength());
            }
            v8ValueObject.setProperty("a", new V8ValueString("1"));
            v8ValueObject.setProperty(new V8ValueString("b"), new V8ValueInteger(2));
            v8ValueObject.setProperty(new V8ValueString("c"), new V8ValueLong(3));
            v8ValueObject.setProperty(new V8ValueString("d"), new V8ValueZonedDateTime(now));
            v8ValueObject.setProperty(new V8ValueString("e"), new V8ValueDouble(1.23));
            v8ValueObject.setProperty(new V8ValueString("f"), new V8ValueBoolean(true));
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getOwnPropertyNames()) {
                assertEquals(6, iV8ValueCollection.getLength());
                assertEquals("a", iV8ValueCollection.getString(0));
                assertEquals("b", iV8ValueCollection.getString(1));
                assertEquals("c", iV8ValueCollection.getString(2));
                assertEquals("d", iV8ValueCollection.getString(3));
                assertEquals("e", iV8ValueCollection.getString(4));
                assertEquals("f", iV8ValueCollection.getString(5));
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
        }
    }
}
