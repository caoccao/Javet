/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueBoolean extends BaseTestJavetRuntime {
    @Test
    public void testBoolean() throws JavetException {
        try (V8ValueBoolean v8ValueBoolean = v8Runtime.execute("1 == 1")) {
            assertNotNull(v8ValueBoolean);
            assertTrue(v8ValueBoolean.isPresent());
            assertTrue(v8ValueBoolean.getValue());
            assertEquals("true", v8ValueBoolean.toString());
            assertEquals(v8Runtime, v8ValueBoolean.getV8Runtime());
        }
        try (V8ValueBoolean v8ValueBoolean = v8Runtime.execute("1 != 1")) {
            assertNotNull(v8ValueBoolean);
            assertTrue(v8ValueBoolean.isPresent());
            assertFalse(v8ValueBoolean.getValue());
            assertEquals("false", v8ValueBoolean.toString());
            assertEquals(v8Runtime, v8ValueBoolean.getV8Runtime());
        }
        assertTrue(v8Runtime.executeBoolean("true"));
        assertFalse(v8Runtime.executeBoolean("false"));
    }

    @Test
    public void testBooleanObject() throws JavetException {
        assertTrue(v8Runtime.executeBoolean("Boolean(true)"));
        assertFalse(v8Runtime.executeBoolean("Boolean(false)"));
    }
}
