package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueProxy extends BaseTestJavetRuntime {
    @Test
    public void testProxy() throws JavetException {
        try (V8ValueProxy v8ValueProxy = v8Runtime.execute("const b = {}; const a = new Proxy(RegExp, b); a;")) {
            assertNotNull(v8ValueProxy);
        }
    }
}
