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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBigInteger;
import com.caoccao.javet.values.primitive.V8ValueZonedDateTime;

import java.math.BigInteger;
import java.time.ZonedDateTime;

/**
 * The interface V8 executable.
 *
 * @since 0.8.5
 */
public interface IV8Executable extends IV8Convertible {
    /**
     * Execute and return V8 value.
     *
     * @param <T> the type parameter
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    default <T extends V8Value> T execute() throws JavetException {
        return execute(true);
    }

    /**
     * Execute and return V8 value if required.
     *
     * @param <T>            the type parameter
     * @param resultRequired the result required
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8Value> T execute(boolean resultRequired) throws JavetException;

    /**
     * Execute and return big integer.
     *
     * @return the big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default BigInteger executeBigInteger() throws JavetException {
        try (V8Value v8Value = execute()) {
            if (v8Value instanceof V8ValueBigInteger) {
                return ((V8ValueBigInteger) v8Value).getValue();
            }
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Execute and return boolean.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default Boolean executeBoolean() throws JavetException {
        try (V8Value v8Value = execute()) {
            return v8Value.asBoolean();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Execute and return double.
     *
     * @return the double
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default Double executeDouble() throws JavetException {
        try (V8Value v8Value = execute()) {
            return v8Value.asDouble();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Execute and return integer.
     *
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default Integer executeInteger() throws JavetException {
        try (V8Value v8Value = execute()) {
            return v8Value.asInt();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Execute and return long.
     *
     * @return the long
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default Long executeLong() throws JavetException {
        try (V8Value v8Value = execute()) {
            return v8Value.asLong();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Execute and return a Java object.
     *
     * @param <T> the type parameter
     * @return the Java object
     * @throws JavetException the javet exception
     * @since 0.8.5
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
     * Execute and return string.
     *
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.8.10
     */
    default String executeString() throws JavetException {
        try (V8Value v8Value = execute()) {
            if (v8Value.isNullOrUndefined()) {
                return null;
            }
            return v8Value.asString();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Execute without a return value.
     *
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void executeVoid() throws JavetException {
        execute(false);
    }

    /**
     * Execute and return zoned date time.
     *
     * @return the zoned date time
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    default ZonedDateTime executeZonedDateTime() throws JavetException {
        try (V8Value v8Value = execute()) {
            if (v8Value instanceof V8ValueZonedDateTime) {
                return ((V8ValueZonedDateTime) v8Value).getValue();
            }
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }
}
