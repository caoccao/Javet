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
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.values.primitive.V8ValueString;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class V8PathExecutor extends V8StringExecutor {
    public static final String NODE_GLOBAL_DIRNAME = "__dirname";
    public static final String NODE_GLOBAL_FILENAME = "__filename";
    protected Path scriptPath;

    public V8PathExecutor(V8Runtime v8Runtime, Path scriptPath) {
        super(v8Runtime);
        this.scriptPath = scriptPath;
    }

    @Override
    public String getScriptString() throws JavetException {
        if (scriptString == null) {
            try {
                if (v8Runtime.getJSRuntimeType().isNode()) {
                    NodeRuntime nodeRuntime = (NodeRuntime) v8Runtime;
                    nodeRuntime.getGlobalObject().set(NODE_GLOBAL_DIRNAME, new V8ValueString(scriptPath.getParent().toString()));
                    nodeRuntime.getGlobalObject().set(NODE_GLOBAL_FILENAME, new V8ValueString(scriptPath.toString()));
                    nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(scriptPath.getParent());
                    nodeRuntime.getNodeModule(NodeModuleProcess.class).setWorkingDirectory(scriptPath.getParent());
                }
                scriptString = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
                v8ScriptOrigin.setResourceName(scriptPath.toFile().getAbsolutePath());
            } catch (IOException e) {
                throw JavetIOException.failedToReadPath(scriptPath, e);
            }
        }
        return scriptString;
    }

    public Path getScriptPath() {
        return scriptPath;
    }
}
