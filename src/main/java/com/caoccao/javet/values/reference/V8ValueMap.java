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
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetConsumer;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueMap extends V8ValueObject implements IV8ValueMap {
    protected static final String FUNCTION_KEYS = "keys";
    protected static final String FUNCTION_VALUES = "values";
    protected static final String FUNCTION_ENTRIES = "entries";

    V8ValueMap(long handle) {
        super(handle);
    }

    @Override
    public <Key extends V8Value> int forEach(IJavetConsumer<Key> consumer) throws JavetException {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<Key> iterator = (IV8ValueIterator<Key>) getKeys()) {
            while (true) {
                try (Key key = iterator.getNext()) {
                    if (key == null){
                        break;
                    }
                    consumer.accept(key);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public <Key extends V8Value, Value extends V8Value> int forEach(
            IJavetBiConsumer<Key, Value> consumer) throws JavetException {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<V8ValueArray> iterator = getEntries()) {
            while (true) {
                try (V8ValueArray entry = iterator.getNext()) {
                    if (entry == null) {
                        break;
                    }
                    try (Key key = entry.get(0); Value value = entry.get(1);) {
                        consumer.accept(key, value);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public IV8ValueIterator<V8ValueArray> getEntries() throws JavetException {
        checkV8Runtime();
        return invoke(FUNCTION_ENTRIES);
    }

    @Override
    public IV8ValueIterator<? extends V8Value> getKeys() throws JavetException {
        checkV8Runtime();
        return invoke(FUNCTION_KEYS);
    }

    @Override
    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Map;
    }

    @Override
    public IV8ValueIterator<? extends V8Value> getValues() throws JavetException {
        checkV8Runtime();
        return invoke(FUNCTION_VALUES);
    }
}
