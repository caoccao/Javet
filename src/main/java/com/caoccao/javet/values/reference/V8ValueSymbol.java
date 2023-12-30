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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The type V8 value symbol.
 *
 * @since 0.9.11
 */
public class V8ValueSymbol extends V8ValueObject {

    /**
     * The constant FUNCTION_TO_STRING.
     *
     * @since 0.9.11
     */
    protected static final String FUNCTION_TO_STRING = "toString";
    /**
     * The constant PROPERTY_DESCRIPTION.
     *
     * @since 0.9.11
     */
    protected static final String PROPERTY_DESCRIPTION = "description";

    /**
     * Instantiates a new V8 value symbol.
     *
     * @param handle the handle
     * @since 0.9.11
     */
    V8ValueSymbol(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Gets description.
     *
     * @return the description
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    public String getDescription() throws JavetException {
        return getPropertyString(PROPERTY_DESCRIPTION);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Symbol;
    }

    @Override
    public String toString() {
        try {
            return invokeString(FUNCTION_TO_STRING);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
