/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;
import java.util.Objects;

@SuppressWarnings("unchecked")
public interface IV8ValuePromise extends IV8ValueObject {
    int STATE_PENDING = 0;
    int STATE_FULFILLED = 1;
    int STATE_REJECTED = 2;

    @CheckReturnValue
    V8ValuePromise except(V8ValueFunction function) throws JavetException;

    @CheckReturnValue
    default V8ValuePromise except(String codeString) throws JavetException {
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().getExecutor(codeString).execute()) {
            return except(v8ValueFunction);
        }
    }

    @CheckReturnValue
    <Value extends V8Value> Value getResult() throws JavetException;

    default boolean getResultBoolean() throws JavetException {
        return getResultPrimitive();
    }

    default double getResultDouble() throws JavetException {
        return getResultPrimitive();
    }

    default int getResultInteger() throws JavetException {
        return getResultPrimitive();
    }

    default long getResultLong() throws JavetException {
        return getResultPrimitive();
    }

    default <T> T getResultObject(Object key) throws JavetException {
        try {
            return getV8Runtime().toObject(getResult(), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R, T extends V8ValuePrimitive<R>> R getResultPrimitive()
            throws JavetException {
        V8Value v8Value = getResult();
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable ignored) {
        }
        return null;
    }

    default String getResultString() throws JavetException {
        return getResultPrimitive();
    }

    default ZonedDateTime getResultZonedDateTime() throws JavetException {
        return getResultPrimitive();
    }

    int getState() throws JavetException;

    boolean hasHandler() throws JavetException;

    default boolean isFulfilled() throws JavetException {
        return STATE_FULFILLED == getState();
    }

    default boolean isPending() throws JavetException {
        return STATE_PENDING == getState();
    }

    default boolean isRejected() throws JavetException {
        return STATE_REJECTED == getState();
    }

    void markAsHandled() throws JavetException;

    @CheckReturnValue
    default V8ValuePromise then(IV8ValueFunction functionFulfilled) throws JavetException {
        return then(functionFulfilled, null);
    }

    @CheckReturnValue
    V8ValuePromise then(IV8ValueFunction functionFulfilled, IV8ValueFunction functionRejected) throws JavetException;

    @CheckReturnValue
    default V8ValuePromise then(String codeStringFulfilled) throws JavetException {
        return then(codeStringFulfilled, null);
    }

    @CheckReturnValue
    default V8ValuePromise then(String codeStringFulfilled, String codeStringRejected) throws JavetException {
        Objects.requireNonNull(codeStringFulfilled);
        try (V8ValueFunction v8ValueFunctionFulfilled = getV8Runtime().getExecutor(codeStringFulfilled).execute()) {
            if (codeStringRejected == null) {
                return then(v8ValueFunctionFulfilled, null);
            } else {
                try (V8ValueFunction v8ValueFunctionRejected = getV8Runtime().getExecutor(codeStringRejected).execute()) {
                    return then(v8ValueFunctionFulfilled, v8ValueFunctionRejected);
                }
            }
        }
    }
}
