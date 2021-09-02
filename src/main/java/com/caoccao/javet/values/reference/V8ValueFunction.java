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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualValueList;

import java.util.Optional;

public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {
    protected Optional<JSFunctionType> jsFunctionType;

    protected V8ValueFunction(long handle) {
        super(handle);
        jsFunctionType = Optional.empty();
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(v8Runtime, objects)) {
            return v8Runtime.callAsConstructor(this, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException {
        checkV8Runtime();
        v8Runtime.decorateV8Values(v8Values);
        return v8Runtime.callAsConstructor(this, v8Values);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException {
        checkV8Runtime();
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(v8Runtime, objects)) {
            return v8Runtime.call(this, receiver, returnResult, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values) throws JavetException {
        checkV8Runtime();
        v8Runtime.decorateV8Values(v8Values);
        return v8Runtime.call(this, receiver, returnResult, v8Values);
    }

    @Override
    @CheckReturnValue
    public IV8ValueArray getInternalProperties() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getInternalProperties(this);
    }

    @Override
    public JSFunctionType getJSFunctionType() throws JavetException {
        if (!jsFunctionType.isPresent()) {
            checkV8Runtime();
            jsFunctionType = Optional.of(v8Runtime.getJSFunctionType(this));
        }
        return jsFunctionType.get();
    }

    @Override
    public JSScopeType getJSScopeType() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getJSScopeType(this);
    }

    @Override
    public String getSourceCode() throws JavetException {
        checkV8Runtime();
        if (getJSFunctionType().isUserDefined()) {
            return v8Runtime.getSourceCode(this);
        }
        return null;
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Function;
    }

    @Override
    public boolean setSourceCode(String sourceCodeString) throws JavetException {
        checkV8Runtime();
        if (getJSFunctionType().isUserDefined()) {
            if (sourceCodeString != null && sourceCodeString.length() > 0) {
                return v8Runtime.setSourceCode(this, sourceCodeString);
            }
        }
        return false;
    }
}
