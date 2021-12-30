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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetUniConsumer;
import com.caoccao.javet.interfaces.IJavetUniIndexedConsumer;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class V8ValueArray extends V8ValueObject implements IV8ValueArray {
    protected static final String FUNCTION_KEYS = "keys";
    protected static final String FUNCTION_NEXT = "next";
    protected static final String FUNCTION_POP = "pop";
    protected static final String FUNCTION_PUSH = "push";
    protected static final String PROPERTY_DONE = "done";
    protected static final String PROPERTY_VALUE = "value";

    V8ValueArray(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public <Value extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Value, E> consumer) throws JavetException, E {
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
    public <Value extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        final int length = getLength();
        for (int i = 0; i < length; ++i) {
            try (Value value = get(i)) {
                consumer.accept(i, value);
            }
        }
        return length;
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T get(int index) throws JavetException {
        return checkV8Runtime().getV8Internal().get(this, v8Runtime.createV8ValueInteger(index));
    }

    @Override
    public List<Integer> getKeys() throws JavetException {
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
        return checkV8Runtime().getV8Internal().getLength(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Array;
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T pop() throws JavetException {
        return invoke(FUNCTION_POP);
    }

    @Override
    public int push(Object value) throws JavetException {
        return invokeInteger(FUNCTION_PUSH, value);
    }
}
