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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueSet extends BaseTestJavetRuntime {
    @Test
    public void testAddHasAndDelete() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.execute("const a = new Set(); a;")) {
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
    public void testHas() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.execute(
                "const a = new Set(); a.add('x', 1); a.add('y', 'b'); a.add(3, 'c'); a;")) {
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
            List<V8Value> keys = v8ValueSet.getKeys();
            assertEquals(3, keys.size());
            assertEquals("x", ((V8ValueString) keys.get(0)).getValue());
            assertEquals("y", ((V8ValueString) keys.get(1)).getValue());
            assertEquals(3, ((V8ValueInteger) keys.get(2)).getValue());
        }
    }

    @Test
    public void testNestedSet() throws JavetException {
        try (V8ValueSet outerObject = v8Runtime.execute("const o = new Set(); o;")) {
            assertEquals(
                    "[]",
                    v8Runtime.executeString(
                            "JSON.stringify(o, (key, value) => value instanceof Set ? [...value] : value);"));
            outerObject.add(new V8ValueString("1"));
            try (V8ValueSet innerObject = v8Runtime.createV8ValueSet()) {
                innerObject.add(new V8ValueString("2"));
                outerObject.add(innerObject);
            }
            assertEquals(
                    "[\"1\",[\"2\"]]",
                    v8Runtime.executeString(
                            "JSON.stringify(o, (key, value) => value instanceof Set ? [...value] : value);"));
        }
    }
}
