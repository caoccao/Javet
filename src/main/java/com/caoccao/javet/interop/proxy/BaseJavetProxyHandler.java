/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;

/**
 * The type Base javet proxy handler.
 *
 * @param <T> the type parameter
 * @since 2.2.0
 */
public abstract class BaseJavetProxyHandler<T>
        implements IJavetProxyHandler<T>, IJavetDirectCallable {
    /**
     * The constant FUNCTION_NAME_TO_V8_VALUE.
     *
     * @since 1.0.4
     */
    protected static final String FUNCTION_NAME_TO_V8_VALUE = "toV8Value";
    /**
     * The Callback contexts.
     *
     * @since 2.2.0
     */
    protected JavetCallbackContext[] callbackContexts;
    /**
     * The V8 runtime.
     *
     * @since 0.9.6
     */
    protected V8Runtime v8Runtime;

    public BaseJavetProxyHandler() {
        callbackContexts = null;
        v8Runtime = null;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }
}
