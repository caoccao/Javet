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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginList;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.io.IOException;
import java.util.ArrayList;

public class MockDirectProxyListHandler<T> extends ArrayList<T> implements IJavetDirectProxyHandler<IOException> {
    protected int callCount;
    protected V8Runtime v8Runtime;

    public MockDirectProxyListHandler() {
        super();
        callCount = 0;
    }

    public int getCallCount() {
        return callCount;
    }

    @Override
    public IClassProxyPlugin getProxyPlugin() {
        return JavetProxyPluginList.getInstance();
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public V8Value proxyGetOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxyGetOwnPropertyDescriptor(target, property);
    }

    @Override
    public V8ValueBoolean proxyHas(V8Value target, V8Value property) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxyHas(target, property);
    }

    @Override
    public V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxyOwnKeys(target);
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
    }

    @Override
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.symbolToPrimitive(v8Values);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
