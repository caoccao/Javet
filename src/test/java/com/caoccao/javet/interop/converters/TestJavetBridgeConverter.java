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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    }

    @Test
    public void testIntArray() throws JavetException {
        v8Runtime.getGlobalObject().set("a", new int[]{1, 2});
        assertEquals(2, v8Runtime.getExecutor("a.length").executeInteger());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testInteger() throws JavetException {
        v8Runtime.getGlobalObject().set("i", 12345);
        assertEquals(12345, (Integer) v8Runtime.getExecutor("i").executeObject());
        assertEquals(12345, v8Runtime.getExecutor("i[Symbol.toPrimitive]()").executeInteger());
        assertEquals(12346, v8Runtime.getExecutor("1 + i").executeInteger());
        v8Runtime.getGlobalObject().delete("i");
    }

    @Test
    public void testIntegerArray() throws JavetException {
        v8Runtime.getGlobalObject().set("a", new Integer[]{1, 2});
        assertEquals(2, v8Runtime.getExecutor("a.length").executeInteger());
        assertEquals(1, (Integer) v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals(2, (Integer) v8Runtime.getExecutor("a[1]").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testLong() throws JavetException {
        v8Runtime.getGlobalObject().set("l", 12345L);
        assertEquals(12345L, (Long) v8Runtime.getExecutor("l").executeObject());
        assertEquals(12345L, v8Runtime.getExecutor("l[Symbol.toPrimitive]()").executeLong());
        assertEquals(12346L, v8Runtime.getExecutor("1n + l").executeLong());
        v8Runtime.getGlobalObject().delete("l");
    }

    @Test
    public void testMap() throws JavetException {
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("x", 1);
            put("y", "2");
        }};
        v8Runtime.getGlobalObject().set("map", map);
        assertTrue(map == v8Runtime.getGlobalObject().getObject("map"));
        assertTrue((Boolean) v8Runtime.getExecutor("map.containsKey('x')").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("map['x']").executeObject());
        assertEquals("2", v8Runtime.getExecutor("map['y']").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("map.x").executeObject());
        assertEquals("2", v8Runtime.getExecutor("map.y").executeObject());
        assertEquals("3", v8Runtime.getExecutor("map['z'] = '3'; map.z;").executeObject());
        assertEquals("3", map.get("z"));
        assertEquals("4", v8Runtime.getExecutor("map.z = '4'; map.z;").executeObject());
        assertEquals("4", map.get("z"));
        v8Runtime.getGlobalObject().delete("map");
    }

    @Test
    public void testSet() throws JavetException {
        Set<String> set = new HashSet<String>() {{
            add("x");
            add("y");
        }};
        v8Runtime.getGlobalObject().set("set", set);
        assertTrue(set == v8Runtime.getGlobalObject().getObject("set"));
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('x')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('y')").executeObject());
        assertFalse((Boolean) v8Runtime.getExecutor("set.contains('z')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.add('z')").executeObject());
        assertTrue((Boolean) v8Runtime.getExecutor("set.contains('z')").executeObject());
        v8Runtime.getGlobalObject().delete("set");
    }

    @Test
    public void testString() throws JavetException {
        v8Runtime.getGlobalObject().set("s", "test");
        assertEquals("test", v8Runtime.getExecutor("s").executeObject());
        assertEquals("test", v8Runtime.getExecutor("s[Symbol.toPrimitive]()").executeString());
        assertEquals("abc test", v8Runtime.getExecutor("'abc ' + s").executeString());
        assertEquals('t', (Character) v8Runtime.getExecutor("s.charAt(0)").executeObject());
        assertEquals(1, (Integer) v8Runtime.getExecutor("s.indexOf('e')").executeObject());
        v8Runtime.getGlobalObject().delete("s");
    }

    @Test
    public void testStringArray() throws JavetException {
        v8Runtime.getGlobalObject().set("a", new String[]{"x", "y"});
        assertEquals(2, v8Runtime.getExecutor("a.length").executeInteger());
        assertEquals("x", v8Runtime.getExecutor("a[0]").executeObject());
        assertEquals("y", v8Runtime.getExecutor("a[1]").executeObject());
        v8Runtime.getGlobalObject().delete("a");
    }

    @Test
    public void testStringList() throws JavetException {
        v8Runtime.getGlobalObject().set("l", Stream.of("x", "y").collect(Collectors.toList()));
        assertEquals(2, (Integer) v8Runtime.getExecutor("l.size()").executeObject());
        assertEquals("x", v8Runtime.getExecutor("l.get(0)").executeObject());
        assertEquals("y", v8Runtime.getExecutor("l.get(1)").executeObject());
        v8Runtime.getGlobalObject().delete("l");
    }
}
