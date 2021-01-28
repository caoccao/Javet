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

public class TestV8ValueDouble extends BaseTestJavetRuntime {
    public static final double DELTA = 0.001;

    @Test
    public void testNumber() throws JavetException {
        try (V8ValueDouble v8ValueDouble = v8Runtime.execute("1.23")) {
            assertNotNull(v8ValueDouble);
            assertEquals(1.23, v8ValueDouble.getValue(), DELTA);
            assertEquals("1.23", v8ValueDouble.toString());
            assertEquals(v8Runtime, v8ValueDouble.getV8Runtime());
        }
        assertEquals(-0.5, v8Runtime.executeDouble("-0.5"), DELTA);
        assertEquals(0, v8Runtime.executeDouble("-0.0"), DELTA);
    }

    @Test
    public void testNumberObject() throws JavetException {
        assertEquals(1.23, v8Runtime.executeDouble("Number(1.23)"), DELTA);
    }
}
