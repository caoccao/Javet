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
import com.caoccao.javet.interop.proxy.JavetUniversalProxyHandler;
import com.caoccao.javet.mock.MockPojo;
import com.caoccao.javet.mock.MockPojoWithGenericGetterAndSetter;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;

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

    @Test
    public void testUniversalInterceptionProxyHandler() throws JavetException {
        JavetUniversalProxyHandler<MockPojo> handler =
                new JavetUniversalProxyHandler<>(v8Runtime, new MockPojo());
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {a:1,b:2}; x;").execute()) {
            try (V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueObject)) {
                assertNotNull(v8ValueProxy);
                assertFalse(v8ValueProxy.isRevoked());
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    iV8ValueObjectHandler.bind(handler);
                }
                v8Runtime.getGlobalObject().set("y", v8ValueProxy);
                for (String methodName : ("add setStringValue getStringValue sSSStringValue ssSStringValue " +
                        "sssStringValue stringValue").split("\\s+")) {
                    assertTrue(v8ValueProxy.has(methodName),
                            MessageFormat.format("{0} should be found", methodName));
                }
                for (String methodName : "subtract ssStringValue a b".split("\\s+")) {
                    assertFalse(v8ValueProxy.has(methodName),
                            MessageFormat.format("{0} should not be found", methodName));
                }
            }
            assertEquals(3, v8Runtime.getExecutor("y.add(1,2)").executeInteger(),
                    "Parameters with primitive type should work.");
            assertEquals(2, v8Runtime.getExecutor("y.add(1)").executeInteger(),
                    "Parameters with varargs should work.");
            assertEquals(12, v8Runtime.getExecutor("y.add(1,2,3)").executeInteger(),
                    "Parameters with varargs should work.");
            assertEquals(3.3, v8Runtime.getExecutor("y.add(1.1,2.2)").executeDouble(), 0.001,
                    "Parameters with non-primitive type should work.");
            assertTrue(v8Runtime.getExecutor("y['a']").execute().isUndefined(),
                    "Generic getter should return undefined.");
            v8Runtime.getExecutor("y['name'] = 'abc';").executeVoid();
            assertEquals("abc", handler.getTargetObject().getName(), "Getter should work.");
            assertEquals("abc", v8Runtime.getExecutor("y['name']").executeString(),
                    "Getter should work.");
            assertEquals("abc", v8Runtime.getExecutor("y.getName()").executeString(),
                    "Getter should work.");
            v8Runtime.getExecutor("y['sssStringValue'] = 'abc';").executeVoid();
            assertEquals("abc", handler.getTargetObject().getSSSStringValue(), "Getter should work.");
            assertEquals("abc", v8Runtime.getExecutor("y['ssSStringValue']").executeString(),
                    "Getter should work.");
            assertEquals("abc", v8Runtime.getExecutor("y.concat('abc')").executeString(),
                    "Parameters with varargs should work.");
            assertEquals("abc, def", v8Runtime.getExecutor("y.concat('abc', 'def')").executeString(),
                    "Parameters with varargs should work.");
            assertEquals(
                    "abc, def, null, null",
                    v8Runtime.getExecutor("y.concat('abc', 'def', null, undefined)").executeString(),
                    "Parameters with varargs should work.");
            assertEquals(3, v8Runtime.getExecutor("y.staticAdd(1,2)").executeInteger(),
                    "Static function should work.");
            v8Runtime.getGlobalObject().delete("y");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testUniversalInterceptionProxyHandlerWithGenericGetterAndSetter() throws JavetException {
        JavetUniversalProxyHandler<MockPojoWithGenericGetterAndSetter> handler =
                new JavetUniversalProxyHandler<>(v8Runtime, new MockPojoWithGenericGetterAndSetter());
        handler.getTargetObject().set("c", "3");
        handler.getTargetObject().set("d", "4");
        try (V8ValueObject v8ValueObject = v8Runtime.getExecutor("const x = {a:1,b:2}; x;").execute()) {
            try (V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueObject)) {
                assertNotNull(v8ValueProxy);
                assertFalse(v8ValueProxy.isRevoked());
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    iV8ValueObjectHandler.bind(handler);
                }
                v8Runtime.getGlobalObject().set("y", v8ValueProxy);
            }
            assertEquals("3", v8Runtime.getExecutor("y['c']").executeString(),
                    "Generic getter should work.");
            assertTrue(v8Runtime.getExecutor("y['a']").execute().isUndefined(),
                    "Generic getter should return undefined.");
            v8Runtime.getExecutor("y['name'] = 'abc';").executeVoid();
            assertNull(handler.getTargetObject().getName(), "Generic getter should take higher priority.");
            assertEquals("abc", handler.getTargetObject().get("name"), "Getter should work.");
            assertEquals("abc", v8Runtime.getExecutor("y['name']").executeString(),
                    "Getter should work.");
            v8Runtime.getGlobalObject().delete("y");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }
}
