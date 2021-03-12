package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.executors.IV8Executor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8DataModule extends BaseTestJavetRuntime {

    @Test
    public void testExecute() throws JavetException {
        IV8Executor iV8Executor = v8Runtime.getExecutor(
                "export function test() { return 1; }");
        iV8Executor.getV8ScriptOrigin().setResourceName("./test.js");
        try (V8DataModule v8DataModule = iV8Executor.compileModule(true)) {
            assertEquals(4, v8DataModule.getScriptId());
            assertEquals("./test.js", v8DataModule.getResourceName());
            try (V8ValuePromise v8ValuePromise = v8DataModule.execute()) {
                assertNotNull(v8ValuePromise);
            }
            assertEquals(V8DataModule.Evaluated, v8DataModule.getStatus());
            assertNull(v8DataModule.getException());
        }
    }

    @Test
    public void testStatusConversion() throws JavetException {
        try (V8DataModule v8DataModule = v8Runtime.getExecutor(
                "export function test() { return 1; }").compileModule(true)) {
            assertEquals(4, v8DataModule.getScriptId());
            assertNotNull(v8DataModule);
            assertEquals(V8DataModule.Uninstantiated, v8DataModule.getStatus());
            assertNull(v8DataModule.getException());
            assertTrue(v8DataModule.instantiate());
            assertEquals(V8DataModule.Instantiated, v8DataModule.getStatus());
            assertFalse(v8DataModule.instantiate());
            assertNull(v8DataModule.getException());
            try (V8ValuePromise v8ValuePromise = v8DataModule.evaluate()) {
                assertNotNull(v8ValuePromise);
            }
            assertEquals(V8DataModule.Evaluated, v8DataModule.getStatus());
            assertNull(v8DataModule.getException());
        }
    }

    @Test
    public void testUnexpectedIdentifier() throws JavetException {
        try (V8DataModule v8DataModule = v8Runtime.getExecutor(
                "a b c").compileModule(true)) {
            fail("Failed to report error.");
        } catch (JavetCompilationException e) {
            assertEquals("SyntaxError: Unexpected identifier", e.getError().getMessage());
        }
    }
}
