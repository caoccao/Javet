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

@SuppressWarnings("unchecked")
public interface IV8ValueCollection extends IV8ValueObject {
    int getLength() throws JavetException;

    default boolean popBoolean() throws JavetException {
        return ((V8ValueBoolean) pop()).getValue();
    }

    default double popDouble() throws JavetException {
        return ((V8ValueDouble) pop()).getValue();
    }

    default int popInteger() throws JavetException {
        return ((V8ValueInteger) pop()).getValue();
    }

    default long popLong() throws JavetException {
        return ((V8ValueLong) pop()).getValue();
    }

    default V8ValueNull popNull() throws JavetException {
        return ((V8ValueNull) pop());
    }

    default String popString() throws JavetException {
        return ((V8ValueString) pop()).getValue();
    }

    default V8ValueUndefined popUndefined() throws JavetException {
        return ((V8ValueUndefined) pop());
    }

    V8Value pop() throws JavetException;

    int push(V8Value v8Value) throws JavetException;

    default int pushBoolean(boolean value) throws JavetException {
        return push(new V8ValueBoolean(value));
    }

    default int pushDouble(double value) throws JavetException {
        return push(new V8ValueDouble(value));
    }

    default int pushInteger(int value) throws JavetException {
        return push(new V8ValueInteger(value));
    }

    default int pushLong(long value) throws JavetException {
        return push(new V8ValueLong(value));
    }

    default int pushNull() throws JavetException {
        return push(new V8ValueNull());
    }

    default int pushString(String value) throws JavetException {
        return push(new V8ValueString(value));
    }

    default int pushUndefined() throws JavetException {
        return push(new V8ValueUndefined());
    }
}
