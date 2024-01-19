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

public class TestV8ValueWeakSet extends BaseTestJavetRuntime {
    @Test
    public void testAddHasAndDelete() throws JavetException {
        try (V8ValueWeakSet v8ValueWeakSet = v8Runtime.getExecutor("const a = new WeakSet(); a;").execute()) {
            assertNotNull(v8ValueWeakSet);
            assertEquals("[object WeakSet]", v8ValueWeakSet.toString());
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8ValueObject.set("x", "1");
                v8ValueWeakSet.add(v8ValueObject);
                assertTrue(v8ValueWeakSet.has(v8ValueObject));
                v8ValueWeakSet.delete(v8ValueObject);
                assertFalse(v8ValueWeakSet.has(v8ValueObject));
            }
        }
    }
}
