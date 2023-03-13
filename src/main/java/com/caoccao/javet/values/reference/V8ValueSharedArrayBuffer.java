/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

import java.nio.ByteBuffer;

public class V8ValueSharedArrayBuffer extends V8ValueArrayBuffer {
    V8ValueSharedArrayBuffer(V8Runtime v8Runtime, long handle, ByteBuffer byteBuffer) throws JavetException {
        super(v8Runtime, handle, byteBuffer);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.SharedArrayBuffer;
    }
}
