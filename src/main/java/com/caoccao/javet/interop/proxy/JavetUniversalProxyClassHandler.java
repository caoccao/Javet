/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Javet universal proxy class handler.
 *
 * @param <T> the type parameter
 * @since 1.1.7
 */
public class JavetUniversalProxyClassHandler<T extends Class<?>> extends BaseJavetProxyHandler<T> {
    /**
     * The constant CLASS_DESCRIPTOR_MAP.
     *
     * @since 1.1.7
     */
    protected static final Map<Class<?>, ClassDescriptor> CLASS_DESCRIPTOR_MAP = new ConcurrentHashMap<>();
    /**
     * The constant METHOD_NAME_CONSTRUCTOR.
     *
     * @since 0.9.8
     */
    protected static final String METHOD_NAME_CONSTRUCTOR = "constructor";

    /**
     * Instantiates a new Javet universal proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 0.9.6
     */
    public JavetUniversalProxyClassHandler(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
        initialize();
    }

    @Override
    public void clearClassDescriptorCache() {
        CLASS_DESCRIPTOR_MAP.clear();
    }

    @V8Function
    @Override
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        V8Value[] v8Values = null;
        try {
            v8Values = arguments.toArray();
            return v8Runtime.toV8Value(execute(
                    v8Runtime,
                    null,
                    (V8ValueObject) target,
                    classDescriptor.getConstructors(),
                    V8ValueUtils.convertToVirtualObjects(v8Values)));
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            throw new JavetException(JavetError.CallbackMethodFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, METHOD_NAME_CONSTRUCTOR,
                            JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
        } finally {
            if (v8Values != null) {
                JavetResourceUtils.safeClose((Object[]) v8Values);
            }
        }
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        V8Value result = getFromField(property);
        result = result == null ? getFromMethod(target, property) : result;
        result = result == null ? getFromGetter(property) : result;
        return result == null ? v8Runtime.createV8ValueUndefined() : result;
    }

    @V8Function
    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = hasFromRegular(property);
        isFound = isFound || hasFromGeneric(property);
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    /**
     * Initialize.
     *
     * @since 1.1.7
     */
    protected void initialize() {
        classDescriptor = CLASS_DESCRIPTOR_MAP.get(targetObject);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(V8ProxyMode.Class, targetObject);
            Class<?> targetClass = targetObject.getClass();
            initializeFieldsAndMethods(targetObject, true);
            initializeFieldsAndMethods(targetClass, false);
            CLASS_DESCRIPTOR_MAP.put(targetObject, classDescriptor);
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
            initializeConstructors(currentClass, conversionMode);
            initializePublicFields(currentClass, conversionMode, staticMode);
            initializePublicMethods(currentClass, conversionMode, staticMode);
            if (currentClass == Object.class) {
                break;
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);
    }

    @V8Function
    @Override
    public V8Value ownKeys(V8Value target) throws JavetException {
        return v8Runtime.toV8Value(classDescriptor.getUniqueKeySet().toArray());
    }

    @V8Function
    @Override
    public V8ValueBoolean set(
            V8Value target,
            V8Value propertyKey,
            V8Value propertyValue,
            V8Value receiver) throws JavetException {
        boolean isSet = setToField(propertyKey, propertyValue);
        isSet = isSet || setToSetter(target, propertyKey, propertyValue);
        return v8Runtime.createV8ValueBoolean(isSet);
    }
}
