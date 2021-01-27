package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueArguments extends BaseTestV8Value {
    @Test
    public void testArguments() throws JavetException {
        try (V8ValueArguments v8ValueArguments = v8Runtime.execute(
                "const a = function(a, b) { return arguments; }; a(1, '2')")) {
            assertNotNull(v8ValueArguments);
            assertEquals(1, v8ValueArguments.getValueInteger(0));
            assertEquals("2", v8ValueArguments.getValueString(1));
        }
    }
}
