package com.caoccao.javet.interop.node;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;

public abstract class BaseNodeModule implements INodeModule {
    protected V8ValueObject moduleObject;
    protected String name;

    BaseNodeModule(V8ValueObject moduleObject, String name) {
        this.moduleObject = moduleObject;
        this.name = name;
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(moduleObject);
    }

    @Override
    public V8ValueObject getModuleObject() {
        return moduleObject;
    }

    public String getName() {
        return name;
    }

}
