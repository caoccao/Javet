/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

import java.util.Objects;

/**
 * The type Base javet proxy handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 2.2.0
 */
public abstract class BaseJavetProxyHandler<T, E extends Exception>
        implements IJavetProxyHandler<T, E>, IJavetDirectCallable {
    /**
     * The Callback contexts.
     *
     * @since 2.2.0
     */
    protected JavetCallbackContext[] callbackContexts;
    /**
     * The Target object.
     *
     * @since 0.9.6
     */
    protected T targetObject;
    /**
     * The V8 runtime.
     *
     * @since 0.9.6
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new Base javet proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 2.2.0
     */
    public BaseJavetProxyHandler(V8Runtime v8Runtime, T targetObject) {
        callbackContexts = null;
        this.targetObject = Objects.requireNonNull(targetObject);
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
    }

    @Override
    public T getTargetObject() {
        return targetObject;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }
}
