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
import com.caoccao.javet.values.reference.global.V8ValueGlobalJson;

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
    public void clearWeak() throws JavetException {
        checkV8Runtime();
        v8Runtime.clearWeak(this);
        weak = false;
    }

    @Override
    public void close() throws JavetException {
        close(false);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (!isWeak()) {
            forceClose = true;
        }
        super.close(forceClose);
    }

    @Override
    public boolean delete(V8Value key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        return v8Runtime.delete(this, key);
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
    public <T extends V8Value> T get(V8Value key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        return v8Runtime.get(this, key);
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
    public <T extends V8Value> T getProperty(V8Value key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        return v8Runtime.getProperty(this, key);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean has(V8Value value) throws JavetException {
        Objects.requireNonNull(value);
        checkV8Runtime();
        return v8Runtime.has(this, value);
    }

    @Override
    public boolean hasOwnProperty(V8Value key) throws JavetException {
        Objects.requireNonNull(key);
        checkV8Runtime();
        return v8Runtime.hasOwnProperty(this, key);
    }

    @Override
    public <T extends V8Value> T invoke(String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.invoke(this, functionName, returnResult, v8Values);
    }

    @Override
    public boolean isWeak() throws JavetException {
        return weak;
    }

    @Override
    public boolean isWeak(boolean force) throws JavetException {
        if (force) {
            checkV8Runtime();
            weak = v8Runtime.isWeak(this);
        }
        return weak;
    }

    @Override
    public boolean set(V8Value key, V8Value value) throws JavetException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        checkV8Runtime();
        return v8Runtime.set(this, key, value);
    }

    @Override
    public boolean setProperty(V8Value key, V8Value value) throws JavetException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        checkV8Runtime();
        return v8Runtime.setProperty(this, key, value);
    }

    @Override
    public void setWeak() throws JavetException {
        checkV8Runtime();
        v8Runtime.setWeak(this);
        weak = true;
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
            try (V8ValueGlobalJson v8ValueGlobalJson = v8Runtime.getGlobalObject().getJson()) {
                return v8ValueGlobalJson.stringify(this);
            }
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
