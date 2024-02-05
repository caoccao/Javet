/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy.polyfill;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The type Javet proxy polyfill object.
 *
 * @since 3.0.4
 */
public final class JavetProxyPolyfillObject {
    private static final String ARRAY = "Array";
    private static final String CONSTRUCTOR = "constructor";
    private static final String OBJECT = "Object";
    private static final String SET = "Set";
    private static final Map<String, IJavetProxyPolyfillFunction<?, ?>> functionMap;

    static {
        functionMap = new HashMap<>();
        functionMap.put(CONSTRUCTOR, JavetProxyPolyfillObject::constructor);
    }

    private JavetProxyPolyfillObject() {
    }

    /**
     * Polyfill constructor.
     * The constructor method is a special method of a class for creating
     * and initializing an object instance of that class.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value constructor(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        if (targetObject instanceof List || targetObject.getClass().isArray()) {
            return v8Runtime.getGlobalObject().get(ARRAY);
        } else if (targetObject instanceof Set) {
            return v8Runtime.getGlobalObject().get(SET);
        }
        return v8Runtime.getGlobalObject().get(OBJECT);
    }

    /**
     * Gets function.
     *
     * @param name the name
     * @return the function
     * @since 3.0.4
     */
    public static IJavetProxyPolyfillFunction<?, ?> getFunction(String name) {
        return functionMap.get(name);
    }
}
