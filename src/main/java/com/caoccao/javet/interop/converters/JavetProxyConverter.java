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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.proxy.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueProxy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Javet proxy converter converts most of Java objects to
 * JS objects via JS proxy bi-directionally.
 * <p>
 * Java Primitive types, Array, List, Set and Map are converted to the
 * corresponding types in JS. Set and Map conversion can be disabled
 * via config.
 *
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetProxyConverter extends JavetObjectConverter {
    /**
     * The constant DUMMY_FUNCTION_STRING contains a JS code snippet
     * which creates an empty anonymous function hidden in a closure.
     * The purpose is to avoid polluting the context because a
     * non-anonymous function can possibly conflict with a variable
     * with the same name in the same context.
     *
     * @since 0.9.8
     */
    protected static final String DUMMY_FUNCTION_STRING =
            "(() => {\n" +
                    "  const DummyFunction = function () { };\n" +
                    "  return DummyFunction;\n" +
                    "})();";

    /**
     * Instantiates a new Javet proxy converter.
     *
     * @since 0.9.6
     */
    public JavetProxyConverter() {
        super();
    }

    /**
     * To proxied V8 value.
     *
     * @param <T>       the type parameter
     * @param v8Runtime the V8 runtime
     * @param object    the object
     * @return the proxied V8 value
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    protected <T extends V8Value> T toProxiedV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        V8Value v8Value;
        V8ProxyMode proxyMode = V8ProxyMode.Object;
        if (object instanceof Class) {
            if (V8ProxyMode.isClassMode((Class<?>) object)) {
                proxyMode = V8ProxyMode.Class;
            }
        } else if (object.getClass().isAnnotationPresent(V8Convert.class)) {
            V8Convert v8Convert = object.getClass().getAnnotation(V8Convert.class);
            if (v8Convert.proxyMode() == V8ProxyMode.Function) {
                proxyMode = V8ProxyMode.Function;
            }
        }
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueProxy v8ValueProxy;
            switch (proxyMode) {
                case Class:
                case Function:
                    try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(DUMMY_FUNCTION_STRING)) {
                        v8ValueProxy = v8Scope.createV8ValueProxy(v8ValueFunction);
                    }
                    break;
                default:
                    v8ValueProxy = v8Scope.createV8ValueProxy();
                    break;
            }
            try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                IJavetProxyHandler<?, ?> javetProxyHandler;
                switch (proxyMode) {
                    case Class:
                        javetProxyHandler = new JavetReflectionProxyClassHandler<>(
                                v8Runtime, config.getReflectionObjectFactory(), (Class<?>) object);
                        break;
                    case Function:
                        if (object instanceof IJavetDirectProxyHandler<?>) {
                            javetProxyHandler = new JavetDirectProxyFunctionHandler<>(
                                    v8Runtime, (IJavetDirectProxyHandler<?>) object);
                        } else {
                            javetProxyHandler = new JavetReflectionProxyFunctionHandler<>(
                                    v8Runtime, config.getReflectionObjectFactory(), object);
                        }
                        break;
                    default:
                        if (object instanceof IJavetDirectProxyHandler<?>) {
                            javetProxyHandler = new JavetDirectProxyObjectHandler<>(
                                    v8Runtime, (IJavetDirectProxyHandler<?>) object);
                        } else {
                            javetProxyHandler = new JavetReflectionProxyObjectHandler<>(
                                    v8Runtime, config.getReflectionObjectFactory(), object);
                        }
                        break;
                }
                List<JavetCallbackContext> javetCallbackContexts = iV8ValueObjectHandler.bind(javetProxyHandler);
                try (V8ValueLong v8ValueLongHandle = v8Runtime.createV8ValueLong(
                        javetCallbackContexts.get(0).getHandle())) {
                    iV8ValueObjectHandler.setPrivateProperty(PRIVATE_PROPERTY_PROXY_TARGET, v8ValueLongHandle);
                }
            }
            v8Value = v8ValueProxy;
            v8Scope.setEscapable();
        }
        return (T) v8Value;
    }

    @Override
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        if (object instanceof V8Value) {
            return (T) object;
        }
        boolean proxyRequired = config.isProxyMapEnabled() && object instanceof Map;
        proxyRequired = proxyRequired || (config.isProxySetEnabled() && object instanceof Set);
        if (!proxyRequired) {
            V8Value v8Value = super.toV8Value(v8Runtime, object, depth);
            if (v8Value != null && !(v8Value.isUndefined())) {
                return (T) v8Value;
            }
        }
        return toProxiedV8Value(v8Runtime, object);
    }
}
