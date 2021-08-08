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
import com.caoccao.javet.enums.JavetErrorType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interfaces.IJavetClosable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetProxyConverter extends BaseTestJavetRuntime {
    protected JavetProxyConverter javetProxyConverter;

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        javetProxyConverter = new JavetProxyConverter();
        v8Runtime.setConverter(javetProxyConverter);
    }

    // TODO
//    @Test
    public void testConstructor() throws JavetException {
        v8Runtime.getGlobalObject().set("StringBuilder", StringBuilder.class);
        assertEquals("abc def", v8Runtime.getExecutor(
                "function main() {\n" +
                        "  return new StringBuilder().append('abc').append(' ').append('def').toString();" +
                        "}\n" +
                        "main();").executeString());
        v8Runtime.getGlobalObject().delete("StringBuilder");
        v8Runtime.lowMemoryNotification();
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
        v8Runtime.lowMemoryNotification();
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
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testInterface() throws JavetException {
        javetProxyConverter.getConfig().setStaticClassEnabled(false);
        v8Runtime.getGlobalObject().set("AutoCloseable", AutoCloseable.class);
        v8Runtime.getGlobalObject().set("IJavetClosable", IJavetClosable.class);
        assertTrue(AutoCloseable.class.isAssignableFrom(IJavetClosable.class));
        assertTrue(v8Runtime.getExecutor("AutoCloseable.isAssignableFrom(IJavetClosable);").executeBoolean());
        assertEquals(AutoCloseable.class, v8Runtime.getExecutor("AutoCloseable").executeObject());
        assertEquals(IJavetClosable.class, v8Runtime.getExecutor("IJavetClosable").executeObject());
        v8Runtime.getGlobalObject().delete("AutoCloseable");
        v8Runtime.getGlobalObject().delete("IJavetClosable");
        javetProxyConverter.getConfig().setStaticClassEnabled(true);
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testMap() throws JavetException {
        javetProxyConverter.getConfig().setProxyMapEnabled(true);
        Map<String, Object> map = new HashMap<String, Object>() {{
            put("x", 1);
            put("y", "2");
        }};
        v8Runtime.getGlobalObject().set("map", map);
        assertTrue(map == v8Runtime.getGlobalObject().getObject("map"));
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
        v8Runtime.getGlobalObject().delete("map");
        v8Runtime.lowMemoryNotification();
        javetProxyConverter.getConfig().setProxyMapEnabled(false);
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
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testPattern() throws JavetException {
        v8Runtime.getGlobalObject().set("Pattern", Pattern.class);
        assertTrue(v8Runtime.getExecutor("let p = Pattern.compile('^\\\\d+$'); p;").executeObject() instanceof Pattern);
        assertTrue(v8Runtime.getExecutor("p.matcher('123').matches();").executeBoolean());
        assertFalse(v8Runtime.getExecutor("p.matcher('a123').matches();").executeBoolean());
        v8Runtime.getGlobalObject().delete("Pattern");
        v8Runtime.getExecutor("p = undefined;").executeVoid();
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testSet() throws JavetException {
        javetProxyConverter.getConfig().setProxySetEnabled(true);
        Set<String> set = new HashSet<String>() {{
            add("x");
            add("y");
        }};
        v8Runtime.getGlobalObject().set("set", set);
        assertTrue(set == v8Runtime.getGlobalObject().getObject("set"));
        assertTrue(v8Runtime.getExecutor("set.contains('x')").executeBoolean());
        assertTrue(v8Runtime.getExecutor("set.contains('y')").executeBoolean());
        assertFalse(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
        assertTrue(v8Runtime.getExecutor("set.add('z')").executeBoolean());
        assertTrue(v8Runtime.getExecutor("set.contains('z')").executeBoolean());
        assertEquals(
                "[\"x\",\"y\",\"z\"]",
                v8Runtime.getExecutor("JSON.stringify(Object.getOwnPropertyNames(set));").executeString());
        v8Runtime.getGlobalObject().delete("set");
        v8Runtime.lowMemoryNotification();
        javetProxyConverter.getConfig().setProxySetEnabled(false);
    }
}
