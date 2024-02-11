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
import com.caoccao.javet.values.reference.V8ValueStringObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueString extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        assertEquals(0, v8Runtime.createV8ValueString("").asInt());
        assertEquals(0, v8Runtime.createV8ValueString("2a").asInt());
        assertEquals(0, v8Runtime.createV8ValueString("a").asInt());
        assertEquals(1, v8Runtime.createV8ValueString("1").asInt());
        assertEquals(2, v8Runtime.createV8ValueString(" 2 ").asInt());
    }

    @Test
    public void testEquals() throws JavetException {
        try (V8ValueString v8ValueString = v8Runtime.getExecutor("'abc'").execute()) {
            assertTrue(v8ValueString.equals(v8Runtime.createV8ValueString("abc")));
            assertFalse(v8ValueString.equals(null));
            assertFalse(v8ValueString.equals(v8Runtime.createV8ValueString("def")));
            assertFalse(v8ValueString.equals(v8Runtime.createV8ValueLong(1)));
        }
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertTrue(v8Runtime.createV8ValueString("0").asBoolean());
        assertFalse(v8Runtime.createV8ValueString("").asBoolean());
        assertTrue(v8Runtime.getExecutor("'0'").execute().asBoolean());
        assertFalse(v8Runtime.getExecutor("''").execute().asBoolean());
    }

    @Test
    public void testString() throws JavetException {
        try (V8ValueString v8ValueString = v8Runtime.getExecutor("'abc' + 'def'").execute()) {
            assertNotNull(v8ValueString);
            assertEquals("abcdef", v8ValueString.getValue());
            assertEquals("abcdef", v8ValueString.toString());
            assertEquals(v8Runtime, v8ValueString.getV8Runtime());
        }
        assertEquals("中文測試", v8Runtime.getExecutor("'中文測試'").executeString());
        assertEquals("français", v8Runtime.getExecutor("'français'").executeString());
        assertEquals("こにちは", v8Runtime.getExecutor("'こにちは'").executeString());
    }

    @Test
    public void testStringObject() throws JavetException {
        try (V8ValueString v8ValueString1 = v8Runtime.createV8ValueString("test")) {
            try (V8ValueStringObject v8ValueStringObject = v8ValueString1.toObject()) {
                try (V8ValueString v8ValueString2 = v8ValueStringObject.valueOf()) {
                    assertEquals("test", v8ValueString2.getValue());
                }
            }
        }
        try (V8ValueStringObject v8ValueStringObject = v8Runtime.createV8ValueStringObject("test")) {
            try (V8ValueString v8ValueString = v8ValueStringObject.valueOf()) {
                assertEquals("test", v8ValueString.getValue());
            }
        }
        try (V8ValueStringObject v8ValueStringObject = v8Runtime.getExecutor("new String('test')").execute()) {
            try (V8ValueString v8ValueString = v8ValueStringObject.valueOf()) {
                assertEquals("test", v8ValueString.getValue());
            }
        }
        assertTrue(v8Runtime.getExecutor("'abc'").executeBoolean());
        assertTrue(v8Runtime.getExecutor("new String('abc')").executeBoolean());
        assertEquals(1.23, v8Runtime.getExecutor("'1.23'").executeDouble(), DELTA);
        assertEquals(1.23, v8Runtime.getExecutor("new String('1.23')").executeDouble(), DELTA);
        assertEquals(1, v8Runtime.getExecutor("'1'").executeInteger());
        assertEquals(1, v8Runtime.getExecutor("new String('1')").executeInteger());
        assertEquals(1L, v8Runtime.getExecutor("'1'").executeLong());
        assertEquals(1L, v8Runtime.getExecutor("new String('1')").executeLong());
        assertEquals("test", v8Runtime.getExecutor("'test'").executeString());
        assertEquals("test", v8Runtime.getExecutor("new String('test')").executeString());
    }

    @Test
    public void testTypeof() throws JavetException {
        assertEquals("bigint", v8Runtime.getExecutor("typeof 1n").executeString());
        assertEquals("object", v8Runtime.getExecutor("typeof (new Object(1n))").executeString());
        assertEquals("number", v8Runtime.getExecutor("typeof 1").executeString());
        assertEquals("string", v8Runtime.getExecutor("typeof '1'").executeString());
    }
}
