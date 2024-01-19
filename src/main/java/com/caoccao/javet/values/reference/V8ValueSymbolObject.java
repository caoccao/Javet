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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The type V8 value symbol object.
 * It has never been used in V8.
 *
 * @since 0.9.11
 */
public class V8ValueSymbolObject extends V8ValueSymbol {
    /**
     * The constant FUNCTION_VALUE_OF.
     *
     * @since 0.9.11
     */
    protected static final String FUNCTION_VALUE_OF = "valueOf";

    /**
     * Instantiates a new V8 value symbol object.
     *
     * @param handle the handle
     * @since 0.9.11
     */
    V8ValueSymbolObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.SymbolObject;
    }

    /**
     * Value of V8 value symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    public V8ValueSymbol valueOf() throws JavetException {
        return invoke(FUNCTION_VALUE_OF);
    }
}
