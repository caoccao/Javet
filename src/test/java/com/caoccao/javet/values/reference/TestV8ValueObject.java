package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.BaseTestV8Value;
import com.caoccao.javet.values.V8ValueNull;
import com.caoccao.javet.values.V8ValueUndefined;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueObject extends BaseTestV8Value {
    @Test
    public void testGetOwnPropertyNames() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'}; x;")) {
            try (IV8ValueCollection iV8ValueCollection = v8ValueObject.getOwnPropertyNames()) {
                assertNotNull(iV8ValueCollection);
                assertEquals(7, iV8ValueCollection.getLength());
                // Order is preserved since ES2015.
                assertEquals("a", iV8ValueCollection.getValueString(0));
                assertEquals("b", iV8ValueCollection.getValueString(1));
                assertEquals("c", iV8ValueCollection.getValueString(2));
                assertEquals("d", iV8ValueCollection.getValueString(3));
                assertEquals("e", iV8ValueCollection.getValueString(4));
                assertEquals("g", iV8ValueCollection.getValueString(5));
                assertEquals("中文", iV8ValueCollection.getValueString(6));
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
                assertEquals("a", iV8ValueCollection.getValueString(0));
                assertEquals("b", iV8ValueCollection.getValueString(1));
                assertEquals("c", iV8ValueCollection.getValueString(2));
                assertEquals("d", iV8ValueCollection.getValueString(3));
                assertEquals("e", iV8ValueCollection.getValueString(4));
                assertEquals("g", iV8ValueCollection.getValueString(5));
                assertEquals("中文", iV8ValueCollection.getValueString(6));
            }
        }
    }

    @Test
    public void testGetValue() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.execute(
                "let x = {'a': 1, 'b': '2', 'c': 3n, d: 1, e: null, g: {h: 1}, '中文': '測試'};"
                        + "x['i'] = true;x['j'] = 1.23;x['k'] = new Date(1611710223719);"
                        + "x;")) {
            assertNotNull(v8ValueObject);
            assertEquals(v8Runtime, v8ValueObject.getV8Runtime());
            assertEquals(1, ((V8ValueInteger) v8ValueObject.getValue("a")).getValue());
            assertEquals(1, v8ValueObject.getValueInteger("a"));
            assertEquals("2", ((V8ValueString) v8ValueObject.getValue("b")).getValue());
            assertEquals("2", v8ValueObject.getValueString("b"));
            assertEquals(3L, ((V8ValueLong) v8ValueObject.getValue("c")).getValue());
            assertEquals(3L, v8ValueObject.getValueLong("c"));
            assertEquals(1, v8ValueObject.getValueInteger("d"));
            assertTrue(v8ValueObject.getValue("e") instanceof V8ValueNull);
            assertEquals("測試", v8ValueObject.getValueString("中文"));
            assertTrue(v8ValueObject.getValue("$") instanceof V8ValueUndefined);
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueObject childV8ValueObject = v8ValueObject.getValue("g")) {
                assertNotNull(childV8ValueObject);
                assertEquals(v8Runtime, childV8ValueObject.getV8Runtime());
                assertEquals(1, childV8ValueObject.getValueInteger("h"));
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertTrue(v8ValueObject.getValueBoolean("i"));
            assertEquals(1.23, v8ValueObject.getValueDouble("j"), 0.001);
            assertEquals(
                    "2021-01-27T01:17:03.719Z[UTC]",
                    v8ValueObject.getValueZonedDateTime("k").withZoneSameInstant(ZoneId.of("UTC")).toString());
            assertEquals(1, v8Runtime.getReferenceCount());
        }
    }
}
