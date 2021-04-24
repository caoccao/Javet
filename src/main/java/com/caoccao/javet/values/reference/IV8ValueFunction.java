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
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

@SuppressWarnings("unchecked")
public interface IV8ValueFunction extends IV8ValueObject {
    <T extends V8Value> T call(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException;

    default <T extends V8Value> T call(IV8ValueObject receiver, Object... objects) throws JavetException {
        return call(receiver, true, objects);
    }

    <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException;

    default Boolean callBoolean(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default Double callDouble(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default Float callFloat(IV8ValueObject receiver, Object... objects) throws JavetException {
        Double result = callDouble(receiver, objects);
        return result == null ? null : result.floatValue();
    }

    default Integer callInteger(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default Long callLong(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default <T extends Object> T callObject(IV8ValueObject receiver, Object... objects) throws JavetException {
        V8Runtime v8Runtime = getV8Runtime();
        IJavetConverter converter = v8Runtime.getConverter();
        try {
            return (T) converter.toObject(call(receiver, true, objects));
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R callPrimitive(
            IV8ValueObject receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = call(receiver, objects)) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String callString(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default <T extends V8Value> T callV8Value(IV8ValueObject receiver, Object... objects) throws JavetException {
        return call(receiver, true, objects);
    }

    default void callVoid(IV8ValueObject receiver, Object... objects) throws JavetException {
        call(receiver, false, objects);
    }
}
