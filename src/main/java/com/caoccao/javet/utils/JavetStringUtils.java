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

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The type Javet string utils.
 *
 * @since 1.0.3
 */
public final class JavetStringUtils {
    private JavetStringUtils() {
    }

    /**
     * Is digital.
     *
     * @param str the str
     * @return true : yes, false : no
     * @since 1.0.6
     */
    public static boolean isDigital(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Join string.
     *
     * @param delimiter the delimiter
     * @param elements  the elements
     * @return the string
     * @since 1.0.3
     */
    public static String join(CharSequence delimiter, CharSequence... elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (CharSequence cs : elements) {
            stringJoiner.add(cs);
        }
        return stringJoiner.toString();
    }

    /**
     * Join string.
     *
     * @param delimiter the delimiter
     * @param elements  the elements
     * @return the string
     * @since 1.0.3
     */
    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (CharSequence cs : elements) {
            stringJoiner.add(cs);
        }
        return stringJoiner.toString();
    }
}
