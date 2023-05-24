/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.io.IOException;

public class MockDirectProxyObjectHandler implements IJavetDirectProxyHandler<IOException> {
    protected int callCount;
    protected V8Runtime v8Runtime;
    protected int x;
    protected int y;

    public MockDirectProxyObjectHandler() {
        callCount = 0;
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
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).toPrimitive();
            if ("x".equals(propertyString)) {
                return v8Runtime.createV8ValueInteger(getX());
            }
            if ("y".equals(propertyString)) {
                return v8Runtime.createV8ValueInteger(getY());
            }
            if ("increaseX".equals(propertyString)) {
                return v8Runtime.createV8ValueFunction(
                        new JavetCallbackContext(
                                "increaseX",
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::increaseX));
            }
        }
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public V8ValueBoolean proxyHas(V8Value target, V8Value property) throws JavetException, IOException {
        ++callCount;
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).toPrimitive();
            if ("x".equals(propertyString) || "y".equals(propertyString)) {
                return v8Runtime.createV8ValueBoolean(true);
            }
        }
        return IJavetDirectProxyHandler.super.proxyHas(target, property);
    }

    @Override
    public V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, IOException {
        ++callCount;
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        v8ValueArray.push(v8Runtime.createV8ValueString("x"), v8Runtime.createV8ValueString("y"));
        return v8ValueArray;
    }

    @Override
    public V8ValueBoolean proxySet(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException, IOException {
        ++callCount;
        if (propertyKey instanceof V8ValueString && propertyValue instanceof V8ValueInteger) {
            String propertyString = ((V8ValueString) propertyKey).toPrimitive();
            int propertyInteger = ((V8ValueInteger) propertyValue).toPrimitive();
            if ("x".equals(propertyString)) {
                x = propertyInteger;
                return v8Runtime.createV8ValueBoolean(true);
            }
            if ("y".equals(propertyString)) {
                y = propertyInteger;
                return v8Runtime.createV8ValueBoolean(true);
            }
        }
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
