/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.node.modules;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

public abstract class BaseNodeModule implements INodeModule {
    protected V8ValueObject moduleObject;
    protected String name;

    public BaseNodeModule(V8ValueObject moduleObject, String name) {
        this.moduleObject = Objects.requireNonNull(moduleObject);
        this.name = name;
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(moduleObject);
        moduleObject = null;
    }

    @Override
    public V8ValueObject getModuleObject() {
        return moduleObject;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isClosed() {
        return moduleObject == null || moduleObject.isClosed();
    }

}
