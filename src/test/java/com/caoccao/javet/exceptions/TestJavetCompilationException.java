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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Script;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetCompilationException extends BaseTestJavetRuntime {
    @Test
    public void testInvalidImport() throws JavetException {
        String codeString = "import { Decimal } from 'decimal.js'\n" +
                "\n" +
                "const a = new Decimal(123.45);\n" +
                "console.log(a);\n" +
                "a;";
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor(codeString).execute()) {
            assertNotNull(v8ValueObject);
        } catch (JavetCompilationException e) {
            assertEquals(JavetError.CompilationFailure, e.getError());
            assertEquals(
                    "SyntaxError: Cannot use import statement outside a module",
                    e.getScriptingError().getMessage());
        }
    }

    @Test
    public void testInvalidOrUnexpectedToken() {
        try (V8Value v8Value = v8Runtime.getExecutor("1a2b").execute()) {
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals(JavetError.CompilationFailure, e.getError());
            assertEquals("SyntaxError: Invalid or unexpected token", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("SyntaxError: Invalid or unexpected token", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("1a2b", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(0, javetScriptingError.getStartColumn());
            assertEquals(1, javetScriptingError.getEndColumn());
            assertEquals(0, javetScriptingError.getStartPosition());
            assertEquals(1, javetScriptingError.getEndPosition());
            assertEquals(
                    "SyntaxError: Invalid or unexpected token\n" +
                            "Resource: undefined\n" +
                            "Source Code: 1a2b\n" +
                            "Line Number: 1\n" +
                            "Column: 0, 1\n" +
                            "Position: 0, 1",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetCompilationException should be thrown.");
        }
    }

    @Test
    public void testUnexpectedIdentifier() {
        try (V8Script v8Script = v8Runtime.getExecutor("const a = 1;\na a a a;").compileV8Script()) {
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals(JavetError.CompilationFailure, e.getError());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("SyntaxError: Unexpected identifier 'a'", e.getMessage());
            assertEquals("SyntaxError: Unexpected identifier 'a'", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("a a a a;", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(2, javetScriptingError.getStartColumn());
            assertEquals(3, javetScriptingError.getEndColumn());
            assertEquals(15, javetScriptingError.getStartPosition());
            assertEquals(16, javetScriptingError.getEndPosition());
            assertEquals(
                    "SyntaxError: Unexpected identifier 'a'\n" +
                            "Resource: undefined\n" +
                            "Source Code: a a a a;\n" +
                            "Line Number: 2\n" +
                            "Column: 2, 3\n" +
                            "Position: 15, 16",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetCompilationException should be thrown.");
        }
    }

    @Test
    public void testUnexpectedToken() {
        try (V8Value v8Value = v8Runtime.getExecutor("const a = 1;\na ==== 2;").execute()) {
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals(JavetError.CompilationFailure, e.getError());
            assertEquals("SyntaxError: Unexpected token '='", e.getMessage());
            JavetScriptingError javetScriptingError = e.getScriptingError();
            assertEquals("SyntaxError: Unexpected token '='", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("a ==== 2;", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(5, javetScriptingError.getStartColumn());
            assertEquals(6, javetScriptingError.getEndColumn());
            assertEquals(18, javetScriptingError.getStartPosition());
            assertEquals(19, javetScriptingError.getEndPosition());
            assertEquals(
                    "SyntaxError: Unexpected token '='\n" +
                            "Resource: undefined\n" +
                            "Source Code: a ==== 2;\n" +
                            "Line Number: 2\n" +
                            "Column: 5, 6\n" +
                            "Position: 18, 19",
                    javetScriptingError.toString());
        } catch (JavetException e) {
            fail("JavetCompilationException should be thrown.");
        }
    }
}
