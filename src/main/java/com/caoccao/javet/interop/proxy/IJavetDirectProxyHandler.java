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

import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiFunction;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.util.Map;

/**
 * The interface Javet direct proxy handler.
 *
 * @param <E> the type parameter
 * @since 2.2.0
 */
public interface IJavetDirectProxyHandler<E extends Exception> {
    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 2.2.0
     */
    V8Runtime getV8Runtime();

    /**
     * Apply to object.
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
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Get by property.
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
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).getValue();
            Map<String, IJavetUniFunction<String, ? extends V8Value, E>> stringGetterMap = proxyGetStringGetterMap();
            if (stringGetterMap != null && !stringGetterMap.isEmpty()) {
                IJavetUniFunction<String, ? extends V8Value, E> getter = stringGetterMap.get(propertyString);
                if (getter != null) {
                    return getter.apply(propertyString);
                }
            }
            if (IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE.equals(propertyString)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            }
        } else if (property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            Map<String, IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> symbolGetterMap = proxyGetSymbolGetterMap();
            if (symbolGetterMap != null && !symbolGetterMap.isEmpty()) {
                IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E> getter = symbolGetterMap.get(description);
                if (getter != null) {
                    return getter.apply(propertySymbol);
                }
            }
            if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolIterator));
            }
        }
        return getV8Runtime().createV8ValueUndefined();
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
     * Has property
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
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).toPrimitive();
            Map<String, IJavetUniFunction<String, ? extends V8Value, E>> stringGetterMap = proxyGetStringGetterMap();
            if (stringGetterMap != null && !stringGetterMap.isEmpty()) {
                hasProperty = stringGetterMap.containsKey(propertyString);
            }
        }
        if (!hasProperty && property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            Map<String, IJavetUniFunction<V8ValueSymbol, ? extends V8Value, E>> symbolGetterMap = proxyGetSymbolGetterMap();
            if (symbolGetterMap != null && !symbolGetterMap.isEmpty()) {
                hasProperty = symbolGetterMap.containsKey(description);
            }
        }
        return getV8Runtime().createV8ValueBoolean(hasProperty);
    }

    /**
     * Own keys.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, E {
        try (V8Scope v8Scope = getV8Runtime().getV8Scope()) {
            V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
            Map<String, IJavetUniFunction<String, ? extends V8Value, E>> stringGetterMap = proxyGetStringGetterMap();
            if (stringGetterMap != null && !stringGetterMap.isEmpty()) {
                V8ValueString[] v8ValueStrings = new V8ValueString[stringGetterMap.size()];
                int index = 0;
                for (String key : stringGetterMap.keySet()) {
                    v8ValueStrings[index++] = getV8Runtime().createV8ValueString(key);
                }
                v8ValueArray.push((Object[]) v8ValueStrings);
            }
            v8Scope.setEscapable();
            return v8ValueArray;
        }
    }

    /**
     * Set value by property.
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
        if (propertyKey instanceof V8ValueString) {
            String propertyKeyString = ((V8ValueString) propertyKey).toPrimitive();
            Map<String, IJavetBiFunction<String, V8Value, Boolean, E>> stringSetterMap = proxyGetStringSetterMap();
            if (stringSetterMap != null && !stringSetterMap.isEmpty()) {
                IJavetBiFunction<String, V8Value, Boolean, E> setter = stringSetterMap.get(propertyKeyString);
                if (setter != null) {
                    isSet = setter.apply(propertyKeyString, propertyValue);
                }
            }
        }
        if (!isSet && propertyKey instanceof V8ValueSymbol) {
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
        return getV8Runtime().createV8ValueBoolean(isSet);
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
}
