/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualValueList;

import java.util.Objects;

/**
 * The type V8 value function.
 *
 * @since 0.7.0
 */
public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {
    /**
     * The JS function type.
     *
     * @since 0.8.8
     */
    protected JSFunctionType jsFunctionType;

    /**
     * Instantiates a new V8 value function.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    protected V8ValueFunction(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
        jsFunctionType = null;
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException {
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(checkV8Runtime(), null, objects)) {
            return v8Runtime.getV8Internal().callAsConstructor(this, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException {
        return checkV8Runtime().getV8Internal().callAsConstructor(this, v8Values);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, Object... objects)
            throws JavetException {
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(checkV8Runtime(), null, objects)) {
            return v8Runtime.getV8Internal().call(this, receiver, returnResult, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values) throws JavetException {
        return checkV8Runtime().getV8Internal().call(this, receiver, returnResult, v8Values);
    }

    @Override
    public void copyScopeInfoFrom(IV8ValueFunction sourceIV8ValueFunction) throws JavetException {
        assert this != Objects.requireNonNull(sourceIV8ValueFunction) : "The source function cannot be the caller.";
        checkV8Runtime().getV8Internal().functionCopyScopeInfoFrom(
                this, sourceIV8ValueFunction);
    }

    @Override
    @CheckReturnValue
    public IV8ValueArray getInternalProperties() throws JavetException {
        return checkV8Runtime().getV8Internal().getInternalProperties(this);
    }

    @Override
    public JSFunctionType getJSFunctionType() throws JavetException {
        if (jsFunctionType == null) {
            jsFunctionType = checkV8Runtime().getV8Internal().getJSFunctionType(this);
        }
        return jsFunctionType;
    }

    @Override
    public JSScopeType getJSScopeType() throws JavetException {
        return checkV8Runtime().getV8Internal().getJSScopeType(this);
    }

    @Override
    public ScriptSource getScriptSource() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetScriptSource(this);
    }

    @Override
    public String getSourceCode() throws JavetException {
        if (getJSFunctionType().isUserDefined()) {
            return checkV8Runtime().getV8Internal().functionGetSourceCode(this);
        }
        return null;
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Function;
    }

    @Override
    public boolean setSourceCode(String sourceCodeString) throws JavetException {
        boolean success = false;
        if (getJSFunctionType().isUserDefined()
                && sourceCodeString != null && sourceCodeString.length() > 0) {
            checkV8Runtime();
            success = v8Runtime.getV8Internal().functionSetSourceCode(this, sourceCodeString);
        }
        return success;
    }
}
