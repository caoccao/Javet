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

import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.interop.binding.ClassDescriptorStore;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginClass;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type Javet reflection proxy class handler.
 *
 * @param <T> the type parameter
 * @since 1.1.7
 */
public class JavetReflectionProxyClassHandler<T extends Class<?>, E extends Exception>
        extends BaseJavetReflectionProxyHandler<T, E> {
    /**
     * The constant METHOD_NAME_CONSTRUCTOR.
     *
     * @since 0.9.8
     */
    protected static final String METHOD_NAME_CONSTRUCTOR = "constructor";

    /**
     * Instantiates a new Javet reflection proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 0.9.6
     */
    public JavetReflectionProxyClassHandler(
            V8Runtime v8Runtime,
            T targetObject) {
        super(v8Runtime, targetObject);
    }

    @Override
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        if (!classDescriptor.getConstructors().isEmpty()) {
            V8Value[] v8Values = null;
            try {
                v8Values = arguments.toArray();
                return v8Runtime.toV8Value(execute(
                        v8Runtime.getConverter().getConfig().getReflectionObjectFactory(),
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
                    JavetResourceUtils.safeClose(v8Values);
                }
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        if (callbackContexts == null) {
            callbackContexts = new JavetCallbackContext[]{
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_CONSTRUCT, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> construct(v8Values[0], (V8ValueArray) v8Values[1], v8Values[2])),
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

    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = hasFromRegular(property);
        isFound = isFound || hasFromGeneric(property);
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    @Override
    protected void initialize() {
        classDescriptor = ClassDescriptorStore.getClassMap().get(targetObject);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(
                    V8ProxyMode.Class,
                    targetObject,
                    JavetProxyPluginClass.getInstance());
            Class<?> targetClass = targetObject.getClass();
            initializeFieldsAndMethods(targetObject, true);
            initializeFieldsAndMethods(targetClass, false);
            ClassDescriptorStore.getClassMap().put(targetObject, classDescriptor);
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

    @Override
    protected V8Value internalGet(V8Value target, V8Value property) throws JavetException, E {
        V8Value v8Value = getByField(property);
        v8Value = v8Value == null ? getByMethod(target, property) : v8Value;
        v8Value = v8Value == null ? getByGetter(property) : v8Value;
        return v8Value;
    }

    @Override
    public V8ValueArray ownKeys(V8Value target) throws JavetException {
        return V8ValueUtils.createV8ValueArray(v8Runtime, classDescriptor.getUniqueKeySet().toArray());
    }

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
