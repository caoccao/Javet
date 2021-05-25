/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetBiConsumer;
import com.caoccao.javet.interfaces.IJavetBiIndexedConsumer;
import com.caoccao.javet.interfaces.IJavetUniConsumer;
import com.caoccao.javet.interfaces.IJavetUniIndexedConsumer;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson;
import com.caoccao.javet.values.virtual.V8VirtualValue;
import com.caoccao.javet.values.virtual.V8VirtualValueList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unchecked")
public class V8ValueObject extends V8ValueReference implements IV8ValueObject {
    protected static final String FUNCTION_ADD = "add";
    protected static final String FUNCTION_DELETE = "delete";
    protected static final String FUNCTION_GET = "get";
    protected static final String FUNCTION_HAS = "has";
    protected static final String FUNCTION_SET = "set";
    protected static final String METHOD_PREFIX_IS = "is";
    protected static final String METHOD_PREFIX_GET = "get";
    protected static final String METHOD_PREFIX_SET = "set";

    protected V8ValueObject(long handle) {
        super(handle);
    }

    @Override
    public List<JavetCallbackContext> bindFunctions(
            Object functionCallbackReceiver,
            boolean thisObjectRequired)
            throws JavetException {
        Map<String, Method> functionMap = new HashMap<>();
        for (Method method : functionCallbackReceiver.getClass().getMethods()) {
            if (method.isAnnotationPresent(V8Function.class)) {
                V8Function v8Function = method.getAnnotation(V8Function.class);
                String functionName = v8Function.name();
                if (functionName == null || functionName.length() == 0) {
                    functionName = method.getName();
                }
                // Duplicated function will be dropped.
                if (!functionMap.containsKey(functionName)) {
                    functionMap.put(functionName, method);
                }
            } else {
                bindV8Runtime(functionCallbackReceiver, method);
            }
        }
        List<JavetCallbackContext> javetCallbackContexts = new ArrayList<>();
        if (!functionMap.isEmpty()) {
            for (Map.Entry<String, Method> entry : functionMap.entrySet()) {
                final Method method = entry.getValue();
                try {
                    // Static method needs to be identified.
                    JavetCallbackContext javetCallbackContext = new JavetCallbackContext(
                            Modifier.isStatic(method.getModifiers()) ? null : functionCallbackReceiver,
                            method, thisObjectRequired);
                    bindFunction(entry.getKey(), javetCallbackContext);
                    javetCallbackContexts.add(javetCallbackContext);
                } catch (Exception e) {
                    throw new JavetException(
                            JavetError.CallbackRegistrationFailure,
                            SimpleMap.of(
                                    JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                    JavetError.PARAMETER_MESSAGE, e.getMessage()),
                            e);
                }
            }
        }
        return javetCallbackContexts;
    }

    @Override
    public List<JavetCallbackContext> bindProperties(Object propertyCallbackReceiver) throws JavetException {
        Map<String, Method> propertyGetterMap = new HashMap<>();
        Map<String, Method> propertySetterMap = new HashMap<>();
        for (Method method : propertyCallbackReceiver.getClass().getMethods()) {
            if (method.isAnnotationPresent(V8Property.class)) {
                V8Property v8Property = method.getAnnotation(V8Property.class);
                String propertyName = v8Property.name();
                if (propertyName == null || propertyName.length() == 0) {
                    String methodName = method.getName();
                    if (methodName.startsWith(METHOD_PREFIX_IS)) {
                        propertyName = methodName.substring(METHOD_PREFIX_IS.length());
                    } else if (methodName.startsWith(METHOD_PREFIX_GET)) {
                        propertyName = methodName.substring(METHOD_PREFIX_GET.length());
                    } else if (methodName.startsWith(METHOD_PREFIX_SET)) {
                        propertyName = methodName.substring(METHOD_PREFIX_SET.length());
                    }
                    if (propertyName != null && propertyName.length() > 0) {
                        propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT) + propertyName.substring(1);
                    }
                }
                if (propertyName != null && propertyName.length() > 0) {
                    if (method.getParameterCount() == 0) {
                        // Duplicated property name will be dropped.
                        if (!propertyGetterMap.containsKey(propertyName)) {
                            propertyGetterMap.put(propertyName, method);
                        }
                    } else if (method.getParameterCount() == 1) {
                        // Duplicated property name will be dropped.
                        if (!propertySetterMap.containsKey(propertyName)) {
                            propertySetterMap.put(propertyName, method);
                        }
                    } else {
                        throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                SimpleMap.of(
                                        JavetError.PARAMETER_METHOD_NAME, method.getName(),
                                        JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 0,
                                        JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, method.getParameterCount()));
                    }
                }
            } else {
                bindV8Runtime(propertyCallbackReceiver, method);
            }
        }
        List<JavetCallbackContext> javetCallbackContexts = new ArrayList<>();
        if (!propertyGetterMap.isEmpty()) {
            for (Map.Entry<String, Method> entry : propertyGetterMap.entrySet()) {
                String propertyName = entry.getKey();
                Method methodGetter = entry.getValue();
                try {
                    // Static method needs to be identified.
                    JavetCallbackContext javetCallbackContextGetter = new JavetCallbackContext(
                            Modifier.isStatic(methodGetter.getModifiers()) ? null : propertyCallbackReceiver,
                            methodGetter, false);
                    javetCallbackContexts.add(javetCallbackContextGetter);
                    JavetCallbackContext javetCallbackContextSetter = null;
                    if (propertySetterMap.containsKey(propertyName)) {
                        Method methodSetter = propertySetterMap.get(propertyName);
                        // Static method needs to be identified.
                        javetCallbackContextSetter = new JavetCallbackContext(
                                Modifier.isStatic(methodSetter.getModifiers()) ? null : propertyCallbackReceiver,
                                methodSetter, false);
                        javetCallbackContexts.add(javetCallbackContextSetter);
                    }
                    bindProperty(propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
                } catch (Exception e) {
                    throw new JavetException(
                            JavetError.CallbackRegistrationFailure,
                            SimpleMap.of(
                                    JavetError.PARAMETER_METHOD_NAME, methodGetter.getName(),
                                    JavetError.PARAMETER_MESSAGE, e.getMessage()),
                            e);
                }
            }
        }
        return javetCallbackContexts;
    }

    @Override
    public boolean bindProperty(
            String propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        Objects.requireNonNull(javetCallbackContextGetter);
        checkV8Runtime();
        return v8Runtime.setAccessor(this, propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
    }

    protected boolean bindV8Runtime(Object callbackReceiver, Method method) throws JavetException {
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
            try {
                method.invoke(callbackReceiver, getV8Runtime());
            } catch (Exception e) {
                throw new JavetException(
                        JavetError.CallbackInjectionFailure,
                        SimpleMap.of(JavetError.PARAMETER_MESSAGE, e.getMessage()),
                        e);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Object key) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key)) {
            return v8Runtime.delete(this, virtualKey.get());
        }
    }

    @Override
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniConsumer<Key, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach(consumer);
        }
    }

    @Override
    public <Key extends V8Value, E extends Throwable> int forEach(
            IJavetUniIndexedConsumer<Key, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach(consumer);
        }
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiConsumer<Key, Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach((Key key) -> {
                try (Value value = get(key)) {
                    consumer.accept(key, value);
                }
            });
        }
    }

    @Override
    public <Key extends V8Value, Value extends V8Value, E extends Throwable> int forEach(
            IJavetBiIndexedConsumer<Key, Value, E> consumer) throws JavetException, E {
        Objects.requireNonNull(consumer);
        try (IV8ValueArray iV8ValueArray = getOwnPropertyNames()) {
            return iV8ValueArray.forEach((int index, Key key) -> {
                try (Value value = get(key)) {
                    consumer.accept(index, key, value);
                }
            });
        }
    }

    @Override
    public <T extends V8Value> T get(Object key) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key)) {
            return v8Runtime.get(this, virtualKey.get());
        }
    }

    @Override
    public int getIdentityHash() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getIdentityHash(this);
    }

    @Override
    public IV8ValueArray getOwnPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getOwnPropertyNames(this);
    }

    @Override
    public IV8ValueArray getPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getPropertyNames(this);
    }

    @Override
    public <T extends V8Value> T getProperty(Object key) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key)) {
            return v8Runtime.getProperty(this, virtualKey.get());
        }
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean has(Object value) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, value)) {
            return v8Runtime.has(this, virtualValue.get());
        }
    }

    @Override
    public boolean hasOwnProperty(Object key) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key)) {
            return v8Runtime.hasOwnProperty(this, virtualKey.get());
        }
    }

    @Override
    public <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, Object... objects)
            throws JavetException {
        checkV8Runtime();
        try (V8VirtualValueList virtualValueList = new V8VirtualValueList(v8Runtime, objects)) {
            return v8Runtime.invoke(this, functionName, returnResult, virtualValueList.get());
        }
    }

    @Override
    public <T extends V8Value> T invokeExtended(String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        checkV8Runtime();
        v8Runtime.decorateV8Values(v8Values);
        return v8Runtime.invoke(this, functionName, returnResult, v8Values);
    }

    @Override
    public boolean sameValue(V8Value v8Value) throws JavetException {
        if (v8Value == null || !(v8Value instanceof V8ValueObject)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
        if (getHandle() == v8ValueObject.getHandle()) {
            return true;
        }
        return v8Runtime.sameValue(this, v8ValueObject);
    }

    @Override
    public boolean set(Object key, Object value) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key);
             V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, value)) {
            return v8Runtime.set(this, virtualKey.get(), virtualValue.get());
        }
    }

    @Override
    public boolean setProperty(Object key, Object value) throws JavetException {
        checkV8Runtime();
        try (V8VirtualValue virtualKey = new V8VirtualValue(v8Runtime, key);
             V8VirtualValue virtualValue = new V8VirtualValue(v8Runtime, value)) {
            return v8Runtime.setProperty(this, virtualKey.get(), virtualValue.get());
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
        return v8Runtime.strictEquals(this, v8ValueObject);
    }

    @Override
    public String toProtoString() {
        try {
            checkV8Runtime();
            return v8Runtime.toProtoString(this);
        } catch (JavetException e) {
            return e.getMessage();
        }
    }

    @Override
    public String toJsonString() {
        try {
            checkV8Runtime();
            try (V8ValueBuiltInJson v8ValueBuiltInJson = v8Runtime.getGlobalObject().getJson()) {
                return v8ValueBuiltInJson.stringify(this);
            }
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
