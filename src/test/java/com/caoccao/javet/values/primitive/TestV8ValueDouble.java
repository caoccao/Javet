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
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestV8ValueDouble extends BaseTestJavetRuntime {
    public static final double DELTA = 0.001;

    @Test
    public void testEquals() throws JavetException {
        V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("1.23").execute();
        assertTrue(v8ValueDouble.equals(new V8ValueDouble(1.23D)));
        assertFalse(v8ValueDouble.equals(null));
        assertFalse(v8ValueDouble.equals(new V8ValueDouble(1.24D)));
        assertFalse(v8ValueDouble.equals(new V8ValueUndefined()));
    }

    @Test
    public void testNumber() throws JavetException {
        try (V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("1.23").execute()) {
            assertNotNull(v8ValueDouble);
            assertEquals(1.23, v8ValueDouble.getValue(), DELTA);
            assertEquals("1.23", v8ValueDouble.toString());
            assertEquals(v8Runtime, v8ValueDouble.getV8Runtime());
        }
        assertEquals(-0.5, v8Runtime.getExecutor("-0.5").executeDouble(), DELTA);
        assertEquals(0, v8Runtime.getExecutor("-0.0").executeDouble(), DELTA);
    }

    @Test
    public void testNumberObject() throws JavetException {
        assertEquals(1.23, v8Runtime.getExecutor("Number(1.23)").executeDouble(), DELTA);
    }
}
