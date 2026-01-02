/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.utils.SimpleList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeFlags {
    protected static final List<GetterAndSetter> SWITCHES = SimpleList.of(
            new GetterAndSetter(NodeFlags::isExperimentalSqlite, NodeFlags::setExperimentalSqlite),
            new GetterAndSetter(NodeFlags::isJsFloat16Array, NodeFlags::setJsFloat16Array),
            new GetterAndSetter(NodeFlags::isNoExperimentalRequireModule, NodeFlags::setNoExperimentalRequireModule),
            new GetterAndSetter(NodeFlags::isNoWarnings, NodeFlags::setNoWarnings)
    );

    @Test
    public void testSeal() {
        final NodeFlags nodeFlags = new NodeFlags();
        // Open
        assertFalse(nodeFlags.isSealed());
        SWITCHES.forEach(getterAndSetter -> {
            assertFalse(getterAndSetter.getter.apply(nodeFlags));
            assertTrue(getterAndSetter.getter.apply(getterAndSetter.setter.apply(nodeFlags, true)));
        });
        assertFalse(nodeFlags.isPermission());
        assertNull(nodeFlags.getAllowFsRead());
        assertArrayEquals(new String[]{"/a", "/b"}, nodeFlags.setAllowFsRead(new String[]{"/a", "/b"}).getAllowFsRead());
        assertTrue(nodeFlags.isPermission());
        assertFalse(nodeFlags.setPermission(false).isPermission());
        assertNull(nodeFlags.getAllowFsWrite());
        assertArrayEquals(new String[]{"/a", "/b"}, nodeFlags.setAllowFsWrite(new String[]{"/a", "/b"}).getAllowFsWrite());
        assertTrue(nodeFlags.isPermission());
        assertNull(nodeFlags.getCustomFlags());
        assertNull(nodeFlags.getIcuDataDir());
        assertEquals("abc", nodeFlags.setIcuDataDir("abc").getIcuDataDir());
        assertArrayEquals(new String[]{"abc", "def"}, nodeFlags.setCustomFlags(new String[]{"abc", "def"}).getCustomFlags());
        // Sealed
        assertTrue(nodeFlags.seal().isSealed());
        assertNotNull(nodeFlags.setAllowFsRead(null).getAllowFsRead());
        assertArrayEquals(new String[]{"abc", "def"}, nodeFlags.setCustomFlags(new String[]{"123", "456"}).getCustomFlags());
        assertEquals("abc", nodeFlags.setIcuDataDir("def").getIcuDataDir());
        SWITCHES.forEach(getterAndSetter -> {
            assertTrue(getterAndSetter.getter.apply(getterAndSetter.setter.apply(nodeFlags, false)));
        });
    }

    @Test
    public void testToString() {
        NodeFlags nodeFlags = new NodeFlags();
        assertEquals(
                "--experimental-sqlite",
                nodeFlags.setExperimentalSqlite(true).toString());
        assertEquals(
                "--allow-fs-read=/a --allow-fs-read=/b --experimental-sqlite --permission",
                nodeFlags.setAllowFsRead(new String[]{"/a", "/b"}).toString());
        nodeFlags.setAllowFsRead(null).setExperimentalSqlite(false);
        assertEquals(
                "--allow-fs-write=/a --allow-fs-write=/b --permission",
                nodeFlags.setAllowFsWrite(new String[]{"/a", "/b"}).toString());
        nodeFlags.setAllowFsWrite(null).setPermission(false);
        assertEquals("--no-warnings", nodeFlags.setNoWarnings(true).toString());
        nodeFlags.setNoWarnings(false);
        assertEquals(
                "--no-experimental-require-module",
                nodeFlags.setNoExperimentalRequireModule(true).toString());
        nodeFlags.setNoExperimentalRequireModule(false);
        assertEquals("--icu-data-dir=abc", nodeFlags.setIcuDataDir("abc").toString());
        nodeFlags.setIcuDataDir(null);
        assertEquals(
                "--js-float16array",
                nodeFlags.setJsFloat16Array(true).toString());
        nodeFlags.setJsFloat16Array(false);
    }

    protected static class GetterAndSetter {
        public Function<NodeFlags, Boolean> getter;
        public BiFunction<NodeFlags, Boolean, NodeFlags> setter;

        public GetterAndSetter(Function<NodeFlags, Boolean> getter, BiFunction<NodeFlags, Boolean, NodeFlags> setter) {
            this.getter = getter;
            this.setter = setter;
        }
    }
}
