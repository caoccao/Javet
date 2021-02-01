package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueMap extends BaseTestJavetRuntime {
    @Test
    public void testGetAndHas() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.execute(
                "const a = new Map(); a.set('x', 1); a.set('y', 'b'); a.set(3, 'c'); a;")) {
            assertNotNull(v8ValueMap);
            assertEquals(3, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getInteger("x"));
            assertEquals("b", v8ValueMap.getString("y"));
            assertEquals("c", v8ValueMap.getString(3));
            assertTrue(v8ValueMap.has("x"));
            assertTrue(v8ValueMap.has(3));
            assertFalse(v8ValueMap.has("p"));
            assertFalse(v8ValueMap.has(0));
            assertEquals("[object Map]", v8ValueMap.toString());
            try (IV8ValueCollection iV8ValueCollection = v8ValueMap.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(0, iV8ValueCollection.getLength());
            }
        }
    }

    @Test
    public void testGetAndSet() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.execute("const a = new Map(); a;")) {
            v8ValueMap.set("a", new V8ValueInteger(1));
            v8ValueMap.set("b", new V8ValueString("2"));
            assertEquals(2, v8ValueMap.getSize());
            assertEquals(1, v8ValueMap.getInteger("a"));
            assertEquals("2", v8ValueMap.getString("b"));
        }
    }
}
