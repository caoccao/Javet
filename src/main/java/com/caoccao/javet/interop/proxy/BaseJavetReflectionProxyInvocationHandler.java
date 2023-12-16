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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueReference;

import java.lang.reflect.InvocationHandler;

/**
 * The type Base javet reflection proxy invocation handler.
 *
 * @param <Reference> the type parameter
 * @since 3.0.3
 */
public abstract class BaseJavetReflectionProxyInvocationHandler<Reference extends V8ValueReference>
        implements InvocationHandler, IJavetClosable {
    /**
     * The constant METHOD_NAME_CLOSE.
     *
     * @since 3.0.3
     */
    protected static final String METHOD_NAME_CLOSE = "close";
    /**
     * The V8 value reference.
     *
     * @since 3.0.3
     */
    protected Reference v8ValueReference;

    /**
     * Instantiates a new Base javet reflection proxy invocation handler.
     *
     * @param v8ValueReference the V8 value object
     * @since 3.0.3
     */
    public BaseJavetReflectionProxyInvocationHandler(Reference v8ValueReference) {
        this.v8ValueReference = v8ValueReference;
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(v8ValueReference);
        v8ValueReference = null;
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    @Override
    public boolean isClosed() {
        return v8ValueReference == null || v8ValueReference.isClosed();
    }
}
