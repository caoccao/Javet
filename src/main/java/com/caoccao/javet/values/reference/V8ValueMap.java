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
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.utils.V8ValueIteratorUtils;
import com.caoccao.javet.values.virtual.V8VirtualList;

import java.util.List;

@SuppressWarnings("unchecked")
public class V8ValueMap extends V8ValueObject implements IV8ValueMap {

    public static final String FUNCTION_KEYS = "keys";
    public static final String FUNCTION_VALUES = "values";
    public static final String FUNCTION_ENTRIES = "entries";

    public V8ValueMap(long handle) {
        super(handle);
    }

    @Override
    public V8VirtualList<V8Value> getEntries() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject mapIterator = invoke(FUNCTION_ENTRIES)) {
            return V8ValueIteratorUtils.convertIteratorToV8ValueList(mapIterator);
        }
    }

    @Override
    public V8VirtualList<V8Value> getKeys() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject mapIterator = invoke(FUNCTION_KEYS)) {
            return V8ValueIteratorUtils.convertIteratorToV8ValueList(mapIterator);
        }
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
    public List<V8Value> getValues() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject mapIterator = invoke(FUNCTION_VALUES)) {
            return V8ValueIteratorUtils.convertIteratorToV8ValueList(mapIterator);
        }
    }

    @Override
    public boolean has(V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.has(this, value);
    }
}
