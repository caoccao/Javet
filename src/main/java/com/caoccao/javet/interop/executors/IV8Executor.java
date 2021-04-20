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
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8Script;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unchecked")
public interface IV8Executor extends IV8Executable {
    default V8Script compileScript() throws JavetException {
        return compileScript(true);
    }

    V8Script compileScript(boolean resultRequired) throws JavetException;

    default void compileScriptVoid() throws JavetException {
        compileScript(false);
    }

    default V8Module compileV8Module() throws JavetException {
        return compileV8Module(true);
    }

    V8Module compileV8Module(boolean resultRequired) throws JavetException;

    default void compileV8ModuleVoid() throws JavetException {
        compileV8Module(false);
    }

    default String getResourceName() {
        return getV8ScriptOrigin().getResourceName();
    }

    default IV8Executor setResourceName(String resourceName) throws JavetException {
        getV8ScriptOrigin().setResourceName(resourceName);
        V8Runtime v8Runtime = getV8Runtime();
        if (v8Runtime.getJSRuntimeType().isNode()) {
            NodeRuntime nodeRuntime = (NodeRuntime) v8Runtime;
            Path resourcePath = new File(resourceName).toPath();
            nodeRuntime.getGlobalObject().set(NodeRuntime.PROPERTY_DIRNAME, new V8ValueString(resourcePath.getParent().toString()));
            nodeRuntime.getGlobalObject().set(NodeRuntime.PROPERTY_FILENAME, new V8ValueString(resourcePath.toString()));
            nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(resourcePath.getParent());
            nodeRuntime.getNodeModule(NodeModuleProcess.class).setWorkingDirectory(resourcePath.getParent());
        }
        return this;
    }

    default boolean isModule() {
        return getV8ScriptOrigin().isModule();
    }

    default IV8Executor setModule(boolean module) {
        getV8ScriptOrigin().setModule(module);
        return this;
    }

    String getScriptString() throws JavetException;

    V8Runtime getV8Runtime();

    V8ScriptOrigin getV8ScriptOrigin();
}
