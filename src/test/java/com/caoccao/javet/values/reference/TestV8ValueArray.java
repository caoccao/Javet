package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueArray extends BaseTestV8Value {
    @Test
    public void testArray() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.execute("[1,2,3]")) {
            assertNotNull(v8ValueArray);
            assertEquals(v8Runtime, v8ValueArray.getV8Runtime());
            assertEquals(3, v8ValueArray.getLength());
        }
    }
}
