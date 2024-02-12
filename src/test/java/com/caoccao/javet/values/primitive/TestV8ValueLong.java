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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueLongObject;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueLong extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        assertEquals(0, v8Runtime.createV8ValueLong(0L).asInt());
        assertEquals(1, v8Runtime.createV8ValueLong(1L).asInt());
    }

    @Test
    public void testBigInt() throws JavetException {
        try (V8ValueLong v8ValueLong = v8Runtime.getExecutor("2n ** 62n").execute()) {
            assertNotNull(v8ValueLong);
            assertEquals(4611686018427387904L, v8ValueLong.getValue());
            assertEquals("4611686018427387904", v8ValueLong.toString());
            assertEquals(-2L, v8Runtime.getExecutor("-2n").executeLong());
            assertEquals(v8Runtime, v8ValueLong.getV8Runtime());
        }
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.set("a", 123L);
            assertEquals(123L, v8ValueObject.getLong("a"));
            v8Runtime.getGlobalObject().set("x", v8ValueObject);
        }
        try (V8ValueObject v8ValueObject = v8Runtime.getGlobalObject().get("x")) {
            assertEquals(123L, v8ValueObject.getLong("a"));
        }
    }

    @Test
    public void testBigIntObject() throws JavetException {
        assertEquals(123L, v8Runtime.getExecutor("BigInt(123n)").executeLong());
    }

    @Test
    public void testEquals() throws JavetException {
        V8ValueLong v8ValueLong = v8Runtime.getExecutor("1n").execute();
        assertTrue(v8ValueLong.equals(v8Runtime.createV8ValueLong(1L)));
        assertFalse(v8ValueLong.equals(null));
        assertFalse(v8ValueLong.equals(v8Runtime.createV8ValueLong(2L)));
        assertFalse(v8ValueLong.equals(v8Runtime.createV8ValueInteger(1)));
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertTrue(v8Runtime.createV8ValueLong(1L).asBoolean());
        assertFalse(v8Runtime.createV8ValueLong(0L).asBoolean());
        assertTrue(v8Runtime.getExecutor("1n").execute().asBoolean());
        assertFalse(v8Runtime.getExecutor("0n").execute().asBoolean());
    }

    @Test
    public void testLongObject() throws JavetException {
        // 2n**62n = 4611686018427387904n
        try (V8ValueLong v8ValueLong1 = v8Runtime.createV8ValueLong(4611686018427387904L)) {
            try (V8ValueLongObject v8ValueLongObject = v8ValueLong1.toObject()) {
                try (V8ValueLong v8ValueLong2 = v8ValueLongObject.valueOf()) {
                    assertEquals(4611686018427387904L, v8ValueLong2.getValue());
                }
            }
        }
        try (V8ValueLongObject v8ValueLongObject = v8Runtime.createV8ValueLongObject(4611686018427387904L)) {
            try (V8ValueLong v8ValueLong = v8ValueLongObject.valueOf()) {
                assertEquals(4611686018427387904L, v8ValueLong.getValue());
            }
        }
        try (V8ValueLong v8ValueLong = v8Runtime.getExecutor("BigInt(4611686018427387904n)").execute()) {
            assertEquals(4611686018427387904L, v8ValueLong.getValue());
        }
        assertTrue(v8Runtime.getExecutor("4611686018427387904n").executeBoolean());
        assertEquals(4611686018427387904D, v8Runtime.getExecutor("4611686018427387904n").executeDouble(), DELTA);
        assertEquals(0, v8Runtime.getExecutor("4611686018427387904n").executeInteger());
        assertEquals(4611686018427387904L, v8Runtime.getExecutor("4611686018427387904n").executeLong());
        assertEquals("4611686018427387904", v8Runtime.getExecutor("4611686018427387904n").executeString());
    }

    @Test
    public void testString() throws JavetException {
        assertEquals("4611686018427387904", v8Runtime.getExecutor("(2n ** 62n).toString()").executeString());
        assertEquals("-2", v8Runtime.getExecutor("(-2n).toString()").executeString());
    }
}
