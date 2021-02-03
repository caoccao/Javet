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

import java.util.List;

@SuppressWarnings("unchecked")
public interface IV8ValueKeyContainer extends IV8ValueObject {
    List<V8Value> getKeys() throws JavetException;

    int getSize() throws JavetException;

    default boolean has(int value) throws JavetException {
        return has(new V8ValueInteger(value));
    }

    default boolean has(long value) throws JavetException {
        return has(new V8ValueLong(value));
    }

    default boolean has(String value) throws JavetException {
        return has(new V8ValueString(value));
    }

    boolean has(V8Value value) throws JavetException;

    default boolean hasNull() throws JavetException {
        return has(new V8ValueNull());
    }

    default boolean hasUndefined() throws JavetException {
        return has(new V8ValueUndefined());
    }
}
