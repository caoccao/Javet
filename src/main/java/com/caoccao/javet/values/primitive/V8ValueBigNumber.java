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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The type V8 value big number.
 *
 * @param <Primitive> the type parameter
 * @since 3.0.4
 */
public abstract class V8ValueBigNumber<Primitive> extends V8ValuePrimitive<Primitive> {
    /**
     * Instantiates a new V8 value big number.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBigNumber(V8Runtime v8Runtime) throws JavetException {
        super(v8Runtime);
    }

    /**
     * Instantiates a new V8 value big number.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBigNumber(V8Runtime v8Runtime, Primitive value) throws JavetException {
        super(v8Runtime, value);
    }
}
