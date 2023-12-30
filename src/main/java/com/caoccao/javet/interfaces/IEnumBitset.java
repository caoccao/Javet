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

package com.caoccao.javet.interfaces;

import java.util.EnumSet;
import java.util.Objects;

/**
 * The interface Enum bitset.
 *
 * @since 1.0.3
 */
public interface IEnumBitset {
    /**
     * Gets enum set.
     *
     * @param <E>         the type parameter
     * @param bitsetValue the bitset value
     * @param enumClass   the enum class
     * @return the enum set
     * @since 1.0.3
     */
    static <E extends Enum<E> & IEnumBitset> EnumSet<E> getEnumSet(int bitsetValue, Class<E> enumClass) {
        return getEnumSet(bitsetValue, enumClass, null);
    }

    /**
     * Gets enum set.
     *
     * @param <E>         the type parameter
     * @param bitsetValue the bitset value
     * @param enumClass   the enum class
     * @param defaultEnum the default enum
     * @return the enum set
     * @since 1.0.3
     */
    static <E extends Enum<E> & IEnumBitset> EnumSet<E> getEnumSet(int bitsetValue, Class<E> enumClass, E defaultEnum) {
        EnumSet<E> enumSet = EnumSet.noneOf(enumClass);
        for (E e : EnumSet.allOf(enumClass)) {
            if ((bitsetValue & e.getValue()) > 0) {
                enumSet.add(e);
            }
        }
        if (enumSet.isEmpty() && defaultEnum != null) {
            enumSet.add(defaultEnum);
        }
        return enumSet;
    }

    /**
     * Gets value.
     *
     * @param <E>     the type parameter
     * @param enumSet the enum set
     * @return the value
     * @since 1.0.3
     */
    static <E extends Enum<E> & IEnumBitset> int getValue(EnumSet<E> enumSet) {
        int value = 0;
        for (E e : Objects.requireNonNull(enumSet)) {
            value |= e.getValue();
        }
        return value;
    }

    /**
     * Gets value.
     *
     * @return the value
     * @since 1.0.3
     */
    int getValue();
}
