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

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetIOException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8Executor {
    void compileOnly() throws JavetException;

    default <T extends V8Value> T execute() throws JavetException {
        return execute(true);
    }

    <T extends V8Value> T execute(boolean resultRequired) throws JavetException;

    default Boolean executeBoolean() throws JavetException {
        return executeObject();
    }

    default Double executeDouble() throws JavetException {
        return executeObject();
    }

    default Integer executeInteger() throws JavetException {
        return executeObject();
    }

    default Long executeLong() throws JavetException {
        return executeObject();
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executeObject() throws JavetException {
        try (V8Value v8Value = execute()) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String executeString()
            throws JavetException {
        return executeObject();
    }

    default void executeVoid() throws JavetException {
        execute(false);
    }

    default ZonedDateTime executeZonedDateTime() throws JavetException {
        return executeObject();
    }

    String getScriptString() throws JavetIOException;

    V8Runtime getV8Runtime();

    V8ScriptOrigin getV8ScriptOrigin();
}
