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

package com.caoccao.javet.values.virtual;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public class V8VirtualValue implements IJavetClosable {
    protected boolean converted;
    protected V8Value value;

    public V8VirtualValue(V8Runtime v8Runtime, Object object) throws JavetException {
        if (object instanceof V8Value) {
            converted = false;
            value = (V8Value) object;
        } else {
            converted = true;
            value = v8Runtime.toV8Value(object);
        }
    }

    @Override
    public void close() throws JavetException {
        if (converted) {
            JavetResourceUtils.safeClose(value);
        }
    }

    public <T extends V8Value> T get() {
        return (T) value;
    }
}
