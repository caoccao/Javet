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
import com.caoccao.javet.enums.JavetPromiseRejectEvent;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetPromiseRejectCallback;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValuePromise extends BaseTestJavetRuntime {
    @Test
    public void testFulfilled() throws JavetException {
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ resolve(1); }).then(x => x)").execute()) {
            assertNotNull(v8ValuePromise);
            assertFalse(v8ValuePromise.hasHandler());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                assertEquals(V8ValuePromise.STATE_PENDING, v8ValuePromise.getState());
                assertTrue(v8ValuePromise.isPending());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            v8Runtime.await();
            assertEquals(V8ValuePromise.STATE_FULFILLED, v8ValuePromise.getState());
            assertTrue(v8ValuePromise.isFulfilled());
            assertEquals(1, v8ValuePromise.getResultInteger());
        }
    }

    @Test
    public void testNoHandler() throws JavetException, NoSuchMethodException {
        List<JavetPromiseRejectEvent> events = new ArrayList<>();
        IJavetPromiseRejectCallback callback = (event, promise, value) -> events.add(event);
        if (v8Host.getJSRuntimeType().isNode()) {
            v8Runtime.getExecutor("const process = require('process');\n" +
                    "var globalReason = null;\n" +
                    "process.on('unhandledRejection', (reason, promise) => {\n" +
                    "  globalReason = reason;\n" +
                    "});").executeVoid();
        } else {
            v8Runtime.setPromiseRejectCallback(callback);
        }
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ reject('a'); })").execute()) {
            assertNotNull(v8ValuePromise);
            assertEquals(V8ValuePromise.STATE_REJECTED, v8ValuePromise.getState());
            assertTrue(v8ValuePromise.isRejected());
            assertFalse(v8ValuePromise.hasHandler());
            assertEquals("a", v8ValuePromise.getResultString());
        }
        if (v8Host.getJSRuntimeType().isNode()) {
            v8Runtime.await();
            assertEquals("a", v8Runtime.getGlobalObject().getString("globalReason"));
        } else {
            assertEquals(1, events.size());
            assertEquals(JavetPromiseRejectEvent.PromiseRejectWithNoHandler, events.get(0));
        }
    }

    @Test
    public void testNoHandlerAndHandlerAddedAfterReject() throws JavetException {
        List<JavetPromiseRejectEvent> events = new ArrayList<>();
        IJavetPromiseRejectCallback callback = (event, promise, value) -> events.add(event);
        if (v8Host.getJSRuntimeType().isNode()) {
            v8Runtime.getExecutor("const process = require('process');\n" +
                    "process.on('unhandledRejection', (reason, promise) => {\n" +
                    "  console.log(reason);\n" +
                    "});").executeVoid();
        } else {
            v8Runtime.setPromiseRejectCallback(callback);
        }
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                "new Promise((resolve, reject)=>{ reject('a'); }).then(r => r).catch(e => e)").execute()) {
            assertNotNull(v8ValuePromise);
            if (v8Host.getJSRuntimeType().isNode()) {
                assertEquals(V8ValuePromise.STATE_PENDING, v8ValuePromise.getState());
                assertTrue(v8ValuePromise.isPending());
            } else {
                assertEquals(V8ValuePromise.STATE_FULFILLED, v8ValuePromise.getState());
                assertTrue(v8ValuePromise.isFulfilled());
            }
            assertFalse(v8ValuePromise.hasHandler());
            if (v8Host.getJSRuntimeType().isNode()) {
                assertTrue(v8ValuePromise.getResult().isUndefined());
            } else {
                assertEquals("a", v8ValuePromise.getResultString());
            }
        }
        if (v8Host.getJSRuntimeType().isV8()) {
            assertEquals(2, events.size());
            assertEquals(JavetPromiseRejectEvent.PromiseRejectWithNoHandler, events.get(0));
            assertEquals(JavetPromiseRejectEvent.PromiseHandlerAddedAfterReject, events.get(1));
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
