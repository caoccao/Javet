/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The type Dynamic proxy V8 value function invocation handler.
 *
 * @since 0.9.10
 */
public class DynamicProxyV8ValueFunctionInvocationHandler implements InvocationHandler, IJavetClosable {
    private static final String METHOD_NAME_CLOSE = "close";
    private V8ValueFunction v8ValueFunction;

    /**
     * Instantiates a new Dynamic proxy V8 value function invocation handler.
     *
     * @param v8ValueFunction the V8 value function
     * @since 0.9.10
     */
    public DynamicProxyV8ValueFunctionInvocationHandler(V8ValueFunction v8ValueFunction) {
        this.v8ValueFunction = v8ValueFunction;
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(v8ValueFunction);
        v8ValueFunction = null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (args == null) {
            args = new Object[0];
        }
        if (method.getName().equals(METHOD_NAME_CLOSE) && args.length == 0) {
            close();
        } else if (v8ValueFunction != null && !v8ValueFunction.isClosed()) {
            result = v8ValueFunction.callObject(null, args);
        }
        return result;
    }

    @Override
    public boolean isClosed() {
        return v8ValueFunction == null || v8ValueFunction.isClosed();
    }
}
