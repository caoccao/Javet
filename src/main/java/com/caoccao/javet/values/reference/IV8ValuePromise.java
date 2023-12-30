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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The interface V8 value promise is 2-sided.
 * 1. As a promise resolver, its API includes getPromise(), resolve(), reject().
 * 2. As a promise, its API includes _catch(), then(), hasHandler(), markAsHandled(), getState(), getResult().
 *
 * @since 0.8.0
 */
@SuppressWarnings("unchecked")
public interface IV8ValuePromise extends IV8ValueObject {
    /**
     * The constant STATE_PENDING.
     *
     * @since 0.8.0
     */
    int STATE_PENDING = 0;
    /**
     * The constant STATE_FULFILLED.
     *
     * @since 0.8.0
     */
    int STATE_FULFILLED = 1;
    /**
     * The constant STATE_REJECTED.
     *
     * @since 0.8.0
     */
    int STATE_REJECTED = 2;

    /**
     * Catch.
     *
     * @param functionCatch the function catch
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    V8ValuePromise _catch(IV8ValueFunction functionCatch) throws JavetException;

    /**
     * Catch.
     *
     * @param codeString the code string
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    default V8ValuePromise _catch(String codeString) throws JavetException {
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().getExecutor(codeString).execute()) {
            return _catch(v8ValueFunction);
        }
    }

    /**
     * Gets promise.
     *
     * @return the promise
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    @CheckReturnValue
    V8ValuePromise getPromise() throws JavetException;

    /**
     * Gets result.
     *
     * @param <Value> the type parameter
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <Value extends V8Value> Value getResult() throws JavetException;

    /**
     * Gets result big integer.
     *
     * @return the result big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default BigInteger getResultBigInteger() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result boolean.
     *
     * @return the result boolean
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default boolean getResultBoolean() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result double.
     *
     * @return the result double
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default double getResultDouble() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result integer.
     *
     * @return the result integer
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default int getResultInteger() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result long.
     *
     * @return the result long
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default long getResultLong() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result object.
     *
     * @param <T> the type parameter
     * @return the result object
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default <T> T getResultObject() throws JavetException {
        try {
            return getV8Runtime().toObject(getResult(), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Gets result primitive.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @return the result primitive
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default <R, T extends V8ValuePrimitive<R>> R getResultPrimitive()
            throws JavetException {
        V8Value v8Value = getResult();
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets result string.
     *
     * @return the result string
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default String getResultString() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets result zoned date time.
     *
     * @return the result zoned date time
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default ZonedDateTime getResultZonedDateTime() throws JavetException {
        return getResultPrimitive();
    }

    /**
     * Gets state.
     *
     * @return the state
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    int getState() throws JavetException;

    /**
     * Has handler.
     *
     * @return true : the handle exists, false : the handle doesn't exist
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    boolean hasHandler() throws JavetException;

    /**
     * Is fulfilled.
     *
     * @return true : fulfilled, false : not fulfilled
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default boolean isFulfilled() throws JavetException {
        return STATE_FULFILLED == getState();
    }

    /**
     * Is pending.
     *
     * @return true : pending, false : not pending
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default boolean isPending() throws JavetException {
        return STATE_PENDING == getState();
    }

    /**
     * Is rejected.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default boolean isRejected() throws JavetException {
        return STATE_REJECTED == getState();
    }

    /**
     * Mark as handled.
     *
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    void markAsHandled() throws JavetException;

    /**
     * Register a listener which receives the promise resolve, reject or catch event.
     *
     * @param listener the listener
     * @return true : the event is fired, false : the event is not fired
     * @throws JavetException the javet exception
     * @since 2.0.4
     */
    boolean register(IListener listener) throws JavetException;

    /**
     * Reject.
     *
     * @param v8Value the V8 value
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    boolean reject(V8Value v8Value) throws JavetException;

    /**
     * Reject.
     *
     * @param object the object
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    default boolean reject(Object object) throws JavetException {
        try (V8Value v8Value = getV8Runtime().toV8Value(object)) {
            return reject(v8Value);
        }
    }

    /**
     * Resolve.
     *
     * @param v8Value the V8 value
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    boolean resolve(V8Value v8Value) throws JavetException;

    /**
     * Resolve.
     *
     * @param object the object
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    default boolean resolve(Object object) throws JavetException {
        try (V8Value v8Value = getV8Runtime().toV8Value(object)) {
            return resolve(v8Value);
        }
    }

    /**
     * Then.
     *
     * @param functionFulfilled the function fulfilled
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    default V8ValuePromise then(IV8ValueFunction functionFulfilled) throws JavetException {
        return then(functionFulfilled, null);
    }

    /**
     * Then.
     *
     * @param functionFulfilled the function fulfilled
     * @param functionRejected  the function rejected
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    V8ValuePromise then(IV8ValueFunction functionFulfilled, IV8ValueFunction functionRejected) throws JavetException;

    /**
     * Then.
     *
     * @param codeStringFulfilled the code string fulfilled
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    default V8ValuePromise then(String codeStringFulfilled) throws JavetException {
        return then(codeStringFulfilled, null);
    }

    /**
     * Then.
     *
     * @param codeStringFulfilled the code string fulfilled
     * @param codeStringRejected  the code string rejected
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
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

    /**
     * The interface Listener is the one which receives the promise resolve, reject or catch event.
     *
     * @since 2.0.4
     */
    interface IListener {
        /**
         * The constant ON_CATCH.
         *
         * @since 2.0.4
         */
        String ON_CATCH = "onCatch";
        /**
         * The constant ON_FULFILLED.
         *
         * @since 2.0.4
         */
        String ON_FULFILLED = "onFulfilled";
        /**
         * The constant ON_REJECTED.
         *
         * @since 2.0.4
         */
        String ON_REJECTED = "onRejected";

        /**
         * On catch.
         *
         * @param v8Value the V8 value
         * @since 2.0.4
         */
        void onCatch(V8Value v8Value);

        /**
         * On fulfilled.
         *
         * @param v8Value the V8 value
         * @since 2.0.4
         */
        void onFulfilled(V8Value v8Value);

        /**
         * On rejected.
         *
         * @param v8Value the V8 value
         * @since 2.0.4
         */
        void onRejected(V8Value v8Value);
    }
}
