/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

/**
 * The type Javet bridge converter.
 *
 * @since 1.0.4
 */
@SuppressWarnings("unchecked")
public class JavetBridgeConverter extends BaseJavetConverter {
    /**
     * The constant PROXY_CONVERTER.
     *
     * @since 1.0.4
     */
    protected static final JavetProxyConverter PROXY_CONVERTER = new JavetProxyConverter();

    /**
     * Instantiates a new Javet bridge converter.
     *
     * @since 1.0.4
     */
    public JavetBridgeConverter() {
        super();
    }

    @Override
    public Object toObject(V8Value v8Value, int depth) throws JavetException {
        return PROXY_CONVERTER.toObject(v8Value, depth);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        PROXY_CONVERTER.validateDepth(depth);
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
        } else if (object.getClass().isArray()) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                if (object instanceof boolean[]) {
                    for (boolean item : (boolean[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof byte[]) {
                    for (byte item : (byte[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof char[]) {
                    for (char item : (char[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof double[]) {
                    for (double item : (double[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof float[]) {
                    for (float item : (float[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof int[]) {
                    for (int item : (int[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof long[]) {
                    for (long item : (long[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else if (object instanceof short[]) {
                    for (short item : (short[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                } else {
                    for (Object item : (Object[]) object) {
                        try (V8Value v8ValueItem = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(v8ValueItem);
                        }
                    }
                }
                v8Value = v8ValueArray;
                v8Scope.setEscapable();
            }
        } else if (object instanceof V8Value) {
            v8Value = (V8Value) object;
        } else {
            v8Value = PROXY_CONVERTER.toProxiedV8Value(v8Runtime, object);
        }
        return (T) v8Runtime.decorateV8Value(v8Value);
    }
}
