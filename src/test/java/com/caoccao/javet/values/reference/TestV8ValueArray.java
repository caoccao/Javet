package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.values.V8ValueNull;
import com.caoccao.javet.values.V8ValueUndefined;
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueArray extends BaseTestJavetRuntime {
    @Test
    public void testGetValue() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.execute(
                "[1,'2',3n, true, 1.23, [4, 5, null, new Date(1611710223719)]]")) {
            assertNotNull(v8ValueArray);
            assertEquals(v8Runtime, v8ValueArray.getV8Runtime());
            assertEquals(6, v8ValueArray.getLength());
            assertEquals(1, ((V8ValueInteger) v8ValueArray.get(0)).getValue());
            assertEquals(1, v8ValueArray.getInteger(0));
            assertEquals("2", ((V8ValueString) v8ValueArray.get(1)).getValue());
            assertEquals("2", v8ValueArray.getString(1));
            assertEquals(3L, ((V8ValueLong) v8ValueArray.get(2)).getValue());
            assertEquals(3L, v8ValueArray.getLong(2));
            assertTrue(((V8ValueBoolean) v8ValueArray.get(3)).getValue());
            assertTrue(v8ValueArray.getBoolean(3));
            assertEquals(1.23, ((V8ValueDouble) v8ValueArray.get(4)).getValue(), 0.001);
            assertEquals(1.23, v8ValueArray.getDouble(4), 0.001);
            assertTrue(v8ValueArray.get(-1) instanceof V8ValueUndefined);
            assertTrue(v8ValueArray.get(100) instanceof V8ValueUndefined);
            assertEquals(
                    "1,2,3,true,1.23,4,5,,Wed Jan 27 2021 09:17:03 GMT+0800 (China Standard Time)",
                    v8ValueArray.toString());
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueArray childV8ValueArray = v8ValueArray.get(5)) {
                assertNotNull(childV8ValueArray);
                assertEquals(v8Runtime, childV8ValueArray.getV8Runtime());
                assertEquals(4, childV8ValueArray.getLength());
                assertEquals(4, childV8ValueArray.getInteger(0));
                assertEquals(5, childV8ValueArray.getInteger(1));
                assertTrue(childV8ValueArray.get(2) instanceof V8ValueNull);
                assertEquals(
                        "2021-01-27T01:17:03.719Z[UTC]",
                        childV8ValueArray.getZonedDateTime(3).withZoneSameInstant(ZoneId.of("UTC")).toString());
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }
}
