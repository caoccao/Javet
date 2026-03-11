/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type V8 value WeakMap, representing a JavaScript WeakMap object.
 */
@SuppressWarnings("unchecked")
public class V8ValueWeakMap extends V8ValueObject {
    V8ValueWeakMap(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof IV8ValueObject)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        invokeVoid(FUNCTION_DELETE, key);
        return true;
    }

    /**
     * Gets a value by string key from this WeakMap.
     *
     * @param <T> the V8 value type
     * @param key the string key
     * @return the value associated with the key
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public <T extends V8Value> T get(String key) throws JavetException {
        return checkV8Runtime().getV8Internal().objectGet(this, v8Runtime.createV8ValueString(key));
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T get(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof IV8ValueObject)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        return invoke(FUNCTION_GET, key);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.WeakMap;
    }

    @Override
    public boolean has(Object key) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof IV8ValueObject)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        return invokeBoolean(FUNCTION_HAS, key);
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        Objects.requireNonNull(key);
        if (!(key instanceof IV8ValueObject)) {
            throw new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, key.toString()));
        }
        invokeVoid(FUNCTION_SET, key, value);
        return true;
    }
}
