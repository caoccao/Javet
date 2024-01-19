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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.*;
import com.caoccao.javet.enums.V8ValueInternalType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetBiIndexedConsumer;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.BindingContext;
import com.caoccao.javet.interop.binding.MethodDescriptor;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.ThreadSafeMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.caoccao.javet.values.virtual.V8VirtualValue;
import com.caoccao.javet.values.virtual.V8VirtualValueList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The type V8 value object.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    public static final String METHOD_PREFIX_GET = "get";
    public static final String METHOD_PREFIX_IS = "is";
    public static final String METHOD_PREFIX_SET = "set";
    /**
     * The constant ERROR_THE_KEY_VALUE_PAIR_MUST_MATCH.
     *
     * @since 2.2.0
     */
    protected static final String ERROR_THE_KEY_VALUE_PAIR_MUST_MATCH = "The key value pair must match.";
    protected static final String FUNCTION_ADD = "add";
    protected static final String FUNCTION_DELETE = "delete";
    protected static final String FUNCTION_GET = "get";
    protected static final String FUNCTION_HAS = "has";
    protected static final String FUNCTION_SET = "set";
    protected static final String PROPERTY_PROTOTYPE = "prototype";
    /**
     * The constant bindingContextMap.
     *
     * @since 1.1.7
     */
    protected static final ThreadSafeMap<Class<?>, BindingContext> bindingContextMap = new ThreadSafeMap<>();

    /**
     * Instantiates a new V8 value object.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 1.0.7
     */
    protected V8ValueObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Gets binding context map.
     *
     * @return the binding context map
     * @since 1.1.7
     */
    public static ThreadSafeMap<Class<?>, BindingContext> getBindingContextMap() {
        return bindingContextMap;
    }

    @Override
    public int batchGet(V8Value[] v8ValueKeys, V8Value[] v8ValueValues, int length) throws JavetException {
        return checkV8Runtime().getV8Internal().batchObjectGet(this, v8ValueKeys, v8ValueValues, length);
    }

    @Override
    public List<JavetCallbackContext> bind(Object callbackReceiver) throws JavetException {
        Objects.requireNonNull(callbackReceiver);
        checkV8Runtime();
        List<JavetCallbackContext> javetCallbackContexts = new ArrayList<>();
        if (callbackReceiver instanceof IJavetDirectCallable) {
            IJavetDirectCallable javetDirectCallable = (IJavetDirectCallable) callbackReceiver;
            javetDirectCallable.setV8Runtime(v8Runtime);
            JavetCallbackContext[] contexts = javetDirectCallable.getCallbackContexts();
            if (contexts != null && contexts.length > 0) {
                Map<String, JavetCallbackContext> getterMap = new HashMap<>();
                Map<String, JavetCallbackContext> setterMap = new HashMap<>();
                for (JavetCallbackContext javetCallbackContext : contexts) {
                    switch (javetCallbackContext.getCallbackType()) {
                        case DirectCallGetterAndNoThis:
                        case DirectCallGetterAndThis:
                            getterMap.put(javetCallbackContext.getName(), javetCallbackContext);
                            break;
                        case DirectCallSetterAndNoThis:
                        case DirectCallSetterAndThis:
                            setterMap.put(javetCallbackContext.getName(), javetCallbackContext);
                            break;
                        default:
                            javetCallbackContexts.add(javetCallbackContext);
                            bindFunction(javetCallbackContext);
                            break;
                    }
                }
                for (JavetCallbackContext javetCallbackContextGetter : getterMap.values()) {
                    JavetCallbackContext javetCallbackContextSetter = setterMap.get(javetCallbackContextGetter.getName());
                    javetCallbackContexts.add(javetCallbackContextGetter);
                    if (javetCallbackContextSetter != null) {
                        javetCallbackContexts.add(javetCallbackContextSetter);
                    }
                    bindProperty(javetCallbackContextGetter, javetCallbackContextSetter);
                }
            }
        } else {
            BindingContext bindingContext = getBindingContext(callbackReceiver.getClass());
            Map<String, MethodDescriptor> propertyGetterMap = bindingContext.getPropertyGetterMap();
            Map<String, MethodDescriptor> propertySetterMap = bindingContext.getPropertySetterMap();
            Map<String, MethodDescriptor> functionMap = bindingContext.getFunctionMap();
            Method v8BindingEnabler = bindingContext.getV8BindingEnabler();
            Method v8RuntimeSetter = bindingContext.getV8RuntimeSetter();
            if (v8RuntimeSetter != null) {
                try {
                    v8RuntimeSetter.invoke(callbackReceiver, getV8Runtime());
                } catch (Exception e) {
                    throw new JavetException(
                            JavetError.CallbackInjectionFailure,
                            SimpleMap.of(JavetError.PARAMETER_MESSAGE, e.getMessage()),
                            e);
                }
            }
            if (!propertyGetterMap.isEmpty()) {
                for (Map.Entry<String, MethodDescriptor> entry : propertyGetterMap.entrySet()) {
                    String propertyName = entry.getKey();
                    final MethodDescriptor getterMethodDescriptor = entry.getValue();
                    try {
                        if (v8BindingEnabler != null && !(boolean) v8BindingEnabler.invoke(
                                callbackReceiver, getterMethodDescriptor.getMethod().getName())) {
                            continue;
                        }
                        // Static method needs to be identified.
                        JavetCallbackContext javetCallbackContextGetter = new JavetCallbackContext(
                                propertyName,
                                getterMethodDescriptor.getSymbolType(),
                                Modifier.isStatic(getterMethodDescriptor.getMethod().getModifiers()) ? null : callbackReceiver,
                                getterMethodDescriptor.getMethod(), getterMethodDescriptor.isThisObjectRequired());
                        javetCallbackContexts.add(javetCallbackContextGetter);
                        JavetCallbackContext javetCallbackContextSetter = null;
                        if (propertySetterMap.containsKey(propertyName)) {
                            MethodDescriptor setterMethodDescriptor = propertySetterMap.get(propertyName);
                            if (v8BindingEnabler != null && !(boolean) v8BindingEnabler.invoke(
                                    callbackReceiver, setterMethodDescriptor.getMethod().getName())) {
                                continue;
                            }
                            // Static method needs to be identified.
                            javetCallbackContextSetter = new JavetCallbackContext(
                                    propertyName,
                                    setterMethodDescriptor.getSymbolType(),
                                    Modifier.isStatic(setterMethodDescriptor.getMethod().getModifiers()) ? null : callbackReceiver,
                                    setterMethodDescriptor.getMethod(), setterMethodDescriptor.isThisObjectRequired());
                            javetCallbackContexts.add(javetCallbackContextSetter);
                        }
                        bindProperty(javetCallbackContextGetter, javetCallbackContextSetter);
                    } catch (Exception e) {
                        throw new JavetException(
                                JavetError.CallbackRegistrationFailure,
                                SimpleMap.of(
                                        JavetError.PARAMETER_METHOD_NAME, getterMethodDescriptor.getMethod().getName(),
                                        JavetError.PARAMETER_MESSAGE, e.getMessage()),
                                e);
                    }
                }
            }
            if (!functionMap.isEmpty()) {
                for (Map.Entry<String, MethodDescriptor> entry : functionMap.entrySet()) {
                    String functionName = entry.getKey();
                    final MethodDescriptor functionMethodDescriptor = entry.getValue();
                    try {
                        if (v8BindingEnabler != null && !(boolean) v8BindingEnabler.invoke(
                                callbackReceiver, functionMethodDescriptor.getMethod().getName())) {
                            continue;
                        }
                        // Static method needs to be identified.
                        JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                                functionName,
                                functionMethodDescriptor.getSymbolType(),
                                Modifier.isStatic(functionMethodDescriptor.getMethod().getModifiers()) ?
                                        null : callbackReceiver,
                                functionMethodDescriptor.getMethod(), functionMethodDescriptor.isThisObjectRequired());
                        javetCallbackContexts.add(javetCallbackContext);
                        bindFunction(javetCallbackContext);
                    } catch (Exception e) {
                        throw new JavetException(
                                JavetError.CallbackRegistrationFailure,
                                SimpleMap.of(
                                        JavetError.PARAMETER_METHOD_NAME, functionMethodDescriptor.getMethod().getName(),
                                        JavetError.PARAMETER_MESSAGE, e.getMessage()),
                                e);
                    }
                }
            }
        }
        return javetCallbackContexts;
    }

    @Override
    public boolean bindFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        String functionName = Objects.requireNonNull(javetCallbackContext).getName();
        switch (javetCallbackContext.getSymbolType()) {
            case BuiltIn:
                try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol();
                     V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getBuiltInSymbol(functionName)) {
                    if (v8ValueSymbol == null) {
                        throw new JavetException(
                                JavetError.ConverterSymbolNotBuiltIn,
                                SimpleMap.of(JavetError.PARAMETER_SYMBOL, functionName));
                    }
                    try (V8ValueFunction v8ValueFunction =
                                 checkV8Runtime().createV8ValueFunction(javetCallbackContext)) {
                        return set(v8ValueSymbol, v8ValueFunction);
                    }
                }
            case Custom:
                try (V8ValueSymbol v8ValueSymbol = v8Runtime.createV8ValueSymbol(functionName, true);
                     V8ValueFunction v8ValueFunction = checkV8Runtime().createV8ValueFunction(javetCallbackContext)) {
                    return set(v8ValueSymbol, v8ValueFunction);
                }
            default:
                try (V8ValueFunction v8ValueFunction = checkV8Runtime().createV8ValueFunction(javetCallbackContext)) {
                    return set(functionName, v8ValueFunction);
                }
        }
    }

    @Override
    public boolean bindProperty(
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter)
            throws JavetException {
        String propertyName = Objects.requireNonNull(javetCallbackContextGetter).getName();
        switch (javetCallbackContextGetter.getSymbolType()) {
            case BuiltIn:
                try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol();
                     V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getBuiltInSymbol(propertyName)) {
                    if (v8ValueSymbol == null) {
                        throw new JavetException(
                                JavetError.ConverterSymbolNotBuiltIn,
                                SimpleMap.of(JavetError.PARAMETER_SYMBOL, propertyName));
                    }
                    return checkV8Runtime().getV8Internal().objectSetAccessor(
                            this, v8ValueSymbol, javetCallbackContextGetter, javetCallbackContextSetter);
                }
            case Custom:
                try (V8ValueSymbol v8ValueSymbol = v8Runtime.createV8ValueSymbol(propertyName, true)) {
                    return checkV8Runtime().getV8Internal().objectSetAccessor(
                            this, v8ValueSymbol, javetCallbackContextGetter, javetCallbackContextSetter);
                }
            default:
                try (V8ValueString v8ValueString = v8Runtime.createV8ValueString(propertyName)) {
                    return checkV8Runtime().getV8Internal().objectSetAccessor(
                            this, v8ValueString, javetCallbackContextGetter, javetCallbackContextSetter);
                }
        }
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectDelete(this, virtualKey.get());
        }
    }

    @Override
    public boolean deletePrivateProperty(String propertyName) throws JavetException {
        return checkV8Runtime().getV8Internal().objectDeletePrivateProperty(
                this, Objects.requireNonNull(propertyName));
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer,
            int batchSize)
            throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            batchSize = Math.max(MIN_BATCH_SIZE, batchSize);
            final int length = iV8ValueArray.getLength();
            if (length > 0) {
                V8Value[] v8ValueKeys = new V8Value[batchSize];
                V8Value[] v8ValueValues = new V8Value[batchSize];
                final int loopCount = (length + batchSize - 1) / batchSize;
                for (int i = 0; i < loopCount; i++) {
                    final int startIndex = i * batchSize;
                    final int endIndex = i == loopCount - 1 ? length : startIndex + batchSize;
                    try {
                        int actualLength = iV8ValueArray.batchGet(v8ValueKeys, startIndex, endIndex);
                        if (actualLength > 0) {
                            batchGet(v8ValueKeys, v8ValueValues, actualLength);
                            for (int j = 0; j < actualLength; j++) {
                                consumer.accept((Key) v8ValueKeys[j], (Value) v8ValueValues[j]);
                            }
                        }
                    } finally {
                        JavetResourceUtils.safeClose(v8ValueKeys);
                        JavetResourceUtils.safeClose(v8ValueValues);
                        Arrays.fill(v8ValueKeys, null);
                        Arrays.fill(v8ValueValues, null);
                    }
                }
            }
            return length;
        }
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer,
            int batchSize)
            throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            batchSize = Math.max(MIN_BATCH_SIZE, batchSize);
            final int length = iV8ValueArray.getLength();
            if (length > 0) {
                V8Value[] v8ValueKeys = new V8Value[batchSize];
                V8Value[] v8ValueValues = new V8Value[batchSize];
                final int loopCount = (length + batchSize - 1) / batchSize;
                for (int i = 0; i < loopCount; i++) {
                    final int startIndex = i * batchSize;
                    final int endIndex = i == loopCount - 1 ? length : startIndex + batchSize;
                    try {
                        int actualLength = iV8ValueArray.batchGet(v8ValueKeys, startIndex, endIndex);
                        batchGet(v8ValueKeys, v8ValueValues, actualLength);
                        for (int j = 0; j < actualLength; j++) {
                            consumer.accept(startIndex + j, (Key) v8ValueKeys[j], (Value) v8ValueValues[j]);
                        }
                    } finally {
                        JavetResourceUtils.safeClose(v8ValueKeys);
                        JavetResourceUtils.safeClose(v8ValueValues);
                        Arrays.fill(v8ValueKeys, null);
                        Arrays.fill(v8ValueValues, null);
                    }
                }
            }
            return length;
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T get(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGet(this, virtualKey.get());
        }
    }

    BindingContext getBindingContext(Class<?> callbackReceiverClass) throws JavetException {
        Objects.requireNonNull(callbackReceiverClass);
        BindingContext bindingContext = bindingContextMap.get(callbackReceiverClass);
        if (bindingContext == null) {
            bindingContext = new BindingContext();
            Map<String, MethodDescriptor> propertyGetterMap = bindingContext.getPropertyGetterMap();
            Map<String, MethodDescriptor> propertySetterMap = bindingContext.getPropertySetterMap();
            Map<String, MethodDescriptor> functionMap = bindingContext.getFunctionMap();
            for (Method method : callbackReceiverClass.getMethods()) {
                boolean methodHandled = false;
                if (method.isAnnotationPresent(V8Property.class)) {
                    V8Property v8Property = method.getAnnotation(V8Property.class);
                    String propertyName = v8Property.name();
                    if (propertyName.length() == 0) {
                        String methodName = method.getName();
                        if (methodName.startsWith(METHOD_PREFIX_IS)) {
                            propertyName = methodName.substring(METHOD_PREFIX_IS.length());
                        } else if (methodName.startsWith(METHOD_PREFIX_GET)) {
                            propertyName = methodName.substring(METHOD_PREFIX_GET.length());
                        } else if (methodName.startsWith(METHOD_PREFIX_SET)) {
                            propertyName = methodName.substring(METHOD_PREFIX_SET.length());
                        } else {
                            propertyName = methodName;
                        }
                        if (propertyName.length() > 0) {
                            propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT) + propertyName.substring(1);
                        }
                    }
                    if (propertyName.length() > 0) {
                        final int expectedGetterParameterCount = v8Property.thisObjectRequired() ? 1 : 0;
                        final int expectedSetterParameterCount = expectedGetterParameterCount + 1;
                        if (method.getParameterCount() == expectedGetterParameterCount) {
                            // Duplicated property name will be dropped.
                            if (!propertyGetterMap.containsKey(propertyName)) {
                                propertyGetterMap.put(
                                        propertyName,
                                        new MethodDescriptor(method, v8Property.thisObjectRequired(), v8Property.symbolType()));
                                methodHandled = true;
                            }
                        } else if (method.getParameterCount() == expectedSetterParameterCount) {
                            // Duplicated property name will be dropped.
                            if (!propertySetterMap.containsKey(propertyName)) {
                                propertySetterMap.put(
                                        propertyName,
                                        new MethodDescriptor(method, v8Property.thisObjectRequired(), v8Property.symbolType()));
                                methodHandled = true;
                            }
                        } else {
                            throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                            JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, expectedGetterParameterCount,
                                            JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, method.getParameterCount()));
                        }
                    }
                }
                if (method.isAnnotationPresent(V8Function.class)) {
                    V8Function v8Function = method.getAnnotation(V8Function.class);
                    String functionName = v8Function.name();
                    if (functionName.length() == 0) {
                        functionName = method.getName();
                    }
                    // Duplicated function will be dropped.
                    if (!functionMap.containsKey(functionName)) {
                        functionMap.put(
                                functionName,
                                new MethodDescriptor(method, v8Function.thisObjectRequired()));
                        methodHandled = true;
                    }
                }
                if (!methodHandled) {
                    if (method.isAnnotationPresent(V8RuntimeSetter.class)) {
                        if (method.getParameterCount() != 1) {
                            throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                            JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 1,
                                            JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, method.getParameterCount()));
                        }
                        if (!V8Runtime.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            throw new JavetException(
                                    JavetError.CallbackSignatureParameterTypeMismatch,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_EXPECTED_PARAMETER_TYPE, V8Runtime.class,
                                            JavetError.PARAMETER_ACTUAL_PARAMETER_TYPE, method.getParameterTypes()[0]));
                        }
                        bindingContext.setV8RuntimeSetter(method);
                    } else if (method.isAnnotationPresent(V8BindingEnabler.class)) {
                        if (method.getParameterCount() != 1) {
                            throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                            JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 1,
                                            JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, method.getParameterCount()));
                        }
                        if (!String.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            throw new JavetException(
                                    JavetError.CallbackSignatureParameterTypeMismatch,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_EXPECTED_PARAMETER_TYPE, String.class,
                                            JavetError.PARAMETER_ACTUAL_PARAMETER_TYPE, method.getParameterTypes()[0]));
                        }
                        bindingContext.setV8BindingEnabler(method);
                    }
                }
            }
            bindingContextMap.put(callbackReceiverClass, bindingContext);
        }
        return bindingContext;
    }

    @Override
    public Boolean getBoolean(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetBoolean(this, virtualKey.get());
        }
    }

    @Override
    public Double getDouble(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetDouble(this, virtualKey.get());
        }
    }

    @Override
    public int getIdentityHash() throws JavetException {
        return checkV8Runtime().getV8Internal().objectGetIdentityHash(this);
    }

    @Override
    public Integer getInteger(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetInteger(this, virtualKey.get());
        }
    }

    @Override
    public Long getLong(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetLong(this, virtualKey.get());
        }
    }

    @Override
    @CheckReturnValue
    public IV8ValueArray getOwnPropertyNames() throws JavetException {
        return checkV8Runtime().getV8Internal().objectGetOwnPropertyNames(this);
    }

    @Override
    public <T extends V8Value> T getPrivateProperty(String propertyName) throws JavetException {
        return checkV8Runtime().getV8Internal().objectGetPrivateProperty(
                this, Objects.requireNonNull(propertyName));
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T getProperty(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetProperty(this, virtualKey.get());
        }
    }

    @Override
    @CheckReturnValue
    public IV8ValueArray getPropertyNames() throws JavetException {
        return checkV8Runtime().getV8Internal().objectGetPropertyNames(this);
    }

    @Override
    public <T extends IV8ValueObject> T getPrototype() throws JavetException {
        return (T) get(PROPERTY_PROTOTYPE);
    }

    @Override
    public String getString(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectGetString(this, virtualKey.get());
        }
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean has(Object value) throws JavetException {
        try (V8VirtualValue virtualValue = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(value))) {
            return v8Runtime.getV8Internal().objectHas(this, virtualValue.get());
        }
    }

    @Override
    public boolean hasInternalType(V8ValueInternalType internalType) throws JavetException {
        return checkV8Runtime().getV8Internal().hasInternalType(this, Objects.requireNonNull(internalType));
    }

    @Override
    public boolean hasOwnProperty(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectHasOwnProperty(this, virtualKey.get());
        }
    }

    @Override
    public boolean hasPrivateProperty(String propertyName) throws JavetException {
        return checkV8Runtime().getV8Internal().objectHasPrivateProperty(
                this, Objects.requireNonNull(propertyName));
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, Object... objects)
            throws JavetException {
        Objects.requireNonNull(functionName);
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(checkV8Runtime(), null, objects)) {
            return v8Runtime.getV8Internal().objectInvoke(
                    this, functionName, returnResult, virtualValueList.get());
        }
    }

    @Override
    @CheckReturnValue
    public <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        Objects.requireNonNull(functionName);
        return checkV8Runtime().getV8Internal().objectInvoke(this, functionName, returnResult, v8Values);
    }

    @Override
    public boolean sameValue(V8Value v8Value) throws JavetException {
        if (!(v8Value instanceof V8ValueObject)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
        if (getHandle() == v8ValueObject.getHandle()) {
            return true;
        }
        return checkV8Runtime().getV8Internal().sameValue(this, v8ValueObject);
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key));
             V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, null, value)) {
            return v8Runtime.getV8Internal().objectSet(this, virtualKey.get(), virtualValue.get());
        }
    }

    @Override
    public boolean set(Object... keysAndValues) throws JavetException {
        assert keysAndValues.length > 0 && keysAndValues.length % 2 == 0 : ERROR_THE_KEY_VALUE_PAIR_MUST_MATCH;
        final int length = keysAndValues.length;
        final int pairLength = keysAndValues.length >> 1;
        Object[] keys = new Object[pairLength];
        Object[] values = new Object[pairLength];
        for (int i = 0; i < pairLength; i++) {
            keys[i] = keysAndValues[i * 2];
            values[i] = keysAndValues[i * 2 + 1];
        }
        try (V8VirtualValueList v8VirtualValueKeys = new V8VirtualValueList(checkV8Runtime(), OBJECT_CONVERTER, keys);
             V8VirtualValueList v8VirtualValueValues = new V8VirtualValueList(v8Runtime, null, values)) {
            V8Value[] v8ValueKeys = v8VirtualValueKeys.get();
            V8Value[] v8ValueValues = v8VirtualValueValues.get();
            V8Value[] v8Values = new V8Value[length];
            for (int i = 0; i < pairLength; i++) {
                v8Values[i * 2] = v8ValueKeys[i];
                v8Values[i * 2 + 1] = v8ValueValues[i];
            }
            return v8Runtime.getV8Internal().objectSet(this, v8Values);
        }
    }

    @Override
    public boolean setBoolean(Object key, Boolean value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().objectSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().objectSetBoolean(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setDouble(Object key, Double value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().objectSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().objectSetDouble(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setInteger(Object key, Integer value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().objectSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().objectSetInteger(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setLong(Object key, Long value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            if (value == null) {
                return v8Runtime.getV8Internal().objectSetNull(this, virtualKey.get());
            }
            return v8Runtime.getV8Internal().objectSetLong(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setNull(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectSetNull(this, virtualKey.get());
        }
    }

    @Override
    public boolean setPrivateProperty(String propertyName, Object propertyValue) throws JavetException {
        Objects.requireNonNull(propertyName);
        try (V8VirtualValue virtualValue = new V8VirtualValue(checkV8Runtime(), null, propertyValue)) {
            return v8Runtime.getV8Internal().objectSetPrivateProperty(this, propertyName, virtualValue.get());
        }
    }

    @Override
    public boolean setProperty(Object key, Object value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key));
             V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, null, value)) {
            return v8Runtime.getV8Internal().objectSetProperty(this, virtualKey.get(), virtualValue.get());
        }
    }

    @Override
    public boolean setPrototype(IV8ValueObject v8ValueObject) throws JavetException {
        return set(PROPERTY_PROTOTYPE, Objects.requireNonNull(v8ValueObject));
    }

    @Override
    public boolean setString(Object key, String value) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectSetString(this, virtualKey.get(), value);
        }
    }

    @Override
    public boolean setUndefined(Object key) throws JavetException {
        try (V8VirtualValue virtualKey = new V8VirtualValue(
                checkV8Runtime(), OBJECT_CONVERTER, Objects.requireNonNull(key))) {
            return v8Runtime.getV8Internal().objectSetUndefined(this, virtualKey.get());
        }
    }

    @Override
    public boolean strictEquals(V8Value v8Value) throws JavetException {
        if (!(v8Value instanceof V8ValueObject)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
        if (getHandle() == v8ValueObject.getHandle()) {
            return true;
        }
        return checkV8Runtime().getV8Internal().strictEquals(this, v8ValueObject);
    }

    @Override
    public String toJsonString() {
        try {
            try (V8ValueBuiltInJson v8ValueBuiltInJson = checkV8Runtime().getGlobalObject().getBuiltInJson()) {
                return v8ValueBuiltInJson.stringify(this);
            }
        } catch (JavetException e) {
            return e.getMessage();
        }
    }

    @Override
    public String toProtoString() {
        try {
            return checkV8Runtime().getV8Internal().objectToProtoString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }

    @Override
    public int unbind(Object callbackReceiver) throws JavetException {
        Objects.requireNonNull(callbackReceiver);
        checkV8Runtime();
        int unbindCount = 0;
        if (callbackReceiver instanceof IJavetDirectCallable) {
            IJavetDirectCallable javetDirectCallable = (IJavetDirectCallable) callbackReceiver;
            javetDirectCallable.setV8Runtime(v8Runtime);
            for (JavetCallbackContext javetCallbackContext :
                    Objects.requireNonNull(javetDirectCallable.getCallbackContexts())) {
                boolean success;
                switch (javetCallbackContext.getCallbackType()) {
                    case DirectCallSetterAndNoThis:
                    case DirectCallSetterAndThis:
                        // Generic getter or setter is ignored and there's no need to unbind setter.
                        success = false;
                        break;
                    case DirectCallGetterAndNoThis:
                    case DirectCallGetterAndThis:
                        success = unbindProperty(javetCallbackContext);
                        break;
                    default:
                        success = unbindFunction(javetCallbackContext.getName());
                        break;
                }
                if (success) {
                    ++unbindCount;
                }
            }
        } else {
            BindingContext bindingContext = getBindingContext(callbackReceiver.getClass());
            Map<String, MethodDescriptor> propertyGetterMap = bindingContext.getPropertyGetterMap();
            Map<String, MethodDescriptor> propertySetterMap = bindingContext.getPropertySetterMap();
            Map<String, MethodDescriptor> functionMap = bindingContext.getFunctionMap();
            Method v8BindingEnabler = bindingContext.getV8BindingEnabler();
            if (!propertyGetterMap.isEmpty()) {
                for (Map.Entry<String, MethodDescriptor> entry : propertyGetterMap.entrySet()) {
                    String propertyName = entry.getKey();
                    final MethodDescriptor getterMethodDescriptor = entry.getValue();
                    try {
                        if (v8BindingEnabler != null && !(boolean) v8BindingEnabler.invoke(
                                callbackReceiver, getterMethodDescriptor.getMethod().getName())) {
                            continue;
                        }
                        if (unbindProperty(propertyName, getterMethodDescriptor.getSymbolType())) {
                            ++unbindCount;
                        }
                    } catch (Exception e) {
                        throw new JavetException(
                                JavetError.CallbackUnregistrationFailure,
                                SimpleMap.of(
                                        JavetError.PARAMETER_METHOD_NAME, getterMethodDescriptor.getMethod().getName(),
                                        JavetError.PARAMETER_MESSAGE, e.getMessage()),
                                e);
                    }
                }
            }
            if (!functionMap.isEmpty()) {
                for (Map.Entry<String, MethodDescriptor> entry : functionMap.entrySet()) {
                    String functionName = entry.getKey();
                    final MethodDescriptor functionMethodDescriptor = entry.getValue();
                    try {
                        if (v8BindingEnabler != null && !(boolean) v8BindingEnabler.invoke(
                                callbackReceiver, functionMethodDescriptor.getMethod().getName())) {
                            continue;
                        }
                        if (unbindFunction(functionName, functionMethodDescriptor.getSymbolType())) {
                            ++unbindCount;
                        }
                    } catch (Exception e) {
                        throw new JavetException(
                                JavetError.CallbackUnregistrationFailure,
                                SimpleMap.of(
                                        JavetError.PARAMETER_METHOD_NAME, functionMethodDescriptor.getMethod().getName(),
                                        JavetError.PARAMETER_MESSAGE, e.getMessage()),
                                e);
                    }
                }
            }
        }
        return unbindCount;
    }

    protected boolean unbindFunction(String functionName, V8ValueSymbolType symbolType) throws JavetException {
        Objects.requireNonNull(functionName);
        switch (Objects.requireNonNull(symbolType)) {
            case BuiltIn:
                try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol();
                     V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getBuiltInSymbol(functionName)) {
                    if (v8ValueSymbol == null) {
                        throw new JavetException(
                                JavetError.ConverterSymbolNotBuiltIn,
                                SimpleMap.of(JavetError.PARAMETER_SYMBOL, functionName));
                    }
                    return delete(v8ValueSymbol);
                }
            case Custom:
                try (V8ValueSymbol v8ValueSymbol = v8Runtime.createV8ValueSymbol(functionName, true)) {
                    return delete(v8ValueSymbol);
                }
            default:
                return delete(functionName);
        }
    }

    @Override
    public boolean unbindProperty(JavetCallbackContext javetCallbackContext) throws JavetException {
        return unbindProperty(
                Objects.requireNonNull(javetCallbackContext).getName(),
                javetCallbackContext.getSymbolType());
    }

    protected boolean unbindProperty(String propertyName, V8ValueSymbolType symbolType) throws JavetException {
        Objects.requireNonNull(propertyName);
        switch (Objects.requireNonNull(symbolType)) {
            case BuiltIn:
                try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol();
                     V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getBuiltInSymbol(propertyName)) {
                    if (v8ValueSymbol == null) {
                        throw new JavetException(
                                JavetError.ConverterSymbolNotBuiltIn,
                                SimpleMap.of(JavetError.PARAMETER_SYMBOL, propertyName));
                    }
                    return unbindProperty(v8ValueSymbol);
                }
            case Custom:
                try (V8ValueSymbol v8ValueSymbol = v8Runtime.createV8ValueSymbol(propertyName, true)) {
                    return unbindProperty(v8ValueSymbol);
                }
            default:
                try (V8ValueString v8ValueString = v8Runtime.createV8ValueString(propertyName)) {
                    return unbindProperty(v8ValueString);
                }
        }
    }

    @Override
    public boolean unbindProperty(V8ValueString propertyName) throws JavetException {
        return checkV8Runtime().getV8Internal().objectSetAccessor(
                this, Objects.requireNonNull(propertyName), null, null);
    }

    @Override
    public boolean unbindProperty(V8ValueSymbol propertyName) throws JavetException {
        return checkV8Runtime().getV8Internal().objectSetAccessor(
                this, Objects.requireNonNull(propertyName), null, null);
    }
}
