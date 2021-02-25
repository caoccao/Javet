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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public class V8ValueArrayBuffer extends V8ValueObject {

    public static final String PROPERTY_BYTE_LENGTH = "byteLength";
    protected ByteBuffer byteBuffer;

    protected V8ValueArrayBuffer(long handle, ByteBuffer byteBuffer) {
        super(handle);
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public int getByteLength() throws JavetException {
        return getInteger(PROPERTY_BYTE_LENGTH);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.ArrayBuffer;
    }
}
