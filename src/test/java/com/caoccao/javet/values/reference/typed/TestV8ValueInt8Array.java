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

package com.caoccao.javet.values.reference.typed;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueArrayBuffer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8ValueInt8Array extends BaseTestJavetRuntime {
    @Test
    public void testFromV8() throws JavetException {
        final int length = 16;
        try (V8ValueInt8Array v8ValueInt8Array =
                     v8Runtime.getExecutor("const a = new Int8Array(" + length + "); a;").execute()) {
            assertEquals(length, v8ValueInt8Array.getLength());
            assertEquals(length, v8ValueInt8Array.getByteLength());
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueInt8Array.getArrayBuffer()) {
                for (int i = 0; i < length; i++) {
                    v8ValueArrayBuffer.getByteBuffer().put(i, (byte) i);
                }
            }
            for (int i = 0; i < length; i++) {
                assertEquals(i, v8Runtime.getExecutor("a[" + i + "];").executeInteger());
            }
        }
    }

    @Test
    public void testToV8() throws JavetException {
        final int length = 16;
        try (V8ValueInt8Array v8ValueInt8Array =
                     v8Runtime.createV8ValueInt8Array(length)) {
            assertEquals(length, v8ValueInt8Array.getLength());
            assertEquals(length, v8ValueInt8Array.getByteLength());
            try (V8ValueArrayBuffer v8ValueArrayBuffer = v8ValueInt8Array.getArrayBuffer()) {
                for (int i = 0; i < length; i++) {
                    v8ValueArrayBuffer.getByteBuffer().put(i, (byte) i);
                }
            }
            v8Runtime.getGlobalObject().set("a", v8ValueInt8Array);
            for (int i = 0; i < length; i++) {
                assertEquals(i, v8Runtime.getExecutor("a[" + i + "];").executeInteger());
            }
        }
    }
}
