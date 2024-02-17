/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@V8Convert(proxyMode = V8ProxyMode.Function)
public class MockDirectProxyFunctionHandler extends MockDirectProxyObjectHandler {
    @Override
    public V8Value proxyApply(V8Value target, V8Value thisObject, V8ValueArray arguments)
            throws JavetException, IOException {
        ++callCount;
        AtomicInteger atomicInteger = new AtomicInteger();
        arguments.forEach(v8Value -> {
            if (v8Value instanceof V8ValueInteger) {
                atomicInteger.addAndGet(((V8ValueInteger) v8Value).toPrimitive());
            }
        });
        return v8Runtime.createV8ValueInteger(atomicInteger.get());
    }
}
