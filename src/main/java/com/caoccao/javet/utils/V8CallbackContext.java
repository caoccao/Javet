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
import com.caoccao.javet.interop.IV8CallbackReceiver;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.IV8ValueFunction;

import java.lang.reflect.Method;
import java.util.Objects;

public class V8CallbackContext {
    protected static final String ERROR_V8_CALLBACK_CONTEXT_HANDLE_IS_INVALID =
            "V8 callback context handle is invalid";
    protected Method callbackMethod;
    protected IV8ValueFunction callbackOwnerFunction;
    protected IV8CallbackReceiver callbackReceiver;
    protected long handle;
    protected boolean returnResult;

    public V8CallbackContext(
            IV8CallbackReceiver callbackReceiver,
            Method callbackMethod) {
        Objects.requireNonNull(callbackReceiver);
        Objects.requireNonNull(callbackMethod);
        callbackOwnerFunction = null;
        this.callbackMethod = callbackMethod;
        this.callbackReceiver = callbackReceiver;
        handle = 0L;
        this.returnResult = !callbackMethod.getReturnType().equals(Void.TYPE);
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

    public IV8CallbackReceiver getCallbackReceiver() {
        return callbackReceiver;
    }

    public Method getCallbackMethod() {
        return callbackMethod;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        assert handle > 0L : ERROR_V8_CALLBACK_CONTEXT_HANDLE_IS_INVALID;
        this.handle = handle;
    }

    public boolean isReturnResult() {
        return returnResult;
    }
}
