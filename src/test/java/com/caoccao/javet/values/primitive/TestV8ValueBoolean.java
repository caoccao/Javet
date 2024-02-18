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
import com.caoccao.javet.values.reference.V8ValueBooleanObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueBoolean extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        assertEquals(0, v8Runtime.createV8ValueBoolean(false).asInt());
        assertEquals(1, v8Runtime.createV8ValueBoolean(true).asInt());
    }

    @Test
    public void testBoolean() throws JavetException {
        try (V8ValueBoolean v8ValueBoolean = v8Runtime.getExecutor("1 == 1").execute()) {
            assertNotNull(v8ValueBoolean);
            assertTrue(v8ValueBoolean.isPresent());
            assertTrue(v8ValueBoolean.getValue());
            assertEquals("true", v8ValueBoolean.toString());
            assertEquals(v8Runtime, v8ValueBoolean.getV8Runtime());
        }
        try (V8ValueBoolean v8ValueBoolean = v8Runtime.getExecutor("1 != 1").execute()) {
            assertNotNull(v8ValueBoolean);
            assertTrue(v8ValueBoolean.isPresent());
            assertFalse(v8ValueBoolean.getValue());
            assertEquals("false", v8ValueBoolean.toString());
            assertEquals(v8Runtime, v8ValueBoolean.getV8Runtime());
        }
        assertTrue(v8Runtime.getExecutor("true").executeBoolean());
        assertFalse(v8Runtime.getExecutor("false").executeBoolean());
    }

    @Test
    public void testBooleanObject() throws JavetException {
        try (V8ValueBoolean v8ValueBoolean1 = v8Runtime.createV8ValueBoolean(true)) {
            try (V8ValueBooleanObject v8ValueBooleanObject = v8ValueBoolean1.toObject()) {
                try (V8ValueBoolean v8ValueBoolean2 = v8ValueBooleanObject.valueOf()) {
                    assertTrue(v8ValueBoolean2.getValue());
                }
            }
        }
        try (V8ValueBooleanObject v8ValueBooleanObject = v8Runtime.createV8ValueBooleanObject(false)) {
            try (V8ValueBoolean v8ValueBoolean = v8ValueBooleanObject.valueOf()) {
                assertFalse(v8ValueBoolean.getValue());
            }
        }
        try (V8ValueBooleanObject v8ValueBooleanObject = v8Runtime.getExecutor("new Boolean(true)").execute()) {
            try (V8ValueBoolean v8ValueBoolean = v8ValueBooleanObject.valueOf()) {
                assertTrue(v8ValueBoolean.getValue());
            }
        }
        assertTrue(v8Runtime.getExecutor("true").executeBoolean());
        assertTrue(v8Runtime.getExecutor("new Boolean(true)").executeBoolean());
        assertEquals(1, v8Runtime.getExecutor("true").executeDouble(), DELTA);
        assertEquals(1, v8Runtime.getExecutor("new Boolean(true)").executeDouble(), DELTA);
        assertEquals(1, v8Runtime.getExecutor("true").executeInteger());
        assertEquals(1, v8Runtime.getExecutor("new Boolean(true)").executeInteger());
        assertEquals(1L, v8Runtime.getExecutor("true").executeLong());
        assertEquals(1L, v8Runtime.getExecutor("new Boolean(true)").executeLong());
        assertEquals("true", v8Runtime.getExecutor("true").executeString());
        assertEquals("true", v8Runtime.getExecutor("new Boolean(true)").executeString());
    }

    @Test
    public void testEquals() throws JavetException {
        V8ValueBoolean v8ValueBoolean = v8Runtime.getExecutor("true").execute();
        assertTrue(v8ValueBoolean.equals(v8Runtime.createV8ValueBoolean(true)));
        assertFalse(v8ValueBoolean.equals(null));
        assertFalse(v8ValueBoolean.equals(v8Runtime.createV8ValueBoolean(false)));
        assertFalse(v8ValueBoolean.equals(v8Runtime.createV8ValueUndefined()));
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertTrue(v8Runtime.createV8ValueBoolean(true).asBoolean());
        assertFalse(v8Runtime.createV8ValueBoolean(false).asBoolean());
        assertTrue(v8Runtime.getExecutor("true").execute().asBoolean());
        assertFalse(v8Runtime.getExecutor("false").execute().asBoolean());
    }
}
