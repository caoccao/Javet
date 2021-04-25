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
import com.caoccao.javet.values.virtual.V8VirtualValue;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueSet extends V8ValueObject implements IV8ValueSet {
    protected static final String FUNCTION_ENTRIES = "entries";
    protected static final String FUNCTION_KEYS = "keys";

    V8ValueSet(long handle) {
        super(handle);
    }

    @Override
    public void add(Object key) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, key)) {
            v8Runtime.add(this, virtualValue.get());
        }
    }

    @Override
    public <Key extends V8Value> int forEach(IJavetConsumer<Key> consumer) throws JavetException {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<V8Value> iterator = getKeys()) {
            while (true) {
                try (Key key = (Key) iterator.getNext()) {
                    if (key == null) {
                        break;
                    }
                    consumer.accept(key);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public IV8ValueIterator<V8ValueArray> getEntries() throws JavetException {
        checkV8Runtime();
        return invokeV8Value(FUNCTION_ENTRIES);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Set;
    }

    @Override
    public IV8ValueIterator<V8Value> getKeys() throws JavetException {
        checkV8Runtime();
        return invokeV8Value(FUNCTION_KEYS);
    }

    @Override
    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

}
