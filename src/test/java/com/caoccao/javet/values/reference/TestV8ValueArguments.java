package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueArguments extends BaseTestJavetRuntime {
    @Test
    public void testArguments() throws JavetException {
        try (V8ValueArguments v8ValueArguments = v8Runtime.execute(
                "const a = function(a, b) { return arguments; }; a(1, '2')")) {
            assertNotNull(v8ValueArguments);
            assertEquals(1, v8ValueArguments.getInteger(0));
            assertEquals("2", v8ValueArguments.getString(1));
        }
    }
}
