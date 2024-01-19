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
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetBiIndexedConsumer;
import com.caoccao.javet.interfaces.IJavetUniConsumer;
import com.caoccao.javet.interfaces.IJavetUniIndexedConsumer;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualValue;
import com.caoccao.javet.values.virtual.V8VirtualValueList;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueMap extends V8ValueObject implements IV8ValueMap {
    protected static final String FUNCTION_ENTRIES = "entries";
    protected static final String FUNCTION_KEYS = "keys";
    protected static final String FUNCTION_VALUES = "values";

    V8ValueMap(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapDelete(this, virtualKey.get());
        }
    }

    @Override
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<Key> iterator = (IV8ValueIterator<Key>) getKeys()) {
            while (true) {
                try (Key key = iterator.getNext()) {
                    if (key == null) {
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
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<Key> iterator = (IV8ValueIterator<Key>) getKeys()) {
            while (true) {
                try (Key key = iterator.getNext()) {
                    if (key == null) {
                        break;
                    }
                    consumer.accept(count, key);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<V8ValueArray> iterator = getEntries()) {
            while (true) {
                try (V8ValueArray entry = iterator.getNext()) {
                    if (entry == null) {
                        break;
                    }
                    try (Key key = entry.get(0); Value value = entry.get(1)) {
                        consumer.accept(key, value);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        int count = 0;
        try (IV8ValueIterator<V8ValueArray> iterator = getEntries()) {
            while (true) {
                try (V8ValueArray entry = iterator.getNext()) {
                    if (entry == null) {
                        break;
                    }
                    try (Key key = entry.get(0); Value value = entry.get(1)) {
                        consumer.accept(count, key, value);
                    }
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public <T extends V8Value> T get(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGet(this, virtualKey.get());
        }
    }

    @Override
    public Boolean getBoolean(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGetBoolean(this, virtualKey.get());
        }
    }

    @Override
    public Double getDouble(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGetDouble(this, virtualKey.get());
        }
    }

    @Override
    @CheckReturnValue
    public IV8ValueIterator<V8ValueArray> getEntries() throws JavetException {
        return invoke(FUNCTION_ENTRIES);
    }

    @Override
    public Integer getInteger(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGetInteger(this, virtualKey.get());
        }
    }

    @Override
    @CheckReturnValue
    public IV8ValueIterator<? extends V8Value> getKeys() throws JavetException {
        return invoke(FUNCTION_KEYS);
    }

    @Override
    public Long getLong(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGetLong(this, virtualKey.get());
        }
    }

    @Override
    public int getSize() throws JavetException {
        return checkV8Runtime().getV8Internal().mapGetSize(this);
    }

    @Override
    public String getString(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapGetString(this, virtualKey.get());
        }
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Map;
    }

    @Override
    @CheckReturnValue
    public IV8ValueIterator<? extends V8Value> getValues() throws JavetException {
        return invoke(FUNCTION_VALUES);
    }

    @Override
    public boolean has(Object value) throws JavetException {
        try (V8VirtualValue virtualValue = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(value))) {
            return v8Runtime.getV8Internal().mapHas(this, virtualValue.get());
        }
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key));
             V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, null, value)) {
            return v8Runtime.getV8Internal().mapSet(this, virtualKey.get(), virtualValue.get());
        }
    }

    @Override
    public boolean set(Object... keysAndValues) throws JavetException {
        assert keysAndValues.length > 0 && keysAndValues.length % 2 == 0 : ERROR_THE_KEY_VALUE_PAIR_MUST_MATCH;
        final int length = keysAndValues.length;
        final int pairLength = keysAndValues.length >> 1;
        Object[] keys = new Object[pairLength];
        Object[] values = new Object[pairLength];
        for (int i = 0; i < pairLength; i++) {
            keys[i] = keysAndValues[i * 2];
            values[i] = keysAndValues[i * 2 + 1];
        }
        try (V8VirtualValueList v8VirtualValueKeys = new V8VirtualValueList(checkV8Runtime(), OBJECT_CONVERTER, keys);
             V8VirtualValueList v8VirtualValueValues = new V8VirtualValueList(v8Runtime, null, values)) {
            V8Value[] v8ValueKeys = v8VirtualValueKeys.get();
            V8Value[] v8ValueValues = v8VirtualValueValues.get();
            V8Value[] v8Values = new V8Value[length];
            for (int i = 0; i < pairLength; i++) {
                v8Values[i * 2] = v8ValueKeys[i];
                v8Values[i * 2 + 1] = v8ValueValues[i];
            }
            return v8Runtime.getV8Internal().mapSet(this, v8Values);
        }
    }

    @Override
    public boolean setBoolean(Object key, Boolean value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().mapSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().mapSetBoolean(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setDouble(Object key, Double value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().mapSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().mapSetDouble(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setInteger(Object key, Integer value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().mapSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().mapSetInteger(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setLong(Object key, Long value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().mapSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().mapSetLong(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setNull(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapSetNull(this, virtualKey.get());
        }
    }

    @Override
    public boolean setString(Object key, String value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapSetString(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setUndefined(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().mapSetUndefined(this, virtualKey.get());
        }
    }
}
