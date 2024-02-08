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
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginDefault;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.util.Map;
import java.util.Objects;

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
        if (classDescriptor.getClassProxyPlugin().isDeleteSupported()) {
            return classDescriptor.getClassProxyPlugin().deleteByObject(targetObject, v8Runtime.toObject(property));
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
                    return v8Runtime.toV8Value(classDescriptor.getClassProxyPlugin().getByIndex(targetObject, index));
                }
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
            IClassProxyPluginFunction<E> iClassProxyPluginFunction =
                    classDescriptor.getClassProxyPlugin().getProxyGetByString(
                            classDescriptor.getTargetClass(), propertyName);
            if (iClassProxyPluginFunction != null) {
                return iClassProxyPluginFunction.invoke(getV8Runtime(), targetObject);
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
                return v8Runtime.createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.PROPERTY_TO_PRIMITIVE, JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::toPrimitive));
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
        if (classDescriptor.getClassProxyPlugin().isHasSupported()) {
            return classDescriptor.getClassProxyPlugin().hasByObject(targetObject, v8Runtime.toObject(property));
        }
        return false;
    }

    @Override
    protected void initialize() {
        Class<T> targetClass = (Class<T>) targetObject.getClass();
        classDescriptor = ClassDescriptorStore.getObjectMap().get(targetClass);
        if (classDescriptor == null) {
            IClassProxyPlugin iClassProxyPlugin = v8Runtime.getConverter().getConfig().getProxyPlugins().stream()
                    .filter(p -> p.isProxyable(targetClass))
                    .findFirst()
                    .orElse(JavetProxyPluginDefault.getInstance());
            classDescriptor = new ClassDescriptor(V8ProxyMode.Object, targetClass, iClassProxyPlugin);
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
        if (classDescriptor.getClassProxyPlugin().isUniqueKeySupported()) {
            classDescriptor.getClassProxyPlugin().populateUniqueKeys(classDescriptor.getUniqueKeySet(), targetObject);
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
        overrideMethods = classDescriptor.getClassProxyPlugin().getOverrideMethods(v8Runtime.getConverter().getConfig());
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
        Object[] keys = classDescriptor.getClassProxyPlugin().getOwnKeys(targetObject);
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
                if (index >= 0 && classDescriptor.getClassProxyPlugin().isIndexedPropertySupported()) {
                    return classDescriptor.getClassProxyPlugin().setByIndex(
                            targetObject, index, v8Runtime.toObject(propertyValue));
                }
            }
        }
        return false;
    }
}
