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

import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.interop.binding.ClassDescriptorStore;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.polyfill.*;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The type Javet reflection proxy object handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetReflectionProxyObjectHandler<T, E extends Exception>
        extends BaseJavetReflectionProxyHandler<T, E> {
    /**
     * The constant LENGTH.
     *
     * @since 3.0.4
     */
    protected static final String LENGTH = "length";

    /**
     * Instantiates a new Javet reflection proxy object handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 0.9.6
     */
    public JavetReflectionProxyObjectHandler(
            V8Runtime v8Runtime,
            T targetObject) {
        super(v8Runtime, Objects.requireNonNull(targetObject));
    }

    /**
     * Delete from collection.
     *
     * @param property the property
     * @return true : deleted, false : not deleted
     * @throws JavetException the javet exception
     */
    protected boolean deleteFromCollection(V8Value property) throws JavetException {
        if (property instanceof V8ValueString) {
            try {
                String propertyString = ((V8ValueString) property).getValue();
                if (StringUtils.isDigital(propertyString)) {
                    final int index = Integer.parseInt(propertyString);
                    if (index >= 0) {
                        if (classDescriptor.getTargetClass().isArray()) {
                            if (index < Array.getLength(targetObject)) {
                                // Only non-primitive array supports delete.
                                if (!classDescriptor.getTargetClass().getComponentType().isPrimitive()) {
                                    Array.set(targetObject, index, null);
                                    return true;
                                }
                            }
                        } else if (classDescriptor.isTargetTypeList()) {
                            List<?> list = (List<?>) targetObject;
                            if (index < list.size()) {
                                list.remove(index);
                                return true;
                            }
                        }
                    }
                }
                if (classDescriptor.isTargetTypeMap()) {
                    Map<?, ?> map = (Map<?, ?>) targetObject;
                    return map.remove(propertyString) != null;
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    @Override
    public V8ValueBoolean deleteProperty(V8Value target, V8Value property) throws JavetException, E {
        boolean result = deleteFromCollection(property);
        return super.deleteProperty(target, property);
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        if (callbackContexts == null) {
            callbackContexts = new JavetCallbackContext[]{
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_DELETE_PROPERTY, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> deleteProperty(v8Values[0], v8Values[1])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_GET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> get(v8Values[0], v8Values[1], v8Values[2])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_GET_OWN_PROPERTY_DESCRIPTOR, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> getOwnPropertyDescriptor(v8Values[0], v8Values[1])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_HAS, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> has(v8Values[0], v8Values[1])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_OWN_KEYS, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> ownKeys(v8Values[0])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_SET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> set(v8Values[0], v8Values[1], v8Values[2], v8Values[3])),
            };
        }
        return callbackContexts;
    }

    /**
     * Gets from collection.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromCollection(V8Value property) throws JavetException {
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).getValue();
            if (StringUtils.isDigital(propertyString)) {
                final int index = Integer.parseInt(propertyString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            return v8Runtime.toV8Value(Array.get(targetObject, index));
                        }
                    } else if (classDescriptor.isTargetTypeList()) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            return v8Runtime.toV8Value(list.get(index));
                        }
                    }
                }
            } else if (classDescriptor.getTargetClass().isArray() && LENGTH.equals(propertyString)) {
                return v8Runtime.toV8Value(Array.getLength(targetObject));
            }
        }
        return null;
    }

    /**
     * Gets from polyfill.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 3.0.3
     */
    protected V8Value getFromPolyfill(V8Value property) throws JavetException, E {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).getValue();
            IJavetProxyPolyfillFunction<T, E> iJavetProxyPolyfillFunction;
            if (classDescriptor.isTargetTypeList()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        JavetProxyPolyfillList.getFunction(propertyName);
            } else if (classDescriptor.isTargetTypeMap()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        JavetProxyPolyfillMap.getFunction(propertyName);
            } else if (classDescriptor.isTargetTypeSet()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        JavetProxyPolyfillSet.getFunction(propertyName);
            } else {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        JavetProxyPolyfillPrimitive.getFunction(classDescriptor.getTargetClass(), propertyName);
            }
            if (iJavetProxyPolyfillFunction != null) {
                return iJavetProxyPolyfillFunction.invoke(getV8Runtime(), getTargetObject());
            }
        }
        return null;
    }

    /**
     * Gets from symbol.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromSymbol(V8Value property) throws JavetException {
        if (property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                return new JavetProxySymbolToPrimitiveConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)
                    && (targetObject instanceof Iterable<?>
                    || targetObject instanceof Map<?, ?>
                    || targetObject instanceof String
                    || classDescriptor.getTargetClass().isArray())) {
                return new JavetProxySymbolIterableConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            }
        }
        return null;
    }

    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = hasFromCollection(property);
        isFound = isFound || hasFromRegular(property);
        isFound = isFound || hasFromGeneric(property);
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    /**
     * Has from collection.
     *
     * @param property the property
     * @return true : has, false: not has
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean hasFromCollection(V8Value property) throws JavetException {
        if (classDescriptor.isTargetTypeMap()) {
            return ((Map<?, ?>) targetObject).containsKey(v8Runtime.toObject(property));
        } else if (classDescriptor.isTargetTypeList()) {
            return ((List<?>) targetObject).contains(v8Runtime.toObject(property));
        } else if (classDescriptor.isTargetTypeSet()) {
            return ((Set<?>) targetObject).contains(v8Runtime.toObject(property));
        } else if (property instanceof V8ValueString) {
            String indexString = ((V8ValueString) property).getValue();
            if (StringUtils.isDigital(indexString)) {
                final int index = Integer.parseInt(indexString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        return index < Array.getLength(targetObject);
                    } else if (List.class.isAssignableFrom(classDescriptor.getTargetClass())) {
                        return index < ((List<?>) targetObject).size();
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void initialize() {
        Class<T> targetClass = (Class<T>) targetObject.getClass();
        classDescriptor = ClassDescriptorStore.getObjectMap().get(targetClass);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(V8ProxyMode.Object, targetClass);
            if (targetObject instanceof Class) {
                initializeFieldsAndMethods((Class<?>) targetObject, true);
            }
            initializeCollection();
            initializeFieldsAndMethods(targetClass, false);
            ClassDescriptorStore.getObjectMap().put(targetClass, classDescriptor);
        }
        initializeOverrideMethods();
    }

    /**
     * Initialize collection.
     *
     * @since 1.1.7
     */
    protected void initializeCollection() {
        if (classDescriptor.isTargetTypeMap()) {
            ((Map<Object, ?>) targetObject).keySet().stream()
                    .map(Object::toString)
                    .filter(Objects::nonNull)
                    .forEach(classDescriptor.getUniqueKeySet()::add);
        } else if (classDescriptor.isTargetTypeSet()) {
            ((Set<Object>) targetObject).stream()
                    .map(Object::toString)
                    .filter(Objects::nonNull)
                    .forEach(classDescriptor.getUniqueKeySet()::add);
        }
    }

    /**
     * Initialize fields and methods.
     *
     * @param currentClass the current class
     * @param staticMode   the static mode
     * @since 0.9.6
     */
    protected void initializeFieldsAndMethods(Class<?> currentClass, boolean staticMode) {
        V8ConversionMode conversionMode = classDescriptor.getConversionMode();
        do {
            initializePublicFields(currentClass, conversionMode, staticMode);
            initializePublicMethods(currentClass, conversionMode, staticMode);
            if (currentClass == Object.class) {
                break;
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);
    }

    /**
     * Initialize override methods.
     *
     * @since 3.0.4
     */
    protected void initializeOverrideMethods() {
        if (classDescriptor.isTargetTypeList()) {
            overrideMethods = v8Runtime.getConverter().getConfig().getProxyListOverrideMethods();
        } else if (classDescriptor.isTargetTypeMap()) {
            overrideMethods = v8Runtime.getConverter().getConfig().getProxyMapOverrideMethods();
        }
    }

    @Override
    protected V8Value internalGet(V8Value target, V8Value property) throws JavetException, E {
        V8Value v8Value = getFromCollection(property);
        v8Value = v8Value == null ? getFromField(property) : v8Value;
        v8Value = v8Value == null ? getFromMethod(target, property) : v8Value;
        v8Value = v8Value == null ? getFromSymbol(property) : v8Value;
        v8Value = v8Value == null ? getFromGetter(property) : v8Value;
        v8Value = v8Value == null ? getFromPolyfill(property) : v8Value;
        return v8Value;
    }

    @Override
    public V8ValueArray ownKeys(V8Value target) throws JavetException {
        Object[] keys = null;
        if (classDescriptor.isTargetTypeMap()) {
            keys = ((Map<?, ?>) targetObject).keySet().toArray();
        } else if (classDescriptor.isTargetTypeSet()) {
            keys = ((Set<?>) targetObject).toArray();
        } else if (classDescriptor.getTargetClass().isArray()
                || Collection.class.isAssignableFrom(classDescriptor.getTargetClass())) {
            final int length = classDescriptor.getTargetClass().isArray()
                    ? Array.getLength(targetObject)
                    : ((List<?>) targetObject).size();
            keys = new Object[length];
            for (int i = 0; i < length; ++i) {
                keys[i] = i;
            }
        }
        if (ArrayUtils.isEmpty(keys)) {
            keys = classDescriptor.getUniqueKeySet().toArray();
        }
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            if (key instanceof String) {
                keys[i] = v8Runtime.createV8ValueString((String) key);
            } else if (!(key instanceof V8ValueString) && !(key instanceof V8ValueSymbol)) {
                keys[i] = v8Runtime.createV8ValueString(key.toString());
            }
        }
        return V8ValueUtils.createV8ValueArray(v8Runtime, keys);
    }

    @Override
    public V8ValueBoolean set(
            V8Value target,
            V8Value propertyKey,
            V8Value propertyValue,
            V8Value receiver) throws JavetException {
        boolean isSet = setToCollection(propertyKey, propertyValue);
        isSet = isSet || setToField(propertyKey, propertyValue);
        isSet = isSet || setToSetter(target, propertyKey, propertyValue);
        return v8Runtime.createV8ValueBoolean(isSet);
    }

    /**
     * Sets to collection.
     *
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean setToCollection(V8Value propertyKey, V8Value propertyValue) throws JavetException {
        if (propertyKey instanceof V8ValueString) {
            String propertyKeyString = ((V8ValueString) propertyKey).getValue();
            if (StringUtils.isDigital(propertyKeyString)) {
                final int index = Integer.parseInt(propertyKeyString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            Array.set(targetObject, index, v8Runtime.toObject(propertyValue));
                            return true;
                        }
                    } else if (classDescriptor.isTargetTypeList()) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            list.set(index, v8Runtime.toObject(propertyValue));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
