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

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueBigInteger extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        assertEquals(0, v8Runtime.createV8ValueBigInteger(BigInteger.valueOf(0)).asInt());
        assertEquals(1, v8Runtime.createV8ValueBigInteger(BigInteger.valueOf(1)).asInt());
    }

    @Test
    public void testBigIntNegative() throws JavetException {
        Random random = new Random();
        BigInteger baseBigInteger = BigInteger.valueOf(2L).pow(65).multiply(BigInteger.valueOf(-1L));
        final int rounds = 5;
        for (int i = 0; i < rounds; i++) {
            BigInteger deltaBigInteger = BigInteger.valueOf(random.nextLong());
            BigInteger expectedBigInteger = baseBigInteger.subtract(deltaBigInteger);
            try (V8ValueBigInteger v8ValueBigInteger =
                         v8Runtime.getExecutor("-1n * (2n ** 65n) - " + deltaBigInteger + "n").execute()) {
                assertNotNull(v8ValueBigInteger);
                assertEquals(expectedBigInteger.toString(), v8ValueBigInteger.toString());
                assertEquals(-1, v8ValueBigInteger.getValue().signum());
                assertEquals(-1, v8ValueBigInteger.getSignum());
                v8Runtime.getGlobalObject().set("a", v8ValueBigInteger);
                assertTrue(v8Runtime.getExecutor("a === " + expectedBigInteger + "n").executeBoolean());
                assertEquals(expectedBigInteger.toString(), v8Runtime.getGlobalObject().getBigInteger("a").toString());
            }
        }
    }

    @Test
    public void testBigIntObject() throws JavetException {
        assertEquals(
                "36893488147419103488",
                v8Runtime.getExecutor("BigInt(36893488147419103488n)").executeBigInteger().toString());
    }

    @Test
    public void testBigIntPositive() throws JavetException {
        Random random = new Random();
        BigInteger baseBigInteger = BigInteger.valueOf(2L).pow(65);
        final int rounds = 5;
        for (int i = 0; i < rounds; i++) {
            BigInteger deltaBigInteger = BigInteger.valueOf(random.nextLong());
            BigInteger expectedBigInteger = baseBigInteger.add(deltaBigInteger);
            try (V8ValueBigInteger v8ValueBigInteger =
                         v8Runtime.getExecutor("2n ** 65n + " + deltaBigInteger + "n").execute()) {
                assertNotNull(v8ValueBigInteger);
                assertEquals(expectedBigInteger.toString(), v8ValueBigInteger.toString());
                assertEquals(1, v8ValueBigInteger.getValue().signum());
                assertEquals(1, v8ValueBigInteger.getSignum());
                v8Runtime.getGlobalObject().set("a", v8ValueBigInteger);
                assertTrue(v8Runtime.getExecutor("a === " + expectedBigInteger + "n").executeBoolean());
                assertEquals(expectedBigInteger.toString(), v8Runtime.getGlobalObject().getBigInteger("a").toString());
            }
        }
    }

    @Test
    public void testDowngradeToLong() throws JavetException {
        long[] longArray = new long[]{Long.MAX_VALUE, 123456L, 1L, 0L, -1L, -123456L, Long.MIN_VALUE};
        for (long l : longArray) {
            V8ValueBigInteger v8ValueBigInteger = v8Runtime.createV8ValueBigInteger(BigInteger.valueOf(l));
            assertEquals(BigInteger.valueOf(l).toString(), v8ValueBigInteger.getValue().toString());
            v8Runtime.getGlobalObject().set("a", v8ValueBigInteger);
            assertEquals(l, v8Runtime.getGlobalObject().getLong("a"));
        }
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertTrue(v8Runtime.createV8ValueBigInteger(BigInteger.valueOf(1)).asBoolean());
        assertFalse(v8Runtime.createV8ValueBigInteger(BigInteger.valueOf(0)).asBoolean());
    }
}
