/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.executors.IV8Executor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Script extends BaseTestJavetRuntime {
    @Test
    public void testCachedData() throws JavetException {
        String codeString = "1 + 1";
        byte[] cachedData;
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./test.js");
        try (V8Script v8Script = iV8Executor.compileV8Script()) {
            assertNotNull(v8Script);
            byte[] initializedCachedData = v8Script.getCachedData();
            assertTrue(initializedCachedData != null && initializedCachedData.length > 0);
            assertEquals(2, v8Script.executeInteger());
            cachedData = initializedCachedData;
        }
        // Cached is only accepted if the source code matches.
        iV8Executor = v8Runtime.getExecutor(codeString, cachedData).setResourceName("./test.js");
        try (V8Script v8Script = iV8Executor.compileV8Script()) {
            assertNotNull(v8Script);
            byte[] uninitializedCachedData = v8Script.getCachedData();
            assertTrue(uninitializedCachedData != null && uninitializedCachedData.length > 0);
            assertEquals(2, v8Script.executeInteger());
        }
        assertEquals(2, iV8Executor.executeInteger());
    }

    @Test
    public void testExecute() throws JavetException {
        IV8Executor iV8Executor = v8Runtime.getExecutor(
                "const a = 1; a;").setResourceName("./test.js");
        try (V8Script v8Script = iV8Executor.compileV8Script()) {
            assertNotNull(v8Script);
            assertEquals(1, v8Script.executeInteger());
        }
    }

    @Test
    public void testUnexpectedIdentifier() throws JavetException {
        try (V8Script v8Script = v8Runtime.getExecutor("a b c").compileV8Script()) {
            fail("Failed to report error.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Unexpected identifier 'b'", e.getScriptingError().getMessage());
        }
    }
}
