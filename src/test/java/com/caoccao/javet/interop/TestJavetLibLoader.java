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

package com.caoccao.javet.interop;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetOSUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetLibLoader {
    @AfterAll
    public static void afterAll() {
        V8Host.setLibraryReloadable(false);
    }

    @BeforeAll
    public static void beforeAll() {
        V8Host.setLibraryReloadable(true);
    }

    @Test
    public void testCustomClassLoader() {
        testCustomClassLoader(JSRuntimeType.Node);
        testCustomClassLoader(JSRuntimeType.V8);
    }

    protected void testCustomClassLoader(JSRuntimeType jsRuntimeType) {
        try {
            for (int i = 0; i < 2; ++i) {
                JavetClassLoader javetClassLoader = new JavetClassLoader(getClass().getClassLoader(), jsRuntimeType);
                javetClassLoader.load();
                javetClassLoader.getNative();
                javetClassLoader = null;
                System.gc();
                System.runFinalization();
            }
        } catch (JavetException e) {
            // This is expected.
        }
    }

    @Test
    public void testLoadAndUnload() throws JavetException {
        if (JavetOSUtils.IS_WINDOWS) {
            testLoadAndUnload(JSRuntimeType.Node);
            testLoadAndUnload(JSRuntimeType.V8);
        }
    }

    protected void testLoadAndUnload(JSRuntimeType jsRuntimeType) throws JavetException {
        V8Host v8Host = V8Host.getInstance(jsRuntimeType);
        assertNotNull(v8Host);
        assertTrue(v8Host.isLibraryLoaded());
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
        }
        assertEquals(0, v8Host.getV8RuntimeCount());
        assertTrue(v8Host.unloadLibrary());
        assertFalse(v8Host.isLibraryLoaded());
        try {
            v8Host.createV8Runtime();
        } catch (JavetException e) {
            assertEquals(JavetError.LibraryNotLoaded, e.getError());
        }
        assertTrue(v8Host.loadLibrary());
        assertTrue(v8Host.isLibraryLoaded());
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
        }
        assertEquals(0, v8Host.getV8RuntimeCount());
    }
}
