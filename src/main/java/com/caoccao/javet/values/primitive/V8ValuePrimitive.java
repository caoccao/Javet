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
import com.caoccao.javet.values.V8Value;

/**
 * The type V8 value primitive.
 *
 * @param <Primitive> the type parameter
 * @since 1.0.7
 */
public abstract class V8ValuePrimitive<Primitive> extends V8Value {
    /**
     * The Value.
     *
     * @since 1.0.7
     */
    protected Primitive value;

    /**
     * Instantiates a new V8 value primitive.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 1.0.7
     */
    public V8ValuePrimitive(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, null);
    }

    /**
     * Instantiates a new V8 value primitive.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 1.0.7
     */
    public V8ValuePrimitive(V8Runtime v8Runtime, Primitive value) throws JavetException {
        super(v8Runtime);
        this.value = value;
    }

    @Override
    public void close() throws JavetException {
        // Primitive V8 value is immutable. So v8Runtime is not reset to null.
    }

    @Override
    public boolean equals(V8Value v8Value) {
        if (!(v8Value instanceof V8ValuePrimitive)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        return getValue().equals(((V8ValuePrimitive<?>) v8Value).getValue());
    }

    /**
     * Gets the primitive value.
     *
     * @return the primitive value
     * @since 1.0.7
     */
    public Primitive getValue() {
        return value;
    }

    /**
     * Is empty.
     *
     * @return true : empty, false : not empty
     * @since 1.0.7
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Is present.
     *
     * @return true : present, false : not present
     * @since 1.0.7
     */
    public boolean isPresent() {
        return value != null;
    }

    @Override
    public boolean sameValue(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public boolean strictEquals(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public String toString() {
        return isEmpty() ? null : value.toString();
    }
}
