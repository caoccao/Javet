/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IV8Convertible {
    <T extends Object, V extends V8Value> T toObject(V v8Value) throws JavetException;

    default <T extends Object, V extends V8Value> T toObject(V v8Value, boolean autoClose) throws JavetException {
        if (autoClose) {
            try {
                return toObject(v8Value);
            } finally {
                JavetResourceUtils.safeClose(v8Value);
            }
        } else {
            return toObject(v8Value);
        }
    }

    <T extends Object, V extends V8Value> V toV8Value(T object) throws JavetException;
}
