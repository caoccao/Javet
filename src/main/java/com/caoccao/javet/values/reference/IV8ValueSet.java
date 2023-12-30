/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;

public interface IV8ValueSet extends IV8ValueKeyContainer {
    void add(Object key) throws JavetException;

    default void addNull() throws JavetException {
        add(getV8Runtime().createV8ValueNull());
    }

    default void addUndefined() throws JavetException {
        add(getV8Runtime().createV8ValueUndefined());
    }

    @CheckReturnValue
    IV8ValueIterator<V8ValueArray> getEntries() throws JavetException;
}
