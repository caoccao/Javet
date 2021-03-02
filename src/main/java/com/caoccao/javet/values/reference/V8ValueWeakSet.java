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

@SuppressWarnings("unchecked")
public class V8ValueWeakSet extends V8ValueObject {
    V8ValueWeakSet(long handle) {
        super(handle);
    }

    public void add(V8Value key) throws JavetException {
        invokeVoid(FUNCTION_ADD, key);
    }

    @Override
    public boolean delete(V8Value key) throws JavetException {
        invokeVoid(FUNCTION_DELETE, key);
        return true;
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.WeakSet;
    }

    @Override
    public boolean has(V8Value key) throws JavetException {
        return invokeBoolean(FUNCTION_HAS, key);
    }

    @Override
    public V8ValueWeakSet toClone() throws JavetException {
        checkV8Runtime();
        return v8Runtime.cloneV8Value(this);
    }
}
