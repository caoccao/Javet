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

package com.caoccao.javet.values;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetObjectConverter;

public abstract class V8Value extends V8Data implements IV8Value {
    protected static final JavetObjectConverter OBJECT_CONVERTER = new JavetObjectConverter();

    protected V8Runtime v8Runtime;

    protected V8Value(V8Runtime v8Runtime) throws JavetException {
        if (v8Runtime == null) {
            throw new JavetException(JavetError.RuntimeNotRegistered);
        }
        this.v8Runtime = v8Runtime;
    }

    protected V8Runtime checkV8Runtime() throws JavetException {
        if (v8Runtime == null) {
            throw new JavetException(JavetError.RuntimeNotRegistered);
        }
        return v8Runtime;
    }

    @Override
    public abstract void close() throws JavetException;

    @Override
    public abstract boolean equals(V8Value v8Value) throws JavetException;

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public boolean isClosed() {
        return v8Runtime == null || v8Runtime.isClosed();
    }

    @Override
    public abstract boolean sameValue(V8Value v8Value) throws JavetException;

    @Override
    public abstract boolean strictEquals(V8Value v8Value) throws JavetException;
}
