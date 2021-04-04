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
import com.caoccao.javet.utils.converters.IJavetConverter;
import com.caoccao.javet.utils.converters.JavetObjectConverter;
import com.caoccao.javet.values.reference.IV8ValueFunction;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public final class JavetCallbackContext {
    private static final String ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID =
            "Callback receiver or callback method is invalid";
    private static final String ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID =
            "Javet callback context handle is invalid";
    private Method callbackMethod;
    private IV8ValueFunction callbackOwnerFunction;
    private Object callbackReceiver;
    private IJavetConverter converter;
    private long handle;
    private boolean returnResult;
    private boolean thisObjectRequired;

    public JavetCallbackContext(
            Object callbackReceiver,
            Method callbackMethod) {
        this(callbackReceiver, callbackMethod, false);
    }

    public JavetCallbackContext(
            Object callbackReceiver,
            Method callbackMethod,
            boolean thisObjectRequired) {
        this(callbackReceiver, callbackMethod, thisObjectRequired, new JavetObjectConverter());
    }

    public JavetCallbackContext(
            Object callbackReceiver,
            Method callbackMethod,
            boolean thisObjectRequired,
            IJavetConverter converter) {
        Objects.requireNonNull(callbackMethod);
        assert (callbackReceiver != null && !Modifier.isStatic(callbackMethod.getModifiers()))
                || (callbackReceiver == null && Modifier.isStatic(callbackMethod.getModifiers()))
                : ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID;
        callbackOwnerFunction = null;
        this.callbackMethod = callbackMethod;
        this.callbackReceiver = callbackReceiver;
        this.converter = converter;
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

    public Object getCallbackReceiver() {
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
