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
import com.caoccao.javet.utils.V8ValueIteratorUtils;
import com.caoccao.javet.values.virtual.V8VirtualList;

@SuppressWarnings("unchecked")
public class V8ValueSet extends V8ValueObject implements IV8ValueSet {

    public static final String FUNCTION_KEYS = "keys";

    public V8ValueSet(long handle) {
        super(handle);
    }

    @Override
    public void add(V8Value key) throws JavetException {
        checkV8Runtime();
        v8Runtime.add(this, key);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Set;
    }

    @Override
    public V8VirtualList<V8Value> getKeys() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject setIterator = invoke(FUNCTION_KEYS)) {
            return V8ValueIteratorUtils.convertIteratorToV8ValueList(setIterator);
        }
    }

    @Override
    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

    @Override
    public boolean has(V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.has(this, value);
    }
}
