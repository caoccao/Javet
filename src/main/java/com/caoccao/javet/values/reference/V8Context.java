/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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
import com.caoccao.javet.enums.V8ContextType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

/**
 * The type V8 context.
 *
 * @since 2.0.1
 */
@SuppressWarnings("unchecked")
public class V8Context extends V8ValueReference implements IV8Context {

    /**
     * The constant ERROR_ELEMENT_LENGTH_MUST_BE_NON_NEGATIVE.
     *
     * @since 2.0.1
     */
    protected static final String ERROR_ELEMENT_LENGTH_MUST_BE_NON_NEGATIVE = "Element length must be non-negative.";

    /**
     * Instantiates a new V8 context.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    V8Context(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T get(int index) throws JavetException {
        return checkV8Runtime().getV8Internal().contextGet(this, index);
    }

    @Override
    public int getLength() throws JavetException {
        return checkV8Runtime().getV8Internal().contextGetLength(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Context;
    }

    @Override
    public boolean isContextType(V8ContextType v8ContextType) throws JavetException {
        return checkV8Runtime().getV8Internal().contextIsContextType(this, v8ContextType.getId());
    }

    @Override
    public boolean setLength(int length) throws JavetException {
        assert length >= 0 : ERROR_ELEMENT_LENGTH_MUST_BE_NON_NEGATIVE;
        return checkV8Runtime().getV8Internal().contextSetLength(this, length);
    }

    @Override
    public V8Context toClone(boolean referenceCopy) throws JavetException {
        return this;
    }
}
