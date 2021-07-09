/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

/**
 * The interface Javet converter.
 *
 * @since 0.7.1
 */
@SuppressWarnings("unchecked")
public interface IJavetConverter {
    /**
     * Gets default boolean.
     *
     * @return the default boolean
     * @since 0.8.2
     */
    default boolean getDefaultBoolean() {
        return false;
    }

    /**
     * Gets default byte.
     *
     * @return the default byte
     * @since 0.8.2
     */
    default byte getDefaultByte() {
        return (byte) 0;
    }

    /**
     * Gets default char.
     *
     * @return the default char
     * @since 0.8.2
     */
    default char getDefaultChar() {
        return '\0';
    }

    /**
     * Gets default double.
     *
     * @return the default double
     * @since 0.8.2
     */
    default double getDefaultDouble() {
        return 0D;
    }

    /**
     * Gets default float.
     *
     * @return the default float
     * @since 0.8.2
     */
    default float getDefaultFloat() {
        return 0F;
    }

    /**
     * Gets default int.
     *
     * @return the default int
     * @since 0.8.2
     */
    default int getDefaultInt() {
        return 0;
    }

    /**
     * Gets default long.
     *
     * @return the default long
     * @since 0.8.2
     */
    default long getDefaultLong() {
        return 0L;
    }

    /**
     * Gets default short.
     *
     * @return the default short
     * @since 0.8.2
     */
    default short getDefaultShort() {
        return (short) 0;
    }

    /**
     * To object.
     * <p>
     * Don't override this function, instead, override the one with depth as argument
     * for circular structure detection.
     *
     * @param v8Value the V8 value
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.7.1
     */
    Object toObject(V8Value v8Value) throws JavetException;

    /**
     * To object.
     *
     * @param v8Value   the V8 value
     * @param autoClose the auto close
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Object toObject(V8Value v8Value, boolean autoClose) throws JavetException {
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
     * To V8 value.
     * <p>
     * Don't override this function, instead, override the one with depth as argument
     * for circular structure detection.
     *
     * @param <T>       the type parameter
     * @param v8Runtime the V8 runtime
     * @param object    the object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException;
}
