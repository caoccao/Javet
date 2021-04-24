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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.enums.V8ValueReferenceType;

@SuppressWarnings("unchecked")
public class V8ValueIterator<T extends V8Value> extends V8ValueObject implements IV8ValueIterator<T> {
    protected static final String FUNCTION_NEXT = "next";
    protected static final String PROPERTY_DONE = "done";
    protected static final String PROPERTY_VALUE = "value";

    V8ValueIterator(long handle) {
        super(handle);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Iterator;
    }

    @Override
    public T getNext() {
        try (V8ValueObject next = invokeV8Value(FUNCTION_NEXT)) {
            if (!next.getBoolean(PROPERTY_DONE)) {
                return next.get(PROPERTY_VALUE);
            }
        } catch (JavetException javetException) {
        }
        return null;
    }
}
