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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.IV8ValuePrimitiveValue;
import com.caoccao.javet.values.reference.V8ValueIntegerObject;

/**
 * The type V8 value integer.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueInteger
        extends V8ValueNumber<Integer>
        implements IV8ValuePrimitiveValue<V8ValueIntegerObject> {
    /**
     * Instantiates a new V8 value integer.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueInteger(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0);
    }

    /**
     * Instantiates a new V8 value integer.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueInteger(V8Runtime v8Runtime, int value) throws JavetException {
        super(v8Runtime, value);
    }

    @Override
    public boolean asBoolean() {
        // 0, -0, and NaN turn into false; other numbers turn into true.
        return value != 0;
    }

    @Override
    public int asInt() {
        return value;
    }

    @Override
    public V8ValueInteger toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public V8ValueIntegerObject toObject() throws JavetException {
        return checkV8Runtime().createV8ValueIntegerObject(value);
    }

    /**
     * To primitive int.
     *
     * @return the int
     * @since 0.7.0
     */
    public int toPrimitive() {
        return value;
    }
}
