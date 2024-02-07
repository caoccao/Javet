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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;

/**
 * The type Javet proxy symbol to primitive converter.
 *
 * @param <T> the type parameter
 * @since 1.0.4
 */
public class JavetProxySymbolToPrimitiveConverter<T> extends BaseJavetProxySymbolConverter<T> {
    /**
     * The constant HINT_BOOLEAN.
     *
     * @since 2.0.0
     */
    protected static final String HINT_BOOLEAN = "boolean";
    /**
     * The constant HINT_DEFAULT.
     *
     * @since 2.0.0
     */
    protected static final String HINT_DEFAULT = "default";
    /**
     * The constant HINT_NUMBER.
     *
     * @since 2.0.0
     */
    protected static final String HINT_NUMBER = "number";
    /**
     * The constant HINT_STRING.
     *
     * @since 2.0.0
     */
    protected static final String HINT_STRING = "string";

    /**
     * Instantiates a new Javet proxy symbol to primitive converter.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 1.0.4
     */
    public JavetProxySymbolToPrimitiveConverter(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
    }

    @CheckReturnValue
    @Override
    public V8Value toV8Value(V8Value... v8Values) throws JavetException {
        if (targetObject != null && ArrayUtils.isNotEmpty(v8Values)) {
            String hintString = V8ValueUtils.asString(v8Values, 0);
            if (HINT_NUMBER.equals(hintString)) {
                if (targetObject instanceof Integer) {
                    return v8Runtime.createV8ValueInteger((Integer) targetObject);
                }
                if (targetObject instanceof Double) {
                    return v8Runtime.createV8ValueDouble((Double) targetObject);
                }
                if (targetObject instanceof Long) {
                    return v8Runtime.createV8ValueInteger(((Long) targetObject).intValue());
                }
                if (targetObject instanceof Float) {
                    return v8Runtime.createV8ValueDouble(((Float) targetObject).doubleValue());
                }
                if (targetObject instanceof Short) {
                    return v8Runtime.createV8ValueInteger(((Short) targetObject).intValue());
                }
                if (targetObject instanceof Boolean) {
                    return v8Runtime.createV8ValueInteger(((Boolean) targetObject) ? 1 : 0);
                }
                return v8Runtime.createV8ValueInteger(0);
            } else if (HINT_STRING.equals(hintString)) {
                return v8Runtime.createV8ValueString(targetObject.toString());
            } else if (HINT_BOOLEAN.equals(hintString)) {
                if (targetObject instanceof Boolean) {
                    return v8Runtime.createV8ValueBoolean((Boolean) targetObject);
                }
                if (targetObject instanceof Integer) {
                    return v8Runtime.createV8ValueBoolean(((Integer) targetObject) != 0);
                }
                if (targetObject instanceof Double) {
                    return v8Runtime.createV8ValueBoolean(((Double) targetObject) != 0);
                }
                if (targetObject instanceof Long) {
                    return v8Runtime.createV8ValueBoolean(((Long) targetObject) != 0);
                }
                if (targetObject instanceof Float) {
                    return v8Runtime.createV8ValueBoolean(((Float) targetObject) != 0);
                }
                if (targetObject instanceof Short) {
                    return v8Runtime.createV8ValueBoolean(((Short) targetObject) != 0);
                }
                if (targetObject instanceof String) {
                    return v8Runtime.createV8ValueBoolean(StringUtils.isNotEmpty((String) targetObject));
                }
                return v8Runtime.createV8ValueBoolean(false);
            } else if (HINT_DEFAULT.equals(hintString)) {
                if (targetObject instanceof Integer) {
                    return v8Runtime.createV8ValueInteger((Integer) targetObject);
                }
                if (targetObject instanceof Double) {
                    return v8Runtime.createV8ValueDouble((Double) targetObject);
                }
                if (targetObject instanceof Long) {
                    return v8Runtime.createV8ValueLong((Long) targetObject);
                }
                if (targetObject instanceof Float) {
                    return v8Runtime.createV8ValueDouble(((Float) targetObject).doubleValue());
                }
                if (targetObject instanceof Short) {
                    return v8Runtime.createV8ValueInteger(((Short) targetObject).intValue());
                }
                if (targetObject instanceof Boolean) {
                    return v8Runtime.createV8ValueBoolean((Boolean) targetObject);
                }
                return v8Runtime.createV8ValueString(targetObject.toString());
            }
        }
        return super.toV8Value(v8Values);
    }
}
