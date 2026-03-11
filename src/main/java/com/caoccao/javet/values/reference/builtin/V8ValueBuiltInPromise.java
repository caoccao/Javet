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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValuePromise;

import java.util.Objects;

/**
 * The type V8 value built-in Promise, providing access to JavaScript Promise static methods.
 */
@SuppressWarnings("unchecked")
public class V8ValueBuiltInPromise extends V8ValueFunction {
    /**
     * The constant NAME.
     */
    public static final String NAME = "Promise";

    /**
     * The constant FUNCTION_ALL.
     */
    public static final String FUNCTION_ALL = "all";
    /**
     * The constant FUNCTION_ALL_SETTLED.
     */
    public static final String FUNCTION_ALL_SETTLED = "allSettled";
    /**
     * The constant FUNCTION_ANY.
     */
    public static final String FUNCTION_ANY = "any";
    /**
     * The constant FUNCTION_RACE.
     */
    public static final String FUNCTION_RACE = "race";
    /**
     * The constant FUNCTION_REJECT.
     */
    public static final String FUNCTION_REJECT = "reject";
    /**
     * The constant FUNCTION_RESOLVE.
     */
    public static final String FUNCTION_RESOLVE = "resolve";

    /**
     * Instantiates a new V8 value built-in Promise.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the native handle
     * @throws JavetException the javet exception
     */
    public V8ValueBuiltInPromise(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Calls Promise.all() which resolves when all input promises resolve.
     *
     * @param v8Value the iterable of promises
     * @return the resulting promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise all(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_ALL, v8Value);
    }

    /**
     * Calls Promise.allSettled() which resolves when all input promises have settled.
     *
     * @param v8Value the iterable of promises
     * @return the resulting promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise allSettled(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_ALL_SETTLED, v8Value);
    }

    /**
     * Calls Promise.allSettled() without returning the result.
     *
     * @param v8Value the iterable of promises
     * @throws JavetException the javet exception
     */
    public void allSettledVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ALL_SETTLED, v8Value);
    }

    /**
     * Calls Promise.all() without returning the result.
     *
     * @param v8Value the iterable of promises
     * @throws JavetException the javet exception
     */
    public void allVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ALL, v8Value);
    }

    /**
     * Calls Promise.any() which resolves when any input promise resolves.
     *
     * @param v8Value the iterable of promises
     * @return the resulting promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise any(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_ANY, v8Value);
    }

    /**
     * Calls Promise.any() without returning the result.
     *
     * @param v8Value the iterable of promises
     * @throws JavetException the javet exception
     */
    public void anyVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ANY, v8Value);
    }

    /**
     * Calls Promise.race() which resolves or rejects when the first input promise settles.
     *
     * @param v8Value the iterable of promises
     * @return the resulting promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise race(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_RACE, v8Value);
    }

    /**
     * Calls Promise.race() without returning the result.
     *
     * @param v8Value the iterable of promises
     * @throws JavetException the javet exception
     */
    public void raceVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_RACE, v8Value);
    }

    /**
     * Calls Promise.reject() to create a rejected promise with the given reason.
     *
     * @param v8Value the rejection reason
     * @return the rejected promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise reject(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_REJECT, v8Value);
    }

    /**
     * Calls Promise.reject() without returning the result.
     *
     * @param v8Value the rejection reason
     * @throws JavetException the javet exception
     */
    public void rejectVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_REJECT, v8Value);
    }

    /**
     * Calls Promise.resolve() to create a resolved promise with the given value.
     *
     * @param v8Value the resolution value
     * @return the resolved promise
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValuePromise resolve(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invoke(FUNCTION_RESOLVE, v8Value);
    }

    /**
     * Calls Promise.resolve() without returning the result.
     *
     * @param v8Value the resolution value
     * @throws JavetException the javet exception
     */
    public void resolveVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_RESOLVE, v8Value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V8ValueBuiltInPromise toClone() throws JavetException {
        return this;
    }
}
