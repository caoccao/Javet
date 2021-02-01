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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestJavetCompilationException extends BaseTestJavetRuntime {
    @Test
    public void testUnexpectedIdentifier() {
        try {
            v8Runtime.compileOnly("const a = 1;\na a a a;");
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Unexpected identifier", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("SyntaxError: Unexpected identifier", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("a a a a;", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(2, javetScriptingError.getStartColumn());
            assertEquals(3, javetScriptingError.getEndColumn());
            assertEquals(15, javetScriptingError.getStartPosition());
            assertEquals(16, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: SyntaxError: Unexpected identifier\n" +
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
        try (V8Value v8Value = v8Runtime.execute("const a = 1;\na ==== 2;")) {
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Unexpected token '='", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("SyntaxError: Unexpected token '='", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("a ==== 2;", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(5, javetScriptingError.getStartColumn());
            assertEquals(6, javetScriptingError.getEndColumn());
            assertEquals(18, javetScriptingError.getStartPosition());
            assertEquals(19, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: SyntaxError: Unexpected token '='\n" +
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

    @Test
    public void testInvalidOrUnexpectedToken() {
        try (V8Value v8Value = v8Runtime.execute("1a2b")) {
            fail("Exception should be thrown.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Invalid or unexpected token", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("SyntaxError: Invalid or unexpected token", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("1a2b", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(0, javetScriptingError.getStartColumn());
            assertEquals(1, javetScriptingError.getEndColumn());
            assertEquals(0, javetScriptingError.getStartPosition());
            assertEquals(1, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: SyntaxError: Invalid or unexpected token\n" +
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
}
