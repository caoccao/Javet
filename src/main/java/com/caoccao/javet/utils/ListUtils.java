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
import java.util.*;

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
     * Add all items from the array to the list.
     *
     * @param <T>   the type parameter
     * @param list  the list
     * @param array the array
     * @return the length of the array
     * @since 3.0.4
     */
    public static <T> int addAll(List<T> list, Object array) {
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            list.add((T) Array.get(array, i));
        }
        return length;
    }

    /**
     * The flat() method of Array instances creates a new array with all sub-array elements concatenated
     * into it recursively up to the specified depth.
     *
     * @param <T>        the type parameter
     * @param targetList the target list
     * @param sourceList the source list
     * @param depth      the depth
     * @since 3.0.4
     */
    public static <T> void flat(List<T> targetList, List<T> sourceList, int depth) {
        if (depth <= 0) {
            targetList.addAll(sourceList);
        } else if (!sourceList.isEmpty()) {
            for (T object : sourceList) {
                if (object instanceof List) {
                    flat(targetList, (List<T>) object, depth - 1);
                } else if (object != null && object.getClass().isArray()) {
                    final int length = Array.getLength(object);
                    List<T> childList = new ArrayList<>(length);
                    for (int i = 0; i < length; ++i) {
                        childList.add((T) Array.get(object, i));
                    }
                    flat(targetList, childList, depth - 1);
                } else {
                    targetList.add(object);
                }
            }
        }
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
     * The indexOf() method of Array instances returns the first index
     * at which a given element can be found in the array, or -1 if it is not present.
     *
     * @param <T>       the type parameter
     * @param list      the list
     * @param element   the element
     * @param fromIndex the from index
     * @return the index
     * @since 3.0.3
     */
    public static <T> int indexOf(List<T> list, T element, int fromIndex) {
        final int length = list.size();
        if (fromIndex >= 0 && fromIndex < length) {
            for (int i = fromIndex; i < length; ++i) {
                if (Objects.equals(list.get(i), element)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Is empty.
     *
     * @param list the list
     * @return true : empty, false : not empty
     * @since 3.0.4
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param list the list
     * @return true : not empty, false : empty
     * @since 3.0.4
     */
    public static boolean isNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * The lastIndexOf() method of Array instances returns the last index at which a given element can be found
     * in the array, or -1 if it is not present. The array is searched backwards, starting at fromIndex.
     *
     * @param <T>       the type parameter
     * @param list      the list
     * @param element   the element
     * @param fromIndex the from index
     * @return the index
     * @since 3.0.3
     */
    public static <T> int lastIndexOf(List<T> list, T element, int fromIndex) {
        final int length = list.size();
        if (fromIndex >= 0 && fromIndex < length) {
            for (int i = fromIndex; i >= 0; --i) {
                if (Objects.equals(list.get(i), element)) {
                    return i;
                }
            }
        }
        return -1;
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
