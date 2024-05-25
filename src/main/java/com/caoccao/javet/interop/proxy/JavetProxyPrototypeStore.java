/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueGlobalObject;

import java.util.Objects;

/**
 * The type Javet proxy prototype store manages the prototype objects for proxy objects.
 *
 * @since 3.1.3
 */
public final class JavetProxyPrototypeStore {
    /**
     * The constant DUMMY_FUNCTION_STRING contains a JS code snippet
     * which creates an empty anonymous function hidden in a closure.
     * The purpose is to avoid polluting the context because a
     * non-anonymous function can possibly conflict with a variable
     * with the same name in the same context.
     *
     * @since 3.1.3
     */
    public static final String DUMMY_FUNCTION_STRING =
            "(() => {\n" +
                    "  const DummyFunction = function () { };\n" +
                    "  return DummyFunction;\n" +
                    "})();";

    private static final String PREFIX = ".proxy.prototype.";

    private JavetProxyPrototypeStore() {
    }

    /**
     * Create or get prototype.
     *
     * @param v8Runtime   the V8 runtime
     * @param v8ProxyMode the V8 proxy mode
     * @param clazz       the clazz
     * @return the prototype
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    public static V8Value createOrGetPrototype(
            V8Runtime v8Runtime, V8ProxyMode v8ProxyMode, Class<?> clazz)
            throws JavetException {
        String key = v8ProxyMode.name() + PREFIX + Objects.requireNonNull(clazz).getName();
        V8ValueGlobalObject globalObject = Objects.requireNonNull(v8Runtime).getGlobalObject();
        if (globalObject.hasPrivateProperty(key)) {
            return globalObject.getPrivateProperty(key);
        }
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8Value v8Value;
            switch (v8ProxyMode) {
                case Class:
                case Function:
                    v8Value = v8Scope.createV8ValueFunction(DUMMY_FUNCTION_STRING);
                    break;
                default:
                    v8Value = v8Scope.createV8ValueObject();
                    break;
            }
            globalObject.setPrivateProperty(key, v8Value);
            v8Scope.setEscapable();
            return v8Value;
        }
    }

    /**
     * Gets prototype.
     *
     * @param v8Runtime   the V8 runtime
     * @param v8ProxyMode the V8 proxy mode
     * @param clazz       the clazz
     * @return the prototype
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    public static V8Value getPrototype(
            V8Runtime v8Runtime, V8ProxyMode v8ProxyMode, Class<?> clazz)
            throws JavetException {
        String key = v8ProxyMode.name() + PREFIX + Objects.requireNonNull(clazz).getName();
        V8ValueGlobalObject globalObject = Objects.requireNonNull(v8Runtime).getGlobalObject();
        return globalObject.hasPrivateProperty(key) ? globalObject.getPrivateProperty(key) : null;
    }
}
