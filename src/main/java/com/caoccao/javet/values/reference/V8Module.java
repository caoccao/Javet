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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type V8 module.
 *
 * @since 0.8.0
 */
@SuppressWarnings("unchecked")
public class V8Module extends V8ValueReference implements IV8Module {

    /**
     * The Resource name.
     *
     * @since 0.8.0
     */
    protected String resourceName;

    /**
     * Instantiates a new V8 module.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    V8Module(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
        resourceName = null;
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T evaluate(boolean resultRequired) throws JavetException {
        return checkV8Runtime().getV8Internal().moduleEvaluate(this, resultRequired);
    }

    @Override
    public byte[] getCachedData() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleGetCachedData(this);
    }

    @Override
    @CheckReturnValue
    public V8ValueError getException() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleGetException(this);
    }

    /**
     * Gets namespace.
     * <p>
     * Note: Please avoid calling this API in production environment
     * because its underlying V8 object is not persisted and core dump will take place.
     *
     * @return the namespace
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    public V8ValueObject getNamespace() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleGetNamespace(this);
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public int getScriptId() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleGetScriptId(this);
    }

    @Override
    public int getStatus() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleGetStatus(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Module;
    }

    @Override
    public boolean instantiate() throws JavetException {
        return checkV8Runtime().getV8Internal().moduleInstantiate(this);
    }

    @Override
    public void setResourceName(String resourceName) {
        this.resourceName = Objects.requireNonNull(resourceName);
    }

    @Override
    @CheckReturnValue
    public V8Module toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public String toString() {
        return resourceName;
    }
}
