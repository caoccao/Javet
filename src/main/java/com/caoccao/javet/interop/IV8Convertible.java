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

package com.caoccao.javet.interop;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

/**
 * The interface V8 convertible.
 *
 * @since 0.8.5
 */
public interface IV8Convertible {
    /**
     * Convert from V8 value to object.
     * The caller is responsible for closing the V8 value.
     *
     * @param <T>     the type parameter
     * @param <V>     the type parameter
     * @param v8Value the V8 value
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    <T, V extends V8Value> T toObject(V v8Value) throws JavetException;

    /**
     * Convert from V8 value to object.
     * The V8 value is closed automatically if autoClose is set to true.
     *
     * @param <T>       the type parameter
     * @param <V>       the type parameter
     * @param v8Value   the V8 value
     * @param autoClose the auto close
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default <T, V extends V8Value> T toObject(V v8Value, boolean autoClose) throws JavetException {
        if (autoClose) {
            try {
                return toObject(v8Value);
            } finally {
                JavetResourceUtils.safeClose(v8Value);
            }
        } else {
            return toObject(v8Value);
        }
    }

    /**
     * Convert object to V8 value.
     *
     * @param <T>    the type parameter
     * @param <V>    the type parameter
     * @param object the object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T, V extends V8Value> V toV8Value(T object) throws JavetException;
}
