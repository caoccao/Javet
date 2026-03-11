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
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;
import java.util.Objects;

/**
 * Represents the Node.js {@code process} module.
 */
@NodeModule(name = "process")
public class NodeModuleProcess extends BaseNodeModule {
    /** The function name for {@code process.chdir()}. */
    public static final String FUNCTION_CHDIR = "chdir";
    /** The function name for {@code process.cwd()}. */
    public static final String FUNCTION_CWD = "cwd";
    /** The function name for {@code process.on()}. */
    public static final String FUNCTION_ON = "on";
    /** The property name for {@code process.version}. */
    public static final String PROPERTY_VERSION = "version";

    /**
     * Instantiates a new Node module process.
     *
     * @param moduleObject the underlying V8 object representing the module
     * @param name         the module name
     */
    public NodeModuleProcess(V8ValueObject moduleObject, String name) {
        super(moduleObject, name);
    }

    /**
     * Gets the Node.js version string (e.g. "v18.0.0").
     *
     * @return the Node.js version
     * @throws JavetException the javet exception
     */
    public String getVersion() throws JavetException {
        return moduleObject.getString(moduleObject.getV8Runtime().createV8ValueString(PROPERTY_VERSION));
    }

    /**
     * Gets the current working directory of the Node.js process.
     *
     * @return the working directory as a {@link File}
     * @throws JavetException the javet exception
     */
    public File getWorkingDirectory() throws JavetException {
        return new File(moduleObject.invokeString(FUNCTION_CWD));
    }

    /**
     * Registers an event listener on the process object.
     *
     * @param eventName       the event name (e.g. "exit", "uncaughtException")
     * @param v8ValueFunction the callback function
     * @throws JavetException the javet exception
     */
    public void on(String eventName, V8ValueFunction v8ValueFunction) throws JavetException {
        moduleObject.invokeVoid(
                FUNCTION_ON,
                moduleObject.getV8Runtime().createV8ValueString(eventName),
                v8ValueFunction);
    }

    /**
     * Sets the current working directory of the Node.js process.
     *
     * @param workingDirectory the working directory as a {@link File}
     * @throws JavetException the javet exception
     */
    public void setWorkingDirectory(File workingDirectory) throws JavetException {
        setWorkingDirectory(Objects.requireNonNull(workingDirectory).getAbsolutePath());
    }

    /**
     * Sets the current working directory of the Node.js process.
     *
     * @param workingDirectory the working directory path
     * @throws JavetException the javet exception
     */
    public void setWorkingDirectory(String workingDirectory) throws JavetException {
        moduleObject.invokeVoid(
                FUNCTION_CHDIR,
                moduleObject.getV8Runtime().createV8ValueString(Objects.requireNonNull(workingDirectory)));
    }
}
