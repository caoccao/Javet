/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

import com.caoccao.javet.values.reference.V8ValueFunction;

import java.lang.reflect.Method;

/**
 * The type Javet reflection proxy V8 value function invocation handler.
 *
 * @since 0.9.10
 */
public final class JavetReflectionProxyV8ValueFunctionInvocationHandler
        extends BaseJavetReflectionProxyInvocationHandler<V8ValueFunction> {
    /**
     * Instantiates a new Javet reflection proxy V8 value function invocation handler.
     *
     * @param v8ValueFunction the V8 value function
     * @since 0.9.10
     */
    public JavetReflectionProxyV8ValueFunctionInvocationHandler(V8ValueFunction v8ValueFunction) {
        super(v8ValueFunction);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (args == null) {
            args = new Object[0];
        }
        String methodName = method.getName();
        if (methodName.equals(METHOD_NAME_CLOSE) && args.length == 0) {
            close();
        } else if (v8ValueReference != null && !v8ValueReference.isClosed()) {
            result = v8ValueReference.callObject(null, args);
        }
        return result;
    }
}
