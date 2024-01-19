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
import com.caoccao.javet.interfaces.IJavetBiFunction;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MockDirectProxyObjectHandler implements IJavetDirectProxyHandler<IOException> {
    protected int callCount;
    protected Map<String, IJavetUniFunction<String, ? extends V8Value, IOException>> stringGetterMap;
    protected Map<String, IJavetBiFunction<String, V8Value, Boolean, IOException>> stringSetterMap;
    protected V8Runtime v8Runtime;
    protected int x;
    protected int y;

    public MockDirectProxyObjectHandler() {
        callCount = 0;
        stringGetterMap = null;
        stringSetterMap = null;
        x = 0;
        y = 0;
    }

    public int getCallCount() {
        return callCount;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public V8Value increaseX(V8Value... v8Values) throws JavetException {
        x++;
        return v8Runtime.createV8ValueBoolean(true);
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, IOException {
        ++callCount;
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public Map<String, IJavetUniFunction<String, ? extends V8Value, IOException>> proxyGetStringGetterMap() {
        if (stringGetterMap == null) {
            stringGetterMap = new HashMap<>();
            registerStringGetterFunction("increaseX", this::increaseX);
            registerStringGetter("x", (propertyName) -> v8Runtime.createV8ValueInteger(getX()));
            registerStringGetter("y", (propertyName) -> v8Runtime.createV8ValueInteger(getY()));
        }
        return stringGetterMap;
    }

    @Override
    public Map<String, IJavetBiFunction<String, V8Value, Boolean, IOException>> proxyGetStringSetterMap() {
        if (stringSetterMap == null) {
            stringSetterMap = new HashMap<>();
            registerStringSetter("x", (propertyName, propertyValue) -> {
                if (propertyValue instanceof V8ValueInteger) {
                    x = ((V8ValueInteger) propertyValue).toPrimitive();
                    return true;
                }
                return false;
            });
            registerStringSetter("y", (propertyName, propertyValue) -> {
                if (propertyValue instanceof V8ValueInteger) {
                    y = ((V8ValueInteger) propertyValue).toPrimitive();
                    return true;
                }
                return false;
            });
        }
        return stringSetterMap;
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
        return v8Runtime.createV8ValueString(toString());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
