package com.caoccao.javet.exceptions;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestJavetExecutionException extends BaseTestJavetRuntime {
    @Test
    public void testAssignmentToConstantVariable() {
        try (V8Value v8Value = v8Runtime.execute("const a = 1; a = 2;")) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals("TypeError: Assignment to constant variable.", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("TypeError: Assignment to constant variable.", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("const a = 1; a = 2;", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(15, javetScriptingError.getStartColumn());
            assertEquals(16, javetScriptingError.getEndColumn());
            assertEquals(15, javetScriptingError.getStartPosition());
            assertEquals(16, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: TypeError: Assignment to constant variable.\n" +
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
    public void testNotAFunction() {
        try (V8Value v8Value = v8Runtime.execute("const a = 1;\nObject.getPPP(a);")) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals("TypeError: Object.getPPP is not a function", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("TypeError: Object.getPPP is not a function", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("Object.getPPP(a);", javetScriptingError.getSourceLine());
            assertEquals(2, javetScriptingError.getLineNumber());
            assertEquals(7, javetScriptingError.getStartColumn());
            assertEquals(8, javetScriptingError.getEndColumn());
            assertEquals(20, javetScriptingError.getStartPosition());
            assertEquals(21, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: TypeError: Object.getPPP is not a function\n" +
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
        try (V8Value v8Value = v8Runtime.execute("Symbol(abc);")) {
            fail("Exception should be thrown.");
        } catch (JavetExecutionException e) {
            assertEquals("ReferenceError: abc is not defined", e.getMessage());
            JavetScriptingError javetScriptingError = e.getError();
            assertEquals("ReferenceError: abc is not defined", javetScriptingError.getMessage());
            assertEquals("undefined", javetScriptingError.getResourceName());
            assertEquals("Symbol(abc);", javetScriptingError.getSourceLine());
            assertEquals(1, javetScriptingError.getLineNumber());
            assertEquals(7, javetScriptingError.getStartColumn());
            assertEquals(8, javetScriptingError.getEndColumn());
            assertEquals(7, javetScriptingError.getStartPosition());
            assertEquals(8, javetScriptingError.getEndPosition());
            assertEquals(
                    "Error: ReferenceError: abc is not defined\n" +
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
