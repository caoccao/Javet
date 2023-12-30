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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.interop.options.V8Flags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Flags {
    @Test
    public void testSeal() {
        V8Flags v8Flags = new V8Flags();
        // Open
        assertFalse(v8Flags.isSealed());
        assertFalse(v8Flags.isAllowNativesSyntax());
        assertTrue(v8Flags.setAllowNativesSyntax(true).isAllowNativesSyntax());
        assertFalse(v8Flags.isExposeGC());
        assertTrue(v8Flags.setExposeGC(true).isExposeGC());
        assertFalse(v8Flags.isExposeInspectorScripts());
        assertTrue(v8Flags.setExposeInspectorScripts(true).isExposeInspectorScripts());
        assertEquals(0, v8Flags.getInitialHeapSize());
        assertEquals(2048, v8Flags.setInitialHeapSize(2048).getInitialHeapSize());
        assertEquals(0, v8Flags.getMaxHeapSize());
        assertEquals(2048, v8Flags.setMaxHeapSize(2048).getMaxHeapSize());
        assertEquals(0, v8Flags.getMaxOldSpaceSize());
        assertEquals(2048, v8Flags.setMaxOldSpaceSize(2048).getMaxOldSpaceSize());
        assertFalse(v8Flags.isTrackRetainingPath());
        assertTrue(v8Flags.setTrackRetainingPath(true).isTrackRetainingPath());
        assertTrue(v8Flags.isUseStrict());
        assertFalse(v8Flags.setUseStrict(false).isUseStrict());
        assertNull(v8Flags.getCustomFlags());
        assertEquals("test 123", v8Flags.setCustomFlags("test 123").getCustomFlags());
        // Sealed
        assertTrue(v8Flags.seal().isSealed());
        assertTrue(v8Flags.setAllowNativesSyntax(false).isAllowNativesSyntax());
        assertTrue(v8Flags.setExposeGC(false).isExposeGC());
        assertTrue(v8Flags.setExposeInspectorScripts(false).isExposeInspectorScripts());
        assertEquals(2048, v8Flags.setInitialHeapSize(1).getInitialHeapSize());
        assertEquals(2048, v8Flags.setMaxHeapSize(1).getMaxHeapSize());
        assertEquals(2048, v8Flags.setMaxOldSpaceSize(1).getMaxOldSpaceSize());
        assertTrue(v8Flags.setTrackRetainingPath(false).isTrackRetainingPath());
        assertFalse(v8Flags.setUseStrict(true).isUseStrict());
        assertEquals("test 123", v8Flags.setCustomFlags("abc def").getCustomFlags());
    }

    @Test
    public void testToString() {
        V8Flags v8Flags = new V8Flags();
        assertEquals(
                "--max-old-space-size=2048 --use-strict",
                v8Flags.setMaxOldSpaceSize(2048).toString());
        assertEquals(
                "--allow-natives-syntax --max-old-space-size=2048 --use-strict",
                v8Flags.setAllowNativesSyntax(true).toString());
    }
}
