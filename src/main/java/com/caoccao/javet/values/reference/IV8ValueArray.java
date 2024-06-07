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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetUniConsumer;
import com.caoccao.javet.interfaces.IJavetUniIndexedConsumer;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * The interface V8 value array.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public interface IV8ValueArray extends IV8ValueSealedArray {
    /**
     * The constant FUNCTION_SHIFT.
     *
     * @since 3.0.4
     */
    String FUNCTION_SHIFT = "shift";
    /**
     * The constant FUNCTION_UNSHIFT.
     *
     * @since 3.0.4
     */
    String FUNCTION_UNSHIFT = "unshift";
    /**
     * The constant FUNCTION_POP.
     *
     * @since 3.0.4
     */
    String FUNCTION_POP = "pop";
    /**
     * The constant FUNCTION_PUSH.
     *
     * @since 3.0.4
     */
    String FUNCTION_PUSH = "push";

    /**
     * Array.prototype.pop().
     * The pop() method of Array instances removes the last element from an array and returns that element.
     * This method changes the length of the array.
     *
     * @param <T> the type parameter
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    default <T extends V8Value> T pop() throws JavetException {
        return invoke(FUNCTION_POP);
    }

    /**
     * Pop big integer from the array.
     *
     * @return the big integer
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default BigInteger popBigInteger() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop boolean from the array.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Boolean popBoolean() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop double from the array.
     *
     * @return the double
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Double popDouble() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop integer from the array.
     *
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Integer popInteger() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop long from the array.
     *
     * @return the long
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default Long popLong() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop V8 value null.
     *
     * @return the V8 value null
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default V8ValueNull popNull() throws JavetException {
        return pop();
    }

    /**
     * Pop object from the array.
     *
     * @param <T> the type parameter
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.8.10
     */
    default <T> T popObject() throws JavetException {
        try {
            return getV8Runtime().toObject(pop(), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Pop primitive from the array.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @return the V8 value primitive
     * @throws JavetException the javet exception
     * @since 0.8.10
     */
    default <R, T extends V8ValuePrimitive<R>> R popPrimitive() throws JavetException {
        try (V8Value v8Value = pop()) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Pop string from the array.
     *
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default String popString() throws JavetException {
        return popPrimitive();
    }

    /**
     * Pop V8 value undefined.
     *
     * @return the V8 value undefined
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default V8ValueUndefined popUndefined() throws JavetException {
        return pop();
    }

    /**
     * Pop zoned date time from the array.
     *
     * @return the string
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default ZonedDateTime popZonedDateTime() throws JavetException {
        return popPrimitive();
    }

    /**
     * Array.prototype.push().
     * The push() method of Array instances adds the specified elements to the end of an array
     * and returns the new length of the array.
     *
     * @param objects the objects
     * @return the array length
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default int push(Object... objects) throws JavetException {
        return invokeInteger(FUNCTION_PUSH, objects);
    }

    /**
     * Push null to the array.
     *
     * @return the array length
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    default int pushNull() throws JavetException {
        return push(getV8Runtime().createV8ValueNull());
    }

    /**
     * Push undefined to the array.
     *
     * @return the array length
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    default int pushUndefined() throws JavetException {
        return push(getV8Runtime().createV8ValueUndefined());
    }

    /**
     * Array.prototype.shift().
     * The shift() method of Array instances removes the first element from an array and returns that removed element.
     * This method changes the length of the array.
     *
     * @param <T> the type parameter
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    default <T extends V8Value> T shift() throws JavetException {
        return invoke(FUNCTION_SHIFT);
    }

    /**
     * Shift big integer from the array.
     *
     * @return the big integer
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default BigInteger shiftBigInteger() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift boolean from the array.
     *
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default Boolean shiftBoolean() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift double from the array.
     *
     * @return the double
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default Double shiftDouble() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift integer from the array.
     *
     * @return the integer
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default Integer shiftInteger() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift long from the array.
     *
     * @return the long
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default Long shiftLong() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift V8 value null.
     *
     * @return the V8 value null
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default V8ValueNull shiftNull() throws JavetException {
        return shift();
    }

    /**
     * Shift object from the array.
     *
     * @param <T> the type parameter
     * @return the object
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default <T> T shiftObject() throws JavetException {
        try {
            return getV8Runtime().toObject(shift(), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Shift primitive from the array.
     *
     * @param <R> the type parameter
     * @param <T> the type parameter
     * @return the V8 value primitive
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default <R, T extends V8ValuePrimitive<R>> R shiftPrimitive() throws JavetException {
        try (V8Value v8Value = shift()) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Shift string from the array.
     *
     * @return the string
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default String shiftString() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Shift V8 value undefined.
     *
     * @return the V8 value undefined
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default V8ValueUndefined shiftUndefined() throws JavetException {
        return shift();
    }

    /**
     * Shift zoned date time from the array.
     *
     * @return the string
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default ZonedDateTime shiftZonedDateTime() throws JavetException {
        return shiftPrimitive();
    }

    /**
     * Array.prototype.unshift().
     * The unshift() method of Array instances adds the specified elements to the beginning of an array
     * and returns the new length of the array.
     *
     * @param objects the objects
     * @return the array length
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default int unshift(Object... objects) throws JavetException {
        return invokeInteger(FUNCTION_UNSHIFT, objects);
    }

    /**
     * Unshift null to the array.
     *
     * @return the array length
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default int unshiftNull() throws JavetException {
        return unshift(getV8Runtime().createV8ValueNull());
    }

    /**
     * Unshift undefined to the array.
     *
     * @return the array length
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    default int unshiftUndefined() throws JavetException {
        return unshift(getV8Runtime().createV8ValueUndefined());
    }
}
