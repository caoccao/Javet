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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

public class V8ValueIterator<T extends V8Value> extends V8ValueObject implements IV8ValueIterator<T> {
    protected static final String FUNCTION_NEXT = "next";
    protected static final String PROPERTY_DONE = "done";
    protected static final String PROPERTY_VALUE = "value";

    V8ValueIterator(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    @CheckReturnValue
    public T getNext() {
        try (V8ValueObject next = invoke(FUNCTION_NEXT)) {
            if (!next.getBoolean(PROPERTY_DONE)) {
                return next.get(PROPERTY_VALUE);
            }
        } catch (JavetException ignored) {
        }
        return null;
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Iterator;
    }
}
