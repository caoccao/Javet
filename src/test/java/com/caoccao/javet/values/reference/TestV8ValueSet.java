package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueSet extends BaseTestJavetRuntime {
    @Test
    public void testSet() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.execute(
                "const a = new Set(); a.add('x', 1); a.add('y', 'b'); a.add(3, 'c'); a;")) {
            assertNotNull(v8ValueSet);
            assertEquals(3, v8ValueSet.getSize());
            assertTrue(v8ValueSet.containsKey("x"));
            assertTrue(v8ValueSet.containsKey("y"));
            assertTrue(v8ValueSet.containsKey(3));
            assertFalse(v8ValueSet.containsKey("p"));
            assertFalse(v8ValueSet.containsKey(0));
            assertEquals("[object Set]", v8ValueSet.toString());
        }
    }
}
