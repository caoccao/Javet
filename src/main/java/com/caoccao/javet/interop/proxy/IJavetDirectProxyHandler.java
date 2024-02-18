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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.entities.JavetEntityPropertyDescriptor;
import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiFunction;
import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;
import com.caoccao.javet.interfaces.IJavetEntitySymbol;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.util.Map;
import java.util.Optional;

/**
 * The interface Javet direct proxy handler.
 *
 * @param <E> the type parameter
 * @since 2.2.0
 */
public interface IJavetDirectProxyHandler<E extends Exception> {
    /**
     * Create target object for the proxy target.
     *
     * @return the V8 value
     * @since 3.0.4
     */
    default V8Value createTargetObject() {
        return Optional.ofNullable(getProxyPlugin())
                .map(p -> p.getTargetObjectConstructor(getClass()))
                .map(f -> {
                    try {
                        return f.invoke(getV8Runtime(), this);
                    } catch (Throwable ignored) {
                    }
                    return null;
                })
                .orElse(null);
    }

    /**
     * Gets proxy plugin.
     *
     * @return the proxy plugin
     * @since 3.0.4
     */
    default IClassProxyPlugin getProxyPlugin() {
        return null;
    }

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 2.2.0
     */
    V8Runtime getV8Runtime();

    /**
     * Proxy handler.apply().
     * The handler.apply() method is a trap for the [[Call]] object internal method,
     * which is used by operations such as function calls.
     *
     * @param target     the target
     * @param thisObject this object
     * @param arguments  the arguments
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value proxyApply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException, E {
        return null;
    }

    /**
     * Proxy handler.deleteProperty().
     * The handler.deleteProperty() method is a trap for the [[Delete]] object internal method,
     * which is used by operations such as the delete operator.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 3.0.4
     */
    default V8ValueBoolean proxyDeleteProperty(V8Value target, V8Value property) throws JavetException, E {
        boolean deleted = false;
        IClassProxyPlugin classProxyPlugin = getProxyPlugin();
        if (classProxyPlugin != null) {
            if (classProxyPlugin.isDeleteSupported(getClass())) {
                deleted = classProxyPlugin.deleteByObject(this, getV8Runtime().toObject(property));
            }
        }
        if (deleted) {
            return getV8Runtime().createV8ValueBoolean(true);
        }
        return null;
    }

    /**
     * Proxy handler.get().
     * The handler.get() method is a trap for the [[Get]] object internal method,
     * which is used by operations such as property accessors.
     *
     * @param target   the target
     * @param property the property
     * @param receiver the receiver
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, E {
        V8Value v8Value = null;
        IClassProxyPlugin classProxyPlugin = getProxyPlugin();
        if (classProxyPlugin != null) {
            if (classProxyPlugin.isIndexSupported(getClass()) && property instanceof V8ValueString) {
                String propertyString = ((V8ValueString) property).getValue();
                if (StringUtils.isDigital(propertyString)) {
                    final int index = Integer.parseInt(propertyString);
                    if (index >= 0) {
                        Object result = classProxyPlugin.getByIndex(this, index);
                        if (result != null) {
                            v8Value = getV8Runtime().toV8Value(result);
                        }
                    }
                }
            }
            if (v8Value == null) {
                IClassProxyPluginFunction<E> classProxyPluginFunction = null;
                if (property instanceof V8ValueString) {
                    String propertyName = ((V8ValueString) property).getValue();
                    classProxyPluginFunction = classProxyPlugin.getProxyGetByString(getClass(), propertyName);
                } else if (property instanceof V8ValueSymbol) {
                    V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
                    String description = propertySymbol.getDescription();
                    classProxyPluginFunction = classProxyPlugin.getProxyGetBySymbol(getClass(), description);
                }
                if (classProxyPluginFunction != null) {
                    v8Value = classProxyPluginFunction.invoke(getV8Runtime(), this);
                }
            }
        }
        if (v8Value == null && property instanceof V8ValueString) {
            final String propertyString = ((V8ValueString) property).getValue();
            Optional<IJavetUniFunction<String, ? extends V8Value, E>> optionalGetter =
                    Optional.ofNullable(proxyGetStringGetterMap()).map(m -> m.get(propertyString));
            if (optionalGetter.isPresent()) {
                v8Value = optionalGetter.get().apply(propertyString);
            } else if (IJavetProxyHandler.FUNCTION_NAME_TO_JSON.equals(propertyString)) {
                v8Value = getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                IJavetProxyHandler.FUNCTION_NAME_TO_JSON,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::toJSON));
            } else if (IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE.equals(propertyString)) {
                v8Value = getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            }
        } else if (v8Value == null && property instanceof V8ValueSymbol) {
            final V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            final String description = propertySymbol.getDescription();
            Optional<IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> optionalGetter =
                    Optional.ofNullable(proxyGetSymbolGetterMap()).map(m -> m.get(description));
            if (optionalGetter.isPresent()) {
                v8Value = optionalGetter.get().apply(propertySymbol);
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                v8Value = getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)) {
                v8Value = getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolIterator));
            }
        }
        return v8Value;
    }

    /**
     * Proxy handler.getOwnPropertyDescriptor().
     * The handler.getOwnPropertyDescriptor() method is a trap for the [[GetOwnProperty]] object internal method,
     * which is used by operations such as Object.getOwnPropertyDescriptor().
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     */
    default V8Value proxyGetOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException, E {
        V8Value v8Value = null;
        IJavetEntityPropertyDescriptor<V8Value> javetEntityPropertyDescriptor = null;
        try {
            if (property instanceof V8ValueString) {
                final String propertyString = ((V8ValueString) property).getValue();
                Optional<IJavetUniFunction<String, ? extends V8Value, E>> optionalGetter =
                        Optional.ofNullable(proxyGetStringGetterMap()).map(m -> m.get(propertyString));
                if (optionalGetter.isPresent()) {
                    v8Value = optionalGetter.get().apply(propertyString);
                }
                if (v8Value == null) {
                    IClassProxyPlugin classProxyPlugin = getProxyPlugin();
                    if (classProxyPlugin != null) {
                        javetEntityPropertyDescriptor =
                                classProxyPlugin.getProxyOwnPropertyDescriptor(this, propertyString);
                        javetEntityPropertyDescriptor.setValue(getV8Runtime().createV8ValueUndefined());
                    }
                } else {
                    javetEntityPropertyDescriptor =
                            new JavetEntityPropertyDescriptor<>(true, true, true, v8Value);
                }
            } else if (property instanceof V8ValueSymbol) {
                final V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
                final String description = propertySymbol.getDescription();
                Optional<IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> optionalGetter =
                        Optional.ofNullable(proxyGetSymbolGetterMap()).map(m -> m.get(description));
                if (optionalGetter.isPresent()) {
                    v8Value = optionalGetter.get().apply(propertySymbol);
                }
                if (v8Value != null) {
                    javetEntityPropertyDescriptor =
                            new JavetEntityPropertyDescriptor<>(true, true, true, v8Value);
                }
            }
            if (javetEntityPropertyDescriptor != null) {
                return getV8Runtime().toV8Value(javetEntityPropertyDescriptor);
            }
        } finally {
            JavetResourceUtils.safeClose(v8Value);
        }
        return null;
    }

    /**
     * Proxy get string getter map.
     *
     * @return the map
     * @since 2.2.0
     */
    default Map<String, IJavetUniFunction<String, ? extends V8Value, E>> proxyGetStringGetterMap() {
        return null;
    }

    /**
     * Proxy get string setter map.
     *
     * @return the map
     * @since 2.2.0
     */
    default Map<String, IJavetBiFunction<String, V8Value, Boolean, E>> proxyGetStringSetterMap() {
        return null;
    }

    /**
     * Proxy get symbol getter map.
     *
     * @return the map
     * @since 2.2.0
     */
    default Map<String, IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> proxyGetSymbolGetterMap() {
        return null;
    }

    /**
     * Proxy get symbol setter map.
     *
     * @return the map
     * @since 2.2.0
     */
    default Map<String, IJavetBiFunction<V8ValueSymbol, V8Value, Boolean, E>> proxyGetSymbolSetterMap() {
        return null;
    }

    /**
     * Proxy handler.has().
     * The handler.has() method is a trap for the [[HasProperty]] object internal method,
     * which is used by operations such as the in operator.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean proxyHas(V8Value target, V8Value property) throws JavetException, E {
        boolean hasProperty = false;
        IClassProxyPlugin classProxyPlugin = getProxyPlugin();
        if (classProxyPlugin != null && classProxyPlugin.isHasSupported(getClass())) {
            hasProperty = classProxyPlugin.hasByObject(this, getV8Runtime().toObject(property));
        }
        if (!hasProperty && property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).toPrimitive();
            Map<String, IJavetUniFunction<String, ? extends V8Value, E>> stringGetterMap = proxyGetStringGetterMap();
            if (stringGetterMap != null && !stringGetterMap.isEmpty()) {
                hasProperty = stringGetterMap.containsKey(propertyString);
            }
        } else if (!hasProperty && property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            Map<String, IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> symbolGetterMap = proxyGetSymbolGetterMap();
            if (symbolGetterMap != null && !symbolGetterMap.isEmpty()) {
                hasProperty = symbolGetterMap.containsKey(description);
            }
        }
        if (hasProperty) {
            return getV8Runtime().createV8ValueBoolean(true);
        }
        return null;
    }

    /**
     * Proxy handler.ownKeys().
     * The handler.ownKeys() method is a trap for the [[OwnPropertyKeys]] object internal method,
     * which is used by operations such as Object.keys(), Reflect.ownKeys(), etc.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, E {
        Object[] keys = null;
        Map<String, IJavetUniFunction<String, ? extends V8Value, E>> stringGetterMap = proxyGetStringGetterMap();
        if (stringGetterMap != null && !stringGetterMap.isEmpty()) {
            keys = new Object[stringGetterMap.size()];
            int index = 0;
            for (String key : stringGetterMap.keySet()) {
                keys[index++] = getV8Runtime().createV8ValueString(key);
            }
        }
        if (keys == null) {
            IClassProxyPlugin classProxyPlugin = getProxyPlugin();
            if (classProxyPlugin.isOwnKeysSupported(getClass())) {
                keys = classProxyPlugin.getProxyOwnKeys(this);
                for (int i = 0; i < keys.length; i++) {
                    Object key = keys[i];
                    if (key instanceof String) {
                        keys[i] = getV8Runtime().createV8ValueString((String) key);
                    } else if (key instanceof IJavetEntitySymbol) {
                        keys[i] = getV8Runtime().createV8ValueSymbol(((IJavetEntitySymbol) key).getDescription());
                    } else {
                        keys[i] = getV8Runtime().createV8ValueString(String.valueOf(key));
                    }
                }
            }
        }
        if (keys != null) {
            return V8ValueUtils.createV8ValueArray(getV8Runtime(), keys);
        }
        return null;
    }

    /**
     * Proxy handler.set().
     * The handler.set() method is a trap for the [[Set]] object internal method,
     * which is used by operations such as using property accessors to set a property's value.
     *
     * @param target        the target
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @param receiver      the receiver
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean proxySet(
            V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException, E {
        boolean isSet = false;
        IClassProxyPlugin classProxyPlugin = getProxyPlugin();
        if (classProxyPlugin != null
                && classProxyPlugin.isIndexSupported(getClass())
                && propertyKey instanceof V8ValueString) {
            String propertyKeyString = ((V8ValueString) propertyKey).getValue();
            if (StringUtils.isDigital(propertyKeyString)) {
                final int index = Integer.parseInt(propertyKeyString);
                if (index >= 0) {
                    isSet = classProxyPlugin.setByIndex(this, index, getV8Runtime().toObject(propertyValue));
                }
            }
        }
        if (!isSet && propertyKey instanceof V8ValueString) {
            String propertyKeyString = ((V8ValueString) propertyKey).toPrimitive();
            Map<String, IJavetBiFunction<String, V8Value, Boolean, E>> stringSetterMap = proxyGetStringSetterMap();
            if (stringSetterMap != null && !stringSetterMap.isEmpty()) {
                IJavetBiFunction<String, V8Value, Boolean, E> setter = stringSetterMap.get(propertyKeyString);
                if (setter != null) {
                    isSet = setter.apply(propertyKeyString, propertyValue);
                }
            }
        } else if (!isSet && propertyKey instanceof V8ValueSymbol) {
            V8ValueSymbol propertyKeySymbol = (V8ValueSymbol) propertyKey;
            String description = propertyKeySymbol.getDescription();
            Map<String, IJavetBiFunction<V8ValueSymbol, V8Value, Boolean, E>> symbolSetterMap = proxyGetSymbolSetterMap();
            if (symbolSetterMap != null && !symbolSetterMap.isEmpty()) {
                IJavetBiFunction<V8ValueSymbol, V8Value, Boolean, E> setter = symbolSetterMap.get(description);
                if (setter != null) {
                    isSet = setter.apply(propertyKeySymbol, propertyValue);
                }
            }
        }
        if (isSet) {
            return getV8Runtime().createV8ValueBoolean(true);
        }
        return null;
    }

    /**
     * Register string getter.
     *
     * @param propertyName the property name
     * @param getter       the getter
     * @since 2.2.1
     */
    default void registerStringGetter(
            String propertyName,
            IJavetUniFunction<String, ? extends V8Value, E> getter) {
        proxyGetStringGetterMap().put(propertyName, getter);
    }

    /**
     * Register string getter function.
     *
     * @param propertyName the property name
     * @param getter       the getter
     * @since 2.2.1
     */
    default void registerStringGetterFunction(
            String propertyName,
            IJavetDirectCallable.NoThisAndResult<?> getter) {
        proxyGetStringGetterMap().put(
                propertyName,
                innerPropertyName -> getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                innerPropertyName,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                getter)));
    }

    /**
     * Register string setter.
     *
     * @param propertyName the property name
     * @param setter       the setter
     * @since 2.2.1
     */
    default void registerStringSetter(
            String propertyName,
            IJavetBiFunction<String, V8Value, Boolean, E> setter) {
        proxyGetStringSetterMap().put(propertyName, setter);
    }

    /**
     * Register symbol getter function.
     *
     * @param propertyName the property name
     * @param getter       the getter
     * @since 2.2.1
     */
    default void registerSymbolGetterFunction(
            String propertyName,
            IJavetDirectCallable.NoThisAndResult<?> getter) {
        proxyGetSymbolGetterMap().put(
                propertyName,
                propertySymbol -> getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                propertySymbol.getDescription(),
                                JavetCallbackType.DirectCallNoThisAndResult,
                                getter)));
    }

    /**
     * Sets V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     * @since 2.2.0
     */
    default void setV8Runtime(V8Runtime v8Runtime) {
    }

    /**
     * Symbol iterator.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value symbolIterator(V8Value... v8Values) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Symbol toPrimitive.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, E {
        return getV8Runtime().createV8ValueNull();
    }

    /**
     * To JSON.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 3.0.4
     */
    default V8Value toJSON(V8Value... v8Values) throws JavetException, E {
        return getV8Runtime().createV8ValueObject();
    }
}
