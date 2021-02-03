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

public class V8ValueMap extends V8ValueObject implements IV8ValueMap {

    public V8ValueMap(long handle) {
        super(handle);
    }

    @Override
    public IV8ValueArray getKeys() throws JavetException {
        return null;
    }

    @Override
    public int getSize() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getSize(this);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Map;
    }

    @Override
    public IV8ValueArray getValues() throws JavetException {
        return null;
    }

    @Override
    public boolean has(V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.has(this, value);
    }
}
