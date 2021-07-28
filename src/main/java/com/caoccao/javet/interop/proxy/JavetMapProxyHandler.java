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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;

import java.util.Map;

/**
 * The type Javet map proxy handler.
 *
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetMapProxyHandler extends BaseJavetProxyHandler<Map> {
    /**
     * Instantiates a new Javet map proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 0.9.6
     */
    public JavetMapProxyHandler(V8Runtime v8Runtime, Map targetObject) {
        super(v8Runtime, targetObject);
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        return v8Runtime.toV8Value(targetObject.get(property.toString()));
    }

    @V8Function
    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        return v8Runtime.createV8ValueBoolean(targetObject.containsKey(property.toString()));
    }

    @V8Function
    @Override
    public V8Value ownKeys(V8Value target) throws JavetException {
        return v8Runtime.toV8Value(targetObject.keySet().toArray());
    }

    @V8Function
    @Override
    public V8ValueBoolean set(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException {
        targetObject.put(propertyKey.toString(), v8Runtime.toObject(propertyValue));
        return v8Runtime.createV8ValueBoolean(true);
    }
}
