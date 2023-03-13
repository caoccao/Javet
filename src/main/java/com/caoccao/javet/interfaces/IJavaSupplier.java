/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

/**
 * Represents a supplier of results.
 * <p>
 * It is a polyfill to the built-in Supplier for Android compatibility.
 *
 * @param <T> the type of results supplied by this supplier
 * @see java.util.function.Supplier
 * @since 1.0.3
 */
@FunctionalInterface
public interface IJavaSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @since 1.0.3
     */
    T get();
}
