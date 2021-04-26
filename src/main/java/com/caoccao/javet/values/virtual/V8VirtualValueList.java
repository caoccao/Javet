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

package com.caoccao.javet.values.virtual;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class V8VirtualValueList implements IJavetClosable {
    protected List<V8Value> toBeClosedValues;
    protected List<V8Value> values;

    public V8VirtualValueList(V8Runtime v8Runtime, Object... objects) throws JavetException {
        toBeClosedValues = new ArrayList<>(objects.length);
        values = new ArrayList<>(objects.length);
        if (objects != null && objects.length > 0) {
            for (Object object : objects) {
                if (object instanceof V8Value) {
                    values.add((V8Value) object);
                } else {
                    V8Value value = v8Runtime.toV8Value(object);
                    values.add(value);
                    toBeClosedValues.add(value);
                }
            }
        }
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(toBeClosedValues);
    }

    public V8Value[] get() {
        return values.toArray(new V8Value[0]);
    }
}
