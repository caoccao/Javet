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

public class TestV8ValueNull extends BaseTestJavetRuntime {
    @Test
    public void testEquals() throws JavetException {
        try (V8ValueNull v8ValueNull = v8Runtime.getExecutor("null").execute()) {
            assertTrue(v8ValueNull.equals(v8Runtime.createV8ValueNull()));
            assertFalse(v8ValueNull.equals(null));
            assertFalse(v8ValueNull.equals(v8Runtime.createV8ValueUndefined()));
        }
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertFalse(v8Runtime.createV8ValueNull().asBoolean());
    }

    @Test
    public void testNull() throws JavetException {
        try (V8ValueNull v8ValueNull = v8Runtime.getExecutor("null").execute()) {
            assertNotNull(v8ValueNull);
            assertEquals(v8Runtime, v8ValueNull.getV8Runtime());
            assertTrue(v8ValueNull.isNull());
            assertTrue(v8ValueNull.isNullOrUndefined());
        }
    }
}
