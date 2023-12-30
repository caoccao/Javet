/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interception;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.IV8ValueObject;

/**
 * The type Base javet interceptor.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetInterceptor implements IJavetInterceptor {
    /**
     * The V8 runtime.
     *
     * @since 0.7.0
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new Base javet interceptor.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.7.0
     */
    public BaseJavetInterceptor(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 0.7.0
     */
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public abstract boolean register(IV8ValueObject... iV8ValueObjects) throws JavetException;

    @Override
    public abstract boolean unregister(IV8ValueObject... iV8ValueObjects) throws JavetException;
}
