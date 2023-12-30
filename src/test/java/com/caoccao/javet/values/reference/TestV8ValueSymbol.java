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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.entities.JavetEntitySymbol;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInObject;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueSymbol extends BaseTestJavetRuntime {
    @Test
    public void testGetAndSet() throws JavetException {
        try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol();
             V8ValueBuiltInObject v8ValueBuiltInObject = v8Runtime.getGlobalObject().getBuiltInObject();
             V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            assertEquals("{}", v8ValueObject.toJsonString());
            try (V8ValueSymbol v8ValueSymbolA = v8ValueBuiltInSymbol._for("a");
                 V8ValueSymbol v8ValueSymbolB = v8ValueBuiltInSymbol._for("b")) {
                assertTrue(v8ValueObject.get(v8ValueSymbolA).isUndefined());
                assertTrue(v8ValueObject.get(v8ValueSymbolB).isUndefined());
                v8ValueObject.set(v8ValueSymbolA, "aaa");
                v8ValueObject.set(v8ValueSymbolB, "bbb");
                assertEquals("aaa", v8ValueObject.getString(v8ValueSymbolA));
                assertEquals("bbb", v8ValueObject.getString(v8ValueSymbolB));
            }
            assertEquals("aaa", v8ValueObject.getString(new JavetEntitySymbol("a")));
            assertEquals("bbb", v8ValueObject.getString(new JavetEntitySymbol("b")));
            assertEquals("{}", v8ValueObject.toJsonString());
            try (IV8ValueArray v8ValueArray = v8ValueBuiltInObject.getOwnPropertySymbols(v8ValueObject)) {
                assertEquals(2, v8ValueArray.getLength());
                assertEquals("a", ((JavetEntitySymbol) v8ValueArray.getObject(0)).getDescription());
                assertEquals("b", ((JavetEntitySymbol) v8ValueArray.getObject(1)).getDescription());
            }
        }
    }

    @Test
    public void testGlobalSymbol() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol1 = v8Runtime.createV8ValueSymbol("test", true);
             V8ValueSymbol v8ValueSymbol2 = v8Runtime.createV8ValueSymbol("test", true);
             V8ValueSymbol v8ValueSymbol3 = v8Runtime.createV8ValueSymbol("test")) {
            assertNotNull(v8ValueSymbol1);
            assertNotNull(v8ValueSymbol2);
            assertNotNull(v8ValueSymbol3);
            assertTrue(v8ValueSymbol1.sameValue(v8ValueSymbol2));
            assertFalse(v8ValueSymbol1.sameValue(v8ValueSymbol3));
            assertEquals("test", v8ValueSymbol1.getDescription());
            assertEquals("test", v8ValueSymbol2.getDescription());
            assertEquals("Symbol(test)", v8ValueSymbol1.toString());
            assertEquals("Symbol(test)", v8ValueSymbol2.toString());
        }
    }

    @Test
    public void testLocalSymbol() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol1 = v8Runtime.createV8ValueSymbol("test");
             V8ValueSymbol v8ValueSymbol2 = v8Runtime.createV8ValueSymbol("test")) {
            assertNotNull(v8ValueSymbol1);
            assertNotNull(v8ValueSymbol2);
            assertFalse(v8ValueSymbol1.sameValue(v8ValueSymbol2));
            assertEquals("test", v8ValueSymbol1.getDescription());
            assertEquals("test", v8ValueSymbol2.getDescription());
            assertEquals("Symbol(test)", v8ValueSymbol1.toString());
            assertEquals("Symbol(test)", v8ValueSymbol2.toString());
        }
    }

    @Test
    public void testNativeNumber() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol = v8Runtime.getExecutor("Symbol(123)").execute()) {
            assertNotNull(v8ValueSymbol);
            assertEquals("123", v8ValueSymbol.getDescription());
            assertEquals("Symbol(123)", v8ValueSymbol.toString());
        }
    }

    @Test
    public void testNativeString() throws JavetException {
        try (V8ValueSymbol v8ValueSymbol = v8Runtime.getExecutor("Symbol('test')").execute()) {
            assertNotNull(v8ValueSymbol);
            assertEquals("test", v8ValueSymbol.getDescription());
            assertEquals("Symbol(test)", v8ValueSymbol.toString());
        }
    }
}
