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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;

public class V8ValueProxy extends V8ValueObject implements IV8ValueProxy {

    V8ValueProxy(long handle) {
        super(handle);
    }

    @Override
    public IV8ValueObject getHandler() throws JavetException {
        checkV8Runtime();
        return v8Runtime.proxyGetHandler(this);
    }

    @Override
    public IV8ValueObject getTarget() throws JavetException {
        checkV8Runtime();
        return v8Runtime.proxyGetTarget(this);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Proxy;
    }

    @Override
    public boolean isRevoked() throws JavetException {
        checkV8Runtime();
        return v8Runtime.proxyIsRevoked(this);
    }

    @Override
    public void revoke() throws JavetException {
        checkV8Runtime();
        v8Runtime.proxyRevoke(this);
    }
}
