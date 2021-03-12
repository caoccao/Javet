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

package com.caoccao.javet.utils.converters;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetPrimitiveConverter extends BaseTestJavetRuntime {
    protected JavetPrimitiveConverter converter;

    public TestJavetPrimitiveConverter() {
        super();
        converter = new JavetPrimitiveConverter();
    }

    @Test
    public void testBoolean() throws JavetException {
        assertTrue((boolean) converter.toObject(v8Runtime.createV8ValueBoolean(true)));
        assertTrue(((V8ValueBoolean) converter.toV8Value(v8Runtime, true)).getValue());
        assertFalse((boolean) converter.toObject(v8Runtime.createV8ValueBoolean(false)));
        assertFalse(((V8ValueBoolean) converter.toV8Value(v8Runtime, false)).getValue());
    }

    @Test
    public void testDouble() throws JavetException {
        assertEquals(1.23D, (double) converter.toObject(new V8ValueDouble(1.23D)), 0.001);
        assertEquals(1.23D, ((V8ValueDouble) converter.toV8Value(v8Runtime, Double.valueOf(1.23D))).getValue(), 0.001);
    }

    @Test
    public void testFloat() throws JavetException {
        assertEquals(1.23F, ((Double) converter.toObject(new V8ValueDouble(1.23F))).floatValue(), 0.001);
        assertEquals(1.23F, ((V8ValueDouble) converter.toV8Value(v8Runtime, Float.valueOf(1.23F))).getValue(), 0.001);
    }

    @Test
    public void testLong() throws JavetException {
        assertEquals(123L, (long) converter.toObject(v8Runtime.createV8ValueLong(123L)));
        assertEquals(123L, ((V8ValueLong) converter.toV8Value(v8Runtime, Long.valueOf(123L))).getValue());
    }

    @Test
    public void testNull() throws JavetException {
        assertNull(converter.toObject(null));
        assertNull(converter.toObject(v8Runtime.createV8ValueNull()));
        assertTrue(converter.toV8Value(v8Runtime, null).isNull());
    }

    @Test
    public void testString() throws JavetException {
        assertEquals("abc", (String) converter.toObject(new V8ValueString("abc")));
        assertEquals("abc", ((V8ValueString) converter.toV8Value(v8Runtime, "abc")).getValue());
    }

    @Test
    public void testUndefined() throws JavetException {
        assertNull(converter.toObject(v8Runtime.createV8ValueUndefined()));
    }

    @Test
    public void testZonedDateTime() throws JavetException {
        assertEquals(123L, ((ZonedDateTime) converter.toObject(new V8ValueZonedDateTime(123L))).toInstant().toEpochMilli());
        assertEquals(123L, ((V8ValueZonedDateTime) converter.toV8Value(v8Runtime, JavetDateTimeUtils.toZonedDateTime(123L))).getValue().toInstant().toEpochMilli());
    }
}
