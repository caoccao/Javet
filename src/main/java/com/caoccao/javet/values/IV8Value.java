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

package com.caoccao.javet.values;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.IV8Cloneable;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

/**
 * The interface V8 value.
 */
public interface IV8Value extends IJavetClosable, IV8Cloneable {
    /**
     * Equals.
     * <p>
     * The behavior is different from JS behavior but is the same as Java behavior.
     *
     * @param v8Value the V8 value
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean equals(V8Value v8Value) throws JavetException;

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     */
    V8Runtime getV8Runtime();

    /**
     * Is null.
     *
     * @return the boolean
     */
    default boolean isNull() {
        return this instanceof V8ValueNull;
    }

    /**
     * Is null or undefined.
     *
     * @return the boolean
     */
    default boolean isNullOrUndefined() {
        return isNull() || isUndefined();
    }

    /**
     * Is undefined.
     *
     * @return the boolean
     */
    default boolean isUndefined() {
        return this instanceof V8ValueUndefined;
    }

    /**
     * Same value.
     * <p>
     * The behavior is different from JS behavior but is the same as Java behavior.
     *
     * @param v8Value the V8 value
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean sameValue(V8Value v8Value) throws JavetException;

    /**
     * Strict equals boolean.
     * <p>
     * The behavior is different from JS behavior but is the same as Java behavior.
     *
     * @param v8Value the V8 value
     * @return the boolean
     * @throws JavetException the javet exception
     */
    boolean strictEquals(V8Value v8Value) throws JavetException;
}
