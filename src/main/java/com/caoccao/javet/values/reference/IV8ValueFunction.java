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
    default <T extends V8Value> T call(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callExtended(receiver, true, objects);
    }

    default <T extends V8Value> T call(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        return callExtended(receiver, true, v8Values);
    }

    <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException;

    <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException;

    default Boolean callBoolean(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default Double callDouble(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException;

    <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException;


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
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return (T) getV8Runtime().getConverter().toObject(v8Value);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R callPrimitive(
            IV8ValueObject receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
            return null;
        }
    }

    default String callString(IV8ValueObject receiver, Object... objects) throws JavetException {
        return callPrimitive(receiver, objects);
    }

    default void callVoid(IV8ValueObject receiver, Object... objects) throws JavetException {
        callExtended(receiver, false, objects);
    }

    default void callVoid(IV8ValueObject receiver, V8Value... v8Values) throws JavetException {
        callExtended(receiver, false, v8Values);
    }
}
