package com.caoccao.javet.exceptions;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestJavetCompilationException extends BaseTestJavetRuntime {
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
