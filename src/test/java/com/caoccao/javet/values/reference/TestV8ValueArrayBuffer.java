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

import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestV8ValueArrayBuffer extends BaseTestJavetRuntime {

    @Test
    public void testByteBufferToV8() throws JavetException {
        final int byteLength = 16;
        byte[] bytes = new byte[byteLength];
        IntStream.range(0, byteLength).forEach(i -> bytes[i] = (byte) i);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteLength);
        assertTrue(byteBuffer.isDirect());
        byteBuffer.put(bytes);
        try (V8ValueArrayBuffer v8ValueArrayBuffer = v8Runtime.createV8ValueArrayBuffer(byteBuffer)) {
            assertEquals("{}", v8ValueArrayBuffer.toJsonString());
            assertEquals("[object ArrayBuffer]", v8ValueArrayBuffer.toString());
            assertEquals(byteLength, v8ValueArrayBuffer.getByteLength());
            v8Runtime.getGlobalObject().set("a", v8ValueArrayBuffer);
            v8Runtime.getExecutor("const b = new Int8Array(a);").executeVoid();
            for (int i = 0; i < byteLength; i++) {
                assertEquals(i, v8Runtime.getExecutor("b[" + i + "];").executeInteger());
            }
        }
    }

    @Test
    public void testLengthFromV8() throws JavetException {
        final int byteLength = 16;
        try (V8ValueArrayBuffer v8ValueArrayBuffer =
                     v8Runtime.getExecutor("const a = new ArrayBuffer(" + byteLength + "); a;").execute()) {
            assertEquals("{}", v8ValueArrayBuffer.toJsonString());
            assertEquals("[object ArrayBuffer]", v8ValueArrayBuffer.toString());
            assertEquals(byteLength, v8ValueArrayBuffer.getByteLength());
            for (int i = 0; i < byteLength; i++) {
                v8ValueArrayBuffer.getByteBuffer().put(i, (byte) i);
            }
            v8Runtime.getExecutor("const b = new Int8Array(a);").executeVoid();
            for (int i = 0; i < byteLength; i++) {
                assertEquals(i, v8Runtime.getExecutor("b[" + i + "];").executeInteger());
            }
        }
    }

    @Test
    public void testLengthToV8() throws JavetException {
        final int byteLength = 16;
        try (V8ValueArrayBuffer v8ValueArrayBuffer = v8Runtime.createV8ValueArrayBuffer(byteLength)) {
            assertEquals("{}", v8ValueArrayBuffer.toJsonString());
            assertEquals("[object ArrayBuffer]", v8ValueArrayBuffer.toString());
            assertEquals(byteLength, v8ValueArrayBuffer.getByteLength());
            for (int i = 0; i < byteLength; i++) {
                v8ValueArrayBuffer.getByteBuffer().put(i, (byte) i);
            }
            v8Runtime.getGlobalObject().set("a", v8ValueArrayBuffer);
            v8Runtime.getExecutor("const b = new Int8Array(a);").executeVoid();
            for (int i = 0; i < byteLength; i++) {
                assertEquals(i, v8Runtime.getExecutor("b[" + i + "];").executeInteger());
            }
        }
    }
}
