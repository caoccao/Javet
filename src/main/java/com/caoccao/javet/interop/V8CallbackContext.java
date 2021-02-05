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

package com.caoccao.javet.interop;

import java.lang.reflect.Method;
import java.util.Objects;

class V8CallbackContext {
    protected static final String ERROR_V8_CALLBACK_HANDLE_IS_INVALID = "V8 callback handle is invalid";
    protected Method callbackMethod;
    protected IV8CallbackReceiver callbackReceiver;
    protected String functionName;
    protected long handle;
    protected boolean returnResult;
    protected V8Runtime v8Runtime;

    public V8CallbackContext(
            V8Runtime v8Runtime,
            String functionName,
            IV8CallbackReceiver callbackReceiver,
            Method callbackMethod) {
        Objects.requireNonNull(v8Runtime);
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(callbackReceiver);
        Objects.requireNonNull(callbackMethod);
        this.callbackMethod = callbackMethod;
        this.callbackReceiver = callbackReceiver;
        this.functionName = functionName;
        handle = 0L;
        this.returnResult = !callbackMethod.getReturnType().equals(Void.TYPE);
        this.v8Runtime = v8Runtime;
    }

    public IV8CallbackReceiver getCallbackReceiver() {
        return callbackReceiver;
    }

    public Method getCallbackMethod() {
        return callbackMethod;
    }

    public String getFunctionName() {
        return functionName;
    }

    public long getHandle() {
        return handle;
    }

    public void setHandle(long handle) {
        assert handle > 0L : ERROR_V8_CALLBACK_HANDLE_IS_INVALID;
        this.handle = handle;
    }

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    public boolean isReturnResult() {
        return returnResult;
    }
}
