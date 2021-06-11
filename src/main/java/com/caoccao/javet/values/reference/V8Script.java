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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8Script extends V8ValueReference implements IV8Script {
    protected String resourceName;

    public V8Script(long handle) {
        super(handle);
        resourceName = null;
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        checkV8Runtime();
        return v8Runtime.scriptRun(this, resultRequired);
    }

    public String getResourceName() {
        return resourceName;
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Script;
    }

    public void setResourceName(String resourceName) {
        Objects.requireNonNull(resourceName);
        this.resourceName = resourceName;
    }

    @Override
    public V8Script toClone() throws JavetException {
        return this;
    }
}
