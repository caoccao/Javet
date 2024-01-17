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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The type List utils.
 *
 * @since 3.0.3
 */
@SuppressWarnings("unchecked")
public final class ListUtils {
    private ListUtils() {
    }

    /**
     * The includes() method of Array instances determines whether an array includes a certain value among its entries,
     * returning true or false as appropriate.
     *
     * @param <T>       the type parameter
     * @param list      the list
     * @param element   the element
     * @param fromIndex the from index
     * @return true : included, false : not included
     * @since 3.0.3
     */
    public static <T> boolean includes(List<T> list, T element, int fromIndex) {
        int index = 0;
        for (T item : list) {
            if (Objects.equals(item, element) && index >= fromIndex) {
                return true;
            }
            ++index;
        }
        return false;
    }

    /**
     * The pop() method of Array instances removes the last element from an array and returns that element.
     * This method changes the length of the array.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the removed element
     * @since 3.0.3
     */
    public static <T> T pop(List<T> list) {
        final int size = list.size();
        if (size > 0) {
            return list.remove(size - 1);
        }
        return null;
    }

    /**
     * The push() method of Array instances adds the specified elements to the end of an array
     * and returns the new length of the array.
     *
     * @param <T>      the type parameter
     * @param list     the list
     * @param elements the elements
     * @return the new length of the array
     * @since 3.0.3
     */
    public static <T> int push(List<T> list, T... elements) {
        if (ArrayUtils.isNotEmpty(elements)) {
            Collections.addAll(list, elements);
        }
        return list.size();
    }

    /**
     * The shift() method of Array instances removes the first element from an array and
     * returns that removed element. This method changes the length of the array.
     *
     * @param <T>  the type parameter
     * @param list the list
     * @return the removed element
     */
    public static <T> T shift(List<T> list) {
        final int size = list.size();
        if (size > 0) {
            return list.remove(0);
        }
        return null;
    }

    /**
     * The unshift() method of Array instances adds the specified elements to the beginning of an array and
     * returns the new length of the array.
     *
     * @param <T>      the type parameter
     * @param list     the list
     * @param elements the elements
     * @return the new length of the array
     */
    public static <T> int unshift(List<T> list, T... elements) {
        if (ArrayUtils.isNotEmpty(elements)) {
            list.addAll(0, Arrays.asList(elements));
        }
        return list.size();
    }
}
