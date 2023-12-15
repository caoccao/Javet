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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The type Javet reflection proxy V8 value object invocation handler.
 *
 * @since 0.9.10
 */
public final class JavetReflectionProxyV8ValueObjectInvocationHandler implements InvocationHandler, IJavetClosable {
    private static final String METHOD_NAME_CLOSE = "close";
    private V8ValueObject v8ValueObject;

    /**
     * Instantiates a new Javet reflection proxy V8 value object invocation handler.
     *
     * @param v8ValueObject the V8 value object
     * @since 0.9.10
     */
    public JavetReflectionProxyV8ValueObjectInvocationHandler(V8ValueObject v8ValueObject) {
        this.v8ValueObject = v8ValueObject;
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(v8ValueObject);
        v8ValueObject = null;
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
        } else if (v8ValueObject != null && !v8ValueObject.isClosed()) {
            if (method.isAnnotationPresent(V8Function.class)) {
                String aliasMethodName = method.getAnnotation(V8Function.class).name();
                if (aliasMethodName != null && !aliasMethodName.isEmpty()) {
                    methodName = aliasMethodName;
                }
            }
            result = v8ValueObject.invokeObject(methodName, args);
        }
        return result;
    }

    @Override
    public boolean isClosed() {
        return v8ValueObject == null || v8ValueObject.isClosed();
    }
}
