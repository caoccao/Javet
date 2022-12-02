/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

import java.util.Objects;

@SuppressWarnings("unchecked")
public final class V8ValueString extends V8ValuePrimitive<String> {
    public V8ValueString(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, null);
    }

    public V8ValueString(V8Runtime v8Runtime, String value) throws JavetException {
        super(v8Runtime, Objects.requireNonNull(value));
    }

    @Override
    public V8ValueString toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    public String toPrimitive() {
        return value;
    }
}
