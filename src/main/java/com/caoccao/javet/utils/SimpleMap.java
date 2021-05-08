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

package com.caoccao.javet.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Simple map is a polyfill because Map.of is not available at JDK 8 .
 */
public final class SimpleMap {

    public static <K, V> Map<K, V> of(
            K k1, V v1
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
            put(k5, v5);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
            put(k5, v5);
            put(k6, v6);
        }};
    }

    public static <K, V> Map<K, V> of(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7
    ) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
            put(k5, v5);
            put(k6, v6);
            put(k7, v7);
        }};
    }

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
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
            put(k5, v5);
            put(k6, v6);
            put(k7, v7);
            put(k8, v8);
        }};
    }

}
