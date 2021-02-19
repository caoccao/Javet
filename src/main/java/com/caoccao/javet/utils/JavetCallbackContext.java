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

package com.caoccao.javet.utils;

import com.caoccao.javet.exceptions.JavetV8CallbackAlreadyRegisteredException;
import com.caoccao.javet.utils.receivers.IJavetCallbackReceiver;
import com.caoccao.javet.utils.converters.IJavetConverter;
import com.caoccao.javet.utils.converters.JavetPrimitiveConverter;
import com.caoccao.javet.values.reference.IV8ValueFunction;

import java.lang.reflect.Method;
import java.util.Objects;

public final class JavetCallbackContext {
    protected static final String ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID =
            "Javet callback context handle is invalid";
    protected Method callbackMethod;
    protected IV8ValueFunction callbackOwnerFunction;
    protected IJavetCallbackReceiver callbackReceiver;
    protected IJavetConverter converter;
    protected long handle;
    protected boolean returnResult;
    protected boolean thisObjectRequired;

    public JavetCallbackContext(
            IJavetCallbackReceiver callbackReceiver,
            Method callbackMethod) {
        this(callbackReceiver, callbackMethod, false);
    }

    public JavetCallbackContext(
            IJavetCallbackReceiver callbackReceiver,
            Method callbackMethod,
            boolean thisObjectRequired) {
        Objects.requireNonNull(callbackReceiver);
        Objects.requireNonNull(callbackMethod);
        callbackOwnerFunction = null;
        this.callbackMethod = callbackMethod;
        this.callbackReceiver = callbackReceiver;
        converter = new JavetPrimitiveConverter();
        handle = 0L;
        this.returnResult = !callbackMethod.getReturnType().equals(Void.TYPE);
        this.thisObjectRequired = thisObjectRequired;
    }

    public boolean isThisObjectRequired() {
        return thisObjectRequired;
    }

    public IV8ValueFunction getCallbackOwnerFunction() {
        return callbackOwnerFunction;
    }

    public void setCallbackOwnerFunction(IV8ValueFunction callbackOwnerFunction)
            throws JavetV8CallbackAlreadyRegisteredException {
        Objects.requireNonNull(callbackOwnerFunction);
        if (this.callbackOwnerFunction == null) {
            this.callbackOwnerFunction = callbackOwnerFunction;
        } else if (this.callbackOwnerFunction != callbackOwnerFunction) {
            throw new JavetV8CallbackAlreadyRegisteredException();
        }
    }

    public IJavetCallbackReceiver getCallbackReceiver() {
        return callbackReceiver;
    }

    public Method getCallbackMethod() {
        return callbackMethod;
    }

    public IJavetConverter getConverter() {
        return converter;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        assert handle > 0L : ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID;
        this.handle = handle;
    }

    public boolean isReturnResult() {
        return returnResult;
    }
}
