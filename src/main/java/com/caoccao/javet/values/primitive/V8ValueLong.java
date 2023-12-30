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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

@SuppressWarnings("unchecked")
public final class V8ValueLong extends V8ValuePrimitive<Long> {
    public V8ValueLong(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0L);
    }

    public V8ValueLong(V8Runtime v8Runtime, long value) throws JavetException {
        super(v8Runtime, value);
    }

    public V8ValueLong(V8Runtime v8Runtime, String value) throws JavetException {
        this(v8Runtime, Long.parseLong(value));
    }

    @Override
    public V8ValueLong toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    public long toPrimitive() {
        return value;
    }
}
