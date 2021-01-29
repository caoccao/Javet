/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.BaseTestJavetRuntime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueLong extends BaseTestJavetRuntime {
    @Test
    public void testBigInt() throws JavetException {
        try (V8ValueLong v8ValueLong = v8Runtime.execute("2n ** 62n")) {
            assertNotNull(v8ValueLong);
            assertEquals(4611686018427387904L, v8ValueLong.getValue());
            assertEquals("4611686018427387904", v8ValueLong.toString());
            assertEquals(-2L, v8Runtime.executeLong("-2n"));
            assertEquals(v8Runtime, v8ValueLong.getV8Runtime());
        }
    }

    @Test
    public void testBigIntObject() throws JavetException {
        assertEquals(123L, v8Runtime.executeLong("BigInt(123n)"));
    }

    @Test
    public void testString() throws JavetException {
        assertEquals("4611686018427387904", v8Runtime.executeString("(2n ** 62n).toString()"));
        assertEquals("-2", v8Runtime.executeString("(-2n).toString()"));
    }
}
