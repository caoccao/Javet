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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8Module extends V8ValueReference implements IV8Module {
    protected String resourceName;

    public V8Module(long handle) {
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

    /**
     * Gets namespace.
     * <p>
     * Note: Please avoid calling this API in production environment
     * because its underlying V8 object is not persisted and core dump will take place.
     *
     * @return the namespace
     * @throws JavetException the javet exception
     */
    public V8ValueObject getNamespace() throws JavetException {
        checkV8Runtime();
        return v8Runtime.moduleGetNamespace(this);
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
    public V8Module toClone() throws JavetException {
        return this;
    }

    @Override
    public String toString() {
        return resourceName;
    }
}
