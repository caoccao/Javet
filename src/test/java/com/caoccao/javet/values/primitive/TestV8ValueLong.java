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
import com.caoccao.javet.values.BaseTestV8Value;
import com.caoccao.javet.values.primitive.V8ValueLong;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueLong extends BaseTestV8Value {
    @Test
    public void testLong() throws JavetException {
        assertEquals("4611686018427387904", v8Runtime.executeString("(2n ** 62n).toString()"));
        V8ValueLong v8ValueLong = v8Runtime.execute("2n ** 62n");
        assertFalse(v8ValueLong.isUnsigned());
        assertNotNull(v8ValueLong);
        assertEquals(4611686018427387904L, v8ValueLong.getValue());
        assertEquals(v8Runtime, v8ValueLong.getV8Runtime());
        assertEquals(-2L, v8Runtime.executeLong("-2n"));
    }
}
