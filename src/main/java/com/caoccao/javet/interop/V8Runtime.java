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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueString;

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

    public String executeString(String scriptString) {
        return ((V8ValueString) execute(scriptString, new V8ScriptOrigin(), true)).getValue();
    }

    public String executeString(String scriptString, V8ScriptOrigin v8ScriptOrigin) {
        return ((V8ValueString) execute(scriptString, v8ScriptOrigin, true)).getValue();
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
