package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8DataModule extends V8Data implements IV8DataModule {
    protected String resourceName;

    public V8DataModule(long handle) {
        super(handle);
        resourceName = null;
    }

    @Override
    public <T extends V8Value> T evaluate(boolean resultRequired) throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleEvaluate(this, resultRequired);
    }

    @Override
    public V8ValueError getException() throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleGetException(this);
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        Objects.requireNonNull(resourceName);
        this.resourceName = resourceName;
    }

    @Override
    public int getScriptId() throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleGetScriptId(this);
    }

    @Override
    public int getStatus() throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleGetStatus(this);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Module;
    }

    @Override
    public boolean instantiate() throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleInstantiate(this);
    }

    @Override
    public V8DataModule toClone() throws JavetException {
        return this;
    }
}
