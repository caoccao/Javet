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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetIOException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Script;

import java.util.Objects;

public abstract class BaseV8Executor implements IV8Executor {
    protected V8Runtime v8Runtime;
    protected V8ScriptOrigin v8ScriptOrigin;

    public BaseV8Executor(V8Runtime v8Runtime) {
        Objects.requireNonNull(v8Runtime);
        this.v8Runtime = v8Runtime;
        this.v8ScriptOrigin = new V8ScriptOrigin();
    }

    @Override
    public abstract V8Script compileScript(boolean resultRequired) throws JavetException;

    @Override
    public abstract <T extends V8Value> T execute(boolean resultRequired) throws JavetException;

    @Override
    public abstract String getScriptString() throws JavetIOException;

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public V8ScriptOrigin getV8ScriptOrigin() {
        return v8ScriptOrigin;
    }
}
