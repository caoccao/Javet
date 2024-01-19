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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetPrimitiveConverter extends BaseTestJavetRuntime {
    protected IJavetAnonymous anonymous;
    protected JavetPrimitiveConverter converter;

    public TestJavetPrimitiveConverter() {
        super();
        converter = new JavetPrimitiveConverter();
        anonymous = new IJavetAnonymous() {
            @V8Function
            public void expectByte(Byte value1, byte value2) {
                assertNotNull(value1);
                assertEquals((byte) 1, value1);
                assertEquals((byte) 1, value2);
            }

            @V8Function
            public void expectCharacter(Character value1, char value2) {
                assertNotNull(value1);
                assertEquals('1', value1);
                assertEquals('1', value2);
            }

            @V8Function
            public void expectDouble(Double value1, double value2) {
                assertNotNull(value1);
                assertEquals(1, value1, 0.001D);
                assertEquals(1, value2, 0.001D);
            }

            @V8Function
            public void expectFloat(Float value1, float value2) {
                assertNotNull(value1);
                assertEquals(1, value1, 0.001F);
                assertEquals(1, value2, 0.001F);
            }

            @V8Function
            public void expectInteger(Integer value1, int value2) {
                assertNotNull(value1);
                assertEquals(1, value1);
                assertEquals(1, value2);
            }

            @V8Function
            public void expectLong(Long value1, long value2) {
                assertNotNull(value1);
                assertEquals(1, value1);
                assertEquals(1, value2);
            }
        };
    }

    @AfterEach
    @Override
    public void afterEach() throws JavetException {
        v8Runtime.getGlobalObject().unbind(anonymous);
        v8Runtime.lowMemoryNotification();
        super.afterEach();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        v8Runtime.getGlobalObject().bind(anonymous);
    }

    @Test
    public void testBigInteger() throws JavetException {
        String bigIntegerValue = "36893488147419103488";
        assertEquals(
                new BigInteger(bigIntegerValue).toString(),
                converter.toObject(v8Runtime.createV8ValueBigInteger(bigIntegerValue)).toString());
        V8ValueBigInteger v8ValueBigInteger = converter.toV8Value(v8Runtime, new BigInteger(bigIntegerValue));
        assertEquals(
                new BigInteger(bigIntegerValue).toString(),
                v8ValueBigInteger.getValue().toString());
    }

    @Test
    public void testBoolean() throws JavetException {
        assertTrue((boolean) converter.toObject(v8Runtime.createV8ValueBoolean(true)));
        assertTrue(((V8ValueBoolean) converter.toV8Value(v8Runtime, true)).getValue());
        assertFalse((boolean) converter.toObject(v8Runtime.createV8ValueBoolean(false)));
        assertFalse(((V8ValueBoolean) converter.toV8Value(v8Runtime, false)).getValue());
    }

    @Test
    public void testByte() throws JavetException {
        String codeString = String.join("\n",
                "expectByte(1, 1); // int to byte",
                "expectByte(1n, 1n); // long to byte");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testCharacter() throws JavetException {
        String codeString = String.join("\n",
                "expectCharacter('1', '1'); // 1-char string to char",
                "expectCharacter('123', '123'); // 3-char string to char");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testDouble() throws JavetException {
        assertEquals(1.23D, (double) converter.toObject(v8Runtime.createV8ValueDouble(1.23D)), 0.001);
        assertEquals(1.23D, ((V8ValueDouble) converter.toV8Value(v8Runtime, 1.23D)).getValue(), 0.001);
        String codeString = String.join("\n",
                "expectDouble(1, 1); // int to double",
                "expectDouble(1n, 1n); // long to double",
                "expectDouble(1.0, 1.0); // double to double");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testFloat() throws JavetException {
        assertEquals(1.23F, ((Double) converter.toObject(v8Runtime.createV8ValueDouble(1.23F))).floatValue(), 0.001);
        assertEquals(1.23F, ((V8ValueDouble) converter.toV8Value(v8Runtime, 1.23F)).getValue(), 0.001);
        String codeString = String.join("\n",
                "expectFloat(1, 1); // int to float",
                "expectFloat(1n, 1n); // long to float",
                "expectFloat(1.0, 1.0); // double to float");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testInteger() throws JavetException {
        assertEquals(123, (int) converter.toObject(v8Runtime.createV8ValueInteger(123)));
        assertEquals(123, ((V8ValueInteger) converter.toV8Value(v8Runtime, 123)).getValue());
        String codeString = String.join("\n",
                "expectInteger(1, 1); // int to int",
                "expectInteger(1n, 1n); // long to int");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testLong() throws JavetException {
        assertEquals(123L, (long) converter.toObject(v8Runtime.createV8ValueLong(123L)));
        assertEquals(123L, ((V8ValueLong) converter.toV8Value(v8Runtime, 123L)).getValue());
        String codeString = String.join("\n",
                "expectLong(1, 1); // int to long",
                "expectLong(1n, 1n); // long to long");
        v8Runtime.getExecutor(codeString).executeVoid();
    }

    @Test
    public void testNull() throws JavetException {
        assertNull(converter.toObject(null));
        assertNull(converter.toObject(v8Runtime.createV8ValueNull()));
        assertTrue(converter.toV8Value(v8Runtime, null).isNull());
    }

    @Test
    public void testOptional() throws JavetException {
        assertTrue(converter.toV8Value(v8Runtime, Optional.empty()).isNull());
        V8Value v8Value;
        // Boolean
        v8Value = converter.toV8Value(v8Runtime, Optional.of(true));
        assertInstanceOf(V8ValueBoolean.class, v8Value);
        assertTrue(((V8ValueBoolean) v8Value).getValue());
        // Double
        v8Value = converter.toV8Value(v8Runtime, Optional.of(1.23D));
        assertInstanceOf(V8ValueDouble.class, v8Value);
        assertEquals(1.23D, ((V8ValueDouble) v8Value).getValue(), 0.0001D);
        // Integer
        v8Value = converter.toV8Value(v8Runtime, Optional.of(1));
        assertInstanceOf(V8ValueInteger.class, v8Value);
        assertEquals(1, ((V8ValueInteger) v8Value).getValue());
        // Long
        v8Value = converter.toV8Value(v8Runtime, Optional.of(Long.MAX_VALUE));
        assertInstanceOf(V8ValueLong.class, v8Value);
        assertEquals(Long.MAX_VALUE, ((V8ValueLong) v8Value).getValue());
        // String
        v8Value = converter.toV8Value(v8Runtime, Optional.of("a"));
        assertInstanceOf(V8ValueString.class, v8Value);
        assertEquals("a", ((V8ValueString) v8Value).getValue());
        // ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        v8Value = converter.toV8Value(v8Runtime, Optional.of(zonedDateTime));
        assertInstanceOf(V8ValueZonedDateTime.class, v8Value);
        assertEquals(
                zonedDateTime.toInstant().toEpochMilli(),
                ((V8ValueZonedDateTime) v8Value).getValue().toInstant().toEpochMilli());
        // int
        v8Value = converter.toV8Value(v8Runtime, OptionalInt.of(1));
        assertInstanceOf(V8ValueInteger.class, v8Value);
        assertEquals(1, ((V8ValueInteger) v8Value).getValue());
        // double
        v8Value = converter.toV8Value(v8Runtime, OptionalDouble.of(1.23D));
        assertInstanceOf(V8ValueDouble.class, v8Value);
        assertEquals(1.23D, ((V8ValueDouble) v8Value).getValue(), 0.0001D);
        // long
        v8Value = converter.toV8Value(v8Runtime, OptionalLong.of(1L));
        assertInstanceOf(V8ValueLong.class, v8Value);
        assertEquals(1L, ((V8ValueLong) v8Value).getValue());
    }

    @Test
    public void testString() throws JavetException {
        assertEquals("abc", converter.toObject(v8Runtime.createV8ValueString("abc")));
        assertEquals("abc", ((V8ValueString) converter.toV8Value(v8Runtime, "abc")).getValue());
    }

    @Test
    public void testUndefined() throws JavetException {
        assertNull(converter.toObject(v8Runtime.createV8ValueUndefined()));
    }

    @Test
    public void testZonedDateTime() throws JavetException {
        assertEquals(123L, ((ZonedDateTime) converter.toObject(v8Runtime.createV8ValueZonedDateTime(123L))).toInstant().toEpochMilli());
        assertEquals(123L, ((V8ValueZonedDateTime) converter.toV8Value(v8Runtime, JavetDateTimeUtils.toZonedDateTime(123L))).getValue().toInstant().toEpochMilli());
    }
}
