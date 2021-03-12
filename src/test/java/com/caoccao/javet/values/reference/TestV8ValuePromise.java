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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValuePromise extends BaseTestJavetRuntime {
    protected JavetStandardConsoleInterceptor interceptor;

    public TestV8ValuePromise() {
        super();
    }

    @AfterEach
    @Override
    public void afterEach() throws JavetException {
        interceptor.unregister(v8Runtime.getGlobalObject());
        v8Runtime.requestGarbageCollectionForTesting(true);
        super.afterEach();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        interceptor = new JavetStandardConsoleInterceptor(v8Runtime);
        interceptor.register(v8Runtime.getGlobalObject());
    }

    @Test
    public void testFulfilled() throws JavetException {
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ resolve(1); }).then(x => x)").execute()) {
            assertNotNull(v8ValuePromise);
            assertEquals(V8ValuePromise.STATE_FULFILLED, v8ValuePromise.getState());
            assertTrue(v8ValuePromise.isFulfilled());
            assertFalse(v8ValuePromise.hasHandler());
            assertEquals(1, v8ValuePromise.getResultInteger());
        }
    }

    @Test
    public void testImportModule() throws JavetException {
        String codeString = "export function test() { return '1'; }";
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString);
        iV8Executor.getV8ScriptOrigin().setResourceName("./test.js");
        try (V8DataModule v8DataModule = iV8Executor.compileModule()) {
            assertEquals(4, v8DataModule.getScriptId());
            try (V8ValuePromise v8ValuePromise = v8DataModule.execute()) {
                assertNotNull(v8ValuePromise);
                assertTrue(v8ValuePromise.isFulfilled());
            }
        }
        codeString = "import * as X from './test.js';";
        iV8Executor = v8Runtime.getExecutor(codeString);
        iV8Executor.getV8ScriptOrigin().setResourceName("./x.js");
        try (V8DataModule v8DataModule = iV8Executor.compileModule()) {
            assertEquals(5, v8DataModule.getScriptId());
            assertFalse(v8DataModule.instantiate());
        }
    }

    @Test
    public void testRejected() throws JavetException {
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ reject('a'); })").execute()) {
            assertNotNull(v8ValuePromise);
            assertEquals(V8ValuePromise.STATE_REJECTED, v8ValuePromise.getState());
            assertTrue(v8ValuePromise.isRejected());
            assertFalse(v8ValuePromise.hasHandler());
            assertEquals("a", v8ValuePromise.getResultString());
        }
    }

    @Test
    public void testResolve() throws JavetException {
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ resolve(1); })").execute()) {
            assertNotNull(v8ValuePromise);
            assertEquals(V8ValuePromise.STATE_FULFILLED, v8ValuePromise.getState());
            assertTrue(v8ValuePromise.isFulfilled());
            assertFalse(v8ValuePromise.hasHandler());
            assertEquals(1, v8ValuePromise.getResultInteger());
        }
    }
}
