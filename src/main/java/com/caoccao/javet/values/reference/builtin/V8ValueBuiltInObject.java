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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.IV8ValueArray;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

/**
 * The type V8 value built in object.
 *
 * @since 0.9.2
 */
@SuppressWarnings("unchecked")
public class V8ValueBuiltInObject extends V8ValueObject {
    /**
     * The constant FUNCTION_ASSIGN.
     *
     * @since 0.9.2
     */
    public static final String FUNCTION_ASSIGN = "assign";
    /**
     * The constant FUNCTION_GET_OWN_PROPERTY_SYMBOLS.
     *
     * @since 0.9.11
     */
    public static final String FUNCTION_GET_OWN_PROPERTY_SYMBOLS = "getOwnPropertySymbols";
    /**
     * The constant FUNCTION_FREEZE.
     *
     * @since 3.0.1
     */
    public static final String FUNCTION_FREEZE = "freeze";
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = "Object";

    /**
     * Instantiates a new V8 value built in object.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    public V8ValueBuiltInObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Assign V8 value object.
     *
     * @param v8Value1 the V8 value 1
     * @param v8Value2 the V8 value 2
     * @return the V8 value object
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    @CheckReturnValue
    public V8ValueObject assign(V8ValueObject v8Value1, V8ValueObject v8Value2) throws JavetException {
        return invoke(FUNCTION_ASSIGN, Objects.requireNonNull(v8Value1), Objects.requireNonNull(v8Value2));
    }

    /**
     * Freeze V8 value object.
     *
     * @param v8ValueObject the V8 value object
     * @throws JavetException the javet exception
     * @since 3.0.1
     */
    public void freeze(V8ValueObject v8ValueObject) throws JavetException {
        invokeVoid(FUNCTION_FREEZE, Objects.requireNonNull(v8ValueObject));
    }

    /**
     * Gets own property symbols.
     *
     * @param iV8ValueObject the V8 value object
     * @return the own property symbols
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    public IV8ValueArray getOwnPropertySymbols(IV8ValueObject iV8ValueObject) throws JavetException {
        return invoke(FUNCTION_GET_OWN_PROPERTY_SYMBOLS, iV8ValueObject);
    }

    @Override
    public V8ValueBuiltInObject toClone() throws JavetException {
        return this;
    }
}
