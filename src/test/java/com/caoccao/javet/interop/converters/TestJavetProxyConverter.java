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
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.interop.proxy.JavetReflectionObjectFactory;
import com.caoccao.javet.mock.MockCallbackReceiver;
import com.caoccao.javet.mock.MockDirectProxyFunctionHandler;
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
        super.afterEach();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        v8Runtime.setConverter(javetProxyConverter);
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
            List<String> list = SimpleList.of("x", "y");
            v8Runtime.getGlobalObject().set("list", list);
            assertSame(list, v8Runtime.getGlobalObject().getObject("list"));
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
            assertTrue(v8Runtime.getExecutor("list.includes('z')").executeBoolean());
            // pop()
            assertEquals("1", v8Runtime.getExecutor("list.pop()").executeString());
            // toJSON()
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list)").executeString());
            // Symbol.iterator
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify([...list])").executeString());
            // with()
            assertEquals(
                    "[\"1\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(list.with(0, '1'))").executeString());
            // toString()
            assertEquals("[x, y, z]", v8Runtime.getExecutor("list.toString()").executeString());
            // values()
            assertEquals("x", v8Runtime.getExecutor("list.values().next().value").executeString());
            // keys()
            assertEquals("0,1,2", v8Runtime.getExecutor("[...list.keys()].toString()").executeString());
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
            assertEquals(2, v8Runtime.getExecutor("list.size()").executeInteger());
            // length
            assertEquals(2, v8Runtime.getExecutor("list.length").executeInteger());
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
            Map<String, Object> map = SimpleMap.of("x", 1, "y", "2");
            v8Runtime.getGlobalObject().set("map", map);
            assertSame(map, v8Runtime.getGlobalObject().getObject("map"));
            assertTrue(v8Runtime.getExecutor("map.containsKey('x')").executeBoolean());
            assertEquals(1, v8Runtime.getExecutor("map['x']").executeInteger());
            assertEquals("2", v8Runtime.getExecutor("map['y']").executeString());
            assertEquals(1, v8Runtime.getExecutor("map.x").executeInteger());
            assertEquals("2", v8Runtime.getExecutor("map.y").executeString());
            assertEquals("3", v8Runtime.getExecutor("map['z'] = '3'; map.z;").executeString());
            assertEquals("3", map.get("z"));
            assertEquals("4", v8Runtime.getExecutor("map.z = '4'; map.z;").executeString());
            assertEquals("4", map.get("z"));
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(map));").executeString());
            assertTrue(v8Runtime.getExecutor("delete map['x']").executeBoolean());
            assertFalse(map.containsKey("x"));
            assertTrue(v8Runtime.getExecutor("delete map['y']").executeBoolean());
            assertFalse(map.containsKey("y"));
            assertEquals(
                    "{\"z\":\"z\"}",
                    v8Runtime.getExecutor("JSON.stringify(map);").executeString());
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
            Set<String> set = SimpleSet.of("x", "y");
            v8Runtime.getGlobalObject().set("set", set);
            assertSame(set, v8Runtime.getGlobalObject().getObject("set"));
            assertTrue(v8Runtime.getExecutor("set.contains('x')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.contains('y')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.add('z')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
            assertTrue(v8Runtime.getExecutor("set.has('z')").executeBoolean());
            assertEquals(
                    "{}",
                    v8Runtime.getExecutor("JSON.stringify(set);").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set));").executeString());
            assertEquals(
                    "[\"x\",\"y\",\"z\"]",
                    v8Runtime.getExecutor("const keys = []; for (let key of set.keys()) { keys.push(key); } JSON.stringify(keys);").executeString());
            assertTrue(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.delete('z')").executeBoolean());
            assertFalse(v8Runtime.getExecutor("set.has('z')").executeBoolean());
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
