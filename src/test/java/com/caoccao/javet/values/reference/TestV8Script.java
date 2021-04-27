package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.executors.IV8Executor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Script extends BaseTestJavetRuntime {
    @Test
    public void testExecute() throws JavetException {
        IV8Executor iV8Executor = v8Runtime.getExecutor(
                "const a = 1; a;").setResourceName("./test.js");
        try (V8Script v8Script = iV8Executor.compileScript()) {
            assertNotNull(v8Script);
            assertEquals(1, v8Script.executeInteger());
        }
    }

    @Test
    public void testUnexpectedIdentifier() throws JavetException {
        try (V8Script v8Script = v8Runtime.getExecutor("a b c").compileScript()) {
            fail("Failed to report error.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Unexpected identifier", e.getScriptingError().getMessage());
        }
    }
}
