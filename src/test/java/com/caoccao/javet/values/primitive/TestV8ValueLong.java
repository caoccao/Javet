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
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueLong extends BaseTestJavetRuntime {
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
    public void testString() throws JavetException {
        assertEquals("4611686018427387904", v8Runtime.getExecutor("(2n ** 62n).toString()").executeString());
        assertEquals("-2", v8Runtime.getExecutor("(-2n).toString()").executeString());
    }
}
