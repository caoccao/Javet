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
import com.caoccao.javet.values.reference.V8ValueDoubleObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueDouble extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        assertEquals(0, v8Runtime.createV8ValueDouble(0.0D).asInt());
        assertEquals(1, v8Runtime.createV8ValueDouble(1.0D).asInt());
    }

    @Test
    public void testDoubleObject() throws JavetException {
        try (V8ValueDouble v8ValueDouble1 = v8Runtime.createV8ValueDouble(1.23D)) {
            try (V8ValueDoubleObject v8ValueDoubleObject = v8ValueDouble1.toObject()) {
                try (V8ValueDouble v8ValueDouble2 = (V8ValueDouble) v8ValueDoubleObject.valueOf()) {
                    assertEquals(1.23D, v8ValueDouble2.getValue(), DELTA);
                }
            }
        }
        try (V8ValueDoubleObject v8ValueDoubleObject = v8Runtime.createV8ValueDoubleObject(1.23D)) {
            try (V8ValueDouble v8ValueDouble = (V8ValueDouble) v8ValueDoubleObject.valueOf()) {
                assertEquals(1.23D, v8ValueDouble.getValue(), DELTA);
            }
        }
        try (V8ValueDoubleObject v8ValueDoubleObject = v8Runtime.getExecutor("new Number(1.23)").execute()) {
            try (V8ValueDouble v8ValueDouble = (V8ValueDouble) v8ValueDoubleObject.valueOf()) {
                assertEquals(1.23D, v8ValueDouble.getValue(), DELTA);
            }
        }
        assertTrue(v8Runtime.getExecutor("1.23").executeBoolean());
        assertTrue(v8Runtime.getExecutor("new Number(1.23)").executeBoolean());
        assertEquals(1.23, v8Runtime.getExecutor("1.23").executeDouble(), DELTA);
        assertEquals(1.23, v8Runtime.getExecutor("new Number(1.23)").executeDouble(), DELTA);
        assertEquals(1, v8Runtime.getExecutor("1.23").executeInteger());
        assertEquals(1, v8Runtime.getExecutor("new Number(1.23)").executeInteger());
        assertEquals(1L, v8Runtime.getExecutor("1.23").executeLong());
        assertEquals(1L, v8Runtime.getExecutor("new Number(1.23)").executeLong());
        assertEquals("1.23", v8Runtime.getExecutor("'1.23'").executeString());
        assertEquals("1.23", v8Runtime.getExecutor("new Number('1.23')").executeString());
    }

    @Test
    public void testEquals() throws JavetException {
        V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("1.23").execute();
        assertTrue(v8ValueDouble.equals(v8Runtime.createV8ValueDouble(1.23D)));
        assertFalse(v8ValueDouble.equals(null));
        assertFalse(v8ValueDouble.equals(v8Runtime.createV8ValueDouble(1.24D)));
        assertFalse(v8ValueDouble.equals(v8Runtime.createV8ValueUndefined()));
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertTrue(v8Runtime.createV8ValueDouble(1.0D).asBoolean());
        assertFalse(v8Runtime.createV8ValueDouble(0.0D).asBoolean());
        assertTrue(v8Runtime.getExecutor("1.0").execute().asBoolean());
        assertFalse(v8Runtime.getExecutor("0.0").execute().asBoolean());
    }

    @Test
    public void testNaNAndInfiniteAndFinite() throws JavetException {
        V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("NaN").execute();
        assertNotNull(v8ValueDouble);
        assertEquals(v8Runtime, v8ValueDouble.getV8Runtime());
        assertEquals(Double.NaN, v8ValueDouble.getValue());
        assertTrue(v8ValueDouble.isNaN());
        v8ValueDouble = v8Runtime.getExecutor("1/0").execute();
        assertTrue(v8ValueDouble.isInfinite());
        v8ValueDouble = v8Runtime.getExecutor("1/2").execute();
        assertTrue(v8ValueDouble.isFinite());
    }

    @Test
    public void testNumber() throws JavetException {
        try (V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("1.23").execute()) {
            assertNotNull(v8ValueDouble);
            assertEquals(1.23, v8ValueDouble.getValue(), DELTA);
            assertEquals("1.23", Double.toString(v8ValueDouble.getValue()));
            assertEquals("1.23", v8ValueDouble.toString());
            assertEquals(v8Runtime, v8ValueDouble.getV8Runtime());
        }
        try (V8ValueDouble v8ValueDouble = v8Runtime.getExecutor("2**32").execute()) {
            assertEquals("4294967296", v8ValueDouble.toString());
        }
        assertEquals(-0.5, v8Runtime.getExecutor("-0.5").executeDouble(), DELTA);
        assertEquals(0, v8Runtime.getExecutor("-0.0").executeDouble(), DELTA);
        assertEquals(2147483648D, v8Runtime.getExecutor("-(1 << -1)").executeDouble(), DELTA);
        assertEquals("2147483648", new BigDecimal(
                v8Runtime.getExecutor("-(1 << -1)").executeDouble()).toPlainString());
        assertTrue(v8Runtime.getExecutor("-(1 << -1) == 2147483648").executeBoolean());
    }
}
