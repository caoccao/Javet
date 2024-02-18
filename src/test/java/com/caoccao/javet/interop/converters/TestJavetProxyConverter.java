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
import com.caoccao.javet.annotations.*;
import com.caoccao.javet.enums.JavetErrorType;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetEntityError;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.interop.proxy.JavetReflectionObjectFactory;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.mock.MockDirectProxyFunctionHandler;
import com.caoccao.javet.mock.MockDirectProxyListHandler;
import com.caoccao.javet.mock.MockDirectProxyObjectHandler;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.primitive.V8ValueZonedDateTime;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetProxyConverter extends BaseTestJavetRuntime {
    protected IJavetAnonymous anonymous;
    protected JavetProxyConverter javetProxyConverter;

    public TestJavetProxyConverter() {
        super();
        javetProxyConverter = new JavetProxyConverter();
        anonymous = new IJavetAnonymous() {
            public void expectByte(Byte value1, byte value2) {
                assertNotNull(value1);
                assertEquals((byte) 1, value1);
                assertEquals((byte) 1, value2);
            }

            public void expectCharacter(Character value1, char value2) {
                assertNotNull(value1);
                assertEquals('1', value1);
                assertEquals('1', value2);
            }

            public void expectDouble(Double value1, double value2) {
                assertNotNull(value1);
                assertEquals(1, value1, 0.001D);
                assertEquals(1, value2, 0.001D);
            }

            public void expectFloat(Float value1, float value2) {
                assertNotNull(value1);
                assertEquals(1, value1, 0.001F);
                assertEquals(1, value2, 0.001F);
            }

            public void expectInteger(Integer value1, int value2) {
                assertNotNull(value1);
                assertEquals(1, value1);
                assertEquals(1, value2);
            }

            public void expectListOfStrings(List<String> list) {
                assertNotNull(list);
                assertEquals(2, list.size());
                assertEquals("a", list.get(0));
                assertNull(list.get(1));
            }

            public void expectLong(Long value1, long value2) {
                assertNotNull(value1);
                assertEquals(1, value1);
                assertEquals(1, value2);
            }

            public void expectZonedDateTime(ZonedDateTime value1, ZonedDateTime value2) {
                assertNotNull(value1);
                assertNotNull(value2);
                assertEquals(253402300799L, value1.toInstant().getEpochSecond());
                assertEquals(-2208988800L, value2.toInstant().getEpochSecond());
            }
        };
    }

    @AfterEach
    @Override
    public void afterEach() throws JavetException {
        v8Runtime.lowMemoryNotification();
        assertFalse(javetProxyConverter.getConfig().isProxyArrayEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxyListEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxyMapEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxySetEnabled());
        assertTrue(javetProxyConverter.getConfig().getProxyPlugins().isEmpty());
        super.afterEach();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        v8Runtime.setConverter(javetProxyConverter);
        assertTrue(javetProxyConverter.getConfig().getProxyPlugins().isEmpty());
        assertFalse(javetProxyConverter.getConfig().isProxyArrayEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxyListEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxyMapEnabled());
        assertFalse(javetProxyConverter.getConfig().isProxySetEnabled());
    }

    @Test
    public void testAnnotationInAllowOnlyMode() throws JavetException {
        AllowOnlyClass allowOnlyClass = new AllowOnlyClass();
        v8Runtime.getGlobalObject().set("x", allowOnlyClass);
        assertEquals(allowOnlyClass.xType, v8Runtime.getExecutor("x['type']").executeString());
        assertEquals(allowOnlyClass.xType, v8Runtime.getExecutor("x.type").executeString());
        assertTrue(v8Runtime.getExecutor("x.xType").execute().isUndefined());
        assertTrue(v8Runtime.getExecutor("x.disallowedType").execute().isUndefined());
        assertEquals(allowOnlyClass.getName(), v8Runtime.getExecutor("x.name()").executeString());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("x.getName()").executeVoid());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("x.getDisallowedName()").executeVoid());
        assertTrue(v8Runtime.getExecutor("x['abc']").execute().isUndefined());
        v8Runtime.getExecutor("x['abc'] = 'def';").executeVoid();
        assertEquals("def", v8Runtime.getExecutor("x['abc']").executeString());
        assertEquals("def", allowOnlyClass.xGetter("abc"));
        v8Runtime.getGlobalObject().delete("x");
    }

    @Test
    public void testAnnotationInBlockOnlyMode() throws JavetException {
        BlockOnlyClass blockOnlyClass = new BlockOnlyClass();
        v8Runtime.getGlobalObject().set("x", blockOnlyClass);
        assertEquals(blockOnlyClass.xType, v8Runtime.getExecutor("x['type']").executeString());
        assertEquals(blockOnlyClass.xType, v8Runtime.getExecutor("x.type").executeString());
        assertTrue(v8Runtime.getExecutor("x.xType").execute().isUndefined());
        assertTrue(v8Runtime.getExecutor("x.disallowedType").execute().isUndefined());
        assertEquals(blockOnlyClass.getName(), v8Runtime.getExecutor("x.name()").executeString());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("x.getName()").executeVoid());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("x.getDisallowedName()").executeVoid());
        assertTrue(v8Runtime.getExecutor("x['abc']").execute().isUndefined());
        v8Runtime.getExecutor("x['abc'] = 'def';").executeVoid();
        assertEquals("def", v8Runtime.getExecutor("x['abc']").executeString());
        assertEquals("def", blockOnlyClass.xGetter("abc"));
        v8Runtime.getGlobalObject().delete("x");
    }

    @Test
    public void testAnnotationInTransparentMode() throws JavetException {
        TransparentClass transparentClass = new TransparentClass();
        v8Runtime.getGlobalObject().set("x", transparentClass);
        assertEquals(transparentClass.xType, v8Runtime.getExecutor("x['type']").executeString());
        assertEquals(transparentClass.xType, v8Runtime.getExecutor("x.type").executeString());
        assertTrue(v8Runtime.getExecutor("x.xType").execute().isUndefined());
        assertEquals(transparentClass.getName(), v8Runtime.getExecutor("x.name()").executeString());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("x.getName()").executeVoid());
        assertTrue(v8Runtime.getExecutor("x['abc']").execute().isUndefined());
        v8Runtime.getExecutor("x['abc'] = 'def';").executeVoid();
        assertEquals("def", v8Runtime.getExecutor("x['abc']").executeString());
        assertEquals("def", transparentClass.xGetter("abc"));
        v8Runtime.getGlobalObject().delete("x");
    }

    @Test
    public void testAnonymousFunction() throws Exception {
        try (StringJoiner stringJoiner = new StringJoiner()) {
            v8Runtime.getGlobalObject().set("stringJoiner", stringJoiner);
            v8Runtime.getExecutor("stringJoiner.setJoiner((a, b) => a + ',' + b);").executeVoid();
            IStringJoiner joiner = stringJoiner.getJoiner();
            assertEquals("a,b", joiner.join("a", "b"));
            assertEquals("a,b,c", joiner.join(joiner.join("a", "b"), "c"));
            try {
                v8Runtime.getExecutor("stringJoiner.invalidFunction();").executeVoid();
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: stringJoiner.invalidFunction is not a function", e.getMessage());
                assertEquals(
                        "TypeError: stringJoiner.invalidFunction is not a function\n" +
                                "Resource: undefined\n" +
                                "Source Code: stringJoiner.invalidFunction();\n" +
                                "Line Number: 1\n" +
                                "Column: 13, 14\n" +
                                "Position: 13, 14",
                        e.getScriptingError().toString());
            }
            v8Runtime.getGlobalObject().delete("stringJoiner");
        }
    }

    @Test
    public void testAnonymousObject() throws Exception {
        try (StringUtils stringUtils = new StringUtils()) {
            v8Runtime.getGlobalObject().set("stringUtils", stringUtils);
            v8Runtime.getExecutor(
                    "stringUtils.setUtils({\n" +
                            "  hello: () => 'hello',\n" +
                            "  join: (separator, ...strings) => [...strings].join(separator),\n" +
                            "  split: (separator, str) => str.split(separator),\n" +
                            "});"
            ).executeVoid();
            IStringUtils utils = stringUtils.getUtils();
            assertEquals("hello", utils.hello());
            assertEquals("a,b,c", utils.join(",", "a", "b", "c"));
            assertArrayEquals(
                    new String[]{"a", "b", "c"},
                    utils.split(",", "a,b,c").toArray(new String[0]));
            assertEquals(
                    "StringUtils",
                    v8Runtime.getExecutor("'' + stringUtils").executeString());
            v8Runtime.getGlobalObject().delete("stringUtils");
        }
    }

    @Test
    public void testArray() throws JavetException {
        try {
            javetProxyConverter.getConfig().setProxyArrayEnabled(true);
            assertTrue(javetProxyConverter.getConfig().isProxyArrayEnabled());
            int[] intArray = new int[]{1, 2};
            String[] stringArray = new String[]{"x", "y"};
            v8Runtime.getGlobalObject().set("intArray", intArray);
            v8Runtime.getGlobalObject().set("stringArray", stringArray);
            assertSame(intArray, v8Runtime.getGlobalObject().getObject("intArray"));
            assertSame(stringArray, v8Runtime.getGlobalObject().getObject("stringArray"));
            // Array.isArray()
            assertTrue(v8Runtime.getExecutor("Array.isArray(intArray)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("Array.isArray(stringArray)").executeBoolean());
            // in
            assertTrue(v8Runtime.getExecutor("0 in intArray").executeBoolean());
            assertFalse(v8Runtime.getExecutor("2 in intArray").executeBoolean());
            assertFalse(v8Runtime.getExecutor("'x' in intArray").executeBoolean());
            assertTrue(v8Runtime.getExecutor("0 in stringArray").executeBoolean());
            assertFalse(v8Runtime.getExecutor("2 in stringArray").executeBoolean());
            assertFalse(v8Runtime.getExecutor("'x' in stringArray").executeBoolean());
            // constructor.name
            assertEquals("Array", v8Runtime.getExecutor("intArray.constructor.name").executeString());
            assertEquals("Array", v8Runtime.getExecutor("stringArray.constructor.name").executeString());
            // includes()
            assertTrue(v8Runtime.getExecutor("intArray.includes(1)").executeBoolean());
            assertFalse(v8Runtime.getExecutor("intArray.includes(1, 1)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("intArray.includes(2, 1)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("stringArray.includes('x')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("stringArray.includes('x', 1)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("stringArray.includes('y', 1)").executeBoolean());
            // toJSON()
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray)").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray)").executeString());
            // valueOf()
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.valueOf())").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.valueOf())").executeString());
            // Symbol.iterator
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify([...intArray])").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify([...stringArray])").executeString());
            // entries()
            assertEquals(
                    "[[0,1],[1,2]]",
                    v8Runtime.getExecutor("JSON.stringify([...intArray.entries()])").executeString());
            assertEquals(
                    "[[0,\"x\"],[1,\"y\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...stringArray.entries()])").executeString());
            // with()
            assertEquals(
                    "[\"1\",2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.with(0, '1'))").executeString());
            assertEquals(
                    "[\"1\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.with(0, '1'))").executeString());
            // toString()
            assertEquals("1,2", v8Runtime.getExecutor("intArray.toString()").executeString());
            assertEquals("x,y", v8Runtime.getExecutor("stringArray.toString()").executeString());
            // values()
            assertEquals("1,2", v8Runtime.getExecutor("[...intArray.values()].toString()").executeString());
            assertEquals("x,y", v8Runtime.getExecutor("[...stringArray.values()].toString()").executeString());
            // keys()
            assertEquals("0,1", v8Runtime.getExecutor("[...intArray.keys()].toString()").executeString());
            assertEquals("0,1", v8Runtime.getExecutor("[...stringArray.keys()].toString()").executeString());
            // concat()
            assertEquals(
                    "[1,2,\"a\",\"b\",\"c\"]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.concat(['a', 'b'], 'c'))").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"a\",\"b\",\"c\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.concat(['a', 'b'], 'c'))").executeString());
            // copyWithin()
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.copyWithin())").executeString());
            assertEquals(
                    "[1,1]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.copyWithin(1))").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.copyWithin())").executeString());
            assertEquals(
                    "[\"x\",\"x\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.copyWithin(1))").executeString());
            // fill()
            assertEquals(
                    "[3,3]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.fill(3))").executeString());
            assertEquals(
                    "[3,4]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.fill(4, 1, 2))").executeString());
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.fill(1).fill(2, 1))").executeString());
            assertEquals(
                    "[\"z\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.fill('z'))").executeString());
            assertEquals(
                    "[\"z\",\"a\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.fill('a', 1, 2))").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.fill('x').fill('y', 1))").executeString());
            // filter()
            assertEquals(
                    "[1]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.filter(x => x == 1))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.filter(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals(
                    "[\"x\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.filter(x => x == 'x'))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.filter(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // find()
            assertEquals(1, v8Runtime.getExecutor("intArray.find(x => x == 1)").executeInteger());
            assertEquals(2, v8Runtime.getExecutor("intArray.find(x => x == 2)").executeInteger());
            assertTrue(v8Runtime.getExecutor("intArray.find(x => x == 'a') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.find(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals("x", v8Runtime.getExecutor("stringArray.find(x => x == 'x')").executeString());
            assertEquals("y", v8Runtime.getExecutor("stringArray.find(x => x == 'y')").executeString());
            assertTrue(v8Runtime.getExecutor("stringArray.find(x => x == 'a') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.find(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findIndex()
            assertEquals(0, v8Runtime.getExecutor("intArray.findIndex(x => x == 1)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.findIndex(x => x == 2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.findIndex(x => x == 'a')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.findIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals(0, v8Runtime.getExecutor("stringArray.findIndex(x => x == 'x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.findIndex(x => x == 'y')").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.findIndex(x => x == 'a')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.findIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findLast()
            assertEquals(1, v8Runtime.getExecutor("intArray.findLast(x => x == 1)").executeInteger());
            assertEquals(2, v8Runtime.getExecutor("intArray.findLast(x => x == 2)").executeInteger());
            assertTrue(v8Runtime.getExecutor("intArray.findLast(x => x == 'a') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.findLast(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals("x", v8Runtime.getExecutor("stringArray.findLast(x => x == 'x')").executeString());
            assertEquals("y", v8Runtime.getExecutor("stringArray.findLast(x => x == 'y')").executeString());
            assertTrue(v8Runtime.getExecutor("stringArray.findLast(x => x == 'a') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.findLast(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findLastIndex()
            assertEquals(0, v8Runtime.getExecutor("intArray.findLastIndex(x => x == 1)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.findLastIndex(x => x == 2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.findLastIndex(x => x == 'a')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.findLastIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals(0, v8Runtime.getExecutor("stringArray.findLastIndex(x => x == 'x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.findLastIndex(x => x == 'y')").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.findLastIndex(x => x == 'a')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.findLastIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // indexOf()
            assertEquals(0, v8Runtime.getExecutor("intArray.indexOf(1)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.indexOf(2)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.indexOf(2,1)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.indexOf(2,2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.indexOf('a')").executeInteger());
            assertEquals(0, v8Runtime.getExecutor("stringArray.indexOf('x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.indexOf('y')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.indexOf('y',1)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.indexOf('y',2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.indexOf('a')").executeInteger());
            // lastIndexOf()
            assertEquals(0, v8Runtime.getExecutor("intArray.lastIndexOf(1)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.lastIndexOf(2)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.lastIndexOf(2,1)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.lastIndexOf(2,0)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("intArray.lastIndexOf('a')").executeInteger());
            assertEquals(0, v8Runtime.getExecutor("stringArray.lastIndexOf('x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.lastIndexOf('y')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("stringArray.lastIndexOf('y',1)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.lastIndexOf('y',0)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("stringArray.lastIndexOf('a')").executeInteger());
            // join()
            assertEquals("12", v8Runtime.getExecutor("intArray.join()").executeString());
            assertEquals("1,2", v8Runtime.getExecutor("intArray.join(',')").executeString());
            assertEquals("xy", v8Runtime.getExecutor("stringArray.join()").executeString());
            assertEquals("x,y", v8Runtime.getExecutor("stringArray.join(',')").executeString());
            // forEach()
            assertEquals(
                    "[\"10true\",\"21true\"]",
                    v8Runtime.getExecutor("const testForEach1 = [];" +
                            "intArray.forEach((x, i, a) => testForEach1.push(x+''+i+(a === intArray)));" +
                            "JSON.stringify(testForEach1)").executeString());
            assertEquals(
                    "[\"x0true\",\"y1true\"]",
                    v8Runtime.getExecutor("const testForEach2 = [];" +
                            "stringArray.forEach((x, i, a) => testForEach2.push(x+''+i+(a === stringArray)));" +
                            "JSON.stringify(testForEach2)").executeString());
            // reduce()
            try {
                v8Runtime.getExecutor("intArray.reduce()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("_10true21true", v8Runtime.getExecutor("intArray.reduce((x,y,i,a)=>''+x+y+i+(a===intArray), '_')").executeString());
            assertEquals("121true", v8Runtime.getExecutor("intArray.reduce((x,y,i,a)=>''+x+y+i+(a===intArray))").executeString());
            try {
                v8Runtime.getExecutor("stringArray.reduce()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("_x0truey1true", v8Runtime.getExecutor("stringArray.reduce((x,y,i,a)=>x+y+i+(a===stringArray), '_')").executeString());
            assertEquals("xy1true", v8Runtime.getExecutor("stringArray.reduce((x,y,i,a)=>x+y+i+(a===stringArray))").executeString());
            // reduceRight()
            try {
                v8Runtime.getExecutor("intArray.reduceRight()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("_21true10true", v8Runtime.getExecutor("intArray.reduceRight((x,y,i,a)=>''+x+y+i+(a===intArray), '_')").executeString());
            assertEquals("210true", v8Runtime.getExecutor("intArray.reduceRight((x,y,i,a)=>''+x+y+i+(a===intArray))").executeString());
            try {
                v8Runtime.getExecutor("stringArray.reduceRight()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("_y1truex0true", v8Runtime.getExecutor("stringArray.reduceRight((x,y,i,a)=>x+y+i+(a===stringArray), '_')").executeString());
            assertEquals("yx0true", v8Runtime.getExecutor("stringArray.reduceRight((x,y,i,a)=>x+y+i+(a===stringArray))").executeString());
            // slice()
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray.slice())").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(0,0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(-5,-5))").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(0,100))").executeString());
            assertEquals("[2]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(1))").executeString());
            assertEquals("[2]", v8Runtime.getExecutor("JSON.stringify(intArray.slice(1,2))").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice())").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(0,0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(-5,-5))").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(0,100))").executeString());
            assertEquals("[\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(1))").executeString());
            assertEquals("[\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.slice(1,2))").executeString());
            // sort()
            assertEquals("[2,1]", v8Runtime.getExecutor("JSON.stringify(intArray.sort((x,y)=>y-x))").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray.sort())").executeString());
            assertEquals("[\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.sort((x,y)=>y.localeCompare(x)))").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.sort())").executeString());
            // reverse()
            assertEquals("[2,1]", v8Runtime.getExecutor("JSON.stringify(intArray.reverse())").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray.reverse())").executeString());
            assertEquals("[\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.reverse())").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.reverse())").executeString());
            // toReversed()
            assertEquals("[2,1]", v8Runtime.getExecutor("JSON.stringify(intArray.toReversed())").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray)").executeString());
            assertEquals("[\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.toReversed())").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray)").executeString());
            // toSorted()
            assertEquals("[2,1]", v8Runtime.getExecutor("JSON.stringify(intArray.toSorted((x,y)=>y-x))").executeString());
            assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(intArray)").executeString());
            assertEquals("[\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.toSorted((x,y)=>y.localeCompare(x)))").executeString());
            assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify(stringArray)").executeString());
            // toSpliced()
            assertEquals(
                    "[1,2][1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.toSpliced())+JSON.stringify(intArray)").executeString());
            assertEquals(
                    "[1][1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.toSpliced(1,1))+JSON.stringify(intArray)").executeString());
            assertEquals(
                    "[1,1,2][1,2]",
                    v8Runtime.getExecutor("JSON.stringify(intArray.toSpliced(1,1,1,2))+JSON.stringify(intArray)").executeString());
            assertEquals(
                    "[\"x\",\"y\"][\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.toSpliced())+JSON.stringify(stringArray)").executeString());
            assertEquals(
                    "[\"x\"][\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.toSpliced(1,1))+JSON.stringify(stringArray)").executeString());
            assertEquals(
                    "[\"x\",\"x\",\"y\"][\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(stringArray.toSpliced(1,1,'x','y'))+JSON.stringify(stringArray)").executeString());
            // Symbol.iterator
            assertEquals(
                    "[1,2]",
                    v8Runtime.getExecutor("JSON.stringify([...intArray[Symbol.iterator]()]);").executeString());
            assertEquals(
                    "[\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify([...stringArray[Symbol.iterator]()]);").executeString());
            // map()
            assertEquals("[\"10\",\"21\"]", v8Runtime.getExecutor("JSON.stringify(intArray.map((x, i) => ''+x+i))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.map(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertEquals("[\"x0\",\"y1\"]", v8Runtime.getExecutor("JSON.stringify(stringArray.map((x, i) => ''+x+i))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.map(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // every()
            assertFalse(v8Runtime.getExecutor("intArray.every((x, i) => x == 1 && i == 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("intArray.every((x, i) => x >= 1 && i >= 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.every(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertFalse(v8Runtime.getExecutor("stringArray.every((x, i) => x == 'x' && i == 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("stringArray.every((x, i) => x >= 'x' && i >= 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.every(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // some()
            assertTrue(v8Runtime.getExecutor("intArray.some((x, i) => x == 1 && i == 0)").executeBoolean());
            assertFalse(v8Runtime.getExecutor("intArray.some((x, i) => x < 1 && i < 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { intArray.some(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            assertTrue(v8Runtime.getExecutor("stringArray.some((x, i) => x == 'x' && i == 0)").executeBoolean());
            assertFalse(v8Runtime.getExecutor("stringArray.some((x, i) => x < 'x' && i < 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { stringArray.some(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // at()
            assertEquals(1, v8Runtime.getExecutor("intArray.at(0)").executeInteger());
            assertEquals(2, v8Runtime.getExecutor("intArray.at(1)").executeInteger());
            assertEquals(2, v8Runtime.getExecutor("intArray.at(-1)").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray.at(-2)").executeInteger());
            assertTrue(v8Runtime.getExecutor("intArray.at(2)").execute().isUndefined());
            assertTrue(v8Runtime.getExecutor("intArray.at(-3)").execute().isUndefined());
            assertEquals("x", v8Runtime.getExecutor("stringArray.at(0)").executeString());
            assertEquals("y", v8Runtime.getExecutor("stringArray.at(1)").executeString());
            assertEquals("y", v8Runtime.getExecutor("stringArray.at(-1)").executeString());
            assertEquals("x", v8Runtime.getExecutor("stringArray.at(-2)").executeString());
            assertTrue(v8Runtime.getExecutor("stringArray.at(2)").execute().isUndefined());
            assertTrue(v8Runtime.getExecutor("stringArray.at(-3)").execute().isUndefined());
            // []
            assertEquals(3, v8Runtime.getExecutor("intArray[0] = 3; intArray[0]").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("intArray[0] = 1; intArray[0]").executeInteger());
            assertEquals("z", v8Runtime.getExecutor("stringArray[0] = 'z'; stringArray[0]").executeString());
            assertEquals("x", v8Runtime.getExecutor("stringArray[0] = 'x'; stringArray[0]").executeString());
            // length
            assertEquals(2, v8Runtime.getExecutor("intArray.length").executeInteger());
            assertEquals(2, v8Runtime.getExecutor("stringArray.length").executeInteger());
            v8Runtime.getGlobalObject().delete("intArray");
            v8Runtime.getGlobalObject().delete("stringArray");
            // flat()
            Object[] objectArray = new Object[]{"x", "y", SimpleList.of("x1", "y1", SimpleList.of("x2", "y2"))};
            v8Runtime.getGlobalObject().set("objectArray", objectArray);
            assertEquals(
                    "[\"x\",\"y\",[\"x1\",\"y1\",[\"x2\",\"y2\"]]]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray);").executeString());
            assertEquals(
                    "[\"x\",\"y\",[\"x1\",\"y1\",[\"x2\",\"y2\"]]]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray.flat(0));").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",[\"x2\",\"y2\"]]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray.flat());").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",[\"x2\",\"y2\"]]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray.flat(1));").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",\"x2\",\"y2\"]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray.flat(2));").executeString());
            // flatMap()
            assertEquals(
                    "[\"x0true\",\"y1true\",\"x1,y1,x2,y22true\"]",
                    v8Runtime.getExecutor("JSON.stringify(objectArray.flatMap((e,i,a)=>e+i+(a===objectArray)));").executeString());
            v8Runtime.getGlobalObject().delete("objectArray");
        } finally {
            javetProxyConverter.getConfig().setProxyArrayEnabled(false);
        }
    }

    @Test
    public void testByte() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectByte(1, 1); // int to byte",
                "a.expectByte(1n, 1n); // long to byte");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testCharacter() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectCharacter('1', '1'); // 1-char string to char",
                "a.expectCharacter('123', '123'); // 3-char string to char");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testClassForName() throws JavetException {
        v8Runtime.getGlobalObject().set("Class", Class.class);
        assertEquals(v8Runtime.getExecutor("Class.forName('java.io.File')").executeObject(), File.class);
        try {
            v8Runtime.getExecutor("Class.forName('i_do_not_exist')").executeVoid();
            fail("Failed to report class not found.");
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals(
                    "Error: Callback forName failed with error message i_do_not_exist\n" +
                            "Resource: undefined\n" +
                            "Source Code: Class.forName('i_do_not_exist')\n" +
                            "Line Number: 1\n" +
                            "Column: 6, 7\n" +
                            "Position: 6, 7",
                    e.getScriptingError().toString());
            assertInstanceOf(ClassNotFoundException.class, e.getCause().getCause());
        }
        v8Runtime.getGlobalObject().delete("Class");
    }

    @Test
    public void testConstructor() throws JavetException {
        v8Runtime.getGlobalObject().set("StringBuilder", StringBuilder.class);
        assertEquals("abc def", v8Runtime.getExecutor(
                "function main() {\n" +
                        "  return new StringBuilder('abc').append(' ').append('def').toString();\n" +
                        "}\n" +
                        "main();").executeString());
        v8Runtime.getGlobalObject().delete("StringBuilder");
    }

    @Test
    public void testDirectProxyFunctionHandler() throws JavetException {
        int expectedCallCount = 0;
        MockDirectProxyFunctionHandler handler = new MockDirectProxyFunctionHandler();
        v8Runtime.getGlobalObject().set("a", handler);
        // Test apply().
        assertEquals(6, v8Runtime.getExecutor("a(1,2,3);").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testDirectProxyListHandler() throws JavetException {
        int expectedCallCount = 0;
        MockDirectProxyListHandler<String> handler = new MockDirectProxyListHandler<>();
        v8Runtime.getGlobalObject().set("a", handler);
        // length
        assertEquals(0, v8Runtime.getExecutor("a.length;").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        handler.add("a");
        assertEquals(1, v8Runtime.getExecutor("a.length;").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // push()
        assertEquals(2, v8Runtime.getExecutor("a.push('b');").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Object.getOwnPropertyNames()
        assertEquals("0,1,length", v8Runtime.getExecutor("Object.getOwnPropertyNames(a).toString();").executeString());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Object.keys()
        assertEquals("0,1", v8Runtime.getExecutor("Object.keys(a).toString();").executeString());
        expectedCallCount += 3;
        assertEquals(++expectedCallCount, handler.getCallCount());
        // in
        assertTrue(v8Runtime.getExecutor("0 in a").executeBoolean());
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertFalse(v8Runtime.getExecutor("3 in a").executeBoolean());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // of
        assertEquals(
                "[\"a\",\"b\"]",
                v8Runtime.getExecutor("const l = []; for (const x of a) { l.push(x); } JSON.stringify(l)").executeString());
        // toJSON
        assertEquals(
                "[\"a\",\"b\"]",
                v8Runtime.getExecutor("JSON.stringify(a)").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testDirectProxyObjectHandler() throws JavetException {
        int expectedCallCount = 0;
        MockDirectProxyObjectHandler handler = new MockDirectProxyObjectHandler();
        v8Runtime.getGlobalObject().set("a", handler);
        // Test get() and set().
        try (V8Value v8Value = v8Runtime.getExecutor("a.z;").execute()) {
            assertInstanceOf(V8ValueUndefined.class, v8Value);
            assertEquals(++expectedCallCount, handler.getCallCount());
        }
        assertEquals(0, v8Runtime.getExecutor("a.x;").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertEquals(0, v8Runtime.getExecutor("a.y;").executeInteger());
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertEquals(3, v8Runtime.getExecutor("a.x = 3; a.x;").executeInteger());
        assertEquals(3, handler.getX());
        ++expectedCallCount;
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertEquals(5, v8Runtime.getExecutor("a.y = 5; a.y;").executeInteger());
        assertEquals(5, handler.getY());
        ++expectedCallCount;
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Test function get().
        assertTrue(v8Runtime.getExecutor("a.increaseX();").executeBoolean());
        assertEquals(4, handler.getX());
        assertEquals(++expectedCallCount, handler.getCallCount());
        try (V8Value v8Value = v8Runtime.getExecutor("a.increaseX;").execute()) {
            assertInstanceOf(V8ValueFunction.class, v8Value);
            assertEquals(++expectedCallCount, handler.getCallCount());
        }
        assertEquals(
                MockDirectProxyObjectHandler.class.getSimpleName(),
                v8Runtime.getExecutor("'' + a;").executeString());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Test ownKeys().
        assertEquals(
                "[\"increaseX\",\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(a));").executeString());
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Test getOwnPropertyDescriptor()
        assertEquals(
                "[\"increaseX\",\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.keys(a));").executeString());
        expectedCallCount += 3;
        assertEquals(++expectedCallCount, handler.getCallCount());
        // Test has().
        assertFalse(v8Runtime.getExecutor("'z' in a;").executeBoolean());
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertTrue(v8Runtime.getExecutor("'x' in a;").executeBoolean());
        assertEquals(++expectedCallCount, handler.getCallCount());
        assertTrue(v8Runtime.getExecutor("'y' in a;").executeBoolean());
        assertEquals(++expectedCallCount, handler.getCallCount());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testDouble() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectDouble(1, 1); // int to double",
                "a.expectDouble(1n, 1n); // long to double",
                "a.expectDouble(1.0, 1.0); // double to double");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testDynamicClassAutoCloseable() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public void test(DynamicClassAutoCloseable mockedDynamicClass) throws Exception {
                DynamicClassAutoCloseable regularDynamicClass = new DynamicClassAutoCloseable();
                assertEquals(0, regularDynamicClass.add(1, 2));
                assertEquals(3, mockedDynamicClass.add(1, 2), "Add should work.");
                ((AutoCloseable) mockedDynamicClass).close();
            }
        };
        try {
            javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
            v8Runtime.getGlobalObject().set("a", anonymous);
            String codeString = "a.test({\n" +
                    "  add: (a, b) => a + b,\n" +
                    "});";
            v8Runtime.getExecutor(codeString).executeVoid();
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            javetProxyConverter.getConfig().setReflectionObjectFactory(null);
        }
    }

    @Test
    public void testDynamicClassForceCloseable() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public void test(DynamicClassForceCloseable mockedDynamicClass) throws Exception {
                DynamicClassForceCloseable regularDynamicClass = new DynamicClassForceCloseable();
                assertEquals(0, regularDynamicClass.add(1, 2));
                assertEquals(3, mockedDynamicClass.add(1, 2), "Add should work.");
                assertEquals("a", regularDynamicClass.getDescription());
                assertEquals("b", mockedDynamicClass.getDescription());
                assertEquals("a", regularDynamicClass.getName());
                assertEquals("b", mockedDynamicClass.getName(), "String function without arguments should work.");
                assertEquals(1, regularDynamicClass.getNumber(2));
                assertEquals(2, mockedDynamicClass.getNumber(2), "Int function with arguments should work.");
                assertEquals("a", regularDynamicClass.getTitle());
                assertEquals("b", mockedDynamicClass.getTitle(), "Property as function should work.");
                assertEquals(0, regularDynamicClass.getValue());
                assertEquals(1, mockedDynamicClass.getValue(), "Getter for value should work.");
                mockedDynamicClass.setValue(2);
                assertEquals(2, mockedDynamicClass.getValue(), "Setter for value should work.");
                assertFalse(regularDynamicClass.isPassed());
                assertTrue(mockedDynamicClass.isPassed());
                ((AutoCloseable) mockedDynamicClass).close();
            }
        };
        try {
            javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
            v8Runtime.getGlobalObject().set("a", anonymous);
            String codeString = "a.test({\n" +
                    "  add: (a, b) => a + b,\n" +
                    "  description: 'b',\n" +
                    "  getName: () => 'b',\n" +
                    "  getNumber: (n) => n,\n" +
                    "  getTitle: 'b',\n" +
                    "  passed: true,\n" +
                    "  value: 1,\n" +
                    "});";
            v8Runtime.getExecutor(codeString).executeVoid();
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            javetProxyConverter.getConfig().setReflectionObjectFactory(null);
        }
    }

    @Test
    public void testEnum() throws JavetException {
        v8Runtime.getGlobalObject().set("JavetErrorType", JavetErrorType.class);
        assertEquals(JavetErrorType.Converter, v8Runtime.getExecutor("JavetErrorType.Converter").executeObject());
        assertThrows(
                JavetExecutionException.class,
                () -> v8Runtime.getExecutor("JavetErrorType.Converter = 1;").executeVoid(),
                "Public final field should not be writable.");
        v8Runtime.getGlobalObject().delete("JavetErrorType");
        v8Runtime.getGlobalObject().set("Converter", JavetErrorType.Converter);
        assertEquals(JavetErrorType.Converter, v8Runtime.getGlobalObject().getObject("Converter"));
        v8Runtime.getGlobalObject().delete("Converter");
    }

    @Test
    public void testError() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            public void throwNullPointerException() {
                throw new NullPointerException("abc");
            }
        };
        v8Runtime.getGlobalObject().set("a", anonymous);
        try {
            v8Runtime.getExecutor("a.throwNullPointerException();").executeVoid();
            fail("Failed to throw NullPointerException");
        } catch (JavetExecutionException e) {
            assertEquals(
                    "Error: Callback throwNullPointerException failed with error message abc",
                    e.getMessage());
            assertInstanceOf(JavetException.class, e.getCause());
            assertInstanceOf(NullPointerException.class, e.getCause().getCause());
            assertEquals("abc", e.getCause().getCause().getMessage());
        } finally {
            v8Runtime.getGlobalObject().delete("a");
        }
    }

    @Test
    public void testFile() throws JavetException {
        File file = new File("/tmp/i-am-not-accessible");
        v8Runtime.getGlobalObject().set("file", file);
        assertEquals(file, v8Runtime.getGlobalObject().getObject("file"));
        assertEquals(file.exists(), v8Runtime.getExecutor("file.exists()").executeBoolean());
        assertEquals(file.isFile(), v8Runtime.getExecutor("file.isFile()").executeBoolean());
        assertEquals(file.isDirectory(), v8Runtime.getExecutor("file.isDirectory()").executeBoolean());
        assertEquals(file.canRead(), v8Runtime.getExecutor("file.canRead()").executeBoolean());
        assertEquals(file.canWrite(), v8Runtime.getExecutor("file.canWrite()").executeBoolean());
        assertEquals(file.canExecute(), v8Runtime.getExecutor("file.canExecute()").executeBoolean());
        v8Runtime.getGlobalObject().delete("file");
    }

    @Test
    public void testFloat() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectFloat(1, 1); // int to float",
                "a.expectFloat(1n, 1n); // long to float",
                "a.expectFloat(1.0, 1.0); // double to float");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testFunctionApply() throws JavetException {
        MockProxyFunction mockProxyFunction = new MockProxyFunction();
        v8Runtime.getGlobalObject().set("a", mockProxyFunction);
        assertEquals(100, v8Runtime.getExecutor("a()").executeInteger());
        assertEquals(3, v8Runtime.getExecutor("a(1, 2)").executeInteger());
        assertEquals(4, v8Runtime.getExecutor("a(1, 2, 3, 'a', 0, 0n, 0n)").executeInteger());
        assertEquals("ab", v8Runtime.getExecutor("a('a', 'b')").executeString());
        assertEquals("abc", v8Runtime.getExecutor("a.echo('abc')").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testGetter() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            private String name;

            public String get(String greeting) {
                return MessageFormat.format("get(): {0} {1}", greeting, name);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String test(String greeting) {
                return MessageFormat.format("test(): {0} {1}", greeting, name);
            }
        };
        v8Runtime.getGlobalObject().set("a", anonymous);
        v8Runtime.getExecutor("a.setName('a');").executeVoid();
        assertEquals("a", v8Runtime.getExecutor("a.getName();").executeString());
        assertEquals("get(): hello a", v8Runtime.getExecutor("a['hello'];").executeString());
        assertEquals("get(): hello a", v8Runtime.getExecutor("a.get('hello');").executeString());
        assertEquals("test(): hello a", v8Runtime.getExecutor("a.test('hello');").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testInteger() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectInteger(1, 1); // int to int",
                "a.expectInteger(1n, 1n); // long to int");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testInterface() throws JavetException {
        v8Runtime.getGlobalObject().set("AutoCloseable", AutoCloseable.class);
        v8Runtime.getGlobalObject().set("IJavetClosable", IJavetClosable.class);
        assertTrue(AutoCloseable.class.isAssignableFrom(IJavetClosable.class));
        assertTrue(v8Runtime.getExecutor("AutoCloseable.isAssignableFrom(IJavetClosable);").executeBoolean());
        assertEquals(AutoCloseable.class, v8Runtime.getExecutor("AutoCloseable").executeObject());
        assertEquals(IJavetClosable.class, v8Runtime.getExecutor("IJavetClosable").executeObject());
        v8Runtime.getGlobalObject().delete("AutoCloseable");
        v8Runtime.getGlobalObject().delete("IJavetClosable");
    }

    @Test
    public void testList() throws JavetException {
        try {
            javetProxyConverter.getConfig().setProxyListEnabled(true);
            assertTrue(javetProxyConverter.getConfig().isProxyListEnabled());
            List<Object> list = SimpleList.of("x", "y");
            v8Runtime.getGlobalObject().set("list", list);
            assertSame(list, v8Runtime.getGlobalObject().getObject("list"));
            // Array.isArray()
            assertTrue(v8Runtime.getExecutor("Array.isArray(list)").executeBoolean());
            // constructor.name
            assertEquals("Array", v8Runtime.getExecutor("list.constructor.name").executeString());
            // in
            assertTrue(v8Runtime.getExecutor("0 in list").executeBoolean());
            assertFalse(v8Runtime.getExecutor("2 in list").executeBoolean());
            assertFalse(v8Runtime.getExecutor("'x' in list").executeBoolean());
            // contains()
            assertTrue(v8Runtime.getExecutor("list.contains('x')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("list.contains('y')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("list.contains('z')").executeBoolean());
            // includes()
            assertTrue(v8Runtime.getExecutor("list.includes('x')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("list.includes('x', 1)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("list.includes('y', 1)").executeBoolean());
            // push()
            assertEquals(4, v8Runtime.getExecutor("list.push('z', '1')").executeInteger());
            assertEquals(4, v8Runtime.getExecutor("list.push()").executeInteger());
            assertTrue(v8Runtime.getExecutor("list.includes('z')").executeBoolean());
            // pop()
            assertEquals("1", v8Runtime.getExecutor("list.pop()").executeString());
            // toJSON()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list)").executeString());
            // valueOf()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.valueOf())").executeString());
            // Symbol.iterator
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...list])").executeString());
            // entries()
            assertEquals(
                    "[[0,\"x\"],[1,\"y\"],[2,\"z\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...list.entries()])").executeString());
            // with()
            assertEquals(
                    "[\"1\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.with(0, '1'))").executeString());
            // toString()
            assertEquals("x,y,z", v8Runtime.getExecutor("list.toString()").executeString());
            // values()
            assertEquals("x,y,z", v8Runtime.getExecutor("[...list.values()].toString()").executeString());
            // keys()
            assertEquals("0,1,2", v8Runtime.getExecutor("[...list.keys()].toString()").executeString());
            // concat()
            assertEquals(
                    "[\"x\",\"y\",\"z\",\"a\",\"b\",\"c\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.concat(['a', 'b'], 'c'))").executeString());
            // copyWithin()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.copyWithin())").executeString());
            assertEquals(
                    "[\"x\",\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.copyWithin(1))").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.copyWithin(1, 2))").executeString());
            assertEquals(
                    "[\"x\",\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.copyWithin(1, 0, 2))").executeString());
            // fill()
            assertEquals(
                    "[\"1\",\"1\",\"1\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.fill('1'))").executeString());
            assertEquals(
                    "[\"1\",\"a\",\"1\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.fill('a', 1, 2))").executeString());
            assertEquals(
                    "[\"1\",\"b\",\"b\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.fill('b', 1))").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.fill('x').fill('y', 1, 2).fill('z', 2))").executeString());
            // filter()
            assertEquals(
                    "[\"x\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.filter(x => x == 'x' || x == 'z'))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.filter(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // find()
            assertEquals("x", v8Runtime.getExecutor("list.find(x => x == 'x')").executeString());
            assertEquals("y", v8Runtime.getExecutor("list.find(x => x == 'y')").executeString());
            assertTrue(v8Runtime.getExecutor("list.find(x => x == '1') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.find(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findIndex()
            assertEquals(0, v8Runtime.getExecutor("list.findIndex(x => x == 'x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.findIndex(x => x == 'y')").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.findIndex(x => x == '1')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.findIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findLast()
            assertEquals("x", v8Runtime.getExecutor("list.findLast(x => x == 'x')").executeString());
            assertEquals("y", v8Runtime.getExecutor("list.findLast(x => x == 'y')").executeString());
            assertTrue(v8Runtime.getExecutor("list.findLast(x => x == '1') === undefined").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.findLast(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // findLastIndex()
            assertEquals(0, v8Runtime.getExecutor("list.findLastIndex(x => x == 'x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.findLastIndex(x => x == 'y')").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.findLastIndex(x => x == '1')").executeInteger());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.findLastIndex(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // indexOf()
            assertEquals(0, v8Runtime.getExecutor("list.indexOf('x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.indexOf('y')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.indexOf('y',1)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.indexOf('y',2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.indexOf('1')").executeInteger());
            // lastIndexOf()
            assertEquals(0, v8Runtime.getExecutor("list.lastIndexOf('x')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.lastIndexOf('y')").executeInteger());
            assertEquals(1, v8Runtime.getExecutor("list.lastIndexOf('y',2)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.lastIndexOf('y',0)").executeInteger());
            assertEquals(-1, v8Runtime.getExecutor("list.lastIndexOf('1')").executeInteger());
            // join()
            assertEquals("xyz", v8Runtime.getExecutor("list.join()").executeString());
            assertEquals("x,y,z", v8Runtime.getExecutor("list.join(',')").executeString());
            // forEach()
            assertEquals(
                    "[\"x0true\",\"y1true\",\"z2true\"]",
                    v8Runtime.getExecutor("const testForEach = [];" +
                            "list.forEach((x, i, a) => testForEach.push(x + i + (a === list)));" +
                            "JSON.stringify(testForEach)").executeString());
            // reduce()
            try {
                v8Runtime.getExecutor("list.reduce()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            try {
                v8Runtime.getExecutor("list.clear(); list.reduce((x,y)=>x+y)").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: Reduce of empty array with no initial value", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("x", v8Runtime.getExecutor("list.clear(); list.reduce((x,y)=>x+y+',', 'x')").executeString());
            assertEquals("_x0true", v8Runtime.getExecutor("list.push('x'); list.reduce((x,y,i,a)=>x+y+i+(a===list), '_')").executeString());
            assertEquals("xy1truez2true", v8Runtime.getExecutor("list.push('y','z'); list.reduce((x,y,i,a)=>x+y+i+(a===list))").executeString());
            // reduceRight()
            try {
                v8Runtime.getExecutor("list.reduceRight()").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: undefined is not a function", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            try {
                v8Runtime.getExecutor("list.clear(); list.reduceRight((x,y)=>x+y)").executeVoid();
                fail("Failed to raise type error.");
            } catch (JavetExecutionException e) {
                assertEquals("TypeError: Reduce of empty array with no initial value", e.getMessage());
                assertEquals(
                        V8ValueErrorType.TypeError,
                        ((IJavetEntityError) e.getScriptingError().getContext()).getType());
            }
            assertEquals("x", v8Runtime.getExecutor("list.clear(); list.reduceRight((x,y)=>x+y+',', 'x')").executeString());
            assertEquals("_x0true", v8Runtime.getExecutor("list.push('x'); list.reduceRight((x,y,i,a)=>x+y+i+(a===list), '_')").executeString());
            assertEquals("zy1truex0true", v8Runtime.getExecutor("list.push('y','z'); list.reduceRight((x,y,i,a)=>x+y+i+(a===list))").executeString());
            // slice()
            assertEquals("[\"x\",\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list.slice())").executeString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list.slice(0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(list.slice(0,0))").executeString());
            assertEquals("[]", v8Runtime.getExecutor("JSON.stringify(list.slice(-5,-5))").executeString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list.slice(0,100))").executeString());
            assertEquals("[\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list.slice(1))").executeString());
            assertEquals("[\"y\"]", v8Runtime.getExecutor("JSON.stringify(list.slice(1,2))").executeString());
            // splice()
            assertEquals(
                    "[][\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.splice())+JSON.stringify(list)").executeString());
            assertEquals(
                    "[\"y\"][\"x\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.splice(1,1))+JSON.stringify(list)").executeString());
            assertEquals(
                    "[\"z\"][\"x\",\"x\",\"y\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.splice(1,1,'x','y'))+JSON.stringify(list)").executeString());
            // sort()
            assertEquals("[]", v8Runtime.getExecutor("list.clear(); JSON.stringify(list.sort())").executeString());
            assertEquals("[1]", v8Runtime.getExecutor("list.push(1); JSON.stringify(list.sort())").executeString());
            assertEquals("[1,2,3]", v8Runtime.getExecutor("list.push(3,2); JSON.stringify(list.sort())").executeString());
            assertEquals("[3,2,1]", v8Runtime.getExecutor("JSON.stringify(list.sort((x,y)=>y-x))").executeString());
            v8Runtime.getExecutor("list.clear(); list.push('x', 'y', 'z')").executeVoid();
            // reverse()
            assertEquals("[\"z\",\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(list.reverse())").executeString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list.reverse())").executeString());
            // toReversed()
            assertEquals("[\"z\",\"y\",\"x\"]", v8Runtime.getExecutor("JSON.stringify(list.toReversed())").executeString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8Runtime.getExecutor("JSON.stringify(list)").executeString());
            // toSorted()
            assertEquals("[]", v8Runtime.getExecutor("list.clear(); JSON.stringify(list.toSorted())").executeString());
            assertEquals("[1]", v8Runtime.getExecutor("list.push(1); JSON.stringify(list.toSorted())").executeString());
            assertEquals("[1,2,3]", v8Runtime.getExecutor("list.push(3,2); JSON.stringify(list.toSorted())").executeString());
            assertEquals("[3,2,1]", v8Runtime.getExecutor("JSON.stringify(list.toSorted((x,y)=>y-x))").executeString());
            assertEquals("[1,3,2]", v8Runtime.getExecutor("JSON.stringify(list)").executeString());
            v8Runtime.getExecutor("list.clear(); list.push('x', 'y', 'z')").executeVoid();
            // toSpliced()
            assertEquals(
                    "[\"x\",\"y\",\"z\"][\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.toSpliced())+JSON.stringify(list)").executeString());
            assertEquals(
                    "[\"x\",\"z\"][\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.toSpliced(1,1))+JSON.stringify(list)").executeString());
            assertEquals(
                    "[\"x\",\"x\",\"y\",\"z\"][\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.toSpliced(1,1,'x','y'))+JSON.stringify(list)").executeString());
            // Symbol.iterator
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...list[Symbol.iterator]()]);").executeString());
            // map()
            assertEquals("[\"x0\",\"y1\",\"z2\"]", v8Runtime.getExecutor("JSON.stringify(list.map((x, i) => x+i))").executeString());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.map(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // every()
            assertFalse(v8Runtime.getExecutor("list.every((x, i) => x == 'x' && i == 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("list.every((x, i) => x >= 'x' && i >= 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.every(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // some()
            assertTrue(v8Runtime.getExecutor("list.some((x, i) => x == 'x' && i == 0)").executeBoolean());
            assertFalse(v8Runtime.getExecutor("list.some((x, i) => x < 'x' && i < 0)").executeBoolean());
            assertTrue(v8Runtime.getExecutor("(() => { try { list.some(); } " +
                    "catch (e) { return e instanceof TypeError; } return false; })()").executeBoolean());
            // at()
            assertEquals("x", v8Runtime.getExecutor("list.at(0)").executeString());
            assertEquals("y", v8Runtime.getExecutor("list.at(1)").executeString());
            assertEquals("z", v8Runtime.getExecutor("list.at(-1)").executeString());
            assertEquals("x", v8Runtime.getExecutor("list.at(-3)").executeString());
            assertTrue(v8Runtime.getExecutor("list.at(3)").execute().isUndefined());
            assertTrue(v8Runtime.getExecutor("list.at(-4)").execute().isUndefined());
            // unshift()
            assertEquals(5, v8Runtime.getExecutor("list.unshift('1', '2')").executeInteger());
            // []
            assertEquals("3", v8Runtime.getExecutor("list[0] = '3'; list[0]").executeString());
            // shift()
            assertEquals("3", v8Runtime.getExecutor("list.shift()").executeString());
            assertEquals("2", v8Runtime.getExecutor("list.shift()").executeString());
            // delete()
            assertTrue(v8Runtime.getExecutor("delete list[2]").executeBoolean());
            // length
            assertEquals(2, v8Runtime.getExecutor("list.length").executeInteger());
            v8Runtime.getGlobalObject().delete("list");
            // flat()
            list = SimpleList.of("x", "y", SimpleList.of("x1", "y1", SimpleList.of("x2", "y2")));
            v8Runtime.getGlobalObject().set("list", list);
            assertEquals(
                    "[\"x\",\"y\",[\"x1\",\"y1\",[\"x2\",\"y2\"]]]",
                    v8Runtime.getExecutor("JSON.stringify(list);").executeString());
            assertEquals(
                    "[\"x\",\"y\",[\"x1\",\"y1\",[\"x2\",\"y2\"]]]",
                    v8Runtime.getExecutor("JSON.stringify(list.flat(0));").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",[\"x2\",\"y2\"]]",
                    v8Runtime.getExecutor("JSON.stringify(list.flat());").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",[\"x2\",\"y2\"]]",
                    v8Runtime.getExecutor("JSON.stringify(list.flat(1));").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"x1\",\"y1\",\"x2\",\"y2\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.flat(2));").executeString());
            // flatMap()
            assertEquals(
                    "[\"x0true\",\"y1true\",\"[x1, y1, [x2, y2]]2true\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.flatMap((e,i,a)=>e+i+(a===list)));").executeString());
            v8Runtime.getGlobalObject().delete("list");
        } finally {
            javetProxyConverter.getConfig().setProxyListEnabled(false);
        }
    }

    @Test
    public void testListOfStrings() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeStringWithCast = "a.expectListOfStrings({\n" +
                "  get: (index) => index == 0? 'a': null,\n" +
                "  size: () => 2,\n" +
                "});";
        v8Runtime.getExecutor(codeStringWithCast).executeVoid();
        String codeStringWithoutCast = "a.expectListOfStrings(['a', null]);";
        v8Runtime.getExecutor(codeStringWithoutCast).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();
    }

    @Test
    public void testLong() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = String.join("\n",
                "a.expectLong(1, 1); // int to long",
                "a.expectLong(1n, 1n); // long to long");
        v8Runtime.getExecutor(codeString).executeVoid();
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testMap() throws JavetException {
        try {
            javetProxyConverter.getConfig().setProxyMapEnabled(true);
            assertTrue(javetProxyConverter.getConfig().isProxyMapEnabled());
            Map<String, Object> map = SimpleMap.of("x", 1, "y", "2");
            v8Runtime.getGlobalObject().set("map", map);
            assertSame(map, v8Runtime.getGlobalObject().getObject("map"));
            assertTrue(v8Runtime.getExecutor("map.containsKey('x')").executeBoolean());
            // constructor.name
            assertEquals("Object", v8Runtime.getExecutor("map.constructor.name").executeString());
            // in
            assertTrue(v8Runtime.getExecutor("'x' in map").executeBoolean());
            assertFalse(v8Runtime.getExecutor("1 in map").executeBoolean());
            assertFalse(v8Runtime.getExecutor("'2' in map").executeBoolean());
            // []
            assertEquals(1, v8Runtime.getExecutor("map['x']").executeInteger());
            assertEquals("2", v8Runtime.getExecutor("map['y']").executeString());
            assertEquals(1, v8Runtime.getExecutor("map.x").executeInteger());
            assertEquals("2", v8Runtime.getExecutor("map.y").executeString());
            assertEquals("3", v8Runtime.getExecutor("map['z'] = '3'; map.z;").executeString());
            assertEquals("3", map.get("z"));
            assertEquals("4", v8Runtime.getExecutor("map.z = '4'; map.z;").executeString());
            assertEquals("4", map.get("z"));
            // set() and get()
            assertTrue(v8Runtime.getExecutor("map.set('z', '5') === map").executeBoolean());
            assertEquals("5", v8Runtime.getExecutor("map.get('z')").executeString());
            assertTrue(v8Runtime.getExecutor("map.set('z', '4') === map").executeBoolean());
            assertEquals("4", v8Runtime.getExecutor("map.get('z')").executeString());
            assertTrue(v8Runtime.getExecutor("map.get('aaa') === undefined").executeBoolean());
            // size
            assertEquals(3, v8Runtime.getExecutor("map.size").executeInteger());
            // ownKeys()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(map).sort());").executeString());
            // getOwnPropertyDescriptor()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(Object.keys(map).sort());").executeString());
            // has()
            assertTrue(v8Runtime.getExecutor("map.has('x')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("map.has('1')").executeBoolean());
            // entries()
            assertEquals(
                    "[[\"x\",1],[\"y\",\"2\"],[\"z\",\"4\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...map.entries()].sort((a,b)=>a[0]-b[0]));").executeString());
            // keys()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...map.keys()].sort());").executeString());
            // values()
            assertEquals(
                    "[1,\"2\",\"4\"]",
                    v8Runtime.getExecutor("JSON.stringify([...map.values()].sort());").executeString());
            // forEach()
            assertEquals(
                    "[\"1xtrue\",\"2ytrue\",\"4ztrue\"]",
                    v8Runtime.getExecutor("const f = []; map.forEach((v,k,m)=>f.push(v+k+(m===map))); JSON.stringify(f);").executeString());
            // delete
            assertTrue(v8Runtime.getExecutor("delete map['x']").executeBoolean());
            assertFalse(map.containsKey("x"));
            // delete()
            assertTrue(v8Runtime.getExecutor("map.delete('y')").executeBoolean());
            assertFalse(map.containsKey("y"));
            // toString()
            assertEquals("[object Map]", v8Runtime.getExecutor("map.toString()").executeString());
            // toJSON()
            assertEquals(
                    "{\"z\":\"4\"}",
                    v8Runtime.getExecutor("JSON.stringify(map);").executeString());
            // valueOf()
            assertEquals(
                    "[[\"z\",\"4\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...map.valueOf()[Symbol.iterator]()]);").executeString());
            // Symbol.iterator
            assertEquals(
                    "[[\"z\",\"4\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...map[Symbol.iterator]()]);").executeString());
            // clear()
            v8Runtime.getExecutor("map.clear()").executeVoid();
            assertEquals(0, v8Runtime.getExecutor("map.size").executeInteger());
            v8Runtime.getGlobalObject().delete("map");
        } finally {
            javetProxyConverter.getConfig().setProxyMapEnabled(false);
        }
    }

    @Test
    public void testMockCallbackReceiver() throws JavetException {
        MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver(v8Runtime);
        v8Runtime.getGlobalObject().set("m", mockCallbackReceiver);
        assertEquals("abc", v8Runtime.getExecutor("m.echo('abc')").executeString());
        assertEquals(
                "[\"abc\",\"def\"]",
                v8Runtime.getExecutor("JSON.stringify(m.echo('abc', 'def'))").executeString());
        v8Runtime.getGlobalObject().delete("m");
    }

    @Test
    public void testPath() throws JavetException {
        Path path = new File("/tmp/i-am-not-accessible").toPath();
        v8Runtime.getGlobalObject().set("path", path);
        assertEquals(path, v8Runtime.getGlobalObject().getObject("path"));
        assertEquals(path.toString(), v8Runtime.getExecutor("path.toString()").executeString());
        Path newPath = v8Runtime.toObject(v8Runtime.getExecutor("path.resolve('abc')").execute(), true);
        assertNotNull(newPath);
        assertEquals(path.resolve("abc").toString(), newPath.toString());
        assertEquals(
                path.resolve("abc").toString(),
                v8Runtime.getExecutor("path.resolve('abc').toString()").executeString());
        v8Runtime.getGlobalObject().delete("path");
    }

    @Test
    public void testPattern() throws JavetException {
        v8Runtime.getGlobalObject().set("Pattern", Pattern.class);
        assertInstanceOf(Pattern.class, v8Runtime.getExecutor("let p = Pattern.compile('^\\\\d+$'); p;").executeObject());
        assertTrue(v8Runtime.getExecutor("p.matcher('123').matches();").executeBoolean());
        assertFalse(v8Runtime.getExecutor("p.matcher('a123').matches();").executeBoolean());
        v8Runtime.getGlobalObject().delete("Pattern");
        v8Runtime.getExecutor("p = undefined;").executeVoid();
    }

    @Test
    @Tag("performance")
    public void testPerformanceBetweenReflectionAndDirectProxies() throws Exception {
        final int v8Iterations = 100_000;
        IJavetAnonymous reflectionObject = new IJavetAnonymous() {
            public int add(int a, int b) {
                return a + b;
            }
        };
        IJavetDirectProxyHandler<IOException> directObject = new IJavetDirectProxyHandler<IOException>() {
            private Map<String, IJavetUniFunction<String, ? extends V8Value, IOException>> stringGetterMap;
            private V8Runtime v8Runtime;

            public V8Value add(V8Value... v8Values) throws JavetException {
                int a = ((V8ValueInteger) v8Values[0]).toPrimitive();
                int b = ((V8ValueInteger) v8Values[1]).toPrimitive();
                return v8Runtime.createV8ValueInteger(a + b);
            }

            @Override
            public V8Runtime getV8Runtime() {
                return v8Runtime;
            }

            @Override
            public Map<String, IJavetUniFunction<String, ? extends V8Value, IOException>> proxyGetStringGetterMap() {
                if (stringGetterMap == null) {
                    stringGetterMap = new HashMap<>();
                    registerStringGetterFunction("add", this::add);
                }
                return stringGetterMap;
            }

            @Override
            public void setV8Runtime(V8Runtime v8Runtime) {
                this.v8Runtime = v8Runtime;
            }
        };
        System.gc();
        try {
            v8Runtime.getGlobalObject().set("a", reflectionObject);
            assertEquals(3, v8Runtime.getExecutor("a.add(1, 2);").executeInteger());
            final long startTime = System.currentTimeMillis();
            v8Runtime.getExecutor("for (let i = 0; i < " + v8Iterations + "; ++i) a.add(1, 2);").executeVoid();
            final long stopTime = System.currentTimeMillis();
            final long tps = v8Iterations * 1000L / (stopTime - startTime);
            logger.logInfo(
                    "{0} reflection proxy calls via V8 completed in {1}ms with TPS {2}.",
                    v8Iterations, stopTime - startTime, tps);
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
        System.gc();
        try {
            v8Runtime.getGlobalObject().set("a", directObject);
            assertEquals(3, v8Runtime.getExecutor("a.add(1, 2);").executeInteger());
            final long startTime = System.currentTimeMillis();
            v8Runtime.getExecutor("for (let i = 0; i < " + v8Iterations + "; ++i) a.add(1, 2);").executeVoid();
            final long stopTime = System.currentTimeMillis();
            final long tps = v8Iterations * 1000L / (stopTime - startTime);
            logger.logInfo(
                    "{0} direct proxy calls via V8 completed in {1}ms with TPS {2}.",
                    v8Iterations, stopTime - startTime, tps);
            v8Runtime.getGlobalObject().delete("a");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testSet() throws JavetException {
        try {
            javetProxyConverter.getConfig().setProxySetEnabled(true);
            assertTrue(javetProxyConverter.getConfig().isProxySetEnabled());
            Set<String> set = SimpleSet.of("x", "y");
            v8Runtime.getGlobalObject().set("set", set);
            assertSame(set, v8Runtime.getGlobalObject().getObject("set"));
            // constructor.name
            assertEquals("Set", v8Runtime.getExecutor("set.constructor.name").executeString());
            // in
            assertTrue(v8Runtime.getExecutor("'x' in set").executeBoolean());
            assertFalse(v8Runtime.getExecutor("1 in set").executeBoolean());
            assertFalse(v8Runtime.getExecutor("'2' in set").executeBoolean());
            // contains()
            assertTrue(v8Runtime.getExecutor("set.contains('x')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.contains('y')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
            // has()
            assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
            // size
            assertEquals(2, v8Runtime.getExecutor("set.size").executeInteger());
            // add()
            assertTrue(v8Runtime.getExecutor("set.add('z') === set").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.has('z')").executeBoolean());
            // toString()
            assertEquals("[object Set]", v8Runtime.getExecutor("set.toString()").executeString());
            // toJSON()
            assertEquals(
                    "{}",
                    v8Runtime.getExecutor("JSON.stringify(set);").executeString());
            // valueOf()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...set.valueOf()[Symbol.iterator]()].sort());").executeString());
            // ownKeys()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set).sort());").executeString());
            // Object.keys()
            assertEquals(
                    "[]",
                    v8Runtime.getExecutor("JSON.stringify(Object.keys(set).sort());").executeString());
            // Symbol.iterator
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...set[Symbol.iterator]()].sort());").executeString());
            // keys()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("const keys = []; for (let key of set.keys()) { keys.push(key); } JSON.stringify(keys.sort());").executeString());
            // values()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("const values = []; for (let value of set.values()) { values.push(value); } JSON.stringify(values.sort());").executeString());
            // entries()
            assertEquals(
                    "[[\"x\",\"x\"],[\"y\",\"y\"],[\"z\",\"z\"]]",
                    v8Runtime.getExecutor("JSON.stringify([...set.entries()].sort((a,b)=>a[0]-b[0]));").executeString());
            // forEach()
            assertEquals(
                    "[\"xxtrue\",\"yytrue\",\"zztrue\"]",
                    v8Runtime.getExecutor("const f = []; set.forEach((v,k,s)=>f.push(k+v+(s===set))); JSON.stringify(f.sort())").executeString());
            // delete()
            assertTrue(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
            // clear()
            v8Runtime.getExecutor("set.clear()").executeVoid();
            assertEquals(0, v8Runtime.getExecutor("set.size").executeInteger());
            v8Runtime.getGlobalObject().delete("set");
        } finally {
            javetProxyConverter.getConfig().setProxySetEnabled(false);
        }
    }

    @Test
    public void testV8ValuesAsArguments() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            private final Map<String, String> map = new HashMap<>();

            @V8Function(thisObjectRequired = true)
            public V8Value callVarargsWithThis(V8ValueObject thisObject, V8Value... v8Values) {
                return thisObject;
            }

            @V8Function
            public int callVarargsWithoutThis(V8Value... v8Values) {
                return v8Values.length;
            }

            @V8Getter
            public V8Value getter(V8ValueString v8ValueKey) throws JavetException {
                String value = map.get(v8ValueKey.getValue());
                return value == null
                        ? v8ValueKey.getV8Runtime().createV8ValueUndefined()
                        : v8ValueKey.getV8Runtime().createV8ValueString(value);
            }

            @V8Setter
            public void setter(V8ValueString v8ValueKey, V8ValueString v8ValueValue) throws JavetException {
                map.put(v8ValueKey.getValue(), v8ValueValue.getValue());
            }
        };
        v8Runtime.getGlobalObject().set("a", anonymous);
        v8Runtime.getExecutor("a['x'] = 'abc';").executeVoid();
        v8Runtime.getExecutor("a['y'] = '123';").executeVoid();
        assertEquals("abc", v8Runtime.getExecutor("a['x']").executeString());
        assertEquals("123", v8Runtime.getExecutor("a['y']").executeString());
        assertTrue(v8Runtime.getExecutor("a['z']").execute().isUndefined());
        assertEquals(0, v8Runtime.getExecutor("a.callVarargsWithoutThis()").executeInteger());
        assertEquals(3, v8Runtime.getExecutor("a.callVarargsWithoutThis(1,2,3)").executeInteger());
        assertTrue(v8Runtime.getExecutor("a.callVarargsWithThis(1,2,3) === a").executeBoolean());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testZonedDateTime() throws JavetException {
        v8Runtime.getGlobalObject().set("a", anonymous);
        String codeString = "a.expectZonedDateTime(new Date(253402300799000), new Date(-2208988800000));";
        v8Runtime.getExecutor(codeString).executeVoid();
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            public ZonedDateTime getZonedDateTime() {
                return ZonedDateTime.of(
                        9999, 12, 31, 23, 59, 59, 0,
                        JavetDateTimeUtils.ZONE_ID_UTC);
            }
        };
        v8Runtime.getGlobalObject().set("a", anonymous);
        try (V8ValueZonedDateTime v8ValueZonedDateTime = v8Runtime.getExecutor("a.getZonedDateTime()").execute()) {
            assertEquals(253402300799L, v8ValueZonedDateTime.getValue().toInstant().getEpochSecond());
        }
        assertEquals(
                253402300799L,
                v8Runtime.getExecutor("a.getZonedDateTime()").executeZonedDateTime().toInstant().getEpochSecond());
        v8Runtime.getGlobalObject().delete("a");
    }

    interface IStringJoiner extends AutoCloseable {
        String join(String a, String b);
    }

    interface IStringUtils extends AutoCloseable {
        String hello();

        String join(String separator, String... strings);

        List<String> split(String separator, String string);
    }

    @V8Convert(mode = V8ConversionMode.AllowOnly)
    public static class AllowOnlyClass {
        @V8Allow
        @V8Property(name = "type")
        public String xType;

        @V8Property(name = "disallowedType")
        public String yType;

        private Map<String, String> map;

        @V8Allow
        public AllowOnlyClass() {
            map = new HashMap<>();
            this.xType = "abc";
        }

        @V8Function(name = "disallowedName")
        public String getDisallowedName() {
            return getClass().getSimpleName();
        }

        @V8Allow
        @V8Function(name = "name")
        public String getName() {
            return getClass().getSimpleName();
        }

        @V8Allow
        @V8Getter
        public String xGetter(String key) {
            return map.get(key);
        }

        @V8Allow
        @V8Setter
        public boolean xSetter(String key, String value) {
            return map.put(key, value) != null;
        }
    }

    @V8Convert(mode = V8ConversionMode.BlockOnly)
    public static class BlockOnlyClass {
        @V8Property(name = "type")
        public String xType;

        @V8Block
        @V8Property(name = "disallowedType")
        public String yType;

        private Map<String, String> map;

        public BlockOnlyClass() {
            map = new HashMap<>();
            this.xType = "abc";
        }

        @V8Block
        @V8Function(name = "disallowedName")
        public String getDisallowedName() {
            return getClass().getSimpleName();
        }

        @V8Function(name = "name")
        public String getName() {
            return getClass().getSimpleName();
        }

        @V8Getter
        public String xGetter(String key) {
            return map.get(key);
        }

        @V8Setter
        public boolean xSetter(String key, String value) {
            return map.put(key, value) != null;
        }
    }

    public static class DynamicClassAutoCloseable implements AutoCloseable {
        public int add(int a, int b) {
            return 0;
        }

        @Override
        public void close() throws Exception {
        }
    }

    public static class DynamicClassForceCloseable {
        public int add(int a, int b) {
            return 0;
        }

        public String getDescription() {
            return "a";
        }

        public String getName() {
            return "a";
        }

        public int getNumber(int n) {
            return 1;
        }

        public String getTitle() {
            return "a";
        }

        public int getValue() {
            return 0;
        }

        public boolean isPassed() {
            return false;
        }

        public void setValue(int value) {
        }
    }

    @V8Convert(proxyMode = V8ProxyMode.Function)
    public static class MockProxyFunction {
        @V8ProxyFunctionApply
        public int apply() {
            return 100;
        }

        @V8ProxyFunctionApply
        public int apply(int a, int b) {
            return a + b;
        }

        @V8ProxyFunctionApply
        public String apply(String a, String b) {
            return a + b;
        }

        @V8ProxyFunctionApply
        public int apply(int a, int b, int c, V8Value... v8Values) {
            return v8Values.length;
        }

        public String echo(String text) {
            return text;
        }
    }

    static class StringJoiner implements AutoCloseable {
        private IStringJoiner joiner;

        public StringJoiner() {
            joiner = null;
        }

        @Override
        public void close() throws Exception {
            if (joiner != null) {
                joiner.close();
                joiner = null;
            }
        }

        public IStringJoiner getJoiner() {
            return joiner;
        }

        public void setJoiner(IStringJoiner joiner) {
            this.joiner = joiner;
        }
    }

    static class StringUtils implements AutoCloseable {
        private IStringUtils utils;

        public StringUtils() {
            utils = null;
        }

        @Override
        public void close() throws Exception {
            if (utils != null) {
                utils.close();
                utils = null;
            }
        }

        public IStringUtils getUtils() {
            return utils;
        }

        public void setUtils(IStringUtils utils) {
            this.utils = utils;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    @V8Convert(mode = V8ConversionMode.Transparent)
    public static class TransparentClass {
        @V8Property(name = "type")
        public String xType;

        private Map<String, String> map;

        public TransparentClass() {
            map = new HashMap<>();
            this.xType = "abc";
        }

        @V8Function(name = "name")
        @V8Allow
        @V8Block
        public String getName() {
            return getClass().getSimpleName();
        }

        @V8Getter
        public String xGetter(String key) {
            return map.get(key);
        }

        @V8Setter
        public boolean xSetter(String key, String value) {
            return map.put(key, value) != null;
        }
    }
}
