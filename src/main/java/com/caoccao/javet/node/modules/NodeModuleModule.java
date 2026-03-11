/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.node.modules;

import com.caoccao.javet.annotations.NodeModule;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;
import java.util.Objects;

/**
 * Represents the Node.js {@code module} module, providing access to {@code createRequire}.
 */
@NodeModule(name = "module")
public class NodeModuleModule extends BaseNodeModule {
    /** The function name for {@code Module.createRequire()}. */
    public static final String FUNCTION_CREATE_REQUIRE = "createRequire";
    /** The property name for the global {@code require} function. */
    public static final String PROPERTY_REQUIRE = "require";

    /**
     * Instantiates a new Node module module.
     *
     * @param moduleObject the underlying V8 object representing the module
     * @param name         the module name
     */
    public NodeModuleModule(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    /**
     * Sets the root directory for the global {@code require} function using {@code Module.createRequire()}.
     *
     * @param requireRootDirectory the root directory path
     * @throws JavetException the javet exception
     */
    public void setRequireRootDirectory(String requireRootDirectory) throws JavetException {
        try (V8ValueObject v8ValueObject = moduleObject.invoke(
                FUNCTION_CREATE_REQUIRE,
                moduleObject.getV8Runtime().createV8ValueString(Objects.requireNonNull(requireRootDirectory)))) {
            moduleObject.getV8Runtime().getGlobalObject().set(PROPERTY_REQUIRE, v8ValueObject);
        }
    }

    /**
     * Sets the root directory for the global {@code require} function using {@code Module.createRequire()}.
     *
     * @param requireRootDirectory the root directory as a {@link File}
     * @throws JavetException the javet exception
     */
    public void setRequireRootDirectory(File requireRootDirectory) throws JavetException {
        setRequireRootDirectory(requireRootDirectory.getAbsolutePath() + File.separator);
    }
}
