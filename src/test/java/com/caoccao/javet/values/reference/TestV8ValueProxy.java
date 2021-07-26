/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueProxy extends BaseTestJavetRuntime {
    @Test
    public void testEmptyHandler() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {a:1,b:2}; x;").execute()) {
            try (V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueObject)) {
                assertNotNull(v8ValueProxy);
                assertFalse(v8ValueProxy.isRevoked());
                assertEquals(1, v8ValueProxy.getInteger("a"));
                assertEquals(2, v8ValueProxy.getInteger("b"));
                assertTrue(v8ValueProxy.get("c").isUndefined());
                v8ValueObject.set("c", 3);
                assertEquals(3, v8ValueProxy.getInteger("c"));
                v8ValueProxy.revoke();
                assertTrue(v8ValueProxy.isRevoked());
            }
        }
    }

    @Test
    public void testEmptyTargetAndEmptyHandler() throws JavetException {
        try (V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy()) {
            assertNotNull(v8ValueProxy);
            assertFalse(v8ValueProxy.isRevoked());
            try (IV8ValueObject iV8ValueObject = v8ValueProxy.getTarget()) {
                try (IV8ValueArray iV8ValueArray = iV8ValueObject.getOwnPropertyNames()) {
                    assertEquals(0, iV8ValueArray.getLength());
                }
                iV8ValueObject.set("a", 1);
            }
            assertEquals("{\"a\":1}", v8ValueProxy.toJsonString());
        }
    }

    @Test
    public void testHandlerGet() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("var x = {a:1,b:2}; x;").execute()) {
            try (V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueObject)) {
                assertNotNull(v8ValueProxy);
                assertFalse(v8ValueProxy.isRevoked());
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    iV8ValueObjectHandler.bindFunction("get", "(target, prop, receiver) => {" +
                            "  if (prop in target) {" +
                            "    return target[prop] + 1;" +
                            "  }" +
                            "  return undefined;" +
                            "};");
                }
                assertEquals(2, v8ValueProxy.getInteger("a"));
                assertEquals(3, v8ValueProxy.getInteger("b"));
                v8ValueProxy.revoke();
                assertTrue(v8ValueProxy.isRevoked());
                assertTrue(v8ValueProxy.get("a").isUndefined());
                assertTrue(v8ValueProxy.get("b").isUndefined());
            }
        }
    }

    @Test
    public void testNativeProxyCreation() throws JavetException {
        try (V8ValueProxy v8ValueProxy = v8Runtime.getExecutor(
                "const b = {}; const a = new Proxy(RegExp, b); a;").execute()) {
            assertNotNull(v8ValueProxy);
            assertFalse(v8ValueProxy.isRevoked());
        }
    }
}
