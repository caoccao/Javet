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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueSet extends BaseTestJavetRuntime {
    @Test
    public void testAddHasAndDelete() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.getExecutor("const a = new Set(); a;").execute()) {
            assertNotNull(v8ValueSet);
            assertEquals(0, v8ValueSet.getSize());
            v8ValueSet.add(1);
            assertEquals(1, v8ValueSet.getSize());
            v8ValueSet.add(1);
            assertEquals(1, v8ValueSet.getSize());
            v8ValueSet.add("x");
            assertEquals(2, v8ValueSet.getSize());
            v8ValueSet.addNull();
            v8ValueSet.addNull();
            v8ValueSet.addUndefined();
            v8ValueSet.addUndefined();
            assertEquals(4, v8ValueSet.getSize());
            assertTrue(v8ValueSet.has(1));
            assertTrue(v8ValueSet.has("x"));
            assertTrue(v8ValueSet.hasNull());
            assertTrue(v8ValueSet.hasUndefined());
            assertTrue(v8ValueSet.deleteNull());
            assertTrue(v8ValueSet.deleteUndefined());
            assertFalse(v8ValueSet.delete(2));
            assertEquals(2, v8ValueSet.getSize());
            assertTrue(v8ValueSet.delete(1));
            assertTrue(v8ValueSet.delete("x"));
            assertEquals(0, v8ValueSet.getSize());
        }
    }

    @Test
    public void testForEach() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.getExecutor(
                "const a = new Set(); a.add('0'); a.add('1'); a.add('2'); a;").execute()) {
            // V8 feature: Order is preserved.
            AtomicInteger count = new AtomicInteger(0);
            assertEquals(3, v8ValueSet.forEach((V8ValueString key) -> {
                assertEquals(Integer.toString(count.getAndIncrement()), key.getValue());
            }));
            assertEquals(3, v8ValueSet.forEach((int index, V8ValueString key) -> {
                assertEquals(Integer.toString(index), key.getValue());
            }));
        }
    }

    @Test
    public void testHas() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.getExecutor(
                "const a = new Set(); a.add('x', 1); a.add('y', 'b'); a.add(3, 'c'); a;").execute()) {
            assertNotNull(v8ValueSet);
            assertEquals(3, v8ValueSet.getSize());
            assertTrue(v8ValueSet.has("x"));
            assertTrue(v8ValueSet.has("y"));
            assertTrue(v8ValueSet.has(3));
            assertFalse(v8ValueSet.has("p"));
            assertFalse(v8ValueSet.has(0));
            assertEquals("{}", v8ValueSet.toJsonString());
            assertEquals("[object Set]", v8ValueSet.toString());
            assertEquals("[object Set]", v8ValueSet.toProtoString());
            try (IV8ValueArray iV8ValueArray = v8ValueSet.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(0, iV8ValueArray.getLength());
            }
            try (IV8ValueIterator<V8Value> iterator = v8ValueSet.getKeys()) {
                assertEquals("x", ((V8ValueString) iterator.getNext()).getValue());
                assertEquals("y", ((V8ValueString) iterator.getNext()).getValue());
                assertEquals(3, ((V8ValueInteger) iterator.getNext()).getValue());
            }
        }
    }

    @Test
    public void testNestedSet() throws JavetException {
        try (V8ValueSet outerObject = v8Runtime.getExecutor("const o = new Set(); o;").execute()) {
            assertEquals(
                    "[]",
                    v8Runtime.getExecutor(
                            "JSON.stringify(o, (key, value) => value instanceof Set ? [...value] : value);").executeString());
            outerObject.add("1");
            try (V8ValueSet innerObject = v8Runtime.createV8ValueSet()) {
                innerObject.add("2");
                outerObject.add(innerObject);
            }
            assertEquals(
                    "[\"1\",[\"2\"]]",
                    v8Runtime.getExecutor(
                            "JSON.stringify(o, (key, value) => value instanceof Set ? [...value] : value);").executeString());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testToClone(boolean referenceCopy) throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.getExecutor("const o = new Set(); o;").execute()) {
            v8ValueSet.add("1");
            assertTrue(v8ValueSet.has("1"));
            try (V8ValueSet clonedV8ValueSet = v8ValueSet.toClone(referenceCopy)) {
                assertTrue(clonedV8ValueSet.has("1"));
                assertNotEquals(v8ValueSet.getHandle(), clonedV8ValueSet.getHandle());
                assertTrue(clonedV8ValueSet.strictEquals(v8ValueSet));
                clonedV8ValueSet.add("2");
                assertTrue(clonedV8ValueSet.has("2"));
                assertTrue(v8ValueSet.has("2"));
                assertEquals(v8Runtime, clonedV8ValueSet.getV8Runtime());
            }
        }
    }
}
