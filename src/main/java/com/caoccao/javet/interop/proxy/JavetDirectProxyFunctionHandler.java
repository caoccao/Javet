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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

/**
 * The type Javet direct proxy object handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 2.2.0
 */
public class JavetDirectProxyFunctionHandler<T extends IJavetDirectProxyHandler<E>, E extends Exception>
        extends JavetDirectProxyObjectHandler<T, E> {
    /**
     * Instantiates a new Base javet direct proxy object handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 2.2.0
     */
    public JavetDirectProxyFunctionHandler(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
    }

    @Override
    public V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException, E {
        return targetObject.proxyApply(target, thisObject, arguments);
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        return new JavetCallbackContext[]{
                new JavetCallbackContext(
                        PROXY_FUNCTION_NAME_APPLY, this, JavetCallbackType.DirectCallNoThisAndResult,
                        (NoThisAndResult<E>) (v8Values) -> apply(v8Values[0], v8Values[1], (V8ValueArray) v8Values[2])),
                new JavetCallbackContext(
                        PROXY_FUNCTION_NAME_GET, this, JavetCallbackType.DirectCallNoThisAndResult,
                        (NoThisAndResult<E>) (v8Values) -> get(v8Values[0], v8Values[1], v8Values[2])),
                new JavetCallbackContext(
                        PROXY_FUNCTION_NAME_HAS, this, JavetCallbackType.DirectCallNoThisAndResult,
                        (NoThisAndResult<E>) (v8Values) -> has(v8Values[0], v8Values[1])),
                new JavetCallbackContext(
                        PROXY_FUNCTION_NAME_OWN_KEYS, this, JavetCallbackType.DirectCallNoThisAndResult,
                        (NoThisAndResult<E>) (v8Values) -> ownKeys(v8Values[0])),
                new JavetCallbackContext(
                        PROXY_FUNCTION_NAME_SET, this, JavetCallbackType.DirectCallNoThisAndResult,
                        (NoThisAndResult<E>) (v8Values) -> set(v8Values[0], v8Values[1], v8Values[2], v8Values[3])),
        };
    }
}
