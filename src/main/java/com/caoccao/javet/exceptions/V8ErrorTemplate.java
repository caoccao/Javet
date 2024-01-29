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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

public final class V8ErrorTemplate {
    private V8ErrorTemplate() {
    }

    public static String typeErrorValueIsNotAFunction(V8Value v8Value) {
        return v8ValueToString(v8Value) + " is not a function";
    }

    private static String v8ValueToString(V8Value v8Value) {
        return v8Value == null ? V8ValueUndefined.UNDEFINED : v8Value.toString();
    }
}
