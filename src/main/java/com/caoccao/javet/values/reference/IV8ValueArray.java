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
     */
    <Value extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Value, E> consumer,
            int batchSize)
            throws JavetException, E;

    @CheckReturnValue
    <T extends V8Value> T get(int index) throws JavetException;

    List<Integer> getKeys() throws JavetException;

    int getLength() throws JavetException;

    @CheckReturnValue
    <T extends V8Value> T pop() throws JavetException;

    default BigInteger popBigInteger() throws JavetException {
        return popPrimitive();
    }

    default Boolean popBoolean() throws JavetException {
        return popPrimitive();
    }

    default Double popDouble() throws JavetException {
        return popPrimitive();
    }

    default Integer popInteger() throws JavetException {
        return popPrimitive();
    }

    default Long popLong() throws JavetException {
        return popPrimitive();
    }

    default V8ValueNull popNull() throws JavetException {
        return pop();
    }

    default <T> T popObject() throws JavetException {
        try {
            return getV8Runtime().toObject(pop(), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R, T extends V8ValuePrimitive<R>> R popPrimitive() throws JavetException {
        try (V8Value v8Value = pop()) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    default String popString() throws JavetException {
        return popPrimitive();
    }

    default V8ValueUndefined popUndefined() throws JavetException {
        return pop();
    }

    int push(Object... objects) throws JavetException;

    default int pushNull() throws JavetException {
        return push(getV8Runtime().createV8ValueNull());
    }

    default int pushUndefined() throws JavetException {
        return push(getV8Runtime().createV8ValueUndefined());
    }

    @CheckReturnValue
    default V8Value[] toArray() throws JavetException {
        return batchGet();
    }
}
