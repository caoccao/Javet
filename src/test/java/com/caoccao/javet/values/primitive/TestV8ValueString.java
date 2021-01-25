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
import com.caoccao.javet.values.primitive.V8ValueString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestV8ValueString extends BaseTestV8Value {
    @Test
    public void testString() throws JavetException {
        V8ValueString v8ValueString = v8Runtime.execute("'abc' + 'def'");
        assertNotNull(v8ValueString);
        assertEquals("abcdef", v8ValueString.getValue());
        assertEquals(v8Runtime, v8ValueString.getV8Runtime());
        assertEquals("中文測試", v8Runtime.executeString("'中文測試'"));
        assertEquals("français", v8Runtime.executeString("'français'"));
        assertEquals("こにちは", v8Runtime.executeString("'こにちは'"));
    }

    @Test
    public void testTypeof() throws JavetException {
        assertEquals("bigint", v8Runtime.executeString("typeof 1n"));
        assertEquals("object", v8Runtime.executeString("typeof (new Object(1n))"));
        assertEquals("number", v8Runtime.executeString("typeof 1"));
        assertEquals("string", v8Runtime.executeString("typeof '1'"));
    }
}
