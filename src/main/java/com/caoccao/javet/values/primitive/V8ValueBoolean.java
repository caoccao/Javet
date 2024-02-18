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
import com.caoccao.javet.values.reference.V8ValueBooleanObject;

/**
 * The type V8 value boolean.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueBoolean
        extends V8ValuePrimitive<Boolean>
        implements IV8ValuePrimitiveValue<V8ValueBooleanObject> {
    /**
     * The constant FALSE.
     *
     * @since 3.0.4
     */
    public static final String FALSE = "false";
    /**
     * The constant TRUE.
     *
     * @since 3.0.4
     */
    public static final String TRUE = "true";

    /**
     * Instantiates a new V8 value boolean.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueBoolean(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, false);
    }

    /**
     * Instantiates a new V8 value boolean.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueBoolean(V8Runtime v8Runtime, boolean value) throws JavetException {
        super(v8Runtime, value);
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public int asInt() {
        return value ? 1 : 0;
    }

    @Override
    public V8ValueBoolean toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public V8ValueBooleanObject toObject() throws JavetException {
        return checkV8Runtime().createV8ValueBooleanObject(value);
    }

    /**
     * To primitive boolean.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean toPrimitive() {
        return value;
    }

    @Override
    public String toString() {
        return value ? TRUE : FALSE;
    }
}
