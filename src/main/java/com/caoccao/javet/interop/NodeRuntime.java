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
import com.caoccao.javet.interop.node.INodeModule;
import com.caoccao.javet.interop.node.NodeModuleAnnotation;
import com.caoccao.javet.interop.node.NodeModuleProcess;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node runtime is a thin wrapper over V8 runtime.
 */
@SuppressWarnings("unchecked")
public class NodeRuntime extends V8Runtime {
    protected Map<String, INodeModule> nodeModuleMap;

    /**
     * Instantiates a new Node runtime.
     *
     * @param v8Host the V8 host
     * @param handle the handle
     * @param pooled the pooled
     */
    NodeRuntime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native) {
        super(v8Host, handle, pooled, v8Native, null);
        nodeModuleMap = new HashMap<>();
    }

    @Override
    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.Node;
    }

    public <NodeModule extends INodeModule> NodeModule getNodeModule(
            Class<NodeModule> nodeModuleClass) throws JavetException {
        if (!nodeModuleClass.isAnnotationPresent(NodeModuleAnnotation.class)) {
            return null;
        }
        NodeModuleAnnotation nodeModuleAnnotation = nodeModuleClass.getAnnotation(NodeModuleAnnotation.class);
        return getNodeModule(nodeModuleAnnotation.name(), nodeModuleClass);
    }

    public <NodeModule extends INodeModule> NodeModule getNodeModule(
            String name, Class<NodeModule> nodeModuleClass) throws JavetException {
        Objects.requireNonNull(name);
        INodeModule nodeModule = null;
        if (nodeModuleMap.containsKey(name)) {
            nodeModule = nodeModuleMap.get(name);
        } else {
            V8ValueObject moduleObject;
            if (nodeModuleClass == NodeModuleProcess.class) {
                moduleObject = getGlobalObject().get(name);
            } else {
                moduleObject = getExecutor("require('" + name + "')").execute();
            }
            try {
                Constructor<NodeModule> constructor = nodeModuleClass.getConstructor(
                        V8ValueObject.class, String.class);
                nodeModule = constructor.newInstance(moduleObject, name);
                nodeModuleMap.put(name, nodeModule);
            } catch (Exception e) {
                getLogger().logError(e, "Failed to create node module {0}.", name);
            }
        }
        return (NodeModule) nodeModule;
    }

    public int getNodeModuleCount() {
        return nodeModuleMap.size();
    }

    public void removeNodeModule(INodeModule iNodeModule) throws JavetException {
        Objects.requireNonNull(iNodeModule);
        if (nodeModuleMap.containsKey(iNodeModule.getName())) {
            nodeModuleMap.remove(iNodeModule.getName());
            iNodeModule.close();
        }
    }

    protected void removeNodeModules() {
        if (!nodeModuleMap.isEmpty()) {
            nodeModuleMap.values().stream().forEach(nodeModule -> JavetResourceUtils.safeClose(nodeModule));
            nodeModuleMap.clear();
        }
    }

    @Override
    protected void removeReferences() throws JavetException {
        removeNodeModules();
        super.removeReferences();
    }
}
