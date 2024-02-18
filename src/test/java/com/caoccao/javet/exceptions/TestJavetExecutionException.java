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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.interfaces.IJavetEntityError;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetExecutionException extends BaseTestJavetRuntime {
    @Test
    public void testAssignmentToConstantVariable() {
        try (V8Value v8Value = v8Runtime.getExecutor("const a = 1; a = 2;").execute()) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals("TypeError: Assignment to constant variable.", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("TypeError: Assignment to constant variable.", javetScriptingError.getDetailedMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("const a = 1; a = 2;", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(15, javetScriptingError.getStartColumn());
            assertEquals(16, javetScriptingError.getEndColumn());
            assertEquals(15, javetScriptingError.getStartPosition());
            assertEquals(16, javetScriptingError.getEndPosition());
            assertEquals(
                    "TypeError: Assignment to constant variable.\n" +
                            "Resource: undefined\n" +
                            "Source Code: const a = 1; a = 2;\n" +
                            "Line Number: 1\n" +
                            "Column: 15, 16\n" +
                            "Position: 15, 16",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetExecutionException should be thrown.");
        }
    }

    @Test
    public void testCustomError() throws JavetException {
        String codeString = "throw Object.assign(new Error('Custom Error.'), { a: 1, b: 2 })";
        try {
            v8Runtime.getExecutor(codeString).executeVoid();
            fail("Failed to capture the custom error.");
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals("Error: Custom Error.", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("Custom Error.", javetScriptingError.getMessage());
            assertEquals("Error: Custom Error.", javetScriptingError.getDetailedMessage());
            assertEquals(codeString, javetScriptingError.getSourceLine());
            assertEquals(
                    "Error: Custom Error.\n" +
                            "Resource: undefined\n" +
                            "Source Code: throw Object.assign(new Error('Custom Error.'), { a: 1, b: 2 })\n" +
                            "Line Number: 1\n" +
                            "Column: 0, 1\n" +
                            "Position: 0, 1",
                    javetScriptingError.toString());
            assertEquals(
                    SimpleMap.of("a", 1, "b", 2),
                    ((IJavetEntityError) javetScriptingError.getContext()).getContext());
        }
    }

    @Test
    public void testInvalidExports() throws JavetException {
        String codeString = "Object.defineProperty(exports, \"__esModule\", { value: true });\n" +
                "const decimal_js_1 = require(\"decimal.js\");\n" +
                "const a = new decimal_js_1.Decimal(123.45);\n" +
                "console.log(a);\n" +
                "a;";
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(codeString).execute()) {
            assertNotNull(v8ValueObject);
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals(
                    "ReferenceError: exports is not defined",
                    e.getScriptingError().getDetailedMessage());
        }
    }

    @Test
    public void testInvalidRequire() throws JavetException {
        String codeString = "const decimal_js_1 = require(\"decimal.js\");\n" +
                "const a = new decimal_js_1.Decimal(123.45);\n" +
                "console.log(a);\n" +
                "a;";
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(codeString).execute()) {
            assertNotNull(v8ValueObject);
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            if (v8Runtime.getJSRuntimeType().isNode()) {
                assertTrue(e.getScriptingError().getDetailedMessage().startsWith("Error: Cannot find module 'decimal.js'"));
            } else {
                assertEquals(
                        "ReferenceError: require is not defined",
                        e.getScriptingError().getDetailedMessage());
            }
        }
    }

    @Test
    public void testNotAFunction() {
        try (V8Value v8Value = v8Runtime.getExecutor("const a = 1;\nObject.getPPP(a);").execute()) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals("TypeError: Object.getPPP is not a function", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("TypeError: Object.getPPP is not a function", javetScriptingError.getDetailedMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("Object.getPPP(a);", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(7, javetScriptingError.getStartColumn());
            assertEquals(8, javetScriptingError.getEndColumn());
            assertEquals(20, javetScriptingError.getStartPosition());
            assertEquals(21, javetScriptingError.getEndPosition());
            assertEquals(
                    "TypeError: Object.getPPP is not a function\n" +
                            "Resource: undefined\n" +
                            "Source Code: Object.getPPP(a);\n" +
                            "Line Number: 2\n" +
                            "Column: 7, 8\n" +
                            "Position: 20, 21",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetExecutionException should be thrown.");
        }
    }

    @Test
    public void testNotDefined() {
        try (V8Value v8Value = v8Runtime.getExecutor("Symbol(abc);").execute()) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals(JavetError.ExecutionFailure, e.getError());
            assertEquals("ReferenceError: abc is not defined", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("ReferenceError: abc is not defined", javetScriptingError.getDetailedMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("Symbol(abc);", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(7, javetScriptingError.getStartColumn());
            assertEquals(8, javetScriptingError.getEndColumn());
            assertEquals(7, javetScriptingError.getStartPosition());
            assertEquals(8, javetScriptingError.getEndPosition());
            assertEquals(
                    "ReferenceError: abc is not defined\n" +
                            "Resource: undefined\n" +
                            "Source Code: Symbol(abc);\n" +
                            "Line Number: 1\n" +
                            "Column: 7, 8\n" +
                            "Position: 7, 8",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetExecutionException should be thrown.");
        }
    }
}
