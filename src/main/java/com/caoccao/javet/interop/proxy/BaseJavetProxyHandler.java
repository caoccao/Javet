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

import com.caoccao.javet.interop.V8Runtime;

import java.util.Objects;

/**
 * The type Base javet proxy handler.
 *
 * @param <T> the type parameter
 * @since 0.9.6
 */
public abstract class BaseJavetProxyHandler<T> implements IJavetProxyHandler<T> {
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
     * @since 0.9.6
     */
    public BaseJavetProxyHandler(V8Runtime v8Runtime, T targetObject) {
        this.targetObject = targetObject;
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
    }

    @Override
    public T getTargetObject() {
        return targetObject;
    }

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 0.9.6
     */
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }
}
