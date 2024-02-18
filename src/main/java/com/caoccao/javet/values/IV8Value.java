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
 *
 * @since 0.7.0
 */
public interface IV8Value extends IJavetClosable, IV8Cloneable {
    /**
     * A simulation of the JS <code>if (variable)</code>
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default boolean asBoolean() throws JavetException {
        // All objects become true.
        return true;
    }

    /**
     * As double.
     *
     * @return the double value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default double asDouble() throws JavetException {
        return asInt();
    }

    /**
     * As int.
     *
     * @return the int value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default int asInt() throws JavetException {
        return 0;
    }

    /**
     * As long.
     *
     * @return the long value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default long asLong() throws JavetException {
        return asInt();
    }

    /**
     * As string.
     *
     * @return the string value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default String asString() throws JavetException {
        return toString();
    }

    /**
     * Equals.
     * <p>
     * The behavior is different from JS behavior but is the same as Java behavior.
     *
     * @param v8Value the V8 value
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean equals(V8Value v8Value) throws JavetException;

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 0.9.1
     */
    V8Runtime getV8Runtime();

    /**
     * Is null.
     *
     * @return true : null, false : not null
     * @since 0.7.2
     */
    default boolean isNull() {
        return this instanceof V8ValueNull;
    }

    /**
     * Is null or undefined.
     *
     * @return true : null or undefined, false : not null and not undefined
     * @since 0.8.4
     */
    default boolean isNullOrUndefined() {
        return isNull() || isUndefined();
    }

    /**
     * Is undefined.
     *
     * @return true : undefined, false : not undefined
     * @since 0.7.2
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
     * @return true : same, false : different
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean sameValue(V8Value v8Value) throws JavetException;

    /**
     * Strict equals.
     * <p>
     * The behavior is different from JS behavior but is the same as Java behavior.
     *
     * @param v8Value the V8 value
     * @return true : strict equals, false : different
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean strictEquals(V8Value v8Value) throws JavetException;
}
