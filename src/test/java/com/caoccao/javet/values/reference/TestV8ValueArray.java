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
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueArray extends BaseTestJavetRuntime {
    @Test
    public void testForEach() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = new Array(0,1,2); a;").execute()) {
            AtomicInteger count = new AtomicInteger(0);
            v8ValueArray.forEach((V8ValueInteger value) -> {
                assertEquals(count.getAndIncrement(), value.getValue());
            });
            v8ValueArray.forEach((int index, V8ValueInteger value) -> {
                assertEquals(index, value.getValue());
            });
        }
    }

    @Test
    public void testGet() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(
                "[1,'2',3n, true, 1.23, [4, 5, null, new Date(1611710223719)]]").execute()) {
            assertNotNull(v8ValueArray);
            assertEquals(v8Runtime, v8ValueArray.getV8Runtime());
            assertEquals(6, v8ValueArray.getLength());
            assertEquals(1, ((V8ValueInteger) v8ValueArray.get(0)).getValue());
            assertEquals(1, v8ValueArray.getInteger(0));
            assertEquals("2", ((V8ValueString) v8ValueArray.get(1)).getValue());
            assertEquals("2", v8ValueArray.getString(1));
            assertEquals(3L, ((V8ValueLong) v8ValueArray.get(2)).getValue());
            assertEquals(3L, v8ValueArray.getLong(2));
            assertTrue(((V8ValueBoolean) v8ValueArray.get(3)).getValue());
            assertTrue(v8ValueArray.getBoolean(3));
            assertEquals(1.23, ((V8ValueDouble) v8ValueArray.get(4)).getValue(), 0.001);
            assertEquals(1.23, v8ValueArray.getDouble(4), 0.001);
            assertTrue(v8ValueArray.get(-1).isUndefined());
            assertTrue(v8ValueArray.get(100).isUndefined());
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueArray childV8ValueArray = v8ValueArray.get(5)) {
                assertNotNull(childV8ValueArray);
                assertEquals(v8Runtime, childV8ValueArray.getV8Runtime());
                assertEquals(4, childV8ValueArray.getLength());
                assertEquals(4, childV8ValueArray.getInteger(0));
                assertEquals(5, childV8ValueArray.getInteger(1));
                assertTrue(childV8ValueArray.get(2).isNull());
                assertEquals(
                        "2021-01-27T01:17:03.719Z[UTC]",
                        childV8ValueArray.getZonedDateTime(3).withZoneSameInstant(ZoneId.of("UTC")).toString());
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }

    @Test
    public void testGetAndSet() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = new Array(); a;").execute()) {
            v8ValueArray.set(0, "x");
            v8ValueArray.set(1, "y");
            v8ValueArray.set(2, "z");
            v8ValueArray.set("a", 1);
            v8ValueArray.set("b", "2");
            assertEquals(3, v8ValueArray.getLength());
            assertEquals("x", v8ValueArray.getString(0));
            assertEquals("y", v8ValueArray.getString(1));
            assertEquals("z", v8ValueArray.getString(2));
            assertEquals(1, v8ValueArray.getInteger("a"));
            assertEquals("2", v8ValueArray.getString("b"));
            assertEquals("x,y,z", v8ValueArray.toString());
            assertEquals("[object Array]", v8ValueArray.toProtoString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8ValueArray.toJsonString());
            List<Integer> keys = v8ValueArray.getKeys();
            assertEquals(3, keys.size());
            assertEquals(0, keys.get(0));
            assertEquals(1, keys.get(1));
            assertEquals(2, keys.get(2));
        }
    }

    @Test
    public void testNestedArray() throws JavetException {
        try (V8ValueArray outerArray = v8Runtime.getExecutor("[1,2,3]").execute()) {
            assertEquals(3, outerArray.getLength());
            try (V8ValueArray innerArray = v8Runtime.createV8ValueArray()) {
                assertEquals(1, innerArray.push("a"));
                assertEquals(1, innerArray.getLength());
                assertEquals(4, outerArray.push(innerArray));
            }
            assertEquals("[1,2,3,[\"a\"]]", outerArray.toJsonString());
        }
    }

    @Test
    public void testPushPop() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[]").execute()) {
            assertEquals(0, v8ValueArray.getLength());
            assertEquals(1, v8ValueArray.push(true));
            assertEquals(2, v8ValueArray.push(1.23));
            assertEquals(3, v8ValueArray.push(4));
            assertEquals(4, v8ValueArray.push(5L));
            assertEquals(5, v8ValueArray.push("x"));
            assertEquals(6, v8ValueArray.pushNull());
            assertEquals(7, v8ValueArray.pushUndefined());
            assertEquals(7, v8ValueArray.getLength());
            assertEquals("true,1.23,4,5,x,,", v8ValueArray.toString());
            assertNotNull(v8ValueArray.popUndefined());
            assertNotNull(v8ValueArray.popNull());
            assertEquals("x", v8ValueArray.popString());
            assertEquals(5L, v8ValueArray.popLong());
            assertEquals(4, v8ValueArray.popInteger());
            assertEquals(1.23, v8ValueArray.popDouble(), 0.001);
            assertEquals(true, v8ValueArray.popBoolean());
            assertEquals(0, v8ValueArray.getLength());
            assertEquals("", v8ValueArray.toString());
        }
    }
}
