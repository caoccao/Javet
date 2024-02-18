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
import com.caoccao.javet.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueUndefined extends BaseTestJavetRuntime {
    @Test
    public void testEquals() throws JavetException {
        try (V8ValueUndefined v8ValueUndefined = v8Runtime.getExecutor("undefined").execute()) {
            assertTrue(v8ValueUndefined.equals(v8Runtime.createV8ValueUndefined()));
            assertFalse(v8ValueUndefined.equals(v8Runtime.createV8ValueNull()));
        }
    }

    @Test
    public void testIfTrue() throws JavetException {
        assertFalse(v8Runtime.createV8ValueUndefined().asBoolean());
    }

    @Test
    public void testUndefined() throws JavetException {
        try (V8ValueUndefined v8ValueUndefined = v8Runtime.getExecutor("undefined").execute()) {
            assertNotNull(v8ValueUndefined);
            assertEquals(v8Runtime, v8ValueUndefined.getV8Runtime());
            assertFalse(v8ValueUndefined.isNull());
            assertTrue(v8ValueUndefined.isNullOrUndefined());
        }
        try (V8ValueUndefined v8ValueUndefined = v8Runtime.getExecutor(StringUtils.EMPTY).execute()) {
            assertNotNull(v8ValueUndefined);
            assertEquals(v8Runtime, v8ValueUndefined.getV8Runtime());
            assertFalse(v8ValueUndefined.isNull());
            assertTrue(v8ValueUndefined.isNullOrUndefined());
        }
    }
}
