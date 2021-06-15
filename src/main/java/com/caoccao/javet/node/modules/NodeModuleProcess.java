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

package com.caoccao.javet.node.modules;

import com.caoccao.javet.annotations.NodeModule;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;
import java.nio.file.Path;

@NodeModule(name = "process")
public class NodeModuleProcess extends BaseNodeModule {
    public static final String FUNCTION_CHDIR = "chdir";
    public static final String FUNCTION_CWD = "cwd";
    public static final String FUNCTION_ON = "on";

    public NodeModuleProcess(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    public Path getWorkingDirectory() throws JavetException {
        return new File(moduleObject.invokeString(FUNCTION_CWD)).toPath();
    }

    public void on(String eventName, V8ValueFunction v8ValueFunction) throws JavetException {
        moduleObject.invokeVoid(FUNCTION_ON, new V8ValueString(eventName), v8ValueFunction);
    }

    public void setWorkingDirectory(Path path) throws JavetException {
        moduleObject.invokeVoid(FUNCTION_CHDIR, new V8ValueString(path.toAbsolutePath().toString()));
    }
}
