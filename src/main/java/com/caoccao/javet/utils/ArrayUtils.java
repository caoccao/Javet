/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

/**
 * The type Array utils.
 *
 * @since 3.0.3
 */
public final class ArrayUtils {
    /**
     * Test if the input byte array is empty.
     *
     * @param array the array
     * @return true : empty, false : not empty
     * @since 3.0.3
     */
    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Test if the input long array is empty.
     *
     * @param array the array
     * @return true : empty, false : not empty
     * @since 3.0.3
     */
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Test if the input array is empty.
     *
     * @param <T>   the type parameter
     * @param array the array
     * @return true : empty, false : not empty
     * @since 3.0.3
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Test if the input byte array is not empty.
     *
     * @param array the array
     * @return true : not empty, false : empty
     * @since 3.0.3
     */
    public static boolean isNotEmpty(byte[] array) {
        return array != null && array.length > 0;
    }

    /**
     * Test if the input long array is not empty.
     *
     * @param array the array
     * @return true : not empty, false : empty
     * @since 3.0.3
     */
    public static boolean isNotEmpty(long[] array) {
        return array != null && array.length > 0;
    }

    /**
     * Test if the input array is not empty.
     *
     * @param <T>   the type parameter
     * @param array the array
     * @return true : not empty, false : empty
     * @since 3.0.3
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return array != null && array.length > 0;
    }
}
