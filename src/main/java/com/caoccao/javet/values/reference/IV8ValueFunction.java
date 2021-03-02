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
    <T extends V8Value> T call(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException;

    default <T extends V8Value> T call(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return call(receiver, true, v8Values);
    }

    <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException;

    default Boolean callBoolean(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callObject(receiver, v8Values);
    }

    default Double callDouble(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callObject(receiver, v8Values);
    }

    default Float callFloat(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        Double result = callDouble(receiver, v8Values);
        return result == null ? null : result.floatValue();
    }

    default Integer callInteger(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callObject(receiver, v8Values);
    }

    default Long callLong(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callObject(receiver, v8Values);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R callObject(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        try (V8Value v8Value = call(receiver, v8Values)) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String callString(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callObject(receiver, v8Values);
    }

    default void callVoid(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        call(receiver, false, v8Values);
    }

    V8Value receiveCallback(V8Value thisObject, V8ValueArray args) throws Throwable;
}
