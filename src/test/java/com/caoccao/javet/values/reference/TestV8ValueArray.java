package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import com.caoccao.javet.values.V8ValueNull;
import com.caoccao.javet.values.V8ValueUndefined;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueArray extends BaseTestV8Value {
    @Test
    public void testGetValue() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.execute(
                "[1,'2',3n, true, [4, 5, null]]")) {
            assertNotNull(v8ValueArray);
            assertEquals(v8Runtime, v8ValueArray.getV8Runtime());
            assertEquals(5, v8ValueArray.getLength());
            assertEquals(1, ((V8ValueInteger) v8ValueArray.getValue(0)).getValue());
            assertEquals(1, v8ValueArray.getValueInteger(0));
            assertEquals("2", ((V8ValueString) v8ValueArray.getValue(1)).getValue());
            assertEquals("2", v8ValueArray.getValueString(1));
            assertEquals(3L, ((V8ValueLong) v8ValueArray.getValue(2)).getValue());
            assertEquals(3L, v8ValueArray.getValueLong(2));
            assertTrue(((V8ValueBoolean) v8ValueArray.getValue(3)).getValue());
            assertTrue(v8ValueArray.getValueBoolean(3));
            assertTrue(v8ValueArray.getValue(-1) instanceof V8ValueUndefined);
            assertTrue(v8ValueArray.getValue(100) instanceof V8ValueUndefined);
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueArray childV8ValueArray = v8ValueArray.getValue(4)) {
                assertNotNull(childV8ValueArray);
                assertEquals(v8Runtime, childV8ValueArray.getV8Runtime());
                assertEquals(3, childV8ValueArray.getLength());
                assertEquals(4, childV8ValueArray.getValueInteger(0));
                assertEquals(5, childV8ValueArray.getValueInteger(1));
                assertTrue(childV8ValueArray.getValue(2) instanceof V8ValueNull);
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }
}
