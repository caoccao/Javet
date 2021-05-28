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

package com.caoccao.javet.interop;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

/**
 * The interface V8 executable.
 */
@SuppressWarnings("unchecked")
public interface IV8Executable extends IV8Convertible {
    /**
     * Execute t.
     *
     * @param <T> the type parameter
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    default <T extends V8Value> T execute() throws JavetException {
        return execute(true);
    }

    /**
     * Execute t.
     *
     * @param <T>            the type parameter
     * @param resultRequired the result required
     * @return the t
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    <T extends V8Value> T execute(boolean resultRequired) throws JavetException;

    /**
     * Execute boolean boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     */
    default Boolean executeBoolean() throws JavetException {
        return executePrimitive();
    }

    /**
     * Execute double double.
     *
     * @return the double
     * @throws JavetException the javet exception
     */
    default Double executeDouble() throws JavetException {
        return executePrimitive();
    }

    /**
     * Execute integer integer.
     *
     * @return the integer
     * @throws JavetException the javet exception
     */
    default Integer executeInteger() throws JavetException {
        return executePrimitive();
    }

    /**
     * Execute long long.
     *
     * @return the long
     * @throws JavetException the javet exception
     */
    default Long executeLong() throws JavetException {
        return executePrimitive();
    }

    /**
     * Execute object t.
     *
     * @param <T> the type parameter
     * @return the t
     * @throws JavetException the javet exception
     */
    default <T> T executeObject() throws JavetException {
        try (V8Value v8Value = execute()) {
            return toObject(v8Value);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Execute primitive r.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @return the r
     * @throws JavetException the javet exception
     */
    default <R, T extends V8ValuePrimitive<R>> R executePrimitive() throws JavetException {
        try (V8Value v8Value = execute()) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Execute string string.
     *
     * @return the string
     * @throws JavetException the javet exception
     */
    default String executeString() throws JavetException {
        return executePrimitive();
    }

    /**
     * Execute void.
     *
     * @throws JavetException the javet exception
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void executeVoid() throws JavetException {
        execute(false);
    }

    /**
     * Execute zoned date time zoned date time.
     *
     * @return the zoned date time
     * @throws JavetException the javet exception
     */
    default ZonedDateTime executeZonedDateTime() throws JavetException {
        return executePrimitive();
    }
}
