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

import java.util.Objects;
import java.util.StringJoiner;

/**
 * The type String utils.
 *
 * @since 3.0.3
 */
public final class StringUtils {
    /**
     * The constant EMPTY.
     *
     * @since 3.0.3
     */
    public static final String EMPTY = "";

    private StringUtils() {
    }

    /**
     * Is blank.
     *
     * @param str the str
     * @return true : blank, false : not blank
     * @since 3.0.3
     */
    public static boolean isBlank(String str) {
        if (!isEmpty(str)) {
            final int length = str.length();
            for (int i = 0; i < length; i++) {
                char c = str.charAt(i);
                if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Is digital.
     *
     * @param str the str
     * @return true : yes, false : no
     * @since 1.0.6
     */
    public static boolean isDigital(String str) {
        if (isEmpty(str)) {
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
     * Is empty.
     *
     * @param str the str
     * @return true : empty, false : not empty
     * @since 3.0.3
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param str the str
     * @return true : not empty, false : empty
     * @since 3.0.3
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
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
