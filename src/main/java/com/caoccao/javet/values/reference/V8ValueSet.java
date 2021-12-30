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
import com.caoccao.javet.values.virtual.V8VirtualValue;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueSet extends V8ValueObject implements IV8ValueSet {
    protected static final String FUNCTION_ENTRIES = "entries";
    protected static final String FUNCTION_KEYS = "keys";

    V8ValueSet(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public void add(Object key) throws JavetException {
        try (V8VirtualValue virtualValue = new V8VirtualValue(checkV8Runtime(), null, key)) {
            v8Runtime.getV8Internal().add(this, virtualValue.get());
        }
    }

    @Override
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer) throws JavetException, E {
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
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<V8Value> iterator = getKeys()) {
            while (true) {
                try (Key key = (Key) iterator.getNext()) {
                    if (key == null) {
                        break;
                    }
                    consumer.accept(count, key);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    @CheckReturnValue
    public IV8ValueIterator<V8ValueArray> getEntries() throws JavetException {
        return invoke(FUNCTION_ENTRIES);
    }

    @Override
    @CheckReturnValue
    public IV8ValueIterator<V8Value> getKeys() throws JavetException {
        return invoke(FUNCTION_KEYS);
    }

    @Override
    public int getSize() throws JavetException {
        return checkV8Runtime().getV8Internal().getSize(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Set;
    }

}
