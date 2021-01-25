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

package com.caoccao.javet.values;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueUndefined extends BaseTestV8Value {
    @Test
    public void testUndefined() {
        V8ValueUndefined v8ValueUndefined = v8Runtime.execute("undefined");
        assertNotNull(v8ValueUndefined);
        v8ValueUndefined = v8Runtime.execute("");
        assertNotNull(v8ValueUndefined);
    }
}
