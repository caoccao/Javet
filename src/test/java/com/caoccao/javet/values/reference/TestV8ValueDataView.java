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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8ValueDataView extends BaseTestJavetRuntime {

    @Test
    public void testFromV8() throws JavetException {
        final int byteLength = 16;
        try (V8ValueArrayBuffer v8ValueArrayBuffer = v8Runtime.getExecutor(
                "const a = new ArrayBuffer(" + byteLength + "); a;").execute()) {
            assertEquals("{}", v8ValueArrayBuffer.toJsonString());
            assertEquals("[object ArrayBuffer]", v8ValueArrayBuffer.toString());
            assertEquals(byteLength, v8ValueArrayBuffer.getByteLength());
            for (int i = 0; i < byteLength; i++) {
                v8ValueArrayBuffer.getByteBuffer().put(i, (byte) i);
            }
        }
        try (V8ValueDataView v8ValueDataView = v8Runtime.getExecutor(
                "const b = new DataView(a); b;").execute()) {
            assertEquals("{}", v8ValueDataView.toJsonString());
            assertEquals("[object DataView]", v8ValueDataView.toString());
            assertEquals(byteLength, v8ValueDataView.getByteLength());
            for (int i = 0; i < byteLength; i++) {
                assertEquals(i, v8ValueDataView.getInt8(i));
            }
        }
    }

    @Test
    public void testToV8() throws JavetException {
        final int byteLength = 16;
        try (V8ValueArrayBuffer v8ValueArrayBuffer = v8Runtime.createV8ValueArrayBuffer(byteLength)) {
            try (V8ValueDataView v8ValueDataView = v8Runtime.createV8ValueDataView(v8ValueArrayBuffer)) {
                assertEquals("{}", v8ValueDataView.toJsonString());
                assertEquals("[object DataView]", v8ValueDataView.toString());
                assertEquals(byteLength, v8ValueDataView.getByteLength());
                for (int i = 0; i < byteLength; i++) {
                    v8ValueDataView.setInt8(i, (byte) i);
                }
            }
            byte[] bytes = new byte[byteLength];
            v8ValueArrayBuffer.getByteBuffer().get(bytes);
            for (int i = 0; i < byteLength; i++) {
                assertEquals(i, bytes[i]);
            }
        }
    }
}
