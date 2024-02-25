/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8Script;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;

/**
 * The interface V8 executor.
 *
 * @since 0.7.0
 */
public interface IV8Executor extends IV8Executable {
    /**
     * Compile V8 module.
     *
     * @return the V8 module
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @CheckReturnValue
    default V8Module compileV8Module() throws JavetException {
        return compileV8Module(true);
    }

    /**
     * Compile V8 module.
     *
     * @param resultRequired the result required
     * @return the V8 module
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @CheckReturnValue
    V8Module compileV8Module(boolean resultRequired) throws JavetException;

    /**
     * Compile V8 module.
     *
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void compileV8ModuleVoid() throws JavetException {
        compileV8Module(false);
    }

    /**
     * Compile V8 script.
     *
     * @return the V8 script
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @CheckReturnValue
    default V8Script compileV8Script() throws JavetException {
        return compileV8Script(true);
    }

    /**
     * Compile V8 script.
     *
     * @param resultRequired the result required
     * @return the V8 script
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @CheckReturnValue
    V8Script compileV8Script(boolean resultRequired) throws JavetException;

    /**
     * Compile V8 script.
     *
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void compileV8ScriptVoid() throws JavetException {
        compileV8Script(false);
    }

    /**
     * Compile V8 value function.
     *
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 2.0.3
     */
    @CheckReturnValue
    default V8ValueFunction compileV8ValueFunction() throws JavetException {
        return compileV8ValueFunction(null, null);
    }

    /**
     * Compile V8 value function.
     *
     * @param arguments the arguments
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 2.0.3
     */
    @CheckReturnValue
    default V8ValueFunction compileV8ValueFunction(String[] arguments) throws JavetException {
        return compileV8ValueFunction(arguments, null);
    }

    /**
     * Compile V8 value function.
     *
     * @param arguments         the arguments
     * @param contextExtensions the context extensions
     * @return the V8 value function
     * @throws JavetException the javet exception
     * @since 2.0.3
     */
    @CheckReturnValue
    V8ValueFunction compileV8ValueFunction(String[] arguments, V8ValueObject[] contextExtensions) throws JavetException;

    /**
     * Get cached data.
     *
     * @return the cached data
     * @since 2.0.3
     */
    byte[] getCachedData();

    /**
     * Gets resource name.
     *
     * @return the resource name
     * @since 0.8.0
     */
    default String getResourceName() {
        return getV8ScriptOrigin().getResourceName();
    }

    /**
     * Gets script string.
     *
     * @return the script string
     * @throws JavetException the javet exception
     * @since 0.9.1
     */
    String getScriptString() throws JavetException;

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 0.9.1
     */
    V8Runtime getV8Runtime();

    /**
     * Gets V8 script origin.
     *
     * @return the V8 script origin
     * @since 0.9.1
     */
    V8ScriptOrigin getV8ScriptOrigin();

    /**
     * Is module boolean.
     *
     * @return the boolean
     * @since 0.9.1
     */
    default boolean isModule() {
        return getV8ScriptOrigin().isModule();
    }

    /**
     * Sets module.
     *
     * @param module the module
     * @return the self
     * @since 0.9.1
     */
    default IV8Executor setModule(boolean module) {
        getV8ScriptOrigin().setModule(module);
        return this;
    }

    /**
     * Sets resource name.
     *
     * @param resourceName the resource name
     * @return the self
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    default IV8Executor setResourceName(String resourceName) throws JavetException {
        getV8ScriptOrigin().setResourceName(resourceName);
        V8Runtime v8Runtime = getV8Runtime();
        if (v8Runtime.getJSRuntimeType().isNode()) {
            if (!JavetOSUtils.IS_ANDROID) {
                NodeRuntime nodeRuntime = (NodeRuntime) v8Runtime;
                File resourceFile = new File(resourceName);
                File parentFile = resourceFile.getParentFile();
                nodeRuntime.getGlobalObject().set(NodeRuntime.PROPERTY_DIRNAME, parentFile.getAbsolutePath());
                nodeRuntime.getGlobalObject().set(NodeRuntime.PROPERTY_FILENAME, resourceFile.getAbsolutePath());
                nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(parentFile.getAbsoluteFile());
                nodeRuntime.getNodeModule(NodeModuleProcess.class).setWorkingDirectory(parentFile.getAbsolutePath());
            }
        }
        return this;
    }

    @Override
    default <T, V extends V8Value> T toObject(V v8Value) throws JavetException {
        return getV8Runtime().toObject(v8Value);
    }

    @Override
    @CheckReturnValue
    default <T, V extends V8Value> V toV8Value(T object) throws JavetException {
        return getV8Runtime().toV8Value(object);
    }
}
