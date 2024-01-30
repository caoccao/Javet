/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueBuiltInJson extends BaseTestJavetRuntime {
    @Test
    public void testCircularStructure() throws JavetException {
        String codeString = "const a = {x: 1};\n" +
                "var b = {y: a, z: 2};\n" +
                "a.x = b;";
        v8Runtime.getExecutor(codeString).executeVoid();
        try (V8ValueObject v8ValueObject = v8Runtime.getGlobalObject().get("b")) {
            assertEquals(2, v8ValueObject.getInteger("z"));
            try (V8ValueBuiltInJson v8ValueBuiltInJson = v8Runtime.getGlobalObject().getBuiltInJson()) {
                v8ValueBuiltInJson.stringify(v8ValueObject);
            } catch (JavetExecutionException e) {
                assertEquals(
                        "TypeError: Converting circular structure to JSON\n" +
                                "    --> starting at object with constructor 'Object'\n" +
                                "    |     property 'y' -> object with constructor 'Object'\n" +
                                "    --- property 'x' closes the circle",
                        e.getMessage());
                assertEquals(
                        "Converting circular structure to JSON\n" +
                                "    --> starting at object with constructor 'Object'\n" +
                                "    |     property 'y' -> object with constructor 'Object'\n" +
                                "    --- property 'x' closes the circle",
                        e.getScriptingError().getMessage());
                assertEquals(
                        "TypeError: Converting circular structure to JSON\n" +
                                "    --> starting at object with constructor 'Object'\n" +
                                "    |     property 'y' -> object with constructor 'Object'\n" +
                                "    --- property 'x' closes the circle\n" +
                                "    at JSON.stringify (<anonymous>)",
                        e.getScriptingError().getStack());
            }
        }
    }

    @Test
    public void testConversion() throws JavetException {
        Object jsonObject = v8Runtime.toObject(v8Runtime.getGlobalObject().getBuiltInJson(), true);
        assertInstanceOf(Map.class, jsonObject);
        assertTrue(((Map<?, ?>) jsonObject).isEmpty());
    }
}
