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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueMap extends BaseTestJavetRuntime {
    @Test
    public void testAsArray() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            v8ValueMap.set("a", 1, "b", "x");
            try (V8ValueArray v8ValueArray = v8ValueMap.asArray()) {
                assertEquals(4, v8ValueArray.getLength());
                assertEquals("a", v8ValueArray.getString(0));
                assertEquals(1, v8ValueArray.getInteger(1));
                assertEquals("b", v8ValueArray.getString(2));
                assertEquals("x", v8ValueArray.getString(3));
            }
        }
    }
    @Test
    public void testClear() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            v8ValueMap.set("a", 1, "b", "x");
            assertEquals(2, v8ValueMap.getSize());
            v8ValueMap.clear();
            assertEquals(0, v8ValueMap.getSize());
        }
    }

    @Test
    public void testForEach() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.getExecutor(
                "const a = new Map(); a.set('0', 0); a.set('1', 1); a.set('2', 2); a;").execute()) {
            // V8 feature: Order is preserved.
            AtomicInteger count = new AtomicInteger(0);
            assertEquals(3, v8ValueMap.forEach((V8ValueString key) -> {
                assertNotNull(key);
                assertEquals(Integer.toString(count.getAndIncrement()), key.getValue());
            }));
            count.set(0);
            assertEquals(3, v8ValueMap.forEach((V8ValueString key, V8ValueInteger value) -> {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals(Integer.toString(count.get()), key.getValue());
                assertEquals(count.getAndIncrement(), value.getValue());
            }));
            assertEquals(3, v8ValueMap.forEach((int index, V8ValueString key) -> {
                assertNotNull(key);
                assertEquals(Integer.toString(index), key.getValue());
            }));
            assertEquals(3, v8ValueMap.forEach((int index, V8ValueString key, V8ValueInteger value) -> {
                assertNotNull(key);
                assertNotNull(value);
                assertEquals(Integer.toString(index), key.getValue());
                assertEquals(index, value.getValue());
            }));
        }
    }

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
            try (IV8ValueIterator iterator = v8ValueMap.getKeys()) {
                assertEquals("x", ((V8ValueString) iterator.getNext()).getValue());
                assertEquals("y", ((V8ValueString) iterator.getNext()).getValue());
                assertEquals(3, ((V8ValueInteger) iterator.getNext()).getValue());
            }
            try (IV8ValueIterator iterator = v8ValueMap.getValues()) {
                assertEquals(1, ((V8ValueInteger) iterator.getNext()).getValue());
                assertEquals("b", ((V8ValueString) iterator.getNext()).getValue());
                assertEquals("c", ((V8ValueString) iterator.getNext()).getValue());
            }
            try (IV8ValueIterator<V8ValueArray> iterator = v8ValueMap.getEntries()) {
                try (V8ValueArray entry = iterator.getNext()) {
                    assertEquals("x", entry.getString(0));
                    assertEquals(1, entry.getInteger(1));
                }
                try (V8ValueArray entry = iterator.getNext()) {
                    assertEquals("y", entry.getString(0));
                    assertEquals("b", entry.getString(1));
                }
                try (V8ValueArray entry = iterator.getNext()) {
                    assertEquals(3, entry.getInteger(0));
                    assertEquals("c", entry.getString(1));
                }
            }
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }

    @Test
    public void testGetSetAndDelete() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.getExecutor("const a = new Map(); a;").execute()) {
            v8ValueMap.set("a", 1);
            v8ValueMap.set("b", "2");
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
            v8ValueMap.setString("d", "1");
            assertEquals("1", v8ValueMap.getString("d"));
            assertTrue(v8ValueMap.has("d"));
            v8ValueMap.setString("d", null);
            assertNull(v8ValueMap.getString("d"));
            assertTrue(v8ValueMap.has("d"));
            Object[] keysAndValues = new Object[]{"x", 1, "y", 2, "z", 3};
            assertTrue(v8ValueMap.set(keysAndValues));
            for (int i = 0; i < keysAndValues.length / 2; i += 2) {
                assertTrue(v8ValueMap.has(keysAndValues[i * 2]));
                assertEquals(keysAndValues[i * 2 + 1], v8ValueMap.getInteger(keysAndValues[i * 2]));
            }
        }
        String key = "a";
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            for (Boolean value : new Boolean[]{true, false, null}) {
                assertTrue(v8ValueMap.setBoolean(key, value));
                assertTrue(v8ValueMap.has(key));
                assertEquals(value, v8ValueMap.getBoolean(key));
            }
        }
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            for (Double value : new Double[]{0.1D, 1.234D, -1.234D, Double.MIN_VALUE, Double.MAX_VALUE}) {
                assertTrue(v8ValueMap.setDouble(key, value));
                assertTrue(v8ValueMap.has(key));
                assertEquals(value, v8ValueMap.getDouble(key), 0.001D);
            }
            assertTrue(v8ValueMap.setDouble(key, null));
            assertTrue(v8ValueMap.has(key));
            assertNull(v8ValueMap.getDouble(key));
        }
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            for (Integer value : new Integer[]{0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, null}) {
                assertTrue(v8ValueMap.setInteger(key, value));
                assertTrue(v8ValueMap.has(key));
                assertEquals(value, v8ValueMap.getInteger(key));
            }
        }
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            for (Long value : new Long[]{0L, 1L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, null}) {
                assertTrue(v8ValueMap.setLong(key, value));
                assertTrue(v8ValueMap.has(key));
                assertEquals(value, v8ValueMap.getLong(key));
            }
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
                innerObject.set("a", "1");
                outerObject.set("x", innerObject);
            }
            assertEquals(
                    "[[\"x\",[[\"a\",\"1\"]]]]",
                    v8Runtime.getExecutor(
                            "JSON.stringify(o, (key, value) => value instanceof Map ? [...value] : value);").executeString());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testToClone(boolean referenceCopy) throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.getExecutor("const o = new Map(); o;").execute()) {
            assertTrue(v8ValueMap.set("a", "1"));
            assertEquals("1", v8ValueMap.getString("a"));
            try (V8ValueMap clonedV8ValueMap = v8ValueMap.toClone(referenceCopy)) {
                assertEquals("1", clonedV8ValueMap.getString("a"));
                assertNotEquals(v8ValueMap.getHandle(), clonedV8ValueMap.getHandle());
                assertTrue(clonedV8ValueMap.strictEquals(v8ValueMap));
                assertTrue(clonedV8ValueMap.set("a", "2"));
                assertEquals("2", clonedV8ValueMap.getString("a"));
                assertEquals("2", v8ValueMap.getString("a"));
                assertEquals(v8Runtime, clonedV8ValueMap.getV8Runtime());
            }
        }
    }
}
