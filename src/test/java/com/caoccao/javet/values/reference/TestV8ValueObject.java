package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.values.V8ValueNull;
import com.caoccao.javet.values.V8ValueUndefined;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueObject extends BaseTestJavetRuntime {
    @Test
    public void testGetOwnPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;")) {
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(7, iV8ValueCollection.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueCollection.getPropertyString(0));
                assertEquals("b", iV8ValueCollection.getPropertyString(1));
                assertEquals("c", iV8ValueCollection.getPropertyString(2));
                assertEquals("d", iV8ValueCollection.getPropertyString(3));
                assertEquals("e", iV8ValueCollection.getPropertyString(4));
                assertEquals("g", iV8ValueCollection.getPropertyString(5));
                assertEquals("中文", iV8ValueCollection.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;")) {
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(7, iV8ValueCollection.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueCollection.getPropertyString(0));
                assertEquals("b", iV8ValueCollection.getPropertyString(1));
                assertEquals("c", iV8ValueCollection.getPropertyString(2));
                assertEquals("d", iV8ValueCollection.getPropertyString(3));
                assertEquals("e", iV8ValueCollection.getPropertyString(4));
                assertEquals("g", iV8ValueCollection.getPropertyString(5));
                assertEquals("中文", iV8ValueCollection.getPropertyString(6));
            }
        }
    }

    @Test
    public void testGetValue() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1, 3: 'x'}, '中文': '測試'};"
                        + "x['i'] = true;x['j'] = 1.23;x['k'] = new Date(1611710223719);"
                        + "x;")) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            assertEquals(1, ((V8ValueInteger) v8ValueObject.getProperty("a")).getValue());
            assertEquals(1, v8ValueObject.getPropertyInteger("a"));
            assertEquals("2", ((V8ValueString) v8ValueObject.getProperty("b")).getValue());
            assertEquals("2", v8ValueObject.getPropertyString("b"));
            assertEquals(3L, ((V8ValueLong) v8ValueObject.getProperty("c")).getValue());
            assertEquals(3L, v8ValueObject.getPropertyLong("c"));
            assertEquals(1, v8ValueObject.getPropertyInteger("d"));
            assertTrue(v8ValueObject.getProperty("e") instanceof V8ValueNull);
            assertEquals("測試", v8ValueObject.getPropertyString("中文"));
            assertTrue(v8ValueObject.getProperty("$") instanceof V8ValueUndefined);
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject childV8ValueObject = v8ValueObject.getProperty("g")) {
                assertNotNull(childV8ValueObject);
                assertEquals(v8Runtime, childV8ValueObject.getV8Runtime());
                assertEquals(1, childV8ValueObject.getPropertyInteger("h"));
                assertEquals("x", childV8ValueObject.getPropertyString(3));
                assertTrue(childV8ValueObject.hasOwnProperty("h"));
                assertTrue(childV8ValueObject.hasOwnProperty(3));
                assertFalse(childV8ValueObject.hasOwnProperty("p"));
                assertFalse(childV8ValueObject.hasOwnProperty(1));
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertTrue(v8ValueObject.getPropertyBoolean("i"));
            assertEquals(1.23, v8ValueObject.getPropertyDouble("j"), 0.001);
            assertEquals(
                    "2021-01-27T01:17:03.719Z[UTC]",
                    v8ValueObject.getPropertyZonedDateTime("k").withZoneSameInstant(ZoneId.of("UTC")).toString());
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }
}
