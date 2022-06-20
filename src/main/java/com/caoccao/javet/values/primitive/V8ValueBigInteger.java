/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

import java.math.BigInteger;

/**
 * The type V8 value big integer.
 * <p>
 * This feature is experimental and not completed yet.
 *
 * @since 1.1.5
 */
@SuppressWarnings("unchecked")
public final class V8ValueBigInteger extends V8ValuePrimitive<BigInteger> {
    /**
     * Instantiates a new V8 value big integer.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    public V8ValueBigInteger(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, BigInteger.ZERO);
    }

    /**
     * Instantiates a new V8 value big integer.
     *
     * @param v8Runtime  the V8 runtime
     * @param bigInteger the big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    public V8ValueBigInteger(V8Runtime v8Runtime, BigInteger bigInteger) throws JavetException {
        super(v8Runtime, bigInteger);
    }

    /**
     * Instantiates a new V8 value big integer.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    public V8ValueBigInteger(V8Runtime v8Runtime, String value) throws JavetException {
        this(v8Runtime, new BigInteger(value));
    }

    @Override
    public V8ValueBigInteger toClone() throws JavetException {
        return this;
    }

    /**
     * To primitive big integer.
     *
     * @return the big integer
     * @since 1.1.5
     */
    public BigInteger toPrimitive() {
        return value;
    }
}
