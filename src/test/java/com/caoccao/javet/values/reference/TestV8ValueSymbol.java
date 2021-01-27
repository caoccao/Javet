package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueSymbol extends BaseTestV8Value {
    @Test
    public void testSymbol() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol = v8Runtime.execute("Symbol('test')")) {
            assertNotNull(v8ValueSymbol);
            assertEquals("test", v8ValueSymbol.getDescription());
        }
    }
}
