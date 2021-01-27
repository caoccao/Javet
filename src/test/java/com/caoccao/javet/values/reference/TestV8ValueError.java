package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueError extends BaseTestV8Value {
    @Test
    public void testError() throws JavetException {
        try (V8ValueError v8ValueError = v8Runtime.execute("Error('test')")) {
            assertNotNull(v8ValueError);
            assertEquals("test", v8ValueError.getMessage());
            assertEquals("Error: test\n    at <anonymous>:1:1", v8ValueError.getStack());
            try (IV8ValueCollection iV8ValueCollection = v8ValueError.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(0, iV8ValueCollection.getLength());
            }
        }
    }
}
