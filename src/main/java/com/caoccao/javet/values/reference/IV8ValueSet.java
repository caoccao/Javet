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
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueLong;
import com.caoccao.javet.values.primitive.V8ValueString;

@SuppressWarnings("unchecked")
public interface IV8ValueSet extends IV8ValueKeyContainer {

    default void add(int value) throws JavetException {
        add(new V8ValueInteger(value));
    }

    default void add(long value) throws JavetException {
        add(new V8ValueLong(value));
    }

    default void add(String value) throws JavetException {
        add(new V8ValueString(value));
    }

    void add(V8Value key) throws JavetException;

    default void addNull() throws JavetException {
        add(new V8ValueNull());
    }

    default void addUndefined() throws JavetException {
        add(new V8ValueUndefined());
    }
}
