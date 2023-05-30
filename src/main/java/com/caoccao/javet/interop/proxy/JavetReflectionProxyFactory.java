/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * The type Javet reflection proxy factory.
 *
 * @since 2.0.1
 */
public final class JavetReflectionProxyFactory {
    private static final JavetReflectionProxyFactory instance = new JavetReflectionProxyFactory();

    private JavetReflectionProxyFactory() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static JavetReflectionProxyFactory getInstance() {
        return instance;
    }

    /**
     * Is reflection proxied function conversion supported.
     *
     * @param type    the type to be converted
     * @param v8Value the V8 value
     * @return true : supported, false: not supported
     * @since 2.0.1
     */
    public boolean isSupportedFunction(Class<?> type, V8Value v8Value) {
        return type.isInterface() && v8Value instanceof V8ValueFunction;
    }

    /**
     * Is reflection proxied object conversion supported.
     *
     * @param type    the type to be converted
     * @param v8Value the V8 value
     * @return true : supported, false: not supported
     * @since 2.0.1
     */
    public boolean isSupportedObject(Class<?> type, V8Value v8Value) {
        return type.isInterface()
                && v8Value instanceof V8ValueObject
                && (!(v8Value instanceof V8ValueProxy))
                && (!(v8Value instanceof V8ValueFunction));
    }

    /**
     * Convert from V8 value to a reflection proxied object.
     *
     * @param type    the type to be converted
     * @param v8Value the V8 value
     * @return the object
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    public Object toObject(Class<?> type, V8Value v8Value) throws JavetException {
        if (type.isInterface()) {
            InvocationHandler invocationHandler = null;
            if (v8Value instanceof V8ValueFunction) {
                invocationHandler = new JavetReflectionProxyV8ValueFunctionInvocationHandler(v8Value.toClone());
            } else if (v8Value instanceof V8ValueObject && !(v8Value instanceof V8ValueProxy)) {
                invocationHandler = new JavetReflectionProxyV8ValueObjectInvocationHandler(v8Value.toClone());
            }
            if (invocationHandler != null) {
                return Proxy.newProxyInstance(
                        getClass().getClassLoader(),
                        new Class[]{type, AutoCloseable.class},
                        invocationHandler);
            }
        }
        return null;
    }
}
