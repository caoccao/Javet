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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.virtual.V8VirtualIterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetBridgeConverter extends BaseTestJavetRuntime {
    protected String functionCastString;
    protected JavetBridgeConverter javetBridgeConverter;

    public TestJavetBridgeConverter() {
        super();
        javetBridgeConverter = new JavetBridgeConverter();
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
        v8Runtime.setConverter(javetBridgeConverter);
    }

    @Test
    public void testBoolean() throws JavetException {
        v8Runtime.getGlobalObject().set("bTrue", true);
        v8Runtime.getGlobalObject().set("bFalse", false);
        assertTrue((Boolean) v8Runtime.getExecutor("bTrue").executeObject());
        assertFalse((Boolean) v8Runtime.getExecutor("bFalse").executeObject());
        assertEquals(1, v8Runtime.getExecutor("bTrue.toV8Value()?1:0").executeInteger());
        assertEquals(0, v8Runtime.getExecutor("bFalse.toV8Value()?1:0").executeInteger());
        assertEquals(1, v8Runtime.getExecutor("bTrue[Symbol.toPrimitive]()?1:0").executeInteger());
        assertEquals(0, v8Runtime.getExecutor("bFalse[Symbol.toPrimitive]()?1:0").executeInteger());
        v8Runtime.getGlobalObject().delete("bTrue");
        v8Runtime.getGlobalObject().delete("bFalse");
    }

    @Test
    public void testIntArray() throws JavetException {
        int[] intArray = new int[]{1, 2};
        v8Runtime.getGlobalObject().set("a", intArray);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        assertArrayEquals(intArray, v8Runtime.getExecutor("a[Symbol.toPrimitive]()").executeObject());
        assertInstanceOf(V8VirtualIterator.class, v8Runtime.getExecutor("a[Symbol.iterator]()").executeObject());
        assertEquals(Integer.valueOf(1), v8Runtime.getExecutor("a[Symbol.iterator]().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().done").executeBoolean());
        assertEquals(Integer.valueOf(2), v8Runtime.getExecutor("a[Symbol.iterator]().next().next().value").executeObject());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().value").execute().isUndefined());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().done").executeBoolean());
        assertArrayEquals(intArray, v8Runtime.getExecutor("a.toV8Value()").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testInteger() throws JavetException {
        v8Runtime.getGlobalObject().set("i", 12345);
        assertEquals(12345, (Integer) v8Runtime.getExecutor("i").executeObject());
        assertEquals(12345, v8Runtime.getExecutor("i.toV8Value()").executeInteger());
        assertEquals(12345, v8Runtime.getExecutor("i[Symbol.toPrimitive]()").executeInteger());
        assertEquals(12346, v8Runtime.getExecutor("1 + i").executeInteger());
        v8Runtime.getGlobalObject().delete("i");
    }

    @Test
    public void testIntegerArray() throws JavetException {
        Integer[] integerArray = new Integer[]{1, 2};
        v8Runtime.getGlobalObject().set("a", integerArray);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(a[Symbol.toPrimitive]())").executeString());
        assertEquals("{}", v8Runtime.getExecutor("JSON.stringify(a[Symbol.iterator]())").executeString());
        assertInstanceOf(V8VirtualIterator.class, v8Runtime.getExecutor("a[Symbol.iterator]()").executeObject());
        assertEquals(Integer.valueOf(1), v8Runtime.getExecutor("a[Symbol.iterator]().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().done").executeBoolean());
        assertEquals(Integer.valueOf(2), v8Runtime.getExecutor("a[Symbol.iterator]().next().next().value").executeObject());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().value").execute().isUndefined());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().done").executeBoolean());
        assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(a.toV8Value())").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testIntegerList() throws JavetException {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(1);
        integerList.add(2);
        v8Runtime.getGlobalObject().set("a", integerList);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        v8Runtime.getExecutor("a.add(3);").executeVoid();
        assertEquals(3, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
        assertEquals(3, (Integer) v8Runtime.getExecutor("a[2]").executeObject());
        assertEquals(3, integerList.size());
        assertEquals(3, integerList.get(2));
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testLong() throws JavetException {
        v8Runtime.getGlobalObject().set("l", 12345L);
        assertEquals(12345L, (Long) v8Runtime.getExecutor("l").executeObject());
        assertEquals(12345L, v8Runtime.getExecutor("l.toV8Value()").executeLong());
        assertEquals(12345L, v8Runtime.getExecutor("l[Symbol.toPrimitive]()").executeLong());
        assertEquals(12346L, v8Runtime.getExecutor("1n + l").executeLong());
        v8Runtime.getGlobalObject().delete("l");
    }

    @Test
    public void testLongList() throws JavetException {
        List<Long> longList = Collections.unmodifiableList(SimpleList.of(1L, 2L));
        v8Runtime.getGlobalObject().set("a", longList);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.size()").executeObject());
        assertEquals(1L, (Long) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2L, (Long) v8Runtime.getExecutor("a[1]").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testMap() throws JavetException {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("x", 1);
            put("y", "2");
        }};
        v8Runtime.getGlobalObject().set("map", map);
        assertSame(map, v8Runtime.getGlobalObject().getObject("map"));
        assertTrue((Boolean) v8Runtime.getExecutor("map.containsKey('x')").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("map.size()").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("map['x']").executeObject());
        assertEquals("2", v8Runtime.getExecutor("map['y']").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("map.x").executeObject());
        assertEquals("2", v8Runtime.getExecutor("map.y").executeObject());
        assertEquals("3", v8Runtime.getExecutor("map['z'] = '3'; map.z;").executeObject());
        assertEquals("3", map.get("z"));
        assertEquals("4", v8Runtime.getExecutor("map.z = '4'; map.z;").executeObject());
        assertEquals("4", map.get("z"));
        assertEquals(
                "[\"x\",\"y\",\"z\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(map));").executeString());
        v8Runtime.getGlobalObject().delete("map");
    }

    @Test
    public void testSet() throws JavetException {
        Set<String> set = SimpleSet.of("x", "y");
        v8Runtime.getGlobalObject().set("set", set);
        assertSame(set, v8Runtime.getGlobalObject().getObject("set"));
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('x')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('y')").executeObject());
        assertFalse((Boolean) v8Runtime.getExecutor("set.contains('z')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.add('z')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('z')").executeObject());
        assertEquals(
                "[\"x\",\"y\",\"z\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set));").executeString());
        v8Runtime.getGlobalObject().delete("set");
    }

    @Test
    public void testString() throws JavetException {
        v8Runtime.getGlobalObject().set("s", "test");
        assertEquals("test", v8Runtime.getExecutor("s").executeObject());
        assertEquals("test", v8Runtime.getExecutor("s.toV8Value()").executeString());
        assertEquals("test", v8Runtime.getExecutor("s[Symbol.toPrimitive]()").executeString());
        assertEquals("abc test", v8Runtime.getExecutor("'abc ' + s").executeString());
        assertEquals('t', (Character) v8Runtime.getExecutor("s.charAt(0)").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("s.indexOf('e')").executeObject());
        v8Runtime.getGlobalObject().delete("s");
    }

    @Test
    public void testStringArray() throws JavetException {
        v8Runtime.getGlobalObject().set("a", new String[]{"x", "y"});
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals("x", v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals("y", v8Runtime.getExecutor("a[1]").executeObject());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(a[Symbol.toPrimitive]())").executeString());
        assertEquals(
                "{}",
                v8Runtime.getExecutor("JSON.stringify(a[Symbol.iterator]())").executeString());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(a.toV8Value())").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testStringList() throws JavetException {
        v8Runtime.getGlobalObject().set("l", Stream.of("x", "y").collect(Collectors.toList()));
        assertEquals(2, (Integer) v8Runtime.getExecutor("l.size()").executeObject());
        assertEquals("x", v8Runtime.getExecutor("l.get(0)").executeObject());
        assertEquals("y", v8Runtime.getExecutor("l.get(1)").executeObject());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(l.toV8Value())").executeString());
        assertEquals(
                "{}",
                v8Runtime.getExecutor("JSON.stringify(l[Symbol.iterator]())").executeString());
        v8Runtime.getGlobalObject().delete("l");
    }
}
