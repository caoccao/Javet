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
public interface IV8Executable extends IV8Convertible {
    default <T extends V8Value> T execute() throws JavetException {
        return execute(true);
    }

    <T extends V8Value> T execute(boolean resultRequired) throws JavetException;

    default Boolean executeBoolean() throws JavetException {
        return executePrimitive();
    }

    default Double executeDouble() throws JavetException {
        return executePrimitive();
    }

    default Integer executeInteger() throws JavetException {
        return executePrimitive();
    }

    default Long executeLong() throws JavetException {
        return executePrimitive();
    }

    default <T extends Object> T executeObject() throws JavetException {
        try (V8Value v8Value = execute()) {
            return toObject(v8Value);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executePrimitive() throws JavetException {
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
        return executePrimitive();
    }

    default void executeVoid() throws JavetException {
        execute(false);
    }

    default ZonedDateTime executeZonedDateTime() throws JavetException {
        return executePrimitive();
    }
}
