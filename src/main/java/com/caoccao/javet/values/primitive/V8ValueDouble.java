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
import com.caoccao.javet.values.reference.V8ValueDoubleObject;

import java.math.BigDecimal;

/**
 * The type V8 value double.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueDouble
        extends V8ValueNumber<Double>
        implements IV8ValuePrimitiveValue<V8ValueDoubleObject> {
    private String cachedToString;

    /**
     * Instantiates a new V8 value double.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueDouble(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0D);
    }

    /**
     * Instantiates a new V8 value double.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueDouble(V8Runtime v8Runtime, double value) throws JavetException {
        super(v8Runtime, value);
        cachedToString = null;
    }

    @Override
    public boolean asBoolean() {
        // 0, -0, and NaN turn into false; other numbers turn into true.
        return value != 0D && !Double.isNaN(value) && Double.isFinite(value);
    }

    @Override
    public double asDouble() throws JavetException {
        return value;
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    @Override
    public long asLong() throws JavetException {
        return value.longValue();
    }

    /**
     * Is finite.
     *
     * @return true : finite, false: infinite
     * @since 0.7.0
     */
    public boolean isFinite() {
        return Double.isFinite(value);
    }

    /**
     * Is infinite.
     *
     * @return true : infinite, false: finite
     * @since 0.7.0
     */
    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    /**
     * Is NaN.
     *
     * @return true : NaN, false: not NaN
     * @since 0.7.0
     */
    public boolean isNaN() {
        return Double.isNaN(value);
    }

    @Override
    public V8ValueDouble toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public V8ValueDoubleObject toObject() throws JavetException {
        return checkV8Runtime().createV8ValueDoubleObject(value);
    }

    /**
     * To primitive double.
     *
     * @return the double
     * @since 0.7.0
     */
    public double toPrimitive() {
        return value;
    }

    @Override
    public String toString() {
        if (cachedToString == null) {
            cachedToString = new BigDecimal(value.toString()).toPlainString();
        }
        return cachedToString;
    }
}
