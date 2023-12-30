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
import com.caoccao.javet.entities.JavetEntitySymbol;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.IV8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8ValueBuiltInObject extends BaseTestJavetRuntime {
    @Test
    public void testAssign() throws JavetException {
        String codeString = "var a = {x: 1};\n" +
                "var b = {y: 2};\n";
        v8Runtime.getExecutor(codeString).executeVoid();
        try (V8ValueObject v8ValueObjectA = v8Runtime.getGlobalObject().get("a");
             V8ValueObject v8ValueObjectB = v8Runtime.getGlobalObject().get("b")) {
            assertEquals(1, v8ValueObjectA.getInteger("x"));
            assertEquals(2, v8ValueObjectB.getInteger("y"));
            try (V8ValueBuiltInObject V8ValueBuiltInObject = v8Runtime.getGlobalObject().getBuiltInObject()) {
                try (V8ValueObject v8ValueObjectC = V8ValueBuiltInObject.assign(v8ValueObjectA, v8ValueObjectB)) {
                    assertEquals("{\"x\":1,\"y\":2}", v8ValueObjectC.toJsonString());
                }
            }
        }
    }

    @Test
    public void testGetOwnPropertySymbols() throws JavetException {
        try (V8ValueBuiltInObject v8ValueBuiltInObject = v8Runtime.getGlobalObject().getBuiltInObject();
             V8ValueObject v8ValueObject = v8Runtime.getExecutor("const a = {};" +
                     "a[Symbol.for('test')] = 1;" +
                     "a;").execute()) {
            try (IV8ValueArray v8ValueArray = v8ValueBuiltInObject.getOwnPropertySymbols(v8ValueObject)) {
                assertEquals(1, v8ValueArray.getLength());
                assertEquals("test", ((JavetEntitySymbol) v8ValueArray.getObject(0)).getDescription());
            }
        }
    }
}
