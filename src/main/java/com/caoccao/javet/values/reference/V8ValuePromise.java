/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

/**
 * The type V8 value promise.
 *
 * @since 0.8.0
 */
public class V8ValuePromise extends V8ValueObject implements IV8ValuePromise {

    /**
     * Instantiates a new V8 value promise.
     *
     * @param handle the handle
     * @since 0.8.0
     */
    V8ValuePromise(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    @Override
    @CheckReturnValue
    public V8ValuePromise _catch(V8ValueFunction function) throws JavetException {
        return checkV8Runtime().getV8Internal().promiseCatch(this, function);
    }

    @Override
    public V8ValuePromise getPromise() throws JavetException {
        return checkV8Runtime().getV8Internal().promiseGetPromise(this);
    }

    @Override
    @CheckReturnValue
    public <Value extends V8Value> Value getResult() throws JavetException {
        return checkV8Runtime().getV8Internal().promiseGetResult(this);
    }

    @Override
    public int getState() throws JavetException {
        return checkV8Runtime().getV8Internal().promiseGetState(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Promise;
    }

    @Override
    public boolean hasHandler() throws JavetException {
        return checkV8Runtime().getV8Internal().promiseHasHandler(this);
    }

    @Override
    public void markAsHandled() throws JavetException {
        checkV8Runtime().getV8Internal().promiseMarkAsHandled(this);
    }

    @Override
    public boolean reject(V8Value v8Value) throws JavetException {
        return checkV8Runtime().getV8Internal().promiseReject(this, v8Value);
    }

    @Override
    public boolean resolve(V8Value v8Value) throws JavetException {
        return checkV8Runtime().getV8Internal().promiseResolve(this, v8Value);
    }

    @Override
    @CheckReturnValue
    public V8ValuePromise then(IV8ValueFunction functionFulfilled, IV8ValueFunction functionRejected)
            throws JavetException {
        return checkV8Runtime().getV8Internal().promiseThen(this, functionFulfilled, functionRejected);
    }
}
