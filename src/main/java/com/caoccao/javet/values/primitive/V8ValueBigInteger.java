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

import java.math.BigInteger;
import java.util.Objects;

/**
 * The type V8 value big integer.
 * <p>
 * This feature is experimental and not completed yet.
 *
 * @since 1.1.5
 */
@SuppressWarnings("unchecked")
public final class V8ValueBigInteger extends V8ValuePrimitive<BigInteger> {

    private static final int BYTE_COUNT_PER_WORD = 8;

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
     * @param v8Runtime the V8 runtime
     * @param value     the big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    public V8ValueBigInteger(V8Runtime v8Runtime, BigInteger value) throws JavetException {
        super(v8Runtime, Objects.requireNonNull(value));
    }

    /**
     * Instantiates a new V8 value big integer.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the string value
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    public V8ValueBigInteger(V8Runtime v8Runtime, String value) throws JavetException {
        this(v8Runtime, new BigInteger(Objects.requireNonNull(value)));
    }

    /**
     * Instantiates a new V8 value big integer.
     *
     * @param v8Runtime the V8 runtime
     * @param signum    the signum
     *                  1: Positive
     *                  0: 0
     *                  -1: Negative
     * @param longArray the long array
     * @throws JavetException the javet exception
     */
    V8ValueBigInteger(V8Runtime v8Runtime, int signum, long[] longArray) throws JavetException {
        this(v8Runtime, toBigInteger(signum, longArray));
    }

    /**
     * To big integer from signum and long array.
     *
     * @param signum    the signum
     *                  1: Positive
     *                  0: 0
     *                  -1: Negative
     * @param longArray the long array
     * @return the big integer
     * @since 1.1.5
     */
    static BigInteger toBigInteger(int signum, long[] longArray) {
        if (signum == 0 || signum > 1 || signum < -1 || longArray == null || longArray.length == 0) {
            return BigInteger.ZERO;
        }
        final int longLength = longArray.length;
        final int byteLength = longLength * BYTE_COUNT_PER_WORD;
        byte[] bytes = new byte[byteLength];
        for (int i = 0; i < longLength; ++i) {
            final int startIndex = (longLength - 1 - i) * BYTE_COUNT_PER_WORD;
            final int endIndex = startIndex + BYTE_COUNT_PER_WORD;
            long l = longArray[i];
            for (int j = startIndex; j < endIndex; ++j) {
                bytes[j] = (byte) (0xFF & (l >> (endIndex - 1 - j) * BYTE_COUNT_PER_WORD));
            }
        }
        return new BigInteger(signum, bytes);
    }

    /**
     * To long array from signum and byte array.
     *
     * @param signum the signum
     *               1: Positive
     *               0: 0
     *               -1: Negative
     * @param bytes  the bytes
     * @return the long array
     * @since 1.1.5
     */
    static long[] toLongArray(final int signum, final byte[] bytes) {
        if (signum == 0) {
            return null;
        }
        final int byteLength = bytes.length;
        final int longLength = (byteLength + BYTE_COUNT_PER_WORD - 1) / BYTE_COUNT_PER_WORD;
        long[] longArray = new long[longLength];
        for (int i = 0; i < longLength; ++i) {
            final int startIndex = byteLength - (i + 1) * BYTE_COUNT_PER_WORD;
            final int endIndex = startIndex + BYTE_COUNT_PER_WORD;
            long longValue = 0L;
            for (int j = Math.max(0, startIndex); j < endIndex; ++j) {
                // Apply NOT operation to every byte if the big integer has a negative signum.
                long l = 0xFF & (signum < 0 ? ~bytes[j] : bytes[j]);
                longValue |= (l << ((endIndex - 1 - j) * BYTE_COUNT_PER_WORD));
            }
            longArray[i] = longValue;
        }
        if (signum < 0) {
            /*
             * Increment the long value if the big integer has a negative signum
             * because of the nature of the logical negation.
             */
            for (int i = 0; i < longLength; ++i) {
                longArray[i]++;
                if (longArray[i] != 0L) {
                    break;
                }
            }
        }
        return longArray;
    }

    /**
     * Get long array.
     *
     * @return the long array
     * @since 1.1.5
     */
    long[] getLongArray() {
        return toLongArray(value.signum(), value.toByteArray());
    }

    /**
     * Gets signum.
     * 1: Positive
     * 0: 0
     * -1: Negative
     *
     * @return the signum
     * @since 1.1.5
     */
    int getSignum() {
        return value.signum();
    }

    @Override
    public V8ValueBigInteger toClone(boolean referenceCopy) throws JavetException {
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
