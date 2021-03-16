package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8Script extends V8ValueReference implements IV8Script {
    protected String resourceName;

    public V8Script(long handle) {
        super(handle);
        resourceName = null;
    }

    @Override
    public <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        checkV8Runtime();
        return v8Runtime.scriptRun(this, resultRequired);
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        Objects.requireNonNull(resourceName);
        this.resourceName = resourceName;
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Script;
    }

    @Override
    public V8Script toClone() throws JavetException {
        return this;
    }
}
