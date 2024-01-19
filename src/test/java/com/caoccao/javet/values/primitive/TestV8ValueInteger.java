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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueInteger extends BaseTestJavetRuntime {
    @Test
    public void testEquals() throws JavetException {
        V8ValueInteger v8ValueInteger = v8Runtime.getExecutor("1").execute();
        assertTrue(v8ValueInteger.equals(v8Runtime.createV8ValueInteger(1)));
        assertFalse(v8ValueInteger.equals(null));
        assertFalse(v8ValueInteger.equals(v8Runtime.createV8ValueInteger(2)));
        assertFalse(v8ValueInteger.equals(v8Runtime.createV8ValueLong(1)));
    }

    @Test
    public void testInt32() throws JavetException {
        try (V8ValueInteger v8ValueInteger = v8Runtime.getExecutor("1 + 1").execute()) {
            assertNotNull(v8ValueInteger);
            assertEquals(2, v8ValueInteger.getValue());
            assertEquals("2", v8ValueInteger.toString());
            assertEquals(v8Runtime, v8ValueInteger.getV8Runtime());
        }
        assertEquals(-1, v8Runtime.getExecutor("1 - 2").executeInteger());
        for (int i = 0; i < 100; ++i) {
            assertEquals(1 << i, v8Runtime.getExecutor("1 << " + i).executeInteger());
        }
    }

    @Test
    public void testInt32Object() throws JavetException {
        assertEquals(123, v8Runtime.getExecutor("Number(123)").executeInteger());
    }
}
