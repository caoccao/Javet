package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestV8ValueBuiltInJson extends BaseTestJavetRuntime {
    @Test
    public void testCircularStructure() throws JavetException {
        String codeString = "const a = {x: 1};\n" +
                "var b = {y: a, z: 2};\n" +
                "a.x = b;";
        v8Runtime.getExecutor(codeString).executeVoid();
        try (V8ValueObject v8ValueObject = v8Runtime.getGlobalObject().get("b")) {
            assertEquals(2, v8ValueObject.getInteger("z"));
            try (V8ValueBuiltInJson v8ValueBuiltInJson = v8Runtime.getGlobalObject().getJson()) {
                v8ValueBuiltInJson.stringify(v8ValueObject);
            } catch (JavetExecutionException e) {
                assertEquals(
                        "TypeError: Converting circular structure to JSON\n" +
                                "    --> starting at object with constructor 'Object'\n" +
                                "    |     property 'y' -> object with constructor 'Object'\n" +
                                "    --- property 'x' closes the circle",
                        e.getMessage());
            }
            assertThrows(StackOverflowError.class, () -> {
                Object b = v8Runtime.toObject(v8ValueObject);
            });
        }
    }
}
