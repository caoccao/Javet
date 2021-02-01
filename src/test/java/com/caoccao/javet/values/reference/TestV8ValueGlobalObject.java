/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueGlobalObject extends BaseTestJavetRuntime {
    @Test
    public void testGetAndSetProperty() throws JavetException {
        assertEquals(0, v8Runtime.getReferenceCount());
        try (V8ValueGlobalObject v8RuntimeGlobalObject = v8Runtime.getGlobalObject()) {
            assertEquals(0, v8Runtime.getReferenceCount());
            assertNotNull(v8RuntimeGlobalObject);
            v8RuntimeGlobalObject.setProperty("a", new V8ValueInteger(1));
            assertEquals(1, v8RuntimeGlobalObject.getPropertyInteger("a"));
        }
        assertEquals(0, v8Runtime.getReferenceCount());
        v8Runtime.executeVoid("var b = 3;");
        try (V8ValueGlobalObject v8RuntimeGlobalObject = v8Runtime.getGlobalObject()) {
            assertNotNull(v8RuntimeGlobalObject);
            assertEquals(3, v8RuntimeGlobalObject.getPropertyInteger("b"));
        }
        assertEquals(2, v8Runtime.executeInteger("a + 1"));
        assertEquals(4, v8Runtime.executeInteger("a + b"));
    }
}
