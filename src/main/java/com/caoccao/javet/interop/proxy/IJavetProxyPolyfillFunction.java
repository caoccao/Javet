/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

/**
 * The interface Javet proxy polyfill function.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 3.0.3
 */
public interface IJavetProxyPolyfillFunction<T, E extends Exception> {
    /**
     * Apply to the handle and return a V8 value.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 3.0.3
     */
    V8Value apply(IJavetProxyHandler<T, E> handler) throws JavetException, E;
}
