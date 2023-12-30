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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.mock.MockModuleResolver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Module extends BaseTestJavetRuntime {

    @Test
    public void testCachedData() throws JavetException {
        String codeString = "Object.a = 1";
        byte[] cachedData;
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            byte[] uninitializedCachedData = v8Module.getCachedData();
            assertTrue(uninitializedCachedData != null && uninitializedCachedData.length > 0);
            try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                assertTrue(v8ValuePromise.isFulfilled());
                byte[] initializedCachedData = v8Module.getCachedData();
                assertTrue(initializedCachedData != null && initializedCachedData.length > 0);
                assertArrayEquals(initializedCachedData, uninitializedCachedData);
            }
            assertEquals(1, v8Runtime.getExecutor("Object.a").executeInteger());
            cachedData = uninitializedCachedData;
        }
        v8Runtime.getExecutor("Object.a = undefined").executeVoid();
        // Cached is only accepted if the source code matches.
        iV8Executor = v8Runtime.getExecutor(codeString, cachedData).setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            byte[] uninitializedCachedData = v8Module.getCachedData();
            assertTrue(uninitializedCachedData != null && uninitializedCachedData.length > 0);
            try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                assertTrue(v8ValuePromise.isFulfilled());
                byte[] initializedCachedData = v8Module.getCachedData();
                assertTrue(initializedCachedData != null && initializedCachedData.length > 0);
                assertArrayEquals(initializedCachedData, uninitializedCachedData);
            }
            assertEquals(1, v8Runtime.getExecutor("Object.a").executeInteger());
        }
        v8Runtime.getExecutor("Object.a = undefined").executeVoid();
        iV8Executor.executeVoid();
        assertEquals(1, v8Runtime.getExecutor("Object.a").executeInteger());
    }

    @Test
    public void testExecute() throws JavetException {
        IV8Executor iV8Executor = v8Runtime.getExecutor(
                "Object.a = 1").setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            assertTrue(v8Module.isSourceTextModule());
            assertFalse(v8Module.isSyntheticModule());
            assertEquals(V8Module.Uninstantiated, v8Module.getStatus());
            assertTrue(v8Runtime.containsV8Module(v8Module.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertTrue(3 <= v8Module.getScriptId() && v8Module.getScriptId() <= 4);
            }
            assertEquals("./test.js", v8Module.getResourceName());
            try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            assertEquals(V8Module.Evaluated, v8Module.getStatus());
            assertNull(v8Module.getException());
            assertEquals(1, v8Runtime.getExecutor("Object.a").executeInteger());
            try (V8ValueObject v8ValueObject = v8Module.getNamespace()) {
                assertNotNull(v8ValueObject);
            }
        }
    }

    @Test
    public void testImportInvalidModuleInModule() throws JavetException {
        String codeString1 = "export function test1() {\n" +
                "  return { x: 1, y: 2, z: 3 };\n" +
                "}";
        String codeString2 = "import { test9 } from './module1.js';\n" +
                "export const test2 = test9;";
        String codeString3 = "import { test1 } from './module9.js';\n" +
                "export const test2 = test1;";
        String moduleName1 = "./module1.js";
        String moduleName2 = "./module2.js";
        String moduleName3 = "./module3.js";
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString1).setResourceName(moduleName1);
        try (V8Module v8Module1 = iV8Executor.compileV8Module()) {
            assertTrue(v8Module1.isSourceTextModule());
            assertFalse(v8Module1.isSyntheticModule());
            assertEquals(V8Module.Uninstantiated, v8Module1.getStatus());
            assertTrue(v8Runtime.containsV8Module(v8Module1.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertTrue(3 <= v8Module1.getScriptId() && v8Module1.getScriptId() <= 4);
            }
            assertTrue(v8Module1.instantiate());
            assertEquals(V8Module.Instantiated, v8Module1.getStatus());
            try (V8ValuePromise v8ValuePromise = v8Module1.evaluate()) {
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            iV8Executor = v8Runtime.getExecutor(codeString2).setResourceName(moduleName2);
            try (V8Module v8Module2 = iV8Executor.compileV8Module()) {
                assertTrue(v8Module2.isSourceTextModule());
                assertFalse(v8Module2.isSyntheticModule());
                assertEquals(V8Module.Uninstantiated, v8Module2.getStatus());
                assertNull(v8Module2.getException());
                assertTrue(v8Runtime.containsV8Module(v8Module2.getResourceName()));
                assertEquals(2, v8Runtime.getV8ModuleCount());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertTrue(4 <= v8Module2.getScriptId() && v8Module2.getScriptId() <= 5);
                }
                assertThrows(JavetExecutionException.class, v8Module2::instantiate, "Function is invalid");
                assertEquals(V8Module.Uninstantiated, v8Module2.getStatus());
                assertNull(v8Module2.getException());
            }
            iV8Executor = v8Runtime.getExecutor(codeString3).setResourceName(moduleName3);
            try (V8Module v8Module3 = iV8Executor.compileV8Module()) {
                assertTrue(v8Module3.isSourceTextModule());
                assertFalse(v8Module3.isSyntheticModule());
                assertEquals(V8Module.Uninstantiated, v8Module3.getStatus());
                assertNull(v8Module3.getException());
                assertTrue(v8Runtime.containsV8Module(v8Module3.getResourceName()));
                assertEquals(2, v8Runtime.getV8ModuleCount());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertTrue(5 <= v8Module3.getScriptId() && v8Module3.getScriptId() <= 6);
                }
                assertThrows(JavetExecutionException.class, v8Module3::instantiate, "Module is invalid");
                assertNull(v8Module3.getException());
            }
        }
    }

    @Test
    public void testImportValidModuleAndExecute() throws JavetException {
        String codeString = "export function test() { return { a: 1 }; };";
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            assertTrue(v8Module.isSourceTextModule());
            assertFalse(v8Module.isSyntheticModule());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                v8Runtime.getExecutor("var globalReason = null;\n" +
                        "process.on('unhandledRejection', (reason, promise) => {\n" +
                        "  globalReason = reason;\n" +
                        "});").executeVoid();
            }
            try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            // Import in module is supported.
            codeString = "import { test } from './test.js'; test();";
            iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./a.js").setModule(true);
            try (V8ValueObject v8ValueObject = iV8Executor.execute()) {
                assertNotNull(v8ValueObject);
                V8ValuePromise v8ValuePromise = (V8ValuePromise) v8ValueObject;
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertFalse(v8Runtime.containsV8Module("./a.js"));
                }
            }
            // V8: Dynamic import is not supported.
            // Node: UnhandledPromiseRejectionWarning: TypeError: Invalid host defined options.
            codeString = "const p = import('./test.js'); p;";
            iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./a.js").setModule(false);
            try (V8ValuePromise v8ValuePromise = iV8Executor.execute()) {
                assertNotNull(v8ValuePromise);
                assertTrue(v8ValuePromise.isRejected());
            }
        }
    }

    @Test
    public void testImportValidModuleInModule() throws JavetException {
        String codeString1 = "export function test1() {\n" +
                "  return { x: 1, y: 2, z: 3 };\n" +
                "}";
        String codeString2 = "import { test1 } from './module1.js';\n" +
                "export const test2 = test1;";
        String moduleName1 = "./module1.js";
        String moduleName2 = "./module2.js";
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString1).setResourceName(moduleName1);
        try (V8Module v8Module1 = iV8Executor.compileV8Module()) {
            assertTrue(v8Runtime.containsV8Module(v8Module1.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertTrue(3 <= v8Module1.getScriptId() && v8Module1.getScriptId() <= 4);
            }
            assertTrue(v8Module1.instantiate());
            try (V8ValuePromise v8ValuePromise = v8Module1.evaluate()) {
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            try (V8ValueObject v8ValueObject = v8Module1.getNamespace()) {
                assertNotNull(v8ValueObject);
                try (V8ValueFunction v8ValueFunction = v8ValueObject.get("test1")) {
                    assertEquals(codeString1.substring(7), v8ValueFunction.toString());
                }
            }
            iV8Executor = v8Runtime.getExecutor(codeString2).setResourceName(moduleName2);
            try (V8Module v8Module2 = iV8Executor.compileV8Module()) {
                assertTrue(v8Runtime.containsV8Module(v8Module2.getResourceName()));
                assertEquals(2, v8Runtime.getV8ModuleCount());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertTrue(4 <= v8Module2.getScriptId() && v8Module2.getScriptId() <= 5);
                }
                assertTrue(v8Module2.instantiate());
                try (V8ValuePromise v8ValuePromise = v8Module2.evaluate()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
                try (V8ValueObject v8ValueObject = v8Module2.getNamespace()) {
                    assertNotNull(v8ValueObject);
                    try (V8ValueFunction v8ValueFunction = v8ValueObject.get("test2")) {
                        assertEquals(codeString1.substring(7), v8ValueFunction.toString());
                    }
                }
            }
        }
    }

    @Test
    public void testInvalidModuleResolver() throws JavetException {
        assertEquals(0, v8Runtime.getV8ModuleCount());
        MockModuleResolver resolver = new MockModuleResolver("./test.js", "a b c");
        v8Runtime.setV8ModuleResolver(resolver);
        assertFalse(resolver.isCalled());
        try (V8Value v8Value = v8Runtime.getExecutor("import { test } from './test.js';")
                .setResourceName("./case.js").setModule(true).execute()) {
            fail("Failed to report SyntaxError.");
        } catch (JavetExecutionException e) {
            assertTrue(e.getCause() instanceof JavetCompilationException);
            assertEquals(
                    "Error: SyntaxError: Unexpected identifier 'b'\n" +
                            "Resource: undefined\n" +
                            "Source Code: \n" +
                            "Line Number: 0\n" +
                            "Column: -1, -1\n" +
                            "Position: -1, -1",
                    e.getScriptingError().toString());
            assertEquals(
                    "SyntaxError: Unexpected identifier 'b'\n" +
                            "Resource: ./test.js\n" +
                            "Source Code: a b c\n" +
                            "Line Number: 1\n" +
                            "Column: 2, 3\n" +
                            "Position: 2, 3",
                    ((JavetCompilationException) e.getCause()).getScriptingError().toString());
        }
        assertTrue(resolver.isCalled());
    }

    @Test
    public void testJavetBuiltInModuleResolver() throws JavetException {
        if (v8Runtime.getJSRuntimeType().isNode()) {
            v8Runtime.setV8ModuleResolver(new JavetBuiltInModuleResolver());
            v8Runtime.getExecutor(
                            "import * as fs from 'node:fs';\n" +
                                    "globalThis.a = fs.existsSync('/path-not-found');")
                    .setModule(true).executeVoid();
            assertFalse(v8Runtime.getGlobalObject().getBoolean("a"));
        }
    }

    @Test
    public void testStatusConversion() throws JavetException {
        try (V8Module v8Module = v8Runtime.getExecutor(
                "export function test() { return 1; }").setResourceName("./test.js").compileV8Module()) {
            assertTrue(v8Runtime.containsV8Module(v8Module.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertTrue(3 <= v8Module.getScriptId() && v8Module.getScriptId() <= 4);
            }
            assertNotNull(v8Module);
            assertEquals(V8Module.Uninstantiated, v8Module.getStatus());
            assertNull(v8Module.getException());
            assertTrue(v8Module.instantiate());
            assertEquals(V8Module.Instantiated, v8Module.getStatus());
            assertFalse(v8Module.instantiate());
            assertNull(v8Module.getException());
            try (V8ValuePromise v8ValuePromise = v8Module.evaluate()) {
                assertTrue(v8ValuePromise.isFulfilled());
                assertTrue(v8ValuePromise.getResult().isUndefined());
            }
            assertEquals(V8Module.Evaluated, v8Module.getStatus());
            assertNull(v8Module.getException());
        }
    }

    @Test
    public void testSyntheticModule() throws JavetException {
        if (v8Runtime.getJSRuntimeType().isNode()) {
            v8Runtime.getExecutor("process.on('unhandledRejection', (reason, promise) => {\n" +
                            "  globalThis.reason = reason.toString();\n" +
                            "});")
                    .executeVoid();
        } else {
            v8Runtime.setPromiseRejectCallback((event, promise, value) -> {
                try {
                    v8Runtime.getGlobalObject().set("reason", value.toString());
                } catch (JavetException e) {
                    fail(e);
                }
            });
        }
        v8Runtime.setV8ModuleResolver((v8Runtime, resourceName, v8ModuleReferrer) -> {
            if ("test.js".equals(resourceName)) {
                try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
                     V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
                    v8ValueObject.set("a", 1);
                    try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction("(x) => x + 1")) {
                        v8ValueObject.set("b", v8ValueFunction);
                    }
                    v8ValueObject.set("c", v8ValueArray);
                    try (V8ValueBuiltInObject v8ValueBuiltInObject = v8Runtime.getGlobalObject().getBuiltInObject()) {
                        v8ValueBuiltInObject.freeze(v8ValueObject);
                    }
                    v8ValueArray.push(1);
                    V8Module v8Module = v8Runtime.createV8Module("test.js", v8ValueObject);
                    assertFalse(v8Module.isSourceTextModule());
                    assertTrue(v8Module.isSyntheticModule());
                    return v8Module;
                }
            }
            return null;
        });
        v8Runtime.getExecutor("import * as x from 'test.js'; Object.assign(globalThis, x);")
                .setModule(true).executeVoid();
        assertEquals(1, v8Runtime.getGlobalObject().getInteger("a"));
        assertEquals(2, v8Runtime.getGlobalObject().invokeInteger("b", 1));
        assertEquals("[1]", v8Runtime.getExecutor("JSON.stringify(c);").executeString());
        v8Runtime.getExecutor("c.push(2);").executeVoid();
        v8Runtime.getExecutor("import * as x from 'test.js'; Object.assign(globalThis, x);")
                .setModule(true).executeVoid();
        assertEquals(
                "[1,2]",
                v8Runtime.getExecutor("JSON.stringify(c);").executeString(),
                "The array should be updated by the previous import.");
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                        "import * as x from 'test.js'; x['z'] = 1;")
                .setModule(true).execute()) {
            assertFalse(v8ValuePromise.isFulfilled());
        }
        v8Runtime.await();
        assertEquals(
                "TypeError: Cannot add property z, object is not extensible",
                v8Runtime.getGlobalObject().getString("reason"));
        assertEquals(1, v8Runtime.getV8ModuleCount());
    }

    @Test
    public void testUnexpectedIdentifier() throws JavetException {
        try (V8Module v8Module = v8Runtime.getExecutor(
                "a b c").setResourceName("./test.js").compileV8Module()) {
            fail("Failed to report error.");
        } catch (JavetCompilationException e) {
            assertFalse(v8Runtime.containsV8Module("./test.js"));
            assertEquals(0, v8Runtime.getV8ModuleCount());
            assertEquals("SyntaxError: Unexpected identifier 'b'", e.getScriptingError().getMessage());
        }
    }

    @Test
    public void testValidModuleResolver() throws JavetException {
        assertEquals(0, v8Runtime.getV8ModuleCount());
        MockModuleResolver resolver = new MockModuleResolver(
                "./test.js",
                "export function test() { return 1; }");
        v8Runtime.setV8ModuleResolver(resolver);
        assertFalse(resolver.isCalled());
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                        "import { test } from './test.js'; globalThis.test = test;")
                .setResourceName("./case.js").setModule(true).execute()) {
            assertEquals(1, v8Runtime.getV8ModuleCount());
            assertTrue(resolver.isCalled(), "Module resolver should be called in the first time.");
            assertTrue(v8ValuePromise.isFulfilled());
            assertEquals(1, v8Runtime.getExecutor("test()").executeInteger());
        }
        resolver.setCalled(false);
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                        "import { test } from './test.js'; globalThis.test = test;")
                .setResourceName("./case.js").setModule(true).execute()) {
            assertEquals(1, v8Runtime.getV8ModuleCount());
            assertFalse(resolver.isCalled(), "Module resolver should not be called in the second time because it is cached.");
            assertTrue(v8ValuePromise.isFulfilled());
            assertEquals(1, v8Runtime.getExecutor("test()").executeInteger());
        }
        v8Runtime.removeV8Modules(true);
        assertEquals(0, v8Runtime.getV8ModuleCount());
        try (V8ValuePromise v8ValuePromise = v8Runtime.getExecutor(
                        "import { test } from './test.js'; globalThis.test = test;")
                .setResourceName("./case.js").setModule(true).execute()) {
            assertEquals(1, v8Runtime.getV8ModuleCount());
            assertTrue(resolver.isCalled(), "Module resolver should be called after modules are cleared.");
            assertTrue(v8ValuePromise.isFulfilled());
            assertEquals(1, v8Runtime.getExecutor("test()").executeInteger());
        }
    }
}
