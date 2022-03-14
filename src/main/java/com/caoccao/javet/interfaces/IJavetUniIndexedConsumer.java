/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * The interface Javet uni indexed consumer.
 *
 * @param <T> the type parameter for value
 * @param <E> the type parameter for custom exception
 * @since 0.8.10
 */
public interface IJavetUniIndexedConsumer<T extends V8Value, E extends Throwable> {
    /**
     * Accept.
     *
     * @param index the index
     * @param value the value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.8.10
     */
    void accept(int index, T value) throws JavetException, E;
}
