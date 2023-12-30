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

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueArguments extends BaseTestJavetRuntime {
    @Test
    public void testArguments() throws JavetException {
        try (V8ValueArguments v8ValueArguments = v8Runtime.getExecutor(
                "const a = function(a, b) { return arguments; }; a(1, '2')").execute()) {
            assertNotNull(v8ValueArguments);
            assertEquals(1, v8ValueArguments.getInteger(0));
            assertEquals("2", v8ValueArguments.getString(1));
            try (V8ValueArguments clonedV8ValueArguments = v8ValueArguments.toClone()) {
                assertEquals(1, v8ValueArguments.getInteger(0));
                assertEquals("2", v8ValueArguments.getString(1));
                assertNotEquals(v8ValueArguments.getHandle(), clonedV8ValueArguments.getHandle());
            }
        }
    }
}
