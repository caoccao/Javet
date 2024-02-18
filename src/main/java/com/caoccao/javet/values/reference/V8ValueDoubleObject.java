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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNumber;

/**
 * The type V8 value double object.
 *
 * @since 3.0.4
 */
public class V8ValueDoubleObject
        extends V8ValueObject
        implements IV8ValuePrimitiveObject<V8Value> {
    /**
     * Instantiates a new V8 value double object.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueDoubleObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.DoubleObject;
    }

    /**
     * Convert to integer object.
     *
     * @return the V8 value integer object
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueIntegerObject toIntegerObject() throws JavetException {
        return new V8ValueIntegerObject(v8Runtime, handle);
    }

    @CheckReturnValue
    @Override
    public V8ValueNumber<?> valueOf() throws JavetException {
        return checkV8Runtime().getV8Internal().doubleObjectValueOf(this);
    }
}
