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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.proxy.JavetUniversalProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueProxy;

import java.util.List;

/**
 * The type Javet proxy converter.
 *
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetProxyConverter extends JavetObjectConverter {
    /**
     * The constant DUMMY_FUNCTION_STRING.
     *
     * @since 0.9.8
     */
    protected static final String DUMMY_FUNCTION_STRING =
            "(() => {\n" +
                    "  const DummyFunction = function () { };\n" +
                    "  return DummyFunction;\n" +
                    "})();";

    /**
     * Instantiates a new Javet proxy converter.
     *
     * @since 0.9.6
     */
    public JavetProxyConverter() {
        super();
    }

    @Override
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        V8Value v8Value = super.toV8Value(v8Runtime, object, depth);
        if (v8Value != null && !(v8Value.isUndefined())) {
            return (T) v8Value;
        }
        boolean staticMode = config.isStaticClassEnabled() && object instanceof Class;
        V8ValueProxy v8ValueProxy;
        if (staticMode) {
            try (V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(DUMMY_FUNCTION_STRING)) {
                v8ValueProxy = v8Runtime.createV8ValueProxy(v8ValueFunction);
            }
        } else {
            v8ValueProxy = v8Runtime.createV8ValueProxy();
        }
        try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
            JavetUniversalProxyHandler<Object> javetUniversalProxyHandler;
            if (staticMode) {
                javetUniversalProxyHandler = new JavetUniversalProxyHandler<>(v8Runtime, (Class<Object>) object, true);
            } else {
                javetUniversalProxyHandler = new JavetUniversalProxyHandler<>(v8Runtime, object);
            }
            List<JavetCallbackContext> javetCallbackContexts = iV8ValueObjectHandler.bind(javetUniversalProxyHandler);
            iV8ValueObjectHandler.set(PROXY_TARGET, javetCallbackContexts.get(0).getHandle());
        }
        v8Value = v8ValueProxy;
        return (T) v8Runtime.decorateV8Value(v8Value);
    }
}
