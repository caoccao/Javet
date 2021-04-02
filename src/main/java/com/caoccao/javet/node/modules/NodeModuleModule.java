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
import com.caoccao.javet.values.reference.V8ValueObject;

import java.nio.file.Path;

@NodeModule(name = "module")
public class NodeModuleModule extends BaseNodeModule {
    public static final String FUNCTION_CREATE_REQUIRE = "createRequire";
    public static final String PROPERTY_REQUIRE = "require";

    public NodeModuleModule(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    public void setRequireRootDirectory(Path path) throws JavetException {
        try (V8ValueObject v8ValueObject = moduleObject.invoke(FUNCTION_CREATE_REQUIRE,
                new V8ValueString(path.toAbsolutePath().toString()))) {
            moduleObject.getV8Runtime().getGlobalObject().set(PROPERTY_REQUIRE, v8ValueObject);
        }
    }
}
