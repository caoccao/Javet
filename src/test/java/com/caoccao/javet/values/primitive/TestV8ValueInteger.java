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

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueInteger extends BaseTestJavetRuntime {
    @Test
    public void testInt32() throws JavetException {
        try (V8ValueInteger v8ValueInteger = v8Runtime.execute("1 + 1")) {
            assertNotNull(v8ValueInteger);
            assertEquals(2, v8ValueInteger.getValue());
            assertEquals("2", v8ValueInteger.toString());
            assertEquals(v8Runtime, v8ValueInteger.getV8Runtime());
        }
        assertEquals(-1, v8Runtime.executeInteger("1 - 2"));
    }

    @Test
    public void testInt32Object() throws JavetException {
        assertEquals(123, v8Runtime.executeInteger("Number(123)"));
    }
}
