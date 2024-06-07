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
public interface IV8ValueArray extends IV8ValueObject {
    /**
     * The constant FUNCTION_FLAT.
     *
     * @since 3.0.4
     */
    String FUNCTION_FLAT = "flat";
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
     * Batch get the given range of items from the array.
     *
     * @param v8Values   the V8 values
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the actual item count
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    int batchGet(V8Value[] v8Values, int startIndex, int endIndex) throws JavetException;

    /**
     * Batch get all the items from the array.
     *
     * @param <T> the type parameter
     * @return the V8 values
     * @throws JavetException the javet exception
     * @since 2.2.0
     */
    default <T extends V8Value> T[] batchGet() throws JavetException {
        final int length = getLength();
        V8Value[] v8Values = new V8Value[length];
        if (length > 0) {
            try {
                batchGet(v8Values, 0, length);
            } catch (Throwable t) {
                JavetResourceUtils.safeClose(v8Values);
                Arrays.fill(v8Values, null);
                throw t;
            }
        }
        return (T[]) v8Values;
    }

    /**
     * Array.prototype.flat().
     * The flat() method of Array instances creates a new array with all sub-array elements concatenated
     * into it recursively up to the default depth 1.
     *
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    default IV8ValueArray flat() throws JavetException {
        return invoke(FUNCTION_FLAT);
    }

    /**
     * Array.prototype.flat().
     * The flat() method of Array instances creates a new array with all sub-array elements concatenated
     * into it recursively up to the specified depth.
     *
     * @param depth the depth level specifying how deep a nested array structure should be flattened. Defaults to 1.
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    default IV8ValueArray flat(int depth) throws JavetException {
        return invoke(FUNCTION_FLAT, getV8Runtime().createV8ValueInteger(depth));
    }

    /**
     * For each of the item, call the consumer and return the item count.
     *
     * @param <Value>   the type parameter
     * @param <E>       the type parameter
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    <Value extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Value, E> consumer,
            int batchSize)
            throws JavetException, E;

    /**
     * For each of the item, call the consumer and return the item count.
     *
     * @param <Value>   the type parameter
     * @param <E>       the type parameter
     * @param consumer  the consumer
     * @param batchSize the batch size
     * @return the item count
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    <Value extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Value, E> consumer,
            int batchSize)
            throws JavetException, E;

    /**
     * Get V8 value by index.
     *
     * @param <T>   the type parameter
     * @param index the index
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T get(int index) throws JavetException;

    /**
     * Gets keys.
     *
     * @return the keys
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    List<Integer> getKeys() throws JavetException;

    /**
     * Gets length.
     *
     * @return the length
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    int getLength() throws JavetException;

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
     * To V8 value array.
     *
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 0.9.10
     */
    @CheckReturnValue
    default V8Value[] toArray() throws JavetException {
        return batchGet();
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
