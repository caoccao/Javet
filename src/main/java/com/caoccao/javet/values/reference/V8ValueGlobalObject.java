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
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.builtin.*;

/**
 * The type V8 value global object is a special object.
 * 1. It lives as long as V8 runtime lives.
 * 2. It does not have reference count.
 * 3. It cannot be set to weak.
 * 4. Its clone is itself.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueGlobalObject extends V8ValueObject {
    /**
     * Instantiates a new V8 value global object.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    V8ValueGlobalObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    protected void addReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void clearWeak() {
        // Global object is persisted.
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        // Global object lives as long as V8 runtime lives.
    }

    /**
     * Gets built-in JSON.
     *
     * @return the built-in JSON
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    @CheckReturnValue
    public V8ValueBuiltInJson getBuiltInJson() throws JavetException {
        V8Value v8Value = v8Runtime.getExecutor(V8ValueBuiltInJson.NAME).execute();
        if (v8Value instanceof V8ValueObject) {
            return new V8ValueBuiltInJson(v8Runtime, ((V8ValueObject) v8Value).getHandle());
        }
        JavetResourceUtils.safeClose(v8Value);
        throw new JavetException(
                JavetError.NotSupported,
                SimpleMap.of(JavetError.PARAMETER_FEATURE, V8ValueBuiltInJson.NAME));
    }

    /**
     * Gets built-in Object.
     *
     * @return the built-in Object
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    @CheckReturnValue
    public V8ValueBuiltInObject getBuiltInObject() throws JavetException {
        V8Value v8Value = v8Runtime.getExecutor(V8ValueBuiltInObject.NAME).execute();
        if (v8Value instanceof V8ValueObject) {
            return new V8ValueBuiltInObject(v8Runtime, ((V8ValueObject) v8Value).getHandle());
        }
        JavetResourceUtils.safeClose(v8Value);
        throw new JavetException(
                JavetError.NotSupported,
                SimpleMap.of(JavetError.PARAMETER_FEATURE, V8ValueBuiltInObject.NAME));
    }

    /**
     * Gets built-in Promise.
     *
     * @return the built-in Promise
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    @CheckReturnValue
    public V8ValueBuiltInPromise getBuiltInPromise() throws JavetException {
        V8Value v8Value = v8Runtime.getExecutor(V8ValueBuiltInPromise.NAME).execute();
        if (v8Value instanceof V8ValueObject) {
            return new V8ValueBuiltInPromise(v8Runtime, ((V8ValueObject) v8Value).getHandle());
        }
        JavetResourceUtils.safeClose(v8Value);
        throw new JavetException(
                JavetError.NotSupported,
                SimpleMap.of(JavetError.PARAMETER_FEATURE, V8ValueBuiltInPromise.NAME));
    }

    /**
     * Gets built-in Reflect.
     *
     * @return the built-in Reflect
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    public V8ValueBuiltInReflect getBuiltInReflect() throws JavetException {
        V8Value v8Value = v8Runtime.getExecutor(V8ValueBuiltInReflect.NAME).execute();
        if (v8Value instanceof V8ValueObject) {
            return new V8ValueBuiltInReflect(v8Runtime, ((V8ValueObject) v8Value).getHandle());
        }
        JavetResourceUtils.safeClose(v8Value);
        throw new JavetException(
                JavetError.NotSupported,
                SimpleMap.of(JavetError.PARAMETER_FEATURE, V8ValueBuiltInReflect.NAME));
    }

    /**
     * Gets built-in Symbol.
     *
     * @return the built-in Symbol
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    public V8ValueBuiltInSymbol getBuiltInSymbol() throws JavetException {
        V8Value v8Value = v8Runtime.getExecutor(V8ValueBuiltInSymbol.NAME).execute();
        if (v8Value instanceof V8ValueObject) {
            return new V8ValueBuiltInSymbol(v8Runtime, ((V8ValueObject) v8Value).getHandle());
        }
        JavetResourceUtils.safeClose(v8Value);
        throw new JavetException(
                JavetError.NotSupported,
                SimpleMap.of(JavetError.PARAMETER_FEATURE, V8ValueBuiltInSymbol.NAME));
    }

    @Override
    public boolean isWeak() {
        return false;
    }

    @Override
    protected void removeReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void setWeak() {
        // Global object is persisted.
    }

    @Override
    public V8ValueGlobalObject toClone(boolean referenceCopy) {
        return this;
    }
}
