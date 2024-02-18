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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;

import java.util.Objects;

/**
 * The type Base javet proxy symbol converter.
 *
 * @param <T> the type parameter
 * @since 1.0.4
 */
public abstract class BaseJavetProxySymbolConverter<T> implements IJavetProxySymbolConverter {
    /**
     * The constant METHOD_NAME_TO_V8_VALUE.
     *
     * @since 1.0.4
     */
    protected static final String METHOD_NAME_TO_V8_VALUE = "toV8Value";
    /**
     * The constant NULL.
     *
     * @since 2.0.0
     */
    protected static final String NULL = "null";
    /**
     * The constant OBJECT_CONVERTER.
     *
     * @since 1.0.4
     */
    protected static final JavetObjectConverter OBJECT_CONVERTER = new JavetObjectConverter();
    /**
     * The Target object.
     *
     * @since 1.0.4
     */
    protected T targetObject;
    /**
     * The V8 runtime.
     *
     * @since 1.0.4
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new Base javet proxy symbol converter.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 1.0.4
     */
    public BaseJavetProxySymbolConverter(V8Runtime v8Runtime, T targetObject) {
        this.targetObject = Objects.requireNonNull(targetObject);
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
    }

    @CheckReturnValue
    @Override
    public V8ValueFunction getV8ValueFunction() throws JavetException {
        return v8Runtime.createV8ValueFunction(
                new JavetCallbackContext(
                        METHOD_NAME_TO_V8_VALUE, JavetCallbackType.DirectCallNoThisAndResult,
                        (IJavetDirectCallable.NoThisAndResult<?>) this::toV8Value));
    }

    /**
     * To V8 value.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    @CheckReturnValue
    @Override
    public V8Value toV8Value(V8Value... v8Values) throws JavetException {
        return OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject);
    }
}
