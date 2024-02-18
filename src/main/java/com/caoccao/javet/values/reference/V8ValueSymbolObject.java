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
import com.caoccao.javet.values.IV8ValuePrimitiveObject;

/**
 * The type V8 value symbol object.
 * It has never been used in V8.
 *
 * @since 0.9.11
 */
public class V8ValueSymbolObject
        extends V8ValueObject
        implements IV8ValuePrimitiveObject<V8ValueSymbol> {
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

    @CheckReturnValue
    @Override
    public V8ValueSymbol valueOf() throws JavetException {
        return checkV8Runtime().getV8Internal().symbolObjectValueOf(this);
    }
}
