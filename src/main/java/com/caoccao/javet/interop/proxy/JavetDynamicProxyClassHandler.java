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
import com.caoccao.javet.utils.ThreadSafeMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type Javet dynamic proxy class handler.
 *
 * @param <T> the type parameter
 * @since 1.1.7
 */
public class JavetDynamicProxyClassHandler<T extends Class<?>> extends BaseJavetProxyHandler<T> {
    /**
     * The constant METHOD_NAME_CONSTRUCTOR.
     *
     * @since 0.9.8
     */
    protected static final String METHOD_NAME_CONSTRUCTOR = "constructor";
    /**
     * The constant classDescriptorMap.
     *
     * @since 1.1.7
     */
    protected static final ThreadSafeMap<Class<?>, ClassDescriptor> classDescriptorMap = new ThreadSafeMap<>();

    /**
     * Instantiates a new Javet dynamic proxy handler.
     *
     * @param v8Runtime            the V8 runtime
     * @param dynamicObjectFactory the dynamic object factory
     * @param targetObject         the target object
     * @since 0.9.6
     */
    public JavetDynamicProxyClassHandler(
            V8Runtime v8Runtime,
            IJavetDynamicObjectFactory dynamicObjectFactory,
            T targetObject) {
        super(v8Runtime, dynamicObjectFactory, targetObject);
    }

    @V8Function
    @Override
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        if (!classDescriptor.getConstructors().isEmpty()) {
            V8Value[] v8Values = null;
            try {
                v8Values = arguments.toArray();
                return v8Runtime.toV8Value(execute(
                        dynamicObjectFactory,
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
        return v8Runtime.createV8ValueUndefined();
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        V8Value result = getFromField(property);
        result = result == null ? getFromMethod(target, property) : result;
        result = result == null ? getFromGetter(property) : result;
        return result == null ? v8Runtime.createV8ValueUndefined() : result;
    }

    @Override
    public ThreadSafeMap<Class<?>, ClassDescriptor> getClassDescriptorCache() {
        return classDescriptorMap;
    }

    @V8Function
    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = hasFromRegular(property);
        isFound = isFound || hasFromGeneric(property);
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    @Override
    protected void initialize() {
        classDescriptor = classDescriptorMap.get(targetObject);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(V8ProxyMode.Class, targetObject);
            Class<?> targetClass = targetObject.getClass();
            initializeFieldsAndMethods(targetObject, true);
            initializeFieldsAndMethods(targetClass, false);
            classDescriptorMap.put(targetObject, classDescriptor);
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
        if (staticMode) {
            initializeConstructors(currentClass, conversionMode);
        }
        do {
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
