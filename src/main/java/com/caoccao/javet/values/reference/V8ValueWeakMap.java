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
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueWeakMap extends V8ValueObject {
    V8ValueWeakMap(long handle) {
        super(handle);
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof V8ValueReference)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        invokeVoid(FUNCTION_DELETE, key);
        return true;
    }

    public <T extends V8Value> T get(String key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, v8Runtime.createV8ValueString(key));
    }

    @Override
    public <T extends V8Value> T get(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof V8ValueReference)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        return invokeV8Value(FUNCTION_GET, key);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.WeakMap;
    }

    @Override
    public boolean has(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof V8ValueReference)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        return invokeBoolean(FUNCTION_HAS, key);
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (!(key instanceof V8ValueReference)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        invokeVoid(FUNCTION_SET, key, value);
        return true;
    }

    @Override
    public V8ValueWeakMap toClone() throws JavetException {
        checkV8Runtime();
        return v8Runtime.cloneV8Value(this);
    }
}
