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
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.virtual.V8VirtualList;

import java.util.List;

@SuppressWarnings("unchecked")
public interface IV8ValueArray extends IV8ValueObject {
    List<Integer> getKeys() throws JavetException;

    int getLength() throws JavetException;

    <T extends V8Value> T pop() throws JavetException;

    default Boolean popBoolean() throws JavetException {
        return popObject();
    }

    default Double popDouble() throws JavetException {
        return popObject();
    }

    default Integer popInteger() throws JavetException {
        return popObject();
    }

    default Long popLong() throws JavetException {
        return popObject();
    }

    default V8ValueNull popNull() throws JavetException {
        return pop();
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R popObject() throws JavetException {
        try (V8Value v8Value = pop()) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String popString() throws JavetException {
        return popObject();
    }

    default V8ValueUndefined popUndefined() throws JavetException {
        return pop();
    }

    int push(V8Value v8Value) throws JavetException;

    default int push(boolean value) throws JavetException {
        return push(new V8ValueBoolean(value));
    }

    default int push(double value) throws JavetException {
        return push(new V8ValueDouble(value));
    }

    default int push(int value) throws JavetException {
        return push(new V8ValueInteger(value));
    }

    default int push(long value) throws JavetException {
        return push(new V8ValueLong(value));
    }

    default int push(String value) throws JavetException {
        return push(new V8ValueString(value));
    }

    default int pushNull() throws JavetException {
        return push(getV8Runtime().createV8ValueNull());
    }

    default int pushUndefined() throws JavetException {
        return push(getV8Runtime().createV8ValueUndefined());
    }
}
