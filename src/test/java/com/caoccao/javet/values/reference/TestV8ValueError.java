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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueError extends BaseTestJavetRuntime {
    @Test
    public void testError() throws JavetException {
        try (V8ValueError v8ValueError = v8Runtime.getExecutor("Error('test')").execute()) {
            assertNotNull(v8ValueError);
            assertEquals("test", v8ValueError.getMessage());
            assertEquals("Error: test\n    at <anonymous>:1:1", v8ValueError.getStack());
            try (IV8ValueArray iV8ValueArray = v8ValueError.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(0, iV8ValueArray.getLength());
            }
        }
    }

    @Test
    public void testProperty() throws JavetException {
        try (V8ValueError v8ValueError = v8Runtime.getExecutor("const e = Error('test'); e.a = 1; e;").execute()) {
            assertNotNull(v8ValueError);
            assertEquals("test", v8ValueError.getMessage());
            assertEquals("Error: test\n    at <anonymous>:1:11", v8ValueError.getStack());
            try (IV8ValueArray iV8ValueArray = v8ValueError.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(1, iV8ValueArray.getLength());
                assertEquals("a", iV8ValueArray.getString(0));
            }
            assertEquals(1, v8ValueError.getInteger("a"));
        }
    }
}
