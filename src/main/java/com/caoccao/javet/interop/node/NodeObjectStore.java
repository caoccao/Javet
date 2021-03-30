package com.caoccao.javet.interop.node;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Node object store.
 * It is not thread-safe.
 */
@SuppressWarnings("unchecked")
public class NodeObjectStore implements IJavetClosable {
    protected Map<String, INodeModule> moduleMap;
    protected NodeRuntime nodeRuntime;

    /**
     * Instantiates a new Node object store.
     *
     * @param nodeRuntime the node runtime
     */
    public NodeObjectStore(NodeRuntime nodeRuntime) {
        moduleMap = new HashMap<>();
        this.nodeRuntime = nodeRuntime;
    }

    @Override
    public void close() throws JavetException {
        moduleMap.values().stream().forEach(module -> JavetResourceUtils.safeClose(module));
        moduleMap.clear();
    }

    public <Module extends INodeModule> Module getModule(String name, Class<Module> moduleClass)
            throws JavetException {
        INodeModule nodeModule = null;
        if (moduleMap.containsKey(name)) {
            nodeModule = moduleMap.get(name);
        } else {
            V8ValueObject moduleObject;
            if (INodeModule.MODULE_PROCESS.equals(name)) {
                moduleObject = nodeRuntime.getGlobalObject().get(name);
            } else {
                moduleObject = nodeRuntime.getExecutor("require('" + name + "')").execute();
            }
            try {
                Constructor<Module> constructor = moduleClass.getConstructor(
                        V8ValueObject.class, String.class);
                nodeModule = constructor.newInstance(moduleObject, name);
                moduleMap.put(name, nodeModule);
            } catch (Exception e) {
                nodeRuntime.getLogger().logError(e, "Failed to create node module {0}.", name);
            }
        }
        return (Module) nodeModule;
    }
}
