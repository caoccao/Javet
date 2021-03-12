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

package com.caoccao.javet.values.reference.global;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type V8 value global object is a special object.
 * 1. It lives as long as V8 runtime lives.
 * 2. It does not have reference count.
 * 3. It cannot be set to weak.
 * 4. Its clone is itself.
 */
@SuppressWarnings("unchecked")
public final class V8ValueGlobalObject extends V8ValueObject {

    public static final String PROPERTY_JSON = "JSON";
    public static final String PROPERTY_PROMISE = "Promise";

    /**
     * Instantiates a new V8 value global object.
     *
     * @param handle the handle
     */
    V8ValueGlobalObject(long handle) {
        super(handle);
    }

    @Override
    protected void addReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void clearWeak() throws JavetException {
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        // Global object lives as long as V8 runtime lives.
    }

    public V8ValueGlobalJson getJson() throws JavetException {
        V8ValueObject v8ValueObject = get(PROPERTY_JSON);
        return v8Runtime.decorateV8Value(new V8ValueGlobalJson(v8ValueObject.getHandle()));
    }

    public V8ValueGlobalPromise getPromise() throws JavetException {
        V8ValueObject v8ValueObject = get(PROPERTY_PROMISE);
        return v8Runtime.decorateV8Value(new V8ValueGlobalPromise(v8ValueObject.getHandle()));
    }

    @Override
    public boolean isWeak() throws JavetException {
        return false;
    }

    @Override
    protected void removeReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void setWeak() throws JavetException {
    }

    @Override
    public V8ValueGlobalObject toClone() {
        return this;
    }
}
