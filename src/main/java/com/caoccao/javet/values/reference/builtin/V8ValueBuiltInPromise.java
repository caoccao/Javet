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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValuePromise;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInPromise extends V8ValueObject {

    public static final String FUNCTION_ALL = "all";
    public static final String FUNCTION_ALL_SETTLED = "allSettled";
    public static final String FUNCTION_ANY = "any";
    public static final String FUNCTION_RACE = "race";
    public static final String FUNCTION_REJECT = "reject";
    public static final String FUNCTION_RESOLVE = "resolve";

    public V8ValueBuiltInPromise(long handle) {
        super(handle);
    }

    public V8ValuePromise all(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_ALL, v8Value);
    }

    public void allVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ALL, v8Value);
    }

    public V8ValuePromise allSettled(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_ALL_SETTLED, v8Value);
    }

    public void allSettledVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ALL_SETTLED, v8Value);
    }

    public V8ValuePromise any(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_ANY, v8Value);
    }

    public void anyVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_ANY, v8Value);
    }

    public V8ValuePromise race(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_RACE, v8Value);
    }

    public void raceVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_RACE, v8Value);
    }

    public void rejectVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_REJECT, v8Value);
    }

    public V8ValuePromise reject(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_REJECT, v8Value);
    }

    public void resolveVoid(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        invokeVoid(FUNCTION_RESOLVE, v8Value);
    }

    public V8ValuePromise resolve(V8Value v8Value) throws JavetException {
        Objects.requireNonNull(v8Value);
        return invokeV8Value(FUNCTION_RESOLVE, v8Value);
    }

    @Override
    public V8ValueBuiltInPromise toClone() throws JavetException {
        return this;
    }
}
