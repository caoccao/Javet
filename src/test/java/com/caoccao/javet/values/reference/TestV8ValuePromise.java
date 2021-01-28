package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValuePromise extends BaseTestJavetRuntime {
    @Test
    public void testPromise() throws JavetException {
        try (V8ValuePromise v8ValuePromise = v8Runtime.execute("new Promise(()=>{})")) {
            assertNotNull(v8ValuePromise);
        }
    }
}
