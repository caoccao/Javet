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

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * The type Array utils.
 *
 * @since 3.0.3
 */
public final class ArrayUtils {
    /**
     * Get a copy of the array object.
     *
     * @param object the array object
     * @return the new array
     * @since 3.0.4
     */
    public static Object[] copyOf(Object object) {
        final int length = Array.getLength(object);
        Object[] objects = new Object[length];
        for (int i = 0; i < length; ++i) {
            objects[i] = Array.get(object, i);
        }
        return objects;
    }

    /**
     * The includes() method of Array instances determines whether an array includes a certain value among its entries,
     * returning true or false as appropriate.
     *
     * @param <T>       the type parameter
     * @param object    the object
     * @param element   the element
     * @param fromIndex the from index
     * @return true : included, false : not included
     * @since 3.0.4
     */
    public static <T> boolean includes(Object object, T element, int fromIndex) {
        final int length = Array.getLength(object);
        for (int index = fromIndex; index < length; ++index) {
            if (Objects.equals(Array.get(object, index), element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The indexOf() method of Array instances returns the first index
     * at which a given element can be found in the array, or -1 if it is not present.
     *
     * @param <T>       the type parameter
     * @param object    the object
     * @param element   the element
     * @param fromIndex the from index
     * @return the index
     * @since 3.0.4
     */
    public static <T> int indexOf(Object object, T element, int fromIndex) {
        final int length = Array.getLength(object);
        for (int index = fromIndex; index < length; ++index) {
            if (Objects.equals(Array.get(object, index), element)) {
                return index;
            }
        }
        return -1;
    }

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

    /**
     * The lastIndexOf() method of Array instances returns the last index at which a given element can be found
     * in the array, or -1 if it is not present. The array is searched backwards, starting at fromIndex.
     *
     * @param <T>       the type parameter
     * @param object    the object
     * @param element   the element
     * @param fromIndex the from index
     * @return the index
     * @since 3.0.4
     */
    public static <T> int lastIndexOf(Object object, T element, int fromIndex) {
        final int length = Array.getLength(object);
        if (fromIndex >= 0 && fromIndex < length) {
            for (int i = fromIndex; i >= 0; --i) {
                if (Objects.equals(Array.get(object, i), element)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Reverse an array object.
     *
     * @param object the object
     */
    public static void reverse(Object object) {
        final int length = Array.getLength(object);
        if (length > 1) {
            int leftIndex = 0;
            int rightIndex = length - 1;
            while (leftIndex < rightIndex) {
                Object temp = Array.get(object, leftIndex);
                Array.set(object, leftIndex, Array.get(object, rightIndex));
                Array.set(object, rightIndex, temp);
                leftIndex++;
                rightIndex--;
            }
        }
    }
}
