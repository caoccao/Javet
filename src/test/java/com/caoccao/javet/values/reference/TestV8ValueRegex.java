package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueRegex extends BaseTestJavetRuntime {
    @Test
    public void testRegex() throws JavetException {
        try (V8ValueRegex v8ValueRegex = v8Runtime.execute("/123/g")) {
            assertNotNull(v8ValueRegex);
        }
        try (V8ValueRegex v8ValueRegex = v8Runtime.execute("new RegExp('123')")) {
            assertNotNull(v8ValueRegex);
        }
    }
}
