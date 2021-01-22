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

import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Value {
    protected V8Runtime v8Runtime;

    @BeforeEach
    public void beforeEach() {
        v8Runtime = V8Host.getInstance().createV8Runtime();
    }

    @AfterEach
    public void afterEach() {
        v8Runtime.close();
    }

    @Test
    public void testInteger() {
        V8ValueInteger v8ValueInteger = v8Runtime.execute("1 + 1");
        assertNotNull(v8ValueInteger);
        assertFalse(v8ValueInteger.isUnsigned());
        assertEquals(2, v8ValueInteger.getValue());
    }

    @Test
    public void testLong() {
        assertEquals("4611686018427387904", v8Runtime.executeString("(2n ** 62n).toString()"));
        V8ValueLong v8ValueLong = v8Runtime.execute("2n ** 62n");
        assertNotNull(v8ValueLong);
        assertEquals(4611686018427387904L, v8ValueLong.getValue());
    }

    @Test
    public void testNull() {
        V8ValueNull v8ValueNull = v8Runtime.execute("null");
        assertNotNull(v8ValueNull);
    }

    @Test
    public void testString() {
        V8ValueString v8ValueString = v8Runtime.execute("'abc' + 'def'");
        assertNotNull(v8ValueString);
        assertEquals("abcdef", v8ValueString.getValue());
    }

    @Test
    public void testTypeof() {
        assertEquals("bigint", v8Runtime.executeString("typeof 1n"));
        assertEquals("object", v8Runtime.executeString("typeof (new Object(1n))"));
        assertEquals("number", v8Runtime.executeString("typeof 1"));
        assertEquals("string", v8Runtime.executeString("typeof '1'"));
    }

    @Test
    public void testUndefined() {
        V8ValueUndefined v8ValueUndefined = v8Runtime.execute("undefined");
        assertNotNull(v8ValueUndefined);
    }
}
