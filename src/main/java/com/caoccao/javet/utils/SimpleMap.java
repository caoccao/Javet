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

package com.caoccao.javet.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Simple map is a polyfill because Map.of() is not available at JDK 8 .
 *
 * @since 0.8.5
 */
public final class SimpleMap {

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @param k4  the k4
     * @param v4  the v4
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @param k4  the k4
     * @param v4  the v4
     * @param k5  the k5
     * @param v5  the v5
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @param k4  the k4
     * @param v4  the v4
     * @param k5  the k5
     * @param v5  the v5
     * @param k6  the k6
     * @param v6  the v6
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @param k4  the k4
     * @param v4  the v4
     * @param k5  the k5
     * @param v5  the v5
     * @param k6  the k6
     * @param v6  the v6
     * @param k7  the k7
     * @param v7  the v7
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        return map;
    }

    /**
     * Of map.
     *
     * @param <K> the type parameter
     * @param <V> the type parameter
     * @param k1  the k1
     * @param v1  the v1
     * @param k2  the k2
     * @param v2  the v2
     * @param k3  the k3
     * @param v3  the v3
     * @param k4  the k4
     * @param v4  the v4
     * @param k5  the k5
     * @param v5  the v5
     * @param k6  the k6
     * @param v6  the v6
     * @param k7  the k7
     * @param v7  the v7
     * @param k8  the k8
     * @param v8  the v8
     * @return the map
     * @since 0.8.5
     */
    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8
    ) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        map.put(k4, v4);
        map.put(k5, v5);
        map.put(k6, v6);
        map.put(k7, v7);
        map.put(k8, v8);
        return map;
    }
}
