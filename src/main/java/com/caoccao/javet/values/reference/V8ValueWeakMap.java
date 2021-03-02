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
import com.caoccao.javet.values.primitive.V8ValueString;

@SuppressWarnings("unchecked")
public class V8ValueWeakMap extends V8ValueObject {
    public static final String FUNCTION_DELETE = "delete";
    public static final String FUNCTION_GET = "get";
    public static final String FUNCTION_HAS = "has";
    public static final String FUNCTION_SET = "set";

    V8ValueWeakMap(long handle) {
        super(handle);
    }

    @Override
    public boolean delete(V8Value key) throws JavetException {
        try (V8ValueFunction v8ValueFunction = get(FUNCTION_DELETE)) {
            v8ValueFunction.call(this, false, key);
        }
        return true;
    }

    @Override
    public <T extends V8Value> T get(String key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, new V8ValueString(key));
    }

    @Override
    public <T extends V8Value> T get(V8Value key) throws JavetException {
        try (V8ValueFunction v8ValueFunction = get(FUNCTION_GET)) {
            return v8ValueFunction.call(this, key);
        }
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.WeakMap;
    }

    @Override
    public boolean has(V8Value key) throws JavetException {
        try (V8ValueFunction v8ValueFunction = get(FUNCTION_HAS)) {
            return v8ValueFunction.callBoolean(this, key);
        }
    }

    @Override
    public boolean set(V8Value key, V8Value value) throws JavetException {
        try (V8ValueFunction v8ValueFunction = get(FUNCTION_SET)) {
            v8ValueFunction.call(this, false, key, value);
        }
        return true;
    }

    @Override
    public V8ValueWeakMap toClone() throws JavetException {
        checkV8Runtime();
        return v8Runtime.cloneV8Value(this);
    }
}
