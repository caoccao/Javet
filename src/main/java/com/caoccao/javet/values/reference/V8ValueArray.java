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
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.utils.V8ValueIteratorUtils;
import com.caoccao.javet.values.virtual.V8VirtualList;

@SuppressWarnings("unchecked")
public class V8ValueArray extends V8ValueObject implements IV8ValueArray {

    public static final String FUNCTION_KEYS = "keys";
    public static final String FUNCTION_POP = "pop";
    public static final String FUNCTION_PUSH = "push";

    public V8ValueArray(long handle) {
        super(handle);
    }

    @Override
    public <T extends V8Value> T get(int index) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, new V8ValueInteger(index));
    }

    @Override
    public V8VirtualList<Integer> getKeys() throws JavetException {
        checkV8Runtime();
        try (V8ValueObject arrayIterator = invoke(FUNCTION_KEYS)) {
            return V8ValueIteratorUtils.convertIteratorToIntegerList(arrayIterator);
        }
    }

    @Override
    public int getLength()
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getLength(this);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Array;
    }

    @Override
    public <T extends V8Value> T pop() throws JavetException {
        checkV8Runtime();
        return invoke(FUNCTION_POP);
    }

    @Override
    public int push(V8Value v8Value) throws JavetException {
        checkV8Runtime();
        return invokeInteger(FUNCTION_PUSH, v8Value);
    }
}
