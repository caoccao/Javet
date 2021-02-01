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
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueMap extends BaseTestJavetRuntime {
    @Test
    public void testGetAndHas() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.execute(
                "const a = new Map(); a.set('x', 1); a.set('y', 'b'); a.set(3, 'c'); a;")) {
            assertNotNull(v8ValueMap);
            assertEquals(3, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getInteger("x"));
            assertEquals("b", v8ValueMap.getString("y"));
            assertEquals("c", v8ValueMap.getString(3));
            assertTrue(v8ValueMap.has("x"));
            assertTrue(v8ValueMap.has(3));
            assertFalse(v8ValueMap.has("p"));
            assertFalse(v8ValueMap.has(0));
            assertEquals("[object Map]", v8ValueMap.toString());
            try (IV8ValueCollection iV8ValueCollection = v8ValueMap.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(0, iV8ValueCollection.getLength());
            }
        }
    }

    @Test
    public void testGetSetAndDelete() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.execute("const a = new Map(); a;")) {
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
}
