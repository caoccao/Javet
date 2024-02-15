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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.proxy.IJavetNonProxy;
import com.caoccao.javet.interop.proxy.plugins.*;
import com.caoccao.javet.values.V8Value;

import java.util.Collections;

/**
 * The type Javet bridge converter converts all Java objects to
 * JS objects via JS proxy bi-directionally.
 * <p>
 * The only exception is Java Array is converted to the JS array.
 *
 * @since 1.0.4
 */
@SuppressWarnings("unchecked")
public class JavetBridgeConverter extends JavetProxyConverter {
    /**
     * The constant DEFAULT_PROXY_PLUGINS.
     *
     * @since 3.0.4
     */
    protected static final IClassProxyPlugin[] DEFAULT_PROXY_PLUGINS = new IClassProxyPlugin[]{
            JavetProxyPluginMap.getInstance(),
            JavetProxyPluginSet.getInstance(),
            JavetProxyPluginList.getInstance(),
            JavetProxyPluginArray.getInstance(),
            JavetProxyPluginClass.getInstance(),
            JavetProxyPluginDefault.getInstance(), // The default proxy plugin must be the last one.
    };

    /**
     * Instantiates a new Javet bridge converter.
     *
     * @since 1.0.4
     */
    public JavetBridgeConverter() {
        super();
        // The bridge converter has all built-in proxy plugins enabled by default.
        Collections.addAll(getConfig().getProxyPlugins(), DEFAULT_PROXY_PLUGINS);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        validateDepth(depth);
        V8Value v8Value;
        if (object == null) {
            v8Value = v8Runtime.createV8ValueNull();
        } else if (object.getClass().isPrimitive()) {
            Class<?> objectClass = object.getClass();
            if (objectClass == int.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == boolean.class) {
                v8Value = v8Runtime.createV8ValueBoolean((boolean) object);
            } else if (objectClass == double.class) {
                v8Value = v8Runtime.createV8ValueDouble((double) object);
            } else if (objectClass == float.class) {
                v8Value = v8Runtime.createV8ValueDouble((double) object);
            } else if (objectClass == long.class) {
                v8Value = v8Runtime.createV8ValueLong((long) object);
            } else if (objectClass == short.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == byte.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == char.class) {
                v8Value = v8Runtime.createV8ValueString(Character.toString((char) object));
            } else {
                v8Value = v8Runtime.createV8ValueUndefined();
            }
        } else if (object instanceof V8Value) {
            v8Value = (V8Value) object;
        } else if (object instanceof IJavetNonProxy) {
            v8Value = super.toV8Value(v8Runtime, object, depth);
        } else {
            v8Value = toProxiedV8Value(v8Runtime, object);
        }
        return (T) v8Value;
    }
}
