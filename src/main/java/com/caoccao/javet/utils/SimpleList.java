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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type Simple list.
 *
 * @since 3.0.3
 */
public final class SimpleList {
    /**
     * Of list.
     *
     * @param <T> the type parameter
     * @return the list
     * @since 3.0.3
     */
    public static <T> List<T> of() {
        return new ArrayList<>();
    }

    /**
     * Of list.
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the list
     * @since 3.0.3
     */
    @SafeVarargs
    public static <T> List<T> of(T... objects) {
        return Arrays.asList(Objects.requireNonNull(objects));
    }
}
