/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.executors.IV8Executor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Module extends BaseTestJavetRuntime {
    @Test
    public void testExecute() throws JavetException {
        IV8Executor iV8Executor = v8Runtime.getExecutor(
                "Object.a = 1").setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            assertTrue(v8Runtime.containsV8Module(v8Module.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertEquals(4, v8Module.getScriptId());
            }
            assertEquals("./test.js", v8Module.getResourceName());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
            } else {
                assertEquals(1, v8Module.executeInteger());
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
    public void testImportValidModuleAndExecute() throws JavetException {
        String codeString = "export function test() { return { a: 1 }; };";
        IV8Executor iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./test.js");
        try (V8Module v8Module = iV8Executor.compileV8Module()) {
            if (v8Runtime.getJSRuntimeType().isNode()) {
                v8Runtime.getExecutor("const process = require('process');\n" +
                        "var globalReason = null;\n" +
                        "process.on('unhandledRejection', (reason, promise) => {\n" +
                        "  globalReason = reason;\n" +
                        "});").executeVoid();
                try (V8ValuePromise v8ValuePromise = v8Module.execute()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
            } else {
                assertTrue(v8Module.execute().isUndefined());
            }
            // Import in module is supported.
            codeString = "import { test } from './test.js'; test();";
            iV8Executor = v8Runtime.getExecutor(codeString).setResourceName("./a.js").setModule(true);
            try (V8ValueObject v8ValueObject = iV8Executor.execute()) {
                assertNotNull(v8ValueObject);
                if (v8Runtime.getJSRuntimeType().isNode()) {
                    V8ValuePromise v8ValuePromise = (V8ValuePromise) v8ValueObject;
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                } else {
                    assertEquals(1, v8ValueObject.getInteger("a"));
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
            assertTrue(v8Runtime.containsV8Module(v8Module1.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertEquals(4, v8Module1.getScriptId());
            }
            assertTrue(v8Module1.instantiate());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                try (V8ValuePromise v8ValuePromise = v8Module1.evaluate()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
            } else {
                assertTrue(v8Module1.evaluate().isUndefined());
            }
            iV8Executor = v8Runtime.getExecutor(codeString2).setResourceName(moduleName2);
            try (V8Module v8Module2 = iV8Executor.compileV8Module()) {
                assertTrue(v8Runtime.containsV8Module(v8Module2.getResourceName()));
                assertEquals(2, v8Runtime.getV8ModuleCount());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertEquals(5, v8Module2.getScriptId());
                }
                assertFalse(v8Module2.instantiate(), "Function is invalid");
                assertNull(v8Module2.getException());
            }
            iV8Executor = v8Runtime.getExecutor(codeString3).setResourceName(moduleName3);
            try (V8Module v8Module3 = iV8Executor.compileV8Module()) {
                assertTrue(v8Runtime.containsV8Module(v8Module3.getResourceName()));
                assertEquals(2, v8Runtime.getV8ModuleCount());
                if (v8Runtime.getJSRuntimeType().isV8()) {
                    assertEquals(6, v8Module3.getScriptId());
                }
                assertFalse(v8Module3.instantiate(), "Module is invalid");
                assertNull(v8Module3.getException());
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
                assertEquals(4, v8Module1.getScriptId());
            }
            assertTrue(v8Module1.instantiate());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                try (V8ValuePromise v8ValuePromise = v8Module1.evaluate()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
            } else {
                assertTrue(v8Module1.evaluate().isUndefined());
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
                    assertEquals(5, v8Module2.getScriptId());
                }
                assertTrue(v8Module2.instantiate());
                if (v8Runtime.getJSRuntimeType().isNode()) {
                    try (V8ValuePromise v8ValuePromise = v8Module2.evaluate()) {
                        assertTrue(v8ValuePromise.isFulfilled());
                        assertTrue(v8ValuePromise.getResult().isUndefined());
                    }
                } else {
                    assertTrue(v8Module2.evaluate().isUndefined());
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
    public void testStatusConversion() throws JavetException {
        try (V8Module v8Module = v8Runtime.getExecutor(
                "export function test() { return 1; }").setResourceName("./test.js").compileV8Module()) {
            assertTrue(v8Runtime.containsV8Module(v8Module.getResourceName()));
            assertEquals(1, v8Runtime.getV8ModuleCount());
            if (v8Runtime.getJSRuntimeType().isV8()) {
                assertEquals(4, v8Module.getScriptId());
            }
            assertNotNull(v8Module);
            assertEquals(V8Module.Uninstantiated, v8Module.getStatus());
            assertNull(v8Module.getException());
            assertTrue(v8Module.instantiate());
            assertEquals(V8Module.Instantiated, v8Module.getStatus());
            assertFalse(v8Module.instantiate());
            assertNull(v8Module.getException());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                try (V8ValuePromise v8ValuePromise = v8Module.evaluate()) {
                    assertTrue(v8ValuePromise.isFulfilled());
                    assertTrue(v8ValuePromise.getResult().isUndefined());
                }
            } else {
                assertTrue(v8Module.evaluate().isUndefined());
            }
            assertEquals(V8Module.Evaluated, v8Module.getStatus());
            assertNull(v8Module.getException());
        }
    }

    @Test
    public void testUnexpectedIdentifier() throws JavetException {
        try (V8Module v8Module = v8Runtime.getExecutor(
                "a b c").setResourceName("./test.js").compileV8Module()) {
            fail("Failed to report error.");
        } catch (JavetCompilationException e) {
            assertFalse(v8Runtime.containsV8Module("./test.js"));
            assertEquals(0, v8Runtime.getV8ModuleCount());
            assertEquals("SyntaxError: Unexpected identifier", e.getScriptingError().getMessage());
        }
    }
}
