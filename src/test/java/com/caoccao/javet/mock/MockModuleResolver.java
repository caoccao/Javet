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

package com.caoccao.javet.mock;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.IV8ModuleResolver;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.IV8Module;

import java.util.Objects;

public class MockModuleResolver implements IV8ModuleResolver {
    protected boolean called;
    protected String resourceName;
    protected String sourceCode;

    public MockModuleResolver(String resourceName, String sourceCode) {
        setCalled(false);
        this.resourceName = Objects.requireNonNull(resourceName);
        this.sourceCode = Objects.requireNonNull(sourceCode);
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public boolean isCalled() {
        return called;
    }

    @Override
    public IV8Module resolve(V8Runtime v8Runtime, String resourceName, IV8Module v8ModuleReferrer) throws JavetException {
        setCalled(true);
        if (this.resourceName.equals(resourceName)) {
            return v8Runtime.getExecutor(sourceCode).setResourceName(resourceName).compileV8Module();
        }
        return null;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }
}
