/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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

/**
 * The interface Javet uni-function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 * @param <E> the type parameter
 * @since 2.2.0
 */
@FunctionalInterface
public interface IJavetUniFunction<T, R, E extends Throwable> {
    /**
     * Apply.
     *
     * @param value the value
     * @return the result
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    R apply(T value) throws JavetException, E;
}
