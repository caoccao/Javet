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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInObject extends V8ValueObject {

    public static final String FUNCTION_ASSIGN = "assign";

    public V8ValueBuiltInObject(long handle) {
        super(handle);
    }

    public V8ValueObject assign(V8ValueObject v8Value1, V8ValueObject v8Value2) throws JavetException {
        Objects.requireNonNull(v8Value1);
        Objects.requireNonNull(v8Value2);
        return invoke(FUNCTION_ASSIGN, v8Value1, v8Value2);
    }

    @Override
    public V8ValueBuiltInObject toClone() throws JavetException {
        return this;
    }
}
