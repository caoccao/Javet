package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueSymbol extends BaseTestJavetRuntime {
    @Test
    public void testSymbol() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol = v8Runtime.execute("Symbol('test')")) {
            assertNotNull(v8ValueSymbol);
            assertEquals("test", v8ValueSymbol.getDescription());
            assertEquals("Symbol(test)", v8ValueSymbol.toString());
        }
        try (V8ValueSymbol v8ValueSymbol = v8Runtime.execute("Symbol(123)")) {
            assertNotNull(v8ValueSymbol);
            assertEquals("123", v8ValueSymbol.getDescription());
            assertEquals("Symbol(123)", v8ValueSymbol.toString());
        }
    }
}
