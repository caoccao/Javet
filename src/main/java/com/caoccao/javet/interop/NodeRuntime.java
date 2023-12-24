/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

package com.caoccao.javet.interop;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.annotations.NodeModule;
import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.node.modules.INodeModule;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node runtime is a thin wrapper over V8 runtime.
 *
 * @since 0.8.0
 */
@SuppressWarnings("unchecked")
public class NodeRuntime extends V8Runtime {
    /**
     * The constant FUNCTION_REQUIRE.
     *
     * @since 0.8.4
     */
    public static final String FUNCTION_REQUIRE = "require";
    /**
     * The constant PROPERTY_DIRNAME.
     *
     * @since 0.8.4
     */
    public static final String PROPERTY_DIRNAME = "__dirname";
    /**
     * The constant PROPERTY_FILENAME.
     *
     * @since 0.8.4
     */
    public static final String PROPERTY_FILENAME = "__filename";

    /**
     * The Node module map.
     *
     * @since 0.8.1
     */
    Map<String, INodeModule> nodeModuleMap;

    /**
     * Instantiates a new Node runtime.
     *
     * @param v8Host         the V8 host
     * @param handle         the handle
     * @param pooled         the pooled
     * @param v8Native       the V8 native
     * @param runtimeOptions the runtime options
     * @since 0.8.0
     */
    NodeRuntime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native, RuntimeOptions<?> runtimeOptions) {
        super(v8Host, handle, pooled, v8Native, runtimeOptions);
        nodeModuleMap = new HashMap<>();
    }

    @Override
    public byte[] createSnapshot() throws JavetException {
        throw new JavetException(JavetError.RuntimeCreateSnapshotDisabled);
    }

    @Override
    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.Node;
    }

    /**
     * Gets node module.
     *
     * @param <Module>        the type parameter
     * @param nodeModuleClass the node module class
     * @return the node module
     * @throws JavetException the javet exception
     * @since 0.8.1
     */
    @CheckReturnValue
    public <Module extends INodeModule> Module getNodeModule(
            Class<Module> nodeModuleClass) throws JavetException {
        if (!nodeModuleClass.isAnnotationPresent(NodeModule.class)) {
            return null;
        }
        NodeModule nodeModule = nodeModuleClass.getAnnotation(NodeModule.class);
        return getNodeModule(nodeModule.name(), nodeModuleClass);
    }

    /**
     * Gets node module.
     *
     * @param <Module>        the type parameter
     * @param name            the name
     * @param nodeModuleClass the node module class
     * @return the node module
     * @throws JavetException the javet exception
     * @since 0.8.1
     */
    @CheckReturnValue
    public <Module extends INodeModule> Module getNodeModule(
            String name, Class<Module> nodeModuleClass) throws JavetException {
        Objects.requireNonNull(name);
        INodeModule nodeModule = null;
        if (nodeModuleMap.containsKey(name)) {
            nodeModule = nodeModuleMap.get(name);
        } else {
            V8ValueObject moduleObject;
            if (nodeModuleClass == NodeModuleProcess.class) {
                moduleObject = getGlobalObject().get(name);
            } else {
                try (V8ValueFunction requireFunction = getGlobalObject().get(FUNCTION_REQUIRE)) {
                    moduleObject = requireFunction.call(null, name);
                }
            }
            try {
                Constructor<Module> constructor = nodeModuleClass.getConstructor(
                        V8ValueObject.class, String.class);
                nodeModule = constructor.newInstance(moduleObject, name);
                nodeModuleMap.put(name, nodeModule);
            } catch (Exception e) {
                getLogger().logError(e, "Failed to create node module {0}.", name);
            }
        }
        return (Module) nodeModule;
    }

    /**
     * Gets node module count.
     *
     * @return the node module count
     * @since 0.8.1
     */
    public int getNodeModuleCount() {
        return nodeModuleMap.size();
    }

    /**
     * Is purge event loop before close.
     *
     * @return true: purge, false: not purge
     * @since 1.1.4
     */
    public boolean isPurgeEventLoopBeforeClose() {
        return ((INodeNative) v8Native).isPurgeEventLoopBeforeClose(handle);
    }

    @Override
    void removeAllReferences() throws JavetException {
        removeNodeModules();
        super.removeAllReferences();
    }

    /**
     * Remove node module.
     *
     * @param iNodeModule the node module
     * @throws JavetException the javet exception
     * @since 0.8.1
     */
    public void removeNodeModule(INodeModule iNodeModule) throws JavetException {
        Objects.requireNonNull(iNodeModule);
        if (nodeModuleMap.containsKey(iNodeModule.getName())) {
            nodeModuleMap.remove(iNodeModule.getName());
            iNodeModule.close();
        }
    }

    /**
     * Remove node modules.
     *
     * @since 0.8.1
     */
    void removeNodeModules() {
        if (!nodeModuleMap.isEmpty()) {
            JavetResourceUtils.safeClose(nodeModuleMap.values());
            nodeModuleMap.clear();
        }
    }

    /**
     * Sets purge event loop before close.
     *
     * @param purgeEventLoopBeforeClose the purge event loop before close
     * @since 1.1.4
     */
    public void setPurgeEventLoopBeforeClose(boolean purgeEventLoopBeforeClose) {
        ((INodeNative) v8Native).setPurgeEventLoopBeforeClose(handle, purgeEventLoopBeforeClose);
    }
}
