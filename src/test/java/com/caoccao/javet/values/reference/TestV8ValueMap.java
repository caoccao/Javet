package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueMap extends BaseTestV8Value {
    @Test
    public void testMap() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.execute(
                "const a = new Map(); a.set('x', 1); a.set('y', 'b'); a.set(3, 'c'); a;")) {
            assertNotNull(v8ValueMap);
            assertEquals(3, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getValueInteger("x"));
            assertEquals("b", v8ValueMap.getValueString("y"));
            assertEquals("c", v8ValueMap.getValueString(3));
            assertTrue(v8ValueMap.containsKey("x"));
            assertTrue(v8ValueMap.containsKey(3));
            assertFalse(v8ValueMap.containsKey("p"));
            assertFalse(v8ValueMap.containsKey(0));
        }
    }
}
