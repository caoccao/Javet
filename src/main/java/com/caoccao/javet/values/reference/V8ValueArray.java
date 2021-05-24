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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetConsumer;
import com.caoccao.javet.values.V8Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueArray extends V8ValueObject implements IV8ValueArray {
    protected static final String FUNCTION_NEXT = "next";
    protected static final String FUNCTION_KEYS = "keys";
    protected static final String FUNCTION_POP = "pop";
    protected static final String FUNCTION_PUSH = "push";
    protected static final String PROPERTY_DONE = "done";
    protected static final String PROPERTY_VALUE = "value";

    V8ValueArray(long handle) {
        super(handle);
    }

    @Override
    public <Value extends V8Value, E extends Throwable> int forEach(
            IJavetConsumer<Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        final int length = getLength();
        for (int i = 0; i < length; ++i) {
            try (Value value = get(i)) {
                consumer.accept(value);
            }
        }
        return length;
    }

    @Override
    public <T extends V8Value> T get(int index) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, v8Runtime.createV8ValueInteger(index));
    }

    @Override
    public List<Integer> getKeys() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject iterator = invoke(FUNCTION_KEYS)) {
            List<Integer> keys = new ArrayList<>();
            while (true) {
                try (V8ValueObject next = iterator.invoke(FUNCTION_NEXT)) {
                    if (next.getBoolean(PROPERTY_DONE)) {
                        break;
                    }
                    keys.add(next.getInteger(PROPERTY_VALUE));
                }
            }
            return keys;
        }
    }

    @Override
    public int getLength() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Array;
    }

    @Override
    public <T extends V8Value> T pop() throws JavetException {
        checkV8Runtime();
        return invoke(FUNCTION_POP);
    }

    @Override
    public int push(Object value) throws JavetException {
        checkV8Runtime();
        return invokeInteger(FUNCTION_PUSH, value);
    }
}
