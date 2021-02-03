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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public interface IV8Executable {
    default void compileOnly(String scriptString) throws JavetException {
        compileOnly(scriptString, new V8ScriptOrigin());
    }

    void compileOnly(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException;

    default <T extends V8Value> T execute(String scriptString) throws JavetException {
        return execute(scriptString, new V8ScriptOrigin());
    }

    default <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return execute(scriptString, v8ScriptOrigin, true);
    }

    <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException;

    default Boolean executeBoolean(String scriptString) throws JavetException {
        return executeBoolean(scriptString, new V8ScriptOrigin());
    }

    default Boolean executeBoolean(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default Double executeDouble(String scriptString) throws JavetException {
        return executeDouble(scriptString, new V8ScriptOrigin());
    }

    default Double executeDouble(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default Integer executeInteger(String scriptString) throws JavetException {
        return executeInteger(scriptString, new V8ScriptOrigin());
    }

    default Integer executeInteger(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default Long executeLong(String scriptString) throws JavetException {
        return executeLong(scriptString, new V8ScriptOrigin());
    }

    default Long executeLong(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(String scriptString)
            throws JavetException {
        return executeObject(scriptString, new V8ScriptOrigin());
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(
            String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        try (V8Value v8Value = execute(scriptString, v8ScriptOrigin, true)) {
            try {
                return ((T) v8Value).getValue();
            } catch (Throwable t) {
            }
        }
        return null;
    }

    default String executeString(String scriptString) throws JavetException {
        return executeString(scriptString, new V8ScriptOrigin());
    }

    default String executeString(String scriptString, V8ScriptOrigin v8ScriptOrigin)
            throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default void executeVoid(String scriptString) throws JavetException {
        executeVoid(scriptString, new V8ScriptOrigin());
    }

    default void executeVoid(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        execute(scriptString, v8ScriptOrigin, false);
    }

    default ZonedDateTime executeZonedDateTime(String scriptString) throws JavetException {
        return executeZonedDateTime(scriptString, new V8ScriptOrigin());
    }

    default ZonedDateTime executeZonedDateTime(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }
}
