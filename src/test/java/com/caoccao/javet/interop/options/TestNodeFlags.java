/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeFlags {
    @Test
    public void testSeal() {
        NodeFlags nodeFlags = new NodeFlags();
        // Open
        assertFalse(nodeFlags.isSealed());
        assertFalse(nodeFlags.isExperimentalPermission());
        assertNull(nodeFlags.getAllowFsRead());
        assertArrayEquals(new String[]{"/a", "/b"}, nodeFlags.setAllowFsRead(new String[]{"/a", "/b"}).getAllowFsRead());
        assertTrue(nodeFlags.isExperimentalPermission());
        assertFalse(nodeFlags.setExperimentalPermission(false).isExperimentalPermission());
        assertNull(nodeFlags.getAllowFsWrite());
        assertArrayEquals(new String[]{"/a", "/b"}, nodeFlags.setAllowFsWrite(new String[]{"/a", "/b"}).getAllowFsWrite());
        assertTrue(nodeFlags.isExperimentalPermission());
        assertNull(nodeFlags.getCustomFlags());
        assertArrayEquals(new String[]{"abc", "def"}, nodeFlags.setCustomFlags(new String[]{"abc", "def"}).getCustomFlags());
        assertFalse(nodeFlags.isExperimentalSqlite());
        assertTrue(nodeFlags.setExperimentalSqlite(true).isExperimentalSqlite());
        assertFalse(nodeFlags.isNoWarnings());
        assertTrue(nodeFlags.setNoWarnings(true).isNoWarnings());
        // Sealed
        assertTrue(nodeFlags.seal().isSealed());
        assertNotNull(nodeFlags.setAllowFsRead(null).getAllowFsRead());
        assertArrayEquals(new String[]{"abc", "def"}, nodeFlags.setCustomFlags(new String[]{"123", "456"}).getCustomFlags());
        assertTrue(nodeFlags.setExperimentalSqlite(false).isExperimentalSqlite());
        assertTrue(nodeFlags.setNoWarnings(false).isNoWarnings());
    }

    @Test
    public void testToString() {
        NodeFlags nodeFlags = new NodeFlags();
        assertEquals(
                "--experimental-sqlite",
                nodeFlags.setExperimentalSqlite(true).toString());
        assertEquals(
                "--allow-fs-read=/a --allow-fs-read=/b --experimental-permission --experimental-sqlite",
                nodeFlags.setAllowFsRead(new String[]{"/a", "/b"}).toString());
        nodeFlags.setAllowFsRead(null).setExperimentalSqlite(false);
        assertEquals(
                "--allow-fs-write=/a --allow-fs-write=/b --experimental-permission",
                nodeFlags.setAllowFsWrite(new String[]{"/a", "/b"}).toString());
        nodeFlags.setAllowFsWrite(null).setExperimentalPermission(false);
        assertEquals(
                "--no-warnings",
                nodeFlags.setNoWarnings(true).toString());
    }
}
