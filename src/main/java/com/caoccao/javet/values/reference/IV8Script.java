/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.values.V8Value;

public interface IV8Script extends IV8ValueReference, IV8Executable {
    String getResourceName();

    void setResourceName(String resourceName);

    @Override
    default <T, V extends V8Value> T toObject(V v8Value) throws JavetException {
        return getV8Runtime().toObject(v8Value);
    }

    @Override
    @CheckReturnValue
    default <T, V extends V8Value> V toV8Value(T object) throws JavetException {
        return getV8Runtime().toV8Value(object);
    }
}
