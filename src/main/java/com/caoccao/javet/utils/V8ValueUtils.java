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

import com.caoccao.javet.values.V8Value;

import java.util.StringJoiner;

/**
 * The type V8 value utils.
 *
 * @since 0.7.1
 */
public final class V8ValueUtils {

    private V8ValueUtils() {
    }

    /**
     * Concat string.
     *
     * @param delimiter the delimiter
     * @param v8Values  the V8 values
     * @return the string
     * @since 0.7.1
     */
    public static String concat(String delimiter, V8Value... v8Values) {
        if (v8Values == null || v8Values.length == 0) {
            return StringUtils.EMPTY;
        }
        if (delimiter == null) {
            delimiter = StringUtils.EMPTY;
        }
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (V8Value v8Value : v8Values) {
            stringJoiner.add(v8Value.toString());
        }
        return stringJoiner.toString();
    }

    /**
     * Convert to virtual objects.
     *
     * @param v8Values the V8 values
     * @return the javet virtual objects
     * @since 0.9.10
     */
    public static JavetVirtualObject[] convertToVirtualObjects(V8Value... v8Values) {
        final int length = v8Values == null ? 0 : v8Values.length;
        JavetVirtualObject[] javetVirtualObjects = new JavetVirtualObject[length];
        for (int i = 0; i < length; ++i) {
            javetVirtualObjects[i] = new JavetVirtualObject(v8Values[i]);
        }
        return javetVirtualObjects;
    }

    /**
     * Trim anonymous function source code.
     *
     * @param sourceCode the source code
     * @return the trimmed source code
     * @since 1.0.0
     */
    public static String trimAnonymousFunction(String sourceCode) {
        if (sourceCode != null) {
            final int length = sourceCode.length();
            if (length > 0) {
                int endPosition = length;
                boolean completed = false;
                while (!completed && endPosition > 0) {
                    switch (sourceCode.charAt(endPosition - 1)) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                        case ';':
                            endPosition--;
                            break;
                        default:
                            completed = true;
                            break;
                    }
                }
                if (endPosition == length) {
                    return sourceCode;
                } else {
                    return sourceCode.substring(0, endPosition);
                }
            }
        }
        return null;
    }
}
