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
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetConsumer;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    protected static final String FUNCTION_ADD = "add";
    protected static final String FUNCTION_DELETE = "delete";
    protected static final String FUNCTION_GET = "get";
    protected static final String FUNCTION_HAS = "has";
    protected static final String FUNCTION_SET = "set";

    protected V8ValueObject(long handle) {
        super(handle);
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        V8Value v8Value = null;
        V8Value toBeClosedV8Value = null;
        try {
            if (key instanceof V8Value) {
                v8Value = (V8Value) key;
            } else {
                toBeClosedV8Value = v8Value = v8Runtime.getConverter().toV8Value(v8Runtime, key);
            }
            return v8Runtime.delete(this, v8Value);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Value);
        }
    }

    @Override
    public <Key extends V8Value> int forEach(IJavetConsumer<Key> consumer) throws JavetException {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach(consumer);
        }
    }

    @Override
    public <Key extends V8Value, Value extends V8Value> int forEach(
            IJavetBiConsumer<Key, Value> consumer) throws JavetException {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach((Key key) -> {
                try (Value value = get(key)) {
                    consumer.accept(key, value);
                }
            });
        }
    }

    @Override
    public <T extends V8Value> T get(Object key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        V8Value v8Value = null;
        V8Value toBeClosedV8Value = null;
        try {
            if (key instanceof V8Value) {
                v8Value = (V8Value) key;
            } else {
                toBeClosedV8Value = v8Value = v8Runtime.getConverter().toV8Value(v8Runtime, key);
            }
            return v8Runtime.get(this, v8Value);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Value);
        }
    }

    @Override
    public int getIdentityHash() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getIdentityHash(this);
    }

    @Override
    public IV8ValueArray getOwnPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getOwnPropertyNames(this);
    }

    @Override
    public IV8ValueArray getPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getPropertyNames(this);
    }

    @Override
    public <T extends V8Value> T getProperty(Object key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        V8Value v8Value = null;
        V8Value toBeClosedV8Value = null;
        try {
            if (key instanceof V8Value) {
                v8Value = (V8Value) key;
            } else {
                toBeClosedV8Value = v8Value = v8Runtime.getConverter().toV8Value(v8Runtime, key);
            }
            return v8Runtime.getProperty(this, v8Value);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Value);
        }
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean has(Object value) throws JavetException {
        Objects.requireNonNull(value);
        checkV8Runtime();
        V8Value v8Value = null;
        V8Value toBeClosedV8Value = null;
        try {
            if (value instanceof V8Value) {
                v8Value = (V8Value) value;
            } else {
                toBeClosedV8Value = v8Value = v8Runtime.getConverter().toV8Value(v8Runtime, value);
            }
            return v8Runtime.has(this, v8Value);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Value);
        }
    }

    @Override
    public boolean hasOwnProperty(Object key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        V8Value v8Value = null;
        V8Value toBeClosedV8Value = null;
        try {
            if (key instanceof V8Value) {
                v8Value = (V8Value) key;
            } else {
                toBeClosedV8Value = v8Value = v8Runtime.getConverter().toV8Value(v8Runtime, key);
            }
            return v8Runtime.hasOwnProperty(this, v8Value);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Value);
        }
    }

    @Override
    public <T extends V8Value> T invoke(String functionName, boolean returnResult, Object... objects)
            throws JavetException {
        checkV8Runtime();
        List<V8Value> v8Values = new ArrayList<>();
        List<V8Value> toBeClosedV8Values = new ArrayList<>();
        IJavetConverter converter = v8Runtime.getConverter();
        try {
            for (Object object : objects) {
                if (object instanceof V8Value) {
                    v8Values.add((V8Value) object);
                } else {
                    V8Value v8Value = converter.toV8Value(v8Runtime, object);
                    toBeClosedV8Values.add(v8Value);
                    v8Values.add(v8Value);
                }
            }
            return v8Runtime.invoke(this, functionName, returnResult, v8Values.toArray(new V8Value[0]));
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Values);
        }
    }

    @Override
    public boolean sameValue(V8Value v8Value) throws JavetException {
        if (v8Value == null || !(v8Value instanceof V8ValueObject)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
        if (getHandle() == v8ValueObject.getHandle()) {
            return true;
        }
        return v8Runtime.sameValue(this, v8ValueObject);
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        checkV8Runtime();
        V8Value v8ValueKey = null;
        V8Value v8ValueValue = null;
        List<V8Value> toBeClosedV8Values = new ArrayList<>();
        IJavetConverter converter = v8Runtime.getConverter();
        try {
            if (key instanceof V8Value) {
                v8ValueKey = (V8Value) key;
            } else {
                v8ValueKey = converter.toV8Value(v8Runtime, key);
                toBeClosedV8Values.add(v8ValueKey);
            }
            if (value instanceof V8Value) {
                v8ValueValue = (V8Value) value;
            } else {
                v8ValueValue = converter.toV8Value(v8Runtime, value);
                toBeClosedV8Values.add(v8ValueValue);
            }
            return v8Runtime.set(this, v8ValueKey, v8ValueValue);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Values);
        }
    }

    @Override
    public boolean setProperty(Object key, Object value) throws JavetException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        checkV8Runtime();
        V8Value v8ValueKey = null;
        V8Value v8ValueValue = null;
        List<V8Value> toBeClosedV8Values = new ArrayList<>();
        IJavetConverter converter = v8Runtime.getConverter();
        try {
            if (key instanceof V8Value) {
                v8ValueKey = (V8Value) key;
            } else {
                v8ValueKey = converter.toV8Value(v8Runtime, key);
                toBeClosedV8Values.add(v8ValueKey);
            }
            if (value instanceof V8Value) {
                v8ValueValue = (V8Value) value;
            } else {
                v8ValueValue = converter.toV8Value(v8Runtime, value);
                toBeClosedV8Values.add(v8ValueValue);
            }
            return v8Runtime.setProperty(this, v8ValueKey, v8ValueValue);
        } finally {
            JavetResourceUtils.safeClose(toBeClosedV8Values);
        }
    }

    @Override
    public boolean strictEquals(V8Value v8Value) throws JavetException {
        if (!(v8Value instanceof V8ValueObject)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
        if (getHandle() == v8ValueObject.getHandle()) {
            return true;
        }
        return v8Runtime.strictEquals(this, v8ValueObject);
    }

    @Override
    public String toProtoString() {
        try {
            checkV8Runtime();
            return v8Runtime.toProtoString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }

    @Override
    public String toJsonString() {
        try {
            checkV8Runtime();
            try (V8ValueBuiltInJson v8ValueBuiltInJson = v8Runtime.getGlobalObject().getJson()) {
                return v8ValueBuiltInJson.stringify(this);
            }
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
