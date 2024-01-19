/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Simple set is a polyfill because Set.of() is not available at JDK 8 .
 *
 * @since 3.0.3
 */
public final class SimpleSet {
    private SimpleSet() {
    }

    /**
     * Of set.
     *
     * @param <T> the type parameter
     * @return the set
     * @since 3.0.3
     */
    public static <T> Set<T> of() {
        return new HashSet<>();
    }

    /**
     * Of set.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the set
     * @since 3.0.3
     */
    @SafeVarargs
    public static <T> Set<T> of(T... objects) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, objects);
        return set;
    }
}
