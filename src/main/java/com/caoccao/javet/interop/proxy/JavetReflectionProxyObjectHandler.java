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
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.JavetStringUtils;
import com.caoccao.javet.utils.ThreadSafeMap;
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
     * The constant FUNCTION_NAME_LENGTH.
     *
     * @since 1.0.6
     */
    protected static final String FUNCTION_NAME_LENGTH = "length";
    /**
     * The constant classDescriptorMap.
     *
     * @since 1.1.7
     */
    protected static final ThreadSafeMap<Class<?>, ClassDescriptor> classDescriptorMap = new ThreadSafeMap<>();

    /**
     * Instantiates a new Javet reflection proxy object handler.
     *
     * @param v8Runtime               the V8 runtime
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @since 0.9.6
     */
    public JavetReflectionProxyObjectHandler(
            V8Runtime v8Runtime,
            IJavetReflectionObjectFactory reflectionObjectFactory,
            T targetObject) {
        super(v8Runtime, reflectionObjectFactory, Objects.requireNonNull(targetObject));
    }

    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        V8Value result = getFromCollection(property);
        result = result == null ? getFromField(property) : result;
        result = result == null ? getFromMethod(target, property) : result;
        result = result == null ? getFromSymbol(property) : result;
        result = result == null ? getFromGetter(property) : result;
        result = result == null ? getFromPolyfill(property) : result;
        return result == null ? v8Runtime.createV8ValueUndefined() : result;
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        if (callbackContexts == null) {
            callbackContexts = new JavetCallbackContext[]{
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_GET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> get(v8Values[0], v8Values[1], v8Values[2])),
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

    @Override
    public ThreadSafeMap<Class<?>, ClassDescriptor> getClassDescriptorCache() {
        return classDescriptorMap;
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
            String propertyString = ((V8ValueString) property).toPrimitive();
            if (JavetStringUtils.isDigital(propertyString)) {
                final int index = Integer.parseInt(propertyString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            return v8Runtime.toV8Value(Array.get(targetObject, index));
                        }
                    } else if (List.class.isAssignableFrom(classDescriptor.getTargetClass())) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            return v8Runtime.toV8Value(list.get(index));
                        }
                    }
                }
            } else if (classDescriptor.getTargetClass().isArray() && FUNCTION_NAME_LENGTH.equals(propertyString)) {
                return v8Runtime.toV8Value(Array.getLength(targetObject));
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
        } else if (classDescriptor.isTargetTypeSet()) {
            return ((Set<?>) targetObject).contains(v8Runtime.toObject(property));
        } else if (property instanceof V8ValueString) {
            String indexString = ((V8ValueString) property).toPrimitive();
            if (JavetStringUtils.isDigital(indexString)) {
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
        classDescriptor = classDescriptorMap.get(targetClass);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(V8ProxyMode.Object, targetClass);
            if (targetObject instanceof Class) {
                initializeFieldsAndMethods((Class<?>) targetObject, true);
            }
            initializeCollection();
            initializeFieldsAndMethods(targetClass, false);
            classDescriptorMap.put(targetClass, classDescriptor);
        }
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
        if (keys != null && keys.length > 0) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                for (Object key : keys) {
                    if (key instanceof String) {
                        v8ValueArray.push(v8Runtime.createV8ValueString((String) key));
                    } else if (key instanceof V8ValueString || key instanceof V8ValueSymbol) {
                        v8ValueArray.push(key);
                    } else if (key != null) {
                        v8ValueArray.push(v8Runtime.createV8ValueString(key.toString()));
                    }
                }
                v8Scope.setEscapable();
                return v8ValueArray;
            }
        }
        return v8Runtime.toV8Value(classDescriptor.getUniqueKeySet().toArray());
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
            String indexString = ((V8ValueString) propertyKey).toPrimitive();
            if (JavetStringUtils.isDigital(indexString)) {
                final int index = Integer.parseInt(indexString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            Array.set(targetObject, index, v8Runtime.toObject(propertyValue));
                            return true;
                        }
                    } else if (List.class.isAssignableFrom(classDescriptor.getTargetClass())) {
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
