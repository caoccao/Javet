/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type V8 error template is for generating the V8 error message.
 *
 * @since 3.0.4
 */
public final class V8ErrorTemplate {
    private V8ErrorTemplate() {
    }

    /**
     * RangeError: Invalid index : ${index}.
     *
     * @param index the index
     * @return the message
     * @since 3.0.4
     */
    public static String rangeErrorInvalidIndex(int index) {
        return "Invalid index : " + index;
    }

    /**
     * RangeError: Start ${start} is out or range.
     *
     * @param start the start
     * @return the message
     * @since 3.0.4
     */
    public static String rangeErrorStartIsOutOfRange(int start) {
        return "Start " + start + " is out of range";
    }

    /**
     * TypeError: ${functionName}() is not supported.
     *
     * @param functionName the function name
     * @return the message
     * @since 3.0.4
     */
    public static String typeErrorFunctionIsNotSupported(String functionName) {
        return Objects.requireNonNull(functionName) + "() is not supported";
    }

    /**
     * TypeError: Reduce of empty array with no initial value.
     *
     * @return the message
     * @since 3.0.4
     */
    public static String typeErrorReduceOfEmptyArrayWithNoInitialValue() {
        return "Reduce of empty array with no initial value";
    }

    /**
     * TypeError: ${value} is not a function.
     *
     * @param functionName the function name
     * @return the message
     * @since 3.1.0
     */
    public static String typeErrorValueIsNotAFunction(String functionName) {
        return Objects.requireNonNull(functionName) + " is not a function";
    }

    /**
     * TypeError: ${value} is not a function.
     *
     * @param v8Value the V8 value
     * @return the message
     * @since 3.0.4
     */
    public static String typeErrorValueIsNotAFunction(V8Value v8Value) {
        return typeErrorValueIsNotAFunction(V8ValueUtils.asString(v8Value));
    }
}
