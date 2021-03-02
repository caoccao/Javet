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

public final class V8ValueUtils {
    public static final String EMPTY = "";

    private V8ValueUtils() {
    }

    public static String concat(String delimiter, V8Value... v8Values) {
        if (v8Values == null || v8Values.length == 0) {
            return EMPTY;
        }
        if (delimiter == null) {
            delimiter = EMPTY;
        }
        return String.join(
                delimiter,
                Arrays.stream(v8Values).map(V8Value::toString).collect(Collectors.toList()));
    }
}
