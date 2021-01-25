/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.interfaces.JavetClosable;
import com.caoccao.javet.interfaces.JavetResettable;
import com.caoccao.javet.values.V8TypedValue;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public final class V8Runtime implements JavetClosable, JavetResettable {
    private String globalName;
    private long handle;
    private V8Host v8Host;

    V8Runtime(V8Host v8Host, long handle, String globalName) {
        this.globalName = globalName;
        this.handle = handle;
        this.v8Host = v8Host;
    }

    long getHandle() {
        return handle;
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public <T extends V8Value> T execute(String scriptString) {
        return execute(scriptString, new V8ScriptOrigin(), true);
    }

    public <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        return execute(scriptString, v8ScriptOrigin, true);
    }

    public <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) {
        T returnValue = (T) V8Native.execute(handle, scriptString,
                resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
        if (returnValue != null) {
            returnValue.setV8Runtime(this);
        }
        return returnValue;
    }

    public Integer executeInteger(String scriptString) {
        return executeInteger(scriptString, new V8ScriptOrigin());
    }

    public Integer executeInteger(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    public Long executeLong(String scriptString) {
        return executeLong(scriptString, new V8ScriptOrigin());
    }

    public Long executeLong(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    public String executeString(String scriptString) {
        return executeString(scriptString, new V8ScriptOrigin());
    }

    public String executeString(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    private <R extends Object, T extends V8TypedValue<R>> R executeObject(String scriptString) {
        return executeObject(scriptString, new V8ScriptOrigin());
    }

    private <R extends Object, T extends V8TypedValue<R>> R executeObject(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        V8Value v8Value = execute(scriptString, v8ScriptOrigin, true);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    public void executeVoid(String scriptString) {
        executeVoid(scriptString, new V8ScriptOrigin());
    }

    public void executeVoid(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        execute(scriptString, v8ScriptOrigin, false);
    }

    @Override
    public void close() throws RuntimeException {
        v8Host.closeV8Runtime(this);
        handle = 0;
    }

    @Override
    public void reset() {
        V8Native.resetV8Runtime(handle, globalName);
    }
}
