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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInJson extends V8ValueObject {

    public static final String FUNCTION_STRINGIFY = "stringify";

    public V8ValueBuiltInJson(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    public String stringify(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeString(FUNCTION_STRINGIFY, v8Value);
    }

    @Override
    public V8ValueBuiltInJson toClone() throws JavetException {
        return this;
    }
}
