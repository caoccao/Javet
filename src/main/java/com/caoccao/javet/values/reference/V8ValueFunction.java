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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {

    V8ValueFunction(long handle) {
        super(handle);
    }

    @Override
    public <T extends V8Value> T call(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.call(this, receiver, returnResult,
                v8Runtime.getConverter().toV8Values(v8Runtime, objects));
    }

    @Override
    public <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException {
        checkV8Runtime();
        return v8Runtime.callAsConstructor(this,
                v8Runtime.getConverter().toV8Values(v8Runtime, objects));
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Function;
    }
}
