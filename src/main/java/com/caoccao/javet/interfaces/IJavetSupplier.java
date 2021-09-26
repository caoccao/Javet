/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interfaces;

import com.caoccao.javet.exceptions.JavetException;

/**
 * The interface Javet supplier.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 0.9.12
 */
@FunctionalInterface
public interface IJavetSupplier<T, E extends Throwable> {
    /**
     * Get the value
     *
     * @return the value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.12
     */
    T get() throws JavetException, E;
}
