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
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.virtual.V8VirtualIterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetBridgeConverter extends BaseTestJavetRuntime {
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
        assertEquals(6, javetBridgeConverter.getConfig().getProxyPlugins().size());
        assertTrue(javetBridgeConverter.getConfig().isProxyArrayEnabled());
        assertTrue(javetBridgeConverter.getConfig().isProxyListEnabled());
        assertTrue(javetBridgeConverter.getConfig().isProxyMapEnabled());
        assertTrue(javetBridgeConverter.getConfig().isProxySetEnabled());
    }

    @Test
    public void testBigInteger() throws JavetException {
        // 2n**65n
        BigInteger bigInteger = new BigInteger("36893488147419103232");
        v8Runtime.getGlobalObject().set("l", bigInteger);
        v8Runtime.getGlobalObject().set("list", SimpleList.of(bigInteger, bigInteger.negate()));
        assertEquals(bigInteger, v8Runtime.getExecutor("l").executeObject());
        assertEquals(bigInteger, v8Runtime.getExecutor("l.toV8Value()").executeBigInteger());
        // valueOf()
        assertEquals(bigInteger, v8Runtime.getExecutor("l.valueOf()").executeBigInteger());
        // toLocaleString()
        assertEquals("36893488147419103232", v8Runtime.getExecutor("l.toLocaleString()").executeString());
        // Symbol.toPrimitive
        assertEquals(bigInteger, v8Runtime.getExecutor("l[Symbol.toPrimitive]()").executeBigInteger());
        // +
        assertEquals(bigInteger.add(new BigInteger("1")), v8Runtime.getExecutor("1n + l").executeBigInteger());
        // Symbol.iterator
        assertEquals(
                "TypeError: l is not iterable",
                assertThrows(
                        JavetExecutionException.class,
                        () -> v8Runtime.getExecutor("JSON.stringify([...l])").executeVoid()).getMessage());
        // toJSON()
        assertEquals(
                "[\"36893488147419103232\",\"-36893488147419103232\"]",
                v8Runtime.getExecutor("JSON.stringify(list, (key, value) =>" +
                        " typeof value === 'bigint'?value.toString():value)").executeString());
        // toString()
        assertEquals("36893488147419103232", v8Runtime.getExecutor("l.toString()").executeString());
        v8Runtime.getGlobalObject().delete("l");
        v8Runtime.getGlobalObject().delete("list");
    }

    @Test
    public void testBoolean() throws JavetException {
        v8Runtime.getGlobalObject().set("bTrue", true);
        v8Runtime.getGlobalObject().set("bFalse", false);
        v8Runtime.getGlobalObject().set("list", SimpleList.of(true, false));
        assertTrue((Boolean) v8Runtime.getExecutor("bTrue").executeObject());
        assertFalse((Boolean) v8Runtime.getExecutor("bFalse").executeObject());
        assertEquals(1, v8Runtime.getExecutor("bTrue.toV8Value()?1:0").executeInteger());
        assertEquals(0, v8Runtime.getExecutor("bFalse.toV8Value()?1:0").executeInteger());
        // Symbol.toPrimitive
        assertEquals(1, v8Runtime.getExecutor("bTrue[Symbol.toPrimitive]()?1:0").executeInteger());
        assertEquals(0, v8Runtime.getExecutor("bFalse[Symbol.toPrimitive]()?1:0").executeInteger());
        // valueOf()
        assertTrue(v8Runtime.getExecutor("bTrue.valueOf()").executeBoolean());
        assertFalse(v8Runtime.getExecutor("bFalse.valueOf()").executeBoolean());
        // toString()
        assertEquals("true", v8Runtime.getExecutor("bTrue.toString()").executeString());
        assertEquals("false", v8Runtime.getExecutor("bFalse.toString()").executeString());
        // toJSON()
        assertEquals("[true,false]", v8Runtime.getExecutor("JSON.stringify(list)").executeString());
        v8Runtime.getGlobalObject().delete("bTrue");
        v8Runtime.getGlobalObject().delete("bFalse");
        v8Runtime.getGlobalObject().delete("list");
    }

    @Test
    public void testDouble() throws JavetException {
        Double d = 1.23D;
        v8Runtime.getGlobalObject().set("d", d);
        assertEquals(1.23D, v8Runtime.getExecutor("d").executeObject(), DELTA);
        assertEquals(1.23D, v8Runtime.getExecutor("d.toV8Value()").executeDouble(), DELTA);
        // valueOf()
        assertEquals(1.23D, v8Runtime.getExecutor("d.valueOf()").executeDouble(), DELTA);
        // toExponential()
        assertEquals("1.23e+0", v8Runtime.getExecutor("d.toExponential()").executeString());
        // toFixed()
        assertEquals("1.2300", v8Runtime.getExecutor("d.toFixed(4)").executeString());
        // toLocaleString()
        assertEquals("1.23", v8Runtime.getExecutor("d.toLocaleString()").executeString());
        // toPrecision()
        assertEquals("1.2300", v8Runtime.getExecutor("d.toPrecision(5)").executeString());
        // Symbol.toPrimitive
        assertEquals(1.23D, v8Runtime.getExecutor("d[Symbol.toPrimitive]()").executeDouble(), DELTA);
        // +
        assertEquals(2.23D, v8Runtime.getExecutor("1 + d").executeDouble(), DELTA);
        // Symbol.iterator
        assertEquals(
                "TypeError: d is not iterable",
                assertThrows(
                        JavetExecutionException.class,
                        () -> v8Runtime.getExecutor("JSON.stringify([...d])").executeVoid()).getMessage());
        // toJSON()
        assertEquals("1.23", v8Runtime.getExecutor("JSON.stringify(d)").executeString());
        // toString()
        assertEquals("1.23", v8Runtime.getExecutor("d.toString()").executeString());
        v8Runtime.getGlobalObject().delete("d");
    }

    @Test
    public void testIntArray() throws JavetException {
        int[] intArray = new int[]{1, 2};
        v8Runtime.getGlobalObject().set("a", intArray);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        assertEquals("1,2", v8Runtime.getExecutor("'' + a[Symbol.toPrimitive]()").executeString());
        assertInstanceOf(V8VirtualIterator.class, v8Runtime.getExecutor("a[Symbol.iterator]()").executeObject());
        assertEquals(Integer.valueOf(1), v8Runtime.getExecutor("a[Symbol.iterator]().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().done").executeBoolean());
        assertEquals(Integer.valueOf(2), v8Runtime.getExecutor("a[Symbol.iterator]().next().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().value").execute().isUndefined());
        assertArrayEquals(intArray, v8Runtime.getExecutor("a.toV8Value()").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testInteger() throws JavetException {
        v8Runtime.getGlobalObject().set("i", 12345);
        v8Runtime.getGlobalObject().set("list", SimpleList.of(1, -1));
        assertEquals(12345, (Integer) v8Runtime.getExecutor("i").executeObject());
        assertEquals(12345, v8Runtime.getExecutor("i.toV8Value()").executeInteger());
        // valueOf()
        assertEquals(12345, v8Runtime.getExecutor("i.valueOf()").executeInteger());
        // toExponential()
        assertEquals("1.2345e+4", v8Runtime.getExecutor("i.toExponential()").executeString());
        // toFixed()
        assertEquals("12345.00", v8Runtime.getExecutor("i.toFixed(2)").executeString());
        // toLocaleString()
        assertEquals("12345", v8Runtime.getExecutor("i.toLocaleString()").executeString());
        // toPrecision()
        assertEquals("1.23e+4", v8Runtime.getExecutor("i.toPrecision(3)").executeString());
        // Symbol.toPrimitive
        assertEquals(12345, v8Runtime.getExecutor("i[Symbol.toPrimitive]()").executeInteger());
        // +
        assertEquals(12346, v8Runtime.getExecutor("1 + i").executeInteger());
        // Symbol.iterator
        assertEquals(
                "TypeError: i is not iterable",
                assertThrows(
                        JavetExecutionException.class,
                        () -> v8Runtime.getExecutor("JSON.stringify([...i])").executeVoid()).getMessage());
        // toJSON()
        assertEquals("[1,-1]", v8Runtime.getExecutor("JSON.stringify(list)").executeString());
        // toString()
        assertEquals("12345", v8Runtime.getExecutor("i.toString()").executeString());
        v8Runtime.getGlobalObject().delete("i");
        v8Runtime.getGlobalObject().delete("list");
    }

    @Test
    public void testIntegerArray() throws JavetException {
        Integer[] integerArray = new Integer[]{1, 2};
        v8Runtime.getGlobalObject().set("a", integerArray);
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        assertEquals("[1,2]", v8Runtime.getExecutor("JSON.stringify(a[Symbol.toPrimitive]())").executeString());
        assertEquals("1,2", v8Runtime.getExecutor("'' + a[Symbol.toPrimitive]()").executeString());
        assertEquals("{}", v8Runtime.getExecutor("JSON.stringify(a[Symbol.iterator]())").executeString());
        assertInstanceOf(V8VirtualIterator.class, v8Runtime.getExecutor("a[Symbol.iterator]()").executeObject());
        assertEquals(Integer.valueOf(1), v8Runtime.getExecutor("a[Symbol.iterator]().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().done").executeBoolean());
        assertEquals(Integer.valueOf(2), v8Runtime.getExecutor("a[Symbol.iterator]().next().next().value").executeObject());
        assertFalse(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().done").executeBoolean());
        assertTrue(v8Runtime.getExecutor("a[Symbol.iterator]().next().next().next().value").execute().isUndefined());
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
        v8Runtime.getGlobalObject().set("list", SimpleList.of(1L, -1L));
        assertEquals(12345L, (Long) v8Runtime.getExecutor("l").executeObject());
        assertEquals(12345L, v8Runtime.getExecutor("l.toV8Value()").executeLong());
        // valueOf()
        assertEquals(12345L, v8Runtime.getExecutor("l.valueOf()").executeLong());
        // toLocaleString()
        assertEquals("12345", v8Runtime.getExecutor("l.toLocaleString()").executeString());
        // Symbol.toPrimitive
        assertEquals(12345L, v8Runtime.getExecutor("l[Symbol.toPrimitive]()").executeLong());
        // +
        assertEquals(12346L, v8Runtime.getExecutor("1n + l").executeLong());
        // Symbol.iterator
        assertEquals(
                "TypeError: l is not iterable",
                assertThrows(
                        JavetExecutionException.class,
                        () -> v8Runtime.getExecutor("JSON.stringify([...l])").executeVoid()).getMessage());
        // toJSON()
        assertEquals("[\"1\",\"-1\"]", v8Runtime.getExecutor("JSON.stringify(list, (key, value) =>" +
                " typeof value === 'bigint'?value.toString():value)").executeString());
        // toString()
        assertEquals("12345", v8Runtime.getExecutor("l.toString()").executeString());
        v8Runtime.getGlobalObject().delete("l");
        v8Runtime.getGlobalObject().delete("list");
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
        Map<String, Object> map = SimpleMap.of("x", 1, "y", "2");
        v8Runtime.getGlobalObject().set("map", map);
        assertSame(map, v8Runtime.getGlobalObject().getObject("map"));
        assertTrue((Boolean) v8Runtime.getExecutor("map.containsKey('x')").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("map.size").executeObject());
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
                v8Runtime.getExecutor("JSON.stringify(Object.keys(map));").executeString());
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
        assertEquals(set, v8Runtime.getExecutor("set.add('z')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('z')").executeObject());
        assertEquals(
                "[]",
                v8Runtime.getExecutor("JSON.stringify(Object.keys(set));").executeString());
        assertEquals(
                "[\"x\",\"y\",\"z\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set));").executeString());
        assertEquals(
                "[\"x\",\"y\",\"z\"]",
                v8Runtime.getExecutor("JSON.stringify([...set]);").executeString());
        v8Runtime.getGlobalObject().delete("set");
    }

    @Test
    public void testString() throws JavetException {
        v8Runtime.getGlobalObject().set("s", "test");
        assertEquals("test", v8Runtime.getExecutor("s").executeObject());
        // length
        assertEquals(4, v8Runtime.getExecutor("s.length").executeInteger());
        assertEquals("test", v8Runtime.getExecutor("s.toV8Value()").executeString());
        // toString()
        assertEquals("test", v8Runtime.getExecutor("s.toString()").executeString());
        // valueOf()
        assertEquals("test", v8Runtime.getExecutor("s.valueOf()").executeString());
        // toJSON()
        assertEquals("\"test\"", v8Runtime.getExecutor("JSON.stringify(s)").executeString());
        // []
        assertEquals("t", v8Runtime.getExecutor("s[0]").executeString());
        // at(0)
        assertEquals("t", v8Runtime.getExecutor("s.at(0)").executeString());
        // charAt()
        assertEquals("t", v8Runtime.getExecutor("s.charAt(0)").executeString());
        // charCodeAt()
        assertEquals('t', v8Runtime.getExecutor("s.charCodeAt(0)").executeInteger());
        // codePointAt()
        assertEquals('t', v8Runtime.getExecutor("s.codePointAt(0)").executeInteger());
        // concat()
        assertEquals("test123", v8Runtime.getExecutor("s.concat('123')").executeString());
        // endsWith()
        assertTrue(v8Runtime.getExecutor("s.endsWith('t')").executeBoolean());
        assertFalse(v8Runtime.getExecutor("s.endsWith('x')").executeBoolean());
        // hashCode()
        assertEquals("test".hashCode(), (Integer) v8Runtime.getExecutor("s.hashCode()").executeObject());
        // includes()
        assertTrue(v8Runtime.getExecutor("s.includes('t')").executeBoolean());
        assertFalse(v8Runtime.getExecutor("s.includes('x')").executeBoolean());
        // indexOf()
        assertEquals(1, v8Runtime.getExecutor("s.indexOf('e')").executeInteger());
        // isWellFormed()
        assertTrue(v8Runtime.getExecutor("s.isWellFormed()").executeBoolean());
        // lastIndexOf()
        assertEquals(1, v8Runtime.getExecutor("s.lastIndexOf('e')").executeInteger());
        // localeCompare()
        assertEquals(0, v8Runtime.getExecutor("s.localeCompare('test')").executeInteger());
        assertEquals(-132, v8Runtime.getExecutor("s.localeCompare('tést')").executeInteger());
        // match()
        assertTrue(v8Runtime.getExecutor("s.match(/^t/)").executeBoolean());
        assertFalse(v8Runtime.getExecutor("s.match(/123/)").executeBoolean());
        // matchAll()
        assertEquals("[[\"t\"],[\"t\"]]", v8Runtime.getExecutor("JSON.stringify([...s.matchAll(/t/g)])").executeString());
        // normalize()
        assertEquals("test", v8Runtime.getExecutor("s.normalize()").executeString());
        // padEnd()
        assertEquals("test..", v8Runtime.getExecutor("s.padEnd(6, '.')").executeString());
        // padStart()
        assertEquals("..test", v8Runtime.getExecutor("s.padStart(6, '.')").executeString());
        // repeat()
        assertEquals("testtest", v8Runtime.getExecutor("s.repeat(2)").executeString());
        // replace()
        assertEquals("xest", v8Runtime.getExecutor("s.replace('t', 'x')").executeString());
        // replaceAll()
        assertEquals("xesx", v8Runtime.getExecutor("s.replaceAll('t', 'x')").executeString());
        // search()
        assertEquals(1, v8Runtime.getExecutor("s.search('e')").executeInteger());
        // slice()
        assertEquals("es", v8Runtime.getExecutor("s.slice(1,3)").executeString());
        // split()
        assertEquals("[\"t\",\"st\"]", v8Runtime.getExecutor("JSON.stringify(s.split('e'))").executeString());
        // startsWith()
        assertTrue(v8Runtime.getExecutor("s.startsWith('t')").executeBoolean());
        assertFalse(v8Runtime.getExecutor("s.startsWith('x')").executeBoolean());
        // substring()
        assertEquals("es", v8Runtime.getExecutor("s.substring(1,3)").executeString());
        // toLocaleLowerCase()
        assertEquals("test", v8Runtime.getExecutor("s.toLocaleLowerCase()").executeString());
        // toLocaleUpperCase()
        assertEquals("TEST", v8Runtime.getExecutor("s.toLocaleUpperCase()").executeString());
        // toLowerCase()
        assertEquals("test", v8Runtime.getExecutor("s.toLowerCase()").executeString());
        // toUpperCase()
        assertEquals("TEST", v8Runtime.getExecutor("s.toUpperCase()").executeString());
        // toWellFormed()
        assertEquals("test", v8Runtime.getExecutor("s.toWellFormed()").executeString());
        // trim()
        assertEquals("test", v8Runtime.getExecutor("s.trim()").executeString());
        // trimEnd()
        assertEquals("test", v8Runtime.getExecutor("s.trimEnd()").executeString());
        // trimStart()
        assertEquals("test", v8Runtime.getExecutor("s.trimStart()").executeString());
        // Object.getOwnPropertyNames()
        assertEquals("[\"0\",\"1\",\"2\",\"3\",\"length\"]", v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(s))").executeString());
        // Object.keys()
        assertEquals("[\"0\",\"1\",\"2\",\"3\"]", v8Runtime.getExecutor("JSON.stringify(Object.keys(s))").executeString());
        // Symbol.toPrimitive
        assertEquals("test", v8Runtime.getExecutor("s[Symbol.toPrimitive]()").executeString());
        // Symbol.iterator
        assertEquals("[\"t\",\"e\",\"s\",\"t\"]", v8Runtime.getExecutor("JSON.stringify([...s])").executeString());
        assertEquals("{\"0\":\"t\",\"1\":\"e\",\"2\":\"s\",\"3\":\"t\"}", v8Runtime.getExecutor("JSON.stringify({...s})").executeString());
        assertEquals("abc test", v8Runtime.getExecutor("'abc ' + s").executeString());
        v8Runtime.getGlobalObject().delete("s");
    }

    @Test
    public void testStringArray() throws JavetException {
        v8Runtime.getGlobalObject().set("a", new String[]{"x", "y"});
        assertEquals(2, (Integer) v8Runtime.getExecutor("a.length").executeObject());
        assertEquals("x", v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals("y", v8Runtime.getExecutor("a[1]").executeObject());
        assertEquals("[\"0\",\"1\",\"length\"]", v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(a))").executeString());
        assertEquals("[\"0\",\"1\"]", v8Runtime.getExecutor("JSON.stringify(Object.keys(a))").executeString());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(a[Symbol.toPrimitive]())").executeString());
        assertEquals(
                "{}",
                v8Runtime.getExecutor("JSON.stringify(a[Symbol.iterator]())").executeString());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(a.toV8Value())").executeString());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(a.valueOf())").executeString());
        assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify([...a])").executeString());
        assertEquals("{\"0\":\"x\",\"1\":\"y\"}", v8Runtime.getExecutor("JSON.stringify({...a})").executeString());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testStringList() throws JavetException {
        v8Runtime.getGlobalObject().set("l", SimpleList.of("x", "y"));
        assertEquals(2, (Integer) v8Runtime.getExecutor("l.size()").executeObject());
        assertEquals("x", v8Runtime.getExecutor("l.get(0)").executeObject());
        assertEquals("y", v8Runtime.getExecutor("l.get(1)").executeObject());
        assertEquals("[\"0\",\"1\",\"length\"]", v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(l))").executeString());
        assertEquals("[\"0\",\"1\"]", v8Runtime.getExecutor("JSON.stringify(Object.keys(l))").executeString());
        assertEquals(
                "[\"x\",\"y\"]",
                v8Runtime.getExecutor("JSON.stringify(l.toV8Value())").executeString());
        assertEquals(
                "{}",
                v8Runtime.getExecutor("JSON.stringify(l[Symbol.iterator]())").executeString());
        assertEquals("[\"x\",\"y\"]", v8Runtime.getExecutor("JSON.stringify([...l])").executeString());
        assertEquals("{\"0\":\"x\",\"1\":\"y\"}", v8Runtime.getExecutor("JSON.stringify({...l})").executeString());
        v8Runtime.getGlobalObject().delete("l");
    }

    @Test
    public void testZonedDateTime() throws JavetException {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2000, 1, 2, 3, 4, 5, 6, JavetDateTimeUtils.ZONE_ID_UTC);
        v8Runtime.getExecutor("const d = new Date('2000-01-02T03:04:05.000Z')").executeVoid();
        v8Runtime.getGlobalObject().set("z", zonedDateTime);
        assertEquals(zonedDateTime, v8Runtime.getExecutor("z").executeObject());
        SimpleList.of("getDate", "getDay", "getFullYear", "getHours", "getMilliseconds",
                "getMinutes", "getMonth", "getSeconds", "getTime", "getTimezoneOffset",
                "getUTCDate", "getUTCDay", "getUTCFullYear", "getUTCHours", "getUTCMilliseconds",
                "getUTCMinutes", "getUTCMonth", "getUTCSeconds").forEach(functionName -> {
            try {
                assertEquals(
                        v8Runtime.getExecutor("d." + functionName + "()").executeInteger(),
                        v8Runtime.getExecutor("z." + functionName + "()").executeInteger(),
                        functionName + "() should match.");
            } catch (JavetException e) {
                fail(e);
            }
        });
        SimpleList.of("setDate", "setFullYear", "setHours", "setMilliseconds", "setMinutes",
                "setMonth", "setSeconds", "setTime", "setUTCDate", "setUTCFullYear",
                "setUTCHours", "setUTCMilliseconds", "setUTCMinutes", "setUTCMonth", "setUTCSeconds",
                "setYear").forEach(functionName -> assertEquals(
                "TypeError: Date.prototype." + functionName + "() is not supported",
                assertThrows(
                        JavetExecutionException.class,
                        () -> v8Runtime.getExecutor("z." + functionName + "()").executeVoid()).getMessage()));
        // toJSON()
        assertEquals(
                v8Runtime.getExecutor("JSON.stringify(d)").executeString(),
                v8Runtime.getExecutor("JSON.stringify(z)").executeString());
        SimpleList.of(
                "toDateString", "toISOString", "toLocaleDateString", "toLocaleString", "toLocaleTimeString",
                "toTimeString", "toUTCString", "toString").forEach(functionName -> {
            try {
                assertEquals(
                        v8Runtime.getExecutor("d." + functionName + "()").executeString(),
                        v8Runtime.getExecutor("z." + functionName + "()").executeString(),
                        functionName + "() should match.");
            } catch (JavetException e) {
                fail(e);
            }
        });
        // valueOf()
        assertEquals(946782245000L, v8Runtime.getExecutor("z.valueOf()").executeLong());
        assertTrue(v8Runtime.getExecutor("d.valueOf() === z.valueOf()").executeBoolean());
        // Symbol.toPrimitive
        assertEquals(
                v8Runtime.getExecutor("d[Symbol.toPrimitive]('string')").executeString(),
                v8Runtime.getExecutor("z[Symbol.toPrimitive]('string')").executeString(),
                "[Symbol.toPrimitive]('string') should match.");
        assertEquals(
                v8Runtime.getExecutor("d[Symbol.toPrimitive]('number')").executeLong(),
                v8Runtime.getExecutor("z[Symbol.toPrimitive]('number')").executeLong(),
                "[Symbol.toPrimitive]('number') should match.");
        v8Runtime.getGlobalObject().delete("z");
    }
}
