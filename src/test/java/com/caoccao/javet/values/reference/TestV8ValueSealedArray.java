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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestV8ValueSealedArray extends BaseTestJavetRuntime {
    @Test
    public void testConversion() throws JavetException {
        assertArrayEquals(
                new Object[]{1, 2},
                v8Runtime.getExecutor("Object.seal([1,2])").executeObject());
        assertArrayEquals(
                new Object[]{"a", "b"},
                v8Runtime.getExecutor("Object.seal(['a','b'])").executeObject());
        assertArrayEquals(
                new Object[]{},
                v8Runtime.getExecutor("Object.seal([])").executeObject());
    }
}
