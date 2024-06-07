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

import java.util.Arrays;
import java.util.List;

/**
 * The interface V8 value array.
 *
 * @since 3.1.3
 */
@SuppressWarnings("unchecked")
public interface IV8ValueSealedArray extends IV8ValueObject {
    /**
     * The constant FUNCTION_FLAT.
     *
     * @since 3.1.3
     */
    String FUNCTION_FLAT = "flat";

    /**
     * Batch get the given range of items from the array.
     *
     * @param v8Values   the V8 values
     * @param startIndex the start index
     * @param endIndex   the end index
     * @return the actual item count
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    int batchGet(V8Value[] v8Values, int startIndex, int endIndex) throws JavetException;

    /**
     * Batch get all the items from the array.
     *
     * @param <T> the type parameter
     * @return the V8 values
     * @throws JavetException the javet exception
     * @since 3.1.3
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
     * @since 3.1.3
     */
    @CheckReturnValue
    default IV8ValueSealedArray flat() throws JavetException {
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
     * @since 3.1.3
     */
    @CheckReturnValue
    default IV8ValueSealedArray flat(int depth) throws JavetException {
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
     * @since 3.1.3
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
     * @since 3.1.3
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
     * @since 3.1.3
     */
    @CheckReturnValue
    <T extends V8Value> T get(int index) throws JavetException;

    /**
     * Gets keys.
     *
     * @return the keys
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    List<Integer> getKeys() throws JavetException;

    /**
     * Gets length.
     *
     * @return the length
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    int getLength() throws JavetException;

    /**
     * To V8 value array.
     *
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    default V8Value[] toArray() throws JavetException {
        return batchGet();
    }
}
