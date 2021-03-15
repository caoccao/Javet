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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8Script;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unchecked")
public interface IV8Executable {
    V8Module compileModule(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException;

    V8Script compileScript(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException;

    <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException;

    default IV8Executor getExecutor(File scriptFile) {
        return getExecutor(scriptFile.toPath());
    }

    IV8Executor getExecutor(Path scriptPath);

    IV8Executor getExecutor(String scriptString);
}
