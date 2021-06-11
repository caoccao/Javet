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

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8Script;

public class V8StringExecutor extends BaseV8Executor {
    protected String scriptString;

    public V8StringExecutor(V8Runtime v8Runtime) {
        this(v8Runtime, null);
    }

    public V8StringExecutor(V8Runtime v8Runtime, String scriptString) {
        super(v8Runtime);
        this.scriptString = scriptString;
    }

    @Override
    @CheckReturnValue
    public V8Script compileScript(boolean resultRequired) throws JavetException {
        return v8Runtime.compileScript(getScriptString(), v8ScriptOrigin, resultRequired);
    }

    @Override
    @CheckReturnValue
    public V8Module compileV8Module(boolean resultRequired) throws JavetException {
        return v8Runtime.compileV8Module(getScriptString(), v8ScriptOrigin, resultRequired);
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        return v8Runtime.execute(getScriptString(), v8ScriptOrigin, resultRequired);
    }

    @Override
    public String getScriptString() throws JavetException {
        return scriptString;
    }
}
