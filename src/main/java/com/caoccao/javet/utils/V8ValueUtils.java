/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.utils;

import com.caoccao.javet.values.V8Value;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type V8 value utils.
 */
public final class V8ValueUtils {
    /**
     * The constant EMPTY.
     */
    public static final String EMPTY = "";

    private V8ValueUtils() {
    }

    /**
     * Concat string.
     *
     * @param delimiter the delimiter
     * @param v8Values  the V8 values
     * @return the string
     */
    public static String concat(String delimiter, V8Value... v8Values) {
        if (v8Values == null || v8Values.length == 0) {
            return EMPTY;
        }
        if (delimiter == null) {
            delimiter = EMPTY;
        }
        return Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.joining(delimiter));
    }

    /**
     * Convert to virtual objects.
     *
     * @param v8Values the V8 values
     * @return the javet virtual objects
     */
    public static JavetVirtualObject[] convertToVirtualObjects(V8Value... v8Values) {
        final int length = v8Values.length;
        JavetVirtualObject[] javetVirtualObjects = new JavetVirtualObject[length];
        for (int i = 0; i < length; ++i) {
            javetVirtualObjects[i] = new JavetVirtualObject(v8Values[i]);
        }
        return javetVirtualObjects;
    }

    public static String trimAnonymousFunction(String anonymousFunctionSourceCode) {
        if (anonymousFunctionSourceCode != null) {
            final int length = anonymousFunctionSourceCode.length();
            if (length > 0) {
                int endPosition = length;
                boolean completed = false;
                while (!completed && endPosition > 0) {
                    switch (anonymousFunctionSourceCode.charAt(endPosition - 1)) {
                        case ' ':
                        case '\n':
                        case ';':
                            endPosition--;
                            break;
                        default:
                            completed = true;
                            break;
                    }
                }
                if (endPosition == length) {
                    return anonymousFunctionSourceCode;
                } else {
                    return anonymousFunctionSourceCode.substring(0, endPosition);
                }
            }
        }
        return null;
    }
}
