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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

@SuppressWarnings("unchecked")
public interface IV8ValueFunction extends IV8ValueObject {
    <T extends V8Value> T invoke(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException;

    default <T extends V8Value> T invoke(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invoke(receiver, true, v8Values);
    }

    default Boolean invokeBoolean(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invokeObject(receiver, v8Values);
    }

    default Double invokeDouble(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invokeObject(receiver, v8Values);
    }

    default Integer invokeInteger(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invokeObject(receiver, v8Values);
    }

    default Long invokeLong(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invokeObject(receiver, v8Values);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R invokeObject(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        try (V8Value v8Value = invoke(receiver, v8Values)) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String invokeString(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return invokeObject(receiver, v8Values);
    }

    default void invokeVoid(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        invoke(receiver, false, v8Values);
    }
}
