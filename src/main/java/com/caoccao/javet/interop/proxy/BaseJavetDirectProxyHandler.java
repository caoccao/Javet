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

/**
 * The type Base javet direct proxy handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 2.2.0
 */
public abstract class BaseJavetDirectProxyHandler<T extends IJavetDirectProxyHandler<E>, E extends Exception>
        extends BaseJavetProxyHandler<T, E> {
    /**
     * Instantiates a new Base javet direct proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 2.2.0
     */
    public BaseJavetDirectProxyHandler(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
        targetObject.setV8Runtime(v8Runtime);
    }
}
