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
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.virtual.V8VirtualList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueMap extends BaseTestJavetRuntime {
    @Test
    public void testGetAndHas() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.getExecutor(
                "const a = new Map(); a.set('x', 1); a.set('y', 'b'); a.set(3, 'c'); a;").execute()) {
            assertNotNull(v8ValueMap);
            assertEquals(3, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getInteger("x"));
            assertEquals("b", v8ValueMap.getString("y"));
            assertEquals("c", v8ValueMap.getString(3));
            assertTrue(v8ValueMap.has("x"));
            assertTrue(v8ValueMap.has(3));
            assertFalse(v8ValueMap.has("p"));
            assertFalse(v8ValueMap.has(0));
            assertEquals("{}", v8ValueMap.toJsonString());
            assertEquals("[object Map]", v8ValueMap.toString());
            assertEquals("[object Map]", v8ValueMap.toProtoString());
            try (IV8ValueArray iV8ValueArray = v8ValueMap.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(0, iV8ValueArray.getLength());
            }
            List<V8Value> keys = v8ValueMap.getKeys();
            assertEquals(3, keys.size());
            assertEquals("x", ((V8ValueString) keys.get(0)).getValue());
            assertEquals("y", ((V8ValueString) keys.get(1)).getValue());
            assertEquals(3, ((V8ValueInteger) keys.get(2)).getValue());
            List<V8Value> values = v8ValueMap.getValues();
            assertEquals(3, values.size());
            assertEquals(1, ((V8ValueInteger) values.get(0)).getValue());
            assertEquals("b", ((V8ValueString) values.get(1)).getValue());
            assertEquals("c", ((V8ValueString) values.get(2)).getValue());
            try (V8VirtualList<V8Value> entries = v8ValueMap.getEntries()) {
                assertEquals(3, values.size());
                assertEquals(4, v8Runtime.getReferenceCount());
                V8ValueArray entry = (V8ValueArray) entries.get(0);
                assertEquals("x", entry.getString(0));
                assertEquals(1, entry.getInteger(1));
                entry = (V8ValueArray) entries.get(1);
                assertEquals("y", entry.getString(0));
                assertEquals("b", entry.getString(1));
                entry = (V8ValueArray) entries.get(2);
                assertEquals(3, entry.getInteger(0));
                assertEquals("c", entry.getString(1));
            }
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }

    @Test
    public void testGetSetAndDelete() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.getExecutor("const a = new Map(); a;").execute()) {
            v8ValueMap.set("a", new V8ValueInteger(1));
            v8ValueMap.set("b", new V8ValueString("2"));
            assertEquals(2, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getInteger("a"));
            assertEquals("2", v8ValueMap.getString("b"));
            v8ValueMap.setNull("c");
            v8ValueMap.setUndefined("d");
            assertEquals(4, v8ValueMap.getSize());
            assertTrue(v8ValueMap.has("a"));
            assertFalse(v8ValueMap.has("x"));
            assertNotNull(v8ValueMap.getNull("c"));
            assertNotNull(v8ValueMap.getUndefined("d"));
            assertTrue(v8ValueMap.delete("c"));
            assertTrue(v8ValueMap.delete("d"));
            assertFalse(v8ValueMap.delete(2));
            assertEquals(2, v8ValueMap.getSize());
            assertTrue(v8ValueMap.delete("a"));
            assertTrue(v8ValueMap.delete("b"));
            assertEquals(0, v8ValueMap.getSize());
        }
    }

    @Test
    public void testNestedMap() throws JavetException {
        try (V8ValueMap outerObject = v8Runtime.getExecutor("const o = new Map(); o;").execute()) {
            assertEquals(
                    "[]",
                    v8Runtime.getExecutor(
                            "JSON.stringify(o, (key, value) => value instanceof Map ? [...value] : value);").executeString());
            try (V8ValueMap innerObject = v8Runtime.createV8ValueMap()) {
                innerObject.set("a", new V8ValueString("1"));
                outerObject.set("x", innerObject);
            }
            assertEquals(
                    "[[\"x\",[[\"a\",\"1\"]]]]",
                    v8Runtime.getExecutor(
                            "JSON.stringify(o, (key, value) => value instanceof Map ? [...value] : value);").executeString());
        }
    }
}
