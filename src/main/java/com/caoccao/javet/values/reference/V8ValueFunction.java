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
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Internal;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.V8ValueUtils;
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
     * The constant ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_IN_ANOTHER_V8_RUNTIME.
     *
     * @since 2.0.1
     */
    protected static final String ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_IN_ANOTHER_V8_RUNTIME =
            "The source function cannot be in another V8 runtime.";
    /**
     * The constant ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_THE_CALLER.
     *
     * @since 2.0.1
     */
    protected static final String ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_THE_CALLER =
            "The source function cannot be the caller.";
    /**
     * The constant ERROR_V8_CONTEXT_CANNOT_BE_NULL.
     *
     * @since 2.0.1
     */
    protected static final String ERROR_V8_CONTEXT_CANNOT_BE_NULL = "V8 context cannot be null.";
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
            return v8Runtime.getV8Internal().functionCallAsConstructor(this, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException {
        return checkV8Runtime().getV8Internal().functionCallAsConstructor(this, v8Values);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(V8Value receiver, boolean returnResult, Object... objects)
            throws JavetException {
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(checkV8Runtime(), null, objects)) {
            return v8Runtime.getV8Internal().functionCall(this, receiver, returnResult, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T callExtended(V8Value receiver, boolean returnResult, V8Value... v8Values) throws JavetException {
        return checkV8Runtime().getV8Internal().functionCall(this, receiver, returnResult, v8Values);
    }

    @Override
    public boolean canDiscardCompiled() throws JavetException {
        return checkV8Runtime().getV8Internal().functionCanDiscardCompiled(this);
    }

    @Override
    public boolean copyScopeInfoFrom(IV8ValueFunction sourceIV8ValueFunction) throws JavetException {
        assert this != Objects.requireNonNull(sourceIV8ValueFunction) : ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_THE_CALLER;
        assert checkV8Runtime() == sourceIV8ValueFunction.getV8Runtime() : ERROR_THE_SOURCE_FUNCTION_CANNOT_BE_IN_ANOTHER_V8_RUNTIME;
        if (!getJSFunctionType().isUserDefined() || !sourceIV8ValueFunction.getJSFunctionType().isUserDefined()) {
            return false;
        }
        return v8Runtime.getV8Internal().functionCopyScopeInfoFrom(this, sourceIV8ValueFunction);
    }

    @Override
    public boolean discardCompiled() throws JavetException {
        return checkV8Runtime().getV8Internal().functionDiscardCompiled(this);
    }

    @Override
    public String[] getArguments() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetArguments(this);
    }

    @Override
    public byte[] getCachedData() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetCachedData(this);
    }

    @Override
    public V8Context getContext() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetContext(this);
    }

    @Override
    @CheckReturnValue
    public IV8ValueArray getInternalProperties() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetInternalProperties(this);
    }

    @Override
    public JSFunctionType getJSFunctionType() throws JavetException {
        if (jsFunctionType == null) {
            jsFunctionType = checkV8Runtime().getV8Internal().functionGetJSFunctionType(this);
        }
        return jsFunctionType;
    }

    @Override
    public JSScopeType getJSScopeType() throws JavetException {
        return checkV8Runtime().getV8Internal().functionGetJSScopeType(this);
    }

    @CheckReturnValue
    @Override
    public ScopeInfos getScopeInfos(GetScopeInfosOptions options)
            throws JavetException {
        try (IV8ValueArray iV8ValueArray = checkV8Runtime().getV8Internal().functionGetScopeInfos(
                this, options)) {
            return new ScopeInfos(iV8ValueArray);
        }
    }

    @Override
    public ScriptSource getScriptSource() throws JavetException {
        if (getJSFunctionType().isUserDefined()) {
            return checkV8Runtime().getV8Internal().functionGetScriptSource(this);
        }
        return null;
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
    public boolean isCompiled() throws JavetException {
        return checkV8Runtime().getV8Internal().functionIsCompiled(this);
    }

    @Override
    public boolean isWrapped() throws JavetException {
        return checkV8Runtime().getV8Internal().functionIsWrapped(this);
    }

    @Override
    public boolean setContext(V8Context v8Context) throws JavetException {
        Objects.requireNonNull(v8Context, ERROR_V8_CONTEXT_CANNOT_BE_NULL);
        return checkV8Runtime().getV8Internal().functionSetContext(this, v8Context);
    }

    @Override
    public boolean setScriptSource(ScriptSource scriptSource, boolean cloneScript) throws JavetException {
        boolean success = false;
        if (getJSFunctionType().isUserDefined() && getJSScopeType().isFunction() && scriptSource != null) {
            success = checkV8Runtime().getV8Internal().functionSetScriptSource(
                    this, scriptSource, cloneScript);
        }
        return success;
    }

    @Override
    public boolean setSourceCode(
            String sourceCodeString,
            SetSourceCodeOptions options) throws JavetException {
        Objects.requireNonNull(options, "Options cannot be null.");
        boolean success = false;
        if (getJSFunctionType().isUserDefined() && getJSScopeType().isFunction()
                && sourceCodeString != null && sourceCodeString.length() > 0) {
            if (options.isTrimTailingCharacters()) {
                sourceCodeString = V8ValueUtils.trimAnonymousFunction(sourceCodeString);
            }
            V8Internal v8Internal = checkV8Runtime().getV8Internal();
            if (options.isPreGC()) {
                v8Runtime.lowMemoryNotification();
            }
            try {
                if (options.isNativeCalculation()) {
                    success = v8Internal.functionSetSourceCode(
                            this, sourceCodeString, options.isCloneScript());
                } else {
                    ScriptSource originalScriptSource = v8Internal.functionGetScriptSource(this);
                    ScriptSource newScriptSource = originalScriptSource.setCodeSnippet(sourceCodeString);
                    if (getJSFunctionType().isUserDefined() && getJSScopeType().isFunction()) {
                        success = v8Internal.functionSetScriptSource(
                                this, newScriptSource, options.isCloneScript());
                    }
                }
            } finally {
                if (options.isPostGC()) {
                    v8Runtime.lowMemoryNotification();
                }
            }
        }
        return success;
    }
}
