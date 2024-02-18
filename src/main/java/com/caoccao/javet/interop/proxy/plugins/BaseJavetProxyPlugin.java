/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy.plugins;

import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.V8ErrorTemplate;
import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;

/**
 * The type Base javet proxy plugin.
 *
 * @since 3.0.4
 */
public abstract class BaseJavetProxyPlugin implements IClassProxyPlugin {
    /**
     * The constant HINT_BOOLEAN.
     *
     * @since 3.0.4
     */
    protected static final String HINT_BOOLEAN = "boolean";
    /**
     * The constant HINT_DEFAULT.
     *
     * @since 3.0.4
     */
    protected static final String HINT_DEFAULT = "default";
    /**
     * The constant HINT_NUMBER.
     *
     * @since 3.0.4
     */
    protected static final String HINT_NUMBER = "number";
    /**
     * The constant HINT_STRING.
     *
     * @since 3.0.4
     */
    protected static final String HINT_STRING = "string";
    /**
     * The constant OBJECT_CONVERTER.
     *
     * @since 3.0.4
     */
    protected static final JavetObjectConverter OBJECT_CONVERTER = new JavetObjectConverter();
    /**
     * The constant PROXY_CONVERTER.
     *
     * @since 3.0.4
     */
    protected static final JavetProxyConverter PROXY_CONVERTER = new JavetProxyConverter();
    /**
     * The constant TO_JSON.
     *
     * @since 3.0.4
     */
    protected static final String TO_JSON = "toJSON";
    /**
     * The constant TO_STRING.
     *
     * @since 3.0.4
     */
    protected static final String TO_STRING = "toString";
    /**
     * The constant VALUE_OF.
     *
     * @since 3.0.4
     */
    protected static final String VALUE_OF = "valueOf";

    /**
     * Instantiates a new Base javet proxy plugin.
     *
     * @since 3.0.4
     */
    public BaseJavetProxyPlugin() {
    }

    /**
     * Call function with the object converter by name.
     *
     * @param functionName the function name
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected V8Value callWithObjectConverter(
            String functionName, V8Runtime v8Runtime, Object targetObject)
            throws JavetException {
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                functionName, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(functionName).execute()) {
                        return v8ValueFunction.call(OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject), v8Values);
                    }
                }));
    }

    @Override
    public boolean deleteByObject(Object targetObject, Object propertyKey) {
        return false;
    }

    @Override
    public Object getByIndex(Object targetObject, int index) {
        return null;
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetBySymbol(
            Class<?> targetClass, String symbolName) {
        if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(symbolName)) {
            return this::symbolToPrimitive;
        }
        return null;
    }

    @Override
    public Object[] getProxyOwnKeys(Object targetObject) {
        return new Object[0];
    }

    @Override
    public <T> IJavetEntityPropertyDescriptor<T> getProxyOwnPropertyDescriptor(Object targetObject, Object propertyName) {
        return null;
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxySymbolToPrimitive() {
        return this::symbolToPrimitive;
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getTargetObjectConstructor(Class<?> targetClass) {
        return (v8Runtime, targetObject) -> null;
    }

    @Override
    public boolean hasByObject(Object targetObject, Object propertyKey) {
        return false;
    }

    @Override
    public boolean isDeleteSupported(Class<?> targetClass) {
        return false;
    }

    @Override
    public boolean isHasSupported(Class<?> targetClass) {
        return false;
    }

    @Override
    public boolean isIndexSupported(Class<?> targetClass) {
        return false;
    }

    @Override
    public boolean isOwnKeysSupported(Class<?> targetClass) {
        return false;
    }

    @Override
    public boolean isUniqueKeySupported(Class<?> targetClass) {
        return false;
    }

    @Override
    public void populateUniqueKeys(Set<String> uniqueKeySet, Object targetObject) {
    }

    @Override
    public boolean setByIndex(Object targetObject, int index, Object value) {
        return false;
    }

    /**
     * Convert to primitive.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value symbolToPrimitive(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (targetObject != null) {
                        String hintString = V8ValueUtils.asString(v8Values, 0, null);
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
                            if (targetObject instanceof BigInteger) {
                                return v8Runtime.createV8ValueBigInteger((BigInteger) targetObject);
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
                                double value = (double) targetObject;
                                return v8Runtime.createV8ValueBoolean(
                                        value != 0F && !Double.isNaN(value) && Double.isFinite(value));
                            }
                            if (targetObject instanceof Long) {
                                return v8Runtime.createV8ValueBoolean(((Long) targetObject) != 0);
                            }
                            if (targetObject instanceof Float) {
                                float value = (float) targetObject;
                                return v8Runtime.createV8ValueBoolean(
                                        value != 0F && !Float.isNaN(value) && Float.isFinite(value));
                            }
                            if (targetObject instanceof Short) {
                                return v8Runtime.createV8ValueBoolean(((Short) targetObject) != 0);
                            }
                            if (targetObject instanceof String) {
                                return v8Runtime.createV8ValueBoolean(StringUtils.isNotEmpty((String) targetObject));
                            }
                            if (targetObject instanceof Character) {
                                return v8Runtime.createV8ValueBoolean(true);
                            }
                            if (targetObject instanceof BigInteger) {
                                return v8Runtime.createV8ValueBoolean(!BigInteger.ZERO.equals(targetObject));
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
                            if (targetObject instanceof BigInteger) {
                                return v8Runtime.createV8ValueBigInteger((BigInteger) targetObject);
                            }
                            return v8Runtime.createV8ValueString(targetObject.toString());
                        }
                    }
                    return OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject);
                }));
    }

    /**
     * TypeError: ${functionName}() is not supported.
     *
     * @param functionName the function name
     * @param v8Runtime    the V8 runtime
     * @return the V8 value
     * @since 3.0.4
     */
    protected V8Value typeErrorFunctionIsNotSupported(String functionName, V8Runtime v8Runtime) {
        String message = V8ErrorTemplate.typeErrorFunctionIsNotSupported(functionName);
        Objects.requireNonNull(v8Runtime).throwError(V8ValueErrorType.TypeError, message);
        return v8Runtime.createV8ValueUndefined();
    }
}
