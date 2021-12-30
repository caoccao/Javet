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
import com.caoccao.javet.interop.V8Runtime;

public class V8ValueError extends V8ValueObject {
    protected static final String MESSAGE = "message";
    protected static final String STACK = "stack";

    V8ValueError(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    public String getMessage() throws JavetException {
        return getPropertyString(MESSAGE);
    }

    public String getStack() throws JavetException {
        return getPropertyString(STACK);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Error;
    }
}
