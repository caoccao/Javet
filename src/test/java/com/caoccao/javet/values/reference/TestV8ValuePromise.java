/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.enums.JavetPromiseRejectEvent;
import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interop.callback.IJavetPromiseRejectCallback;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.mock.MockFS;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInPromise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValuePromise extends BaseTestJavetRuntime {
    @Test
    public void testAsyncGenerator() throws JavetException {
        if (v8Runtime.getJSRuntimeType().isV8()) {
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8ValueObject.bind(new IJavetAnonymous() {
                    @V8Property(symbolType = V8ValueSymbolType.BuiltIn)
                    public V8ValueFunction asyncIterator() throws JavetException, NoSuchMethodException {
                        IJavetAnonymous anonymous = new IJavetAnonymous() {
                            public V8ValueObject get() throws JavetException {
                                AtomicInteger count = new AtomicInteger(0);
                                V8ValueObject v8ValueObjectAsyncGenerator = v8Runtime.createV8ValueObject();
                                v8ValueObjectAsyncGenerator.bind(
                                        new IJavetAnonymous() {
                                            @V8Function
                                            public V8ValuePromise next() throws JavetException {
                                                try (V8ValueObject v8ValueObjectResult = v8Runtime.createV8ValueObject();
                                                     V8ValueBuiltInPromise v8ValueBuiltInPromise =
                                                             v8Runtime.getGlobalObject().getBuiltInPromise()) {
                                                    if (count.get() > 3) {
                                                        v8ValueObjectResult.set("done", true);
                                                    } else {
                                                        v8ValueObjectResult.set("value", count.getAndIncrement());
                                                        v8ValueObjectResult.set("done", false);
                                                    }
                                                    return v8ValueBuiltInPromise.resolve(v8ValueObjectResult);
                                                } catch (JavetException e) {
                                                    fail(e.getMessage());
                                                    throw e;
                                                }
                                            }
                                        }
                                );
                                return v8ValueObjectAsyncGenerator;
                            }
                        };
                        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                                anonymous, anonymous.getClass().getMethod("get"));
                        return v8Runtime.createV8ValueFunction(javetCallbackContext);
                    }
                });
                v8Runtime.getGlobalObject().set("a", v8ValueObject);
                v8Runtime.getExecutor(
                        "const x = [];\n" +
                                "let count = 0;\n" +
                                "for await (const i of a) {\n" +
                                "  x.push(i);\n" +
                                "  if (count > 3) { break; } else { ++count; }\n" +
                                "}\n" +
                                "globalThis.x = x;").setModule(true).executeVoid();
                assertEquals("[0,1,2,3]", v8Runtime.getExecutor("JSON.stringify(x);").executeString());
                v8Runtime.getGlobalObject().delete("a");
            } finally {
                v8Runtime.lowMemoryNotification();
            }
        }
    }

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

    @Test
    public void testResolverReject() throws Exception {
        try (MockFS mockFS = new MockFS(100)) {
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("fs", v8ValueObject);
                v8ValueObject.bind(mockFS);
            }
            v8Runtime.getExecutor("const logs = [1, ];").executeVoid();
            try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                    "fs.readFileAsync('a').then(fileContent => {\n" +
                            "  logs.push(3);\n" +
                            "  return fileContent;\n" +
                            "}).catch(e => {\n" +
                            "  logs.push(4);\n" +
                            "  return e;\n" +
                            "});").execute()) {
                v8Runtime.getExecutor("logs.push(2);").executeVoid();
                final int count = runAndWait(200, () -> {
                    try {
                        return v8ValuePromise.isFulfilled();
                    } catch (JavetException e) {
                        return false;
                    }
                });
                assertTrue(count > 0);
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            assertEquals("[1,2,4]", v8Runtime.getExecutor("JSON.stringify(logs);").executeString());
            v8Runtime.getGlobalObject().delete("fs");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testResolverResolve() throws Exception {
        try (MockFS mockFS = new MockFS(100)) {
            mockFS.getFileMap().put("a", "a");
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("fs", v8ValueObject);
                v8ValueObject.bind(mockFS);
            }
            v8Runtime.getExecutor("const logs = [1, ];").executeVoid();
            assertEquals("a", v8Runtime.getExecutor("fs.readFileSync('a');").executeString());
            try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                    "fs.readFileAsync('a').then(fileContent => {\n" +
                            "  logs.push(3);\n" +
                            "  return fileContent;\n" +
                            "});").execute()) {
                v8Runtime.getExecutor("logs.push(2);").executeVoid();
                final int count = runAndWait(200, () -> {
                    try {
                        return v8ValuePromise.isFulfilled();
                    } catch (JavetException e) {
                        return false;
                    }
                });
                assertTrue(count > 0);
                assertTrue(v8ValuePromise.isFulfilled());
                assertEquals("a", v8ValuePromise.getResultString());
            }
            assertEquals("[1,2,3]", v8Runtime.getExecutor("JSON.stringify(logs);").executeString());
            v8Runtime.getGlobalObject().delete("fs");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, true})
    public void testThen(boolean fulfilled) throws JavetException {
        v8Runtime.getExecutor("const result = { fulfilled: false, rejected: false }").executeVoid();
        try (V8ValueFunction functionFulfilled = v8Runtime.createV8ValueFunction("(r) => result.fulfilled = true");
             V8ValueFunction functionRejected = v8Runtime.createV8ValueFunction("(r, e) => result.rejected = true");
             V8ValuePromise v8ValuePromiseResolver = v8Runtime.createV8ValuePromise();
             V8ValuePromise v8ValuePromise1 = v8ValuePromiseResolver.then(functionFulfilled, functionRejected);
             V8ValuePromise v8ValuePromise2 = v8ValuePromiseResolver._catch("(e) => result.rejected = true");
             V8ValuePromise v8ValuePromise3 = v8ValuePromiseResolver.getPromise()) {
            v8Runtime.getGlobalObject().set("p", v8ValuePromiseResolver);
            if (fulfilled) {
                assertTrue(v8ValuePromise3.resolve(1));
            } else {
                assertTrue(v8ValuePromise3.reject(1));
            }
            v8Runtime.getExecutor("Promise.all([p,])").executeVoid();
            v8Runtime.await();
            assertEquals(fulfilled, v8Runtime.getExecutor("result.fulfilled").executeBoolean());
            assertEquals(!fulfilled, v8Runtime.getExecutor("result.rejected").executeBoolean());
        }
    }
}
