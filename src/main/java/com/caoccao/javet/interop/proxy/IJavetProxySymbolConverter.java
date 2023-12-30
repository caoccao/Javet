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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;

/**
 * The interface Javet proxy symbol converter.
 *
 * @since 1.0.4
 */
public interface IJavetProxySymbolConverter {
    /**
     * Gets V8 value function.
     *
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    @CheckReturnValue
    V8ValueFunction getV8ValueFunction() throws JavetException;

    /**
     * To V8 value V8 value.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    @CheckReturnValue
    V8Value toV8Value(V8Value... v8Values) throws JavetException;
}
