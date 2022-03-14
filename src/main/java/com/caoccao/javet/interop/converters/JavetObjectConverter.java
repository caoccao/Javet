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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.entities.JavetEntityFunction;
import com.caoccao.javet.entities.JavetEntityMap;
import com.caoccao.javet.entities.JavetEntitySymbol;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetEntityFunction;
import com.caoccao.javet.interfaces.IJavetEntityMap;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.proxy.IJavetProxyHandler;
import com.caoccao.javet.interop.proxy.JavetUniversalProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.BaseStream;

/**
 * The type Javet object converter converts Java primitive types,
 * Array, List, Map and Set to JS primitive types, Array, Map, Set
 * and Object bi-directionally.
 *
 * @since 0.7.2
 */
@SuppressWarnings("unchecked")
public class JavetObjectConverter extends JavetPrimitiveConverter {
    /**
     * The constant EXECUTABLE_INDEX_DEFAULT_CONSTRUCTOR.
     *
     * @since 0.9.12
     */
    protected static final int EXECUTABLE_INDEX_DEFAULT_CONSTRUCTOR = 0;
    /**
     * The constant EXECUTABLE_INDEX_FROM_MAP.
     *
     * @since 0.9.12
     */
    protected static final int EXECUTABLE_INDEX_FROM_MAP = 1;
    /**
     * The constant EXECUTABLE_INDEX_TO_MAP.
     *
     * @since 0.9.12
     */
    protected static final int EXECUTABLE_INDEX_TO_MAP = 2;
    /**
     * The constant METHOD_NAME_FROM_MAP.
     *
     * @since 0.9.12
     */
    protected static final String METHOD_NAME_FROM_MAP = "fromMap";
    /**
     * The constant METHOD_NAME_TO_MAP.
     *
     * @since 0.9.12
     */
    protected static final String METHOD_NAME_TO_MAP = "toMap";
    /**
     * The constant PRIVATE_PROPERTY_CUSTOM_OBJECT_CLASS_NAME.
     *
     * @since 0.9.6
     */
    protected static final String PRIVATE_PROPERTY_CUSTOM_OBJECT_CLASS_NAME = "JavetObjectConverter#customObjectClassName";
    /**
     * The constant PRIVATE_PROPERTY_PROXY_TARGET.
     *
     * @since 0.9.6
     */
    protected static final String PRIVATE_PROPERTY_PROXY_TARGET = "Javet#proxyTarget";
    /**
     * The constant PROPERTY_NAME.
     *
     * @since 0.7.2
     */
    protected static final String PROPERTY_NAME = "name";
    /**
     * The constant PUBLIC_PROPERTY_CONSTRUCTOR.
     *
     * @since 0.7.2
     */
    protected static final String PUBLIC_PROPERTY_CONSTRUCTOR = "constructor";
    /**
     * The Custom object lock.
     *
     * @since 0.9.12
     */
    protected ReentrantReadWriteLock customObjectLock;
    /**
     * The Custom object map.
     *
     * @since 0.9.12
     */
    protected Map<String, AccessibleObject[]> customObjectMap;

    /**
     * Instantiates a new Javet object converter.
     *
     * @since 0.7.1
     */
    public JavetObjectConverter() {
        super();
        customObjectLock = new ReentrantReadWriteLock();
        customObjectMap = new HashMap<>();
    }

    /**
     * Create entity function javet entity function.
     *
     * @return the javet entity function
     * @since 0.9.4
     */
    protected IJavetEntityFunction createEntityFunction() {
        return new JavetEntityFunction();
    }

    /**
     * Create entity map.
     *
     * @return the map
     * @since 0.7.2
     */
    protected Map<String, Object> createEntityMap() {
        return new JavetEntityMap();
    }

    /**
     * Register custom object.
     *
     * @param customObjectClass the custom object class
     * @return true : success, false: failure
     * @since 0.9.12
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean registerCustomObject(Class<?> customObjectClass) {
        return registerCustomObject(customObjectClass, METHOD_NAME_FROM_MAP, METHOD_NAME_TO_MAP);
    }

    /**
     * Register custom object.
     *
     * @param customObjectClass the custom object class
     * @param methodNameFromMap the method name from map
     * @param methodNameToMap   the method name to map
     * @return true : success, false: failure
     * @since 0.9.12
     */
    public boolean registerCustomObject(Class<?> customObjectClass, String methodNameFromMap, String methodNameToMap) {
        if (customObjectClass == null
                || methodNameFromMap == null || methodNameToMap == null
                || methodNameFromMap.length() == 0 || methodNameToMap.length() == 0
                || methodNameFromMap.equals(methodNameToMap)) {
            return false;
        }
        String customObjectClassName = customObjectClass.getName();
        Lock readLock = customObjectLock.readLock();
        try {
            readLock.lock();
            if (customObjectMap.containsKey(customObjectClassName)) {
                return false;
            }
        } finally {
            readLock.unlock();
        }
        try {
            Constructor defaultConstructor = customObjectClass.getConstructor();
            Method methodFromMap = customObjectClass.getMethod(methodNameFromMap, Map.class);
            if (Modifier.isStatic(methodFromMap.getModifiers())) {
                return false;
            }
            Method methodToMap = customObjectClass.getMethod(methodNameToMap);
            if (Modifier.isStatic(methodToMap.getModifiers())) {
                return false;
            }
            AccessibleObject[] executables = new AccessibleObject[]{defaultConstructor, methodFromMap, methodToMap};
            Lock writeLock = customObjectLock.writeLock();
            try {
                writeLock.lock();
                customObjectMap.put(customObjectClass.getName(), executables);
            } finally {
                writeLock.unlock();
            }
        } catch (Throwable t) {
            // Do nothing.
            t.printStackTrace(System.err);
        }
        return false;
    }

    @Override
    protected Object toObject(V8Value v8Value, final int depth) throws JavetException {
        Object returnObject = super.toObject(v8Value, depth);
        if (!(returnObject instanceof V8Value)) {
            return returnObject;
        }
        if (v8Value instanceof V8ValueArray) {
            V8ValueArray v8ValueArray = (V8ValueArray) v8Value;
            final List<Object> list = new ArrayList<>();
            v8ValueArray.forEach(value -> list.add(toObject(value, depth + 1)));
            return list;
        } else if (v8Value instanceof V8ValueSet) {
            V8ValueSet v8ValueSet = (V8ValueSet) v8Value;
            final HashSet<Object> set = new HashSet<>();
            v8ValueSet.forEach(key -> set.add(toObject(key, depth + 1)));
            return set;
        } else if (v8Value instanceof V8ValueMap) {
            V8ValueMap v8ValueMap = (V8ValueMap) v8Value;
            final Map<String, Object> map = createEntityMap();
            v8ValueMap.forEach((V8Value key, V8Value value) -> map.put(key.toString(), toObject(value, depth + 1)));
            return map;
        } else if (v8Value instanceof V8ValueTypedArray) {
            V8ValueTypedArray v8ValueTypedArray = (V8ValueTypedArray) v8Value;
            switch (v8ValueTypedArray.getType()) {
                case Int8Array:
                case Uint8Array:
                case Uint8ClampedArray:
                    return v8ValueTypedArray.toBytes();
                case Int16Array:
                case Uint16Array:
                    return v8ValueTypedArray.toShorts();
                case Int32Array:
                case Uint32Array:
                    return v8ValueTypedArray.toIntegers();
                case Float32Array:
                    return v8ValueTypedArray.toFloats();
                case Float64Array:
                    return v8ValueTypedArray.toDoubles();
                case BigInt64Array:
                case BigUint64Array:
                    return v8ValueTypedArray.toLongs();
                default:
                    break;
            }
        } else if (v8Value instanceof V8ValueFunction) {
            final IJavetEntityFunction javetEntityFunction = createEntityFunction();
            if (config.isExtractFunctionSourceCode()) {
                V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Value;
                javetEntityFunction.setJSFunctionType(v8ValueFunction.getJSFunctionType());
                switch (javetEntityFunction.getJSFunctionType()) {
                    case Native:
                    case API:
                        javetEntityFunction.setSourceCode(v8ValueFunction.toString());
                        break;
                    case UserDefined:
                        javetEntityFunction.setSourceCode(v8ValueFunction.getSourceCode());
                        break;
                    default:
                        break;
                }
            }
            return javetEntityFunction;
        } else if (v8Value instanceof V8ValueSymbol) {
            final V8ValueSymbol v8ValueSymbol = (V8ValueSymbol) v8Value;
            return new JavetEntitySymbol(v8ValueSymbol.getDescription());
        } else if (v8Value instanceof V8ValueObject) {
            if (v8Value instanceof V8ValueProxy) {
                final V8ValueProxy v8ValueProxy = (V8ValueProxy) v8Value;
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    Long handle = iV8ValueObjectHandler.getPrivatePropertyLong(PRIVATE_PROPERTY_PROXY_TARGET);
                    if (handle != null) {
                        JavetCallbackContext javetCallbackContext = v8ValueProxy.getV8Runtime().getCallbackContext(handle);
                        if (javetCallbackContext != null) {
                            IJavetProxyHandler<Object> iJavetProxyHandler =
                                    (IJavetProxyHandler<Object>) javetCallbackContext.getCallbackReceiver();
                            Object targetObject = iJavetProxyHandler.getTargetObject();
                            if (targetObject != null) {
                                return targetObject;
                            }
                        }
                    }
                }
            }
            V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
            final Map<String, Object> map = new HashMap<>();
            v8ValueObject.forEach((V8Value key, V8Value value) -> {
                String keyString = key.toString();
                if (PUBLIC_PROPERTY_CONSTRUCTOR.equals(keyString)) {
                    map.put(PUBLIC_PROPERTY_CONSTRUCTOR, ((V8ValueObject) value).getString(PROPERTY_NAME));
                } else if (value.isUndefined()) {
                    return;
                } else if (config.isSkipFunctionInObject() && value instanceof V8ValueFunction) {
                    return;
                } else {
                    Object object = toObject(value, depth + 1);
                    map.put(keyString, object);
                }
            });
            if (!customObjectMap.isEmpty()
                    && v8ValueObject.hasPrivateProperty(PRIVATE_PROPERTY_CUSTOM_OBJECT_CLASS_NAME)) {
                String customObjectClassName =
                        v8ValueObject.getPrivatePropertyString(PRIVATE_PROPERTY_CUSTOM_OBJECT_CLASS_NAME);
                Lock readLock = customObjectLock.readLock();
                Constructor defaultConstructor = null;
                Method methodFromMap = null;
                try {
                    readLock.lock();
                    AccessibleObject[] executables = customObjectMap.get(customObjectClassName);
                    if (executables != null) {
                        defaultConstructor = (Constructor) executables[EXECUTABLE_INDEX_DEFAULT_CONSTRUCTOR];
                        methodFromMap = (Method) executables[EXECUTABLE_INDEX_FROM_MAP];
                    }
                } catch (Throwable t) {
                    // Do nothing
                } finally {
                    readLock.unlock();
                }
                if (defaultConstructor != null) {
                    try {
                        Object customObject = defaultConstructor.newInstance();
                        methodFromMap.invoke(customObject, map);
                        return customObject;
                    } catch (Throwable t) {
                        // Do nothing
                    }
                }
            }
            return map;
        }
        return v8Value;
    }

    @Override
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        V8Value v8Value = super.toV8Value(v8Runtime, object, depth);
        if (v8Value != null && !(v8Value.isUndefined())) {
            return (T) v8Value;
        }
        if (object instanceof IJavetEntityMap) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                V8ValueMap v8ValueMap = v8Scope.createV8ValueMap();
                final Map<?, ?> mapObject = (Map<?, ?>) object;
                for (Object key : mapObject.keySet()) {
                    try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key), depth + 1)) {
                        String childStringKey = key instanceof String ? (String) key : key.toString();
                        v8ValueMap.set(childStringKey, childV8Value);
                    }
                }
                v8Value = v8ValueMap;
                v8Scope.setEscapable();
            }
        } else if (object instanceof Map) {
            if (config.isProxyMapEnabled()) {
                try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                    V8ValueProxy v8ValueProxy = v8Scope.createV8ValueProxy();
                    try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                        JavetUniversalProxyHandler<Map<?, ?>> javetUniversalProxyHandler =
                                new JavetUniversalProxyHandler<>(v8Runtime, (Map<?, ?>) object);
                        List<JavetCallbackContext> javetCallbackContexts =
                                iV8ValueObjectHandler.bind(javetUniversalProxyHandler);
                        iV8ValueObjectHandler.setPrivateProperty(PRIVATE_PROPERTY_PROXY_TARGET, javetCallbackContexts.get(0).getHandle());
                    }
                    v8Value = v8ValueProxy;
                    v8Scope.setEscapable();
                }
            } else {
                try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                    V8ValueObject v8ValueObject = v8Scope.createV8ValueObject();
                    final Map<?, ?> mapObject = (Map<?, ?>) object;
                    for (Object key : mapObject.keySet()) {
                        try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key), depth + 1)) {
                            String childStringKey = key instanceof String ? (String) key : key.toString();
                            v8ValueObject.set(childStringKey, childV8Value);
                        }
                    }
                    v8Value = v8ValueObject;
                    v8Scope.setEscapable();
                }
            }
        } else if (object instanceof Set) {
            if (config.isProxySetEnabled()) {
                try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                    V8ValueProxy v8ValueProxy = v8Scope.createV8ValueProxy();
                    try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                        JavetUniversalProxyHandler<Set<?>> javetUniversalProxyHandler =
                                new JavetUniversalProxyHandler<>(v8Runtime, (Set<?>) object);
                        List<JavetCallbackContext> javetCallbackContexts =
                                iV8ValueObjectHandler.bind(javetUniversalProxyHandler);
                        iV8ValueObjectHandler.setPrivateProperty(PRIVATE_PROPERTY_PROXY_TARGET, javetCallbackContexts.get(0).getHandle());
                    }
                    v8Value = v8ValueProxy;
                    v8Scope.setEscapable();
                }
            } else {
                try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                    V8ValueSet v8ValueSet = v8Scope.createV8ValueSet();
                    final Set<?> setObject = (Set<?>) object;
                    for (Object item : setObject) {
                        try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueSet.add(childV8Value);
                        }
                    }
                    v8Value = v8ValueSet;
                    v8Scope.setEscapable();
                }
            }
        } else if (object instanceof Collection) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                for (Object item : (Collection<?>) object) {
                    try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                        v8ValueArray.push(childV8Value);
                    }
                }
                v8Value = v8ValueArray;
                v8Scope.setEscapable();
            }
        } else if (object instanceof BaseStream) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                final Iterator<?> iterator = ((BaseStream<?, ?>) object).iterator();
                while (iterator.hasNext()) {
                    try (V8Value childV8Value = toV8Value(v8Runtime, iterator.next(), depth + 1)) {
                        v8ValueArray.push(childV8Value);
                    }
                }
                v8Value = v8ValueArray;
                v8Scope.setEscapable();
            }
        } else if (object instanceof IJavetEntityFunction) {
            final IJavetEntityFunction javetEntityFunction = (IJavetEntityFunction) object;
            String sourceCode = javetEntityFunction.getJSFunctionType().isUserDefined() ?
                    javetEntityFunction.getSourceCode() : null;
            if (sourceCode == null || sourceCode.length() == 0) {
                v8Value = v8Runtime.createV8ValueNull();
            } else {
                v8Value = v8Runtime.getExecutor(sourceCode).execute();
            }
        } else if (object instanceof JavetEntitySymbol) {
            final JavetEntitySymbol javetEntitySymbol = (JavetEntitySymbol) object;
            v8Value = v8Runtime.createV8ValueSymbol(javetEntitySymbol.getDescription(), true);
        } else if (object.getClass().isArray()) {
            try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                if (object instanceof boolean[]) {
                    V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                    for (boolean item : (boolean[]) object) {
                        v8ValueArray.push(v8Runtime.createV8ValueBoolean(item));
                    }
                    v8Value = v8ValueArray;
                } else if (object instanceof byte[]) {
                    byte[] bytes = (byte[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.Int8Array, bytes.length);
                    v8ValueTypedArray.fromBytes(bytes);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof char[]) {
                    V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                    for (char c : (char[]) object) {
                        v8ValueArray.push(Character.toString(c));
                    }
                    v8Value = v8ValueArray;
                } else if (object instanceof double[]) {
                    double[] doubles = (double[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.Float64Array, doubles.length);
                    v8ValueTypedArray.fromDoubles(doubles);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof float[]) {
                    float[] floats = (float[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.Float32Array, floats.length);
                    v8ValueTypedArray.fromFloats(floats);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof int[]) {
                    int[] integers = (int[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.Int32Array, integers.length);
                    v8ValueTypedArray.fromIntegers(integers);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof long[]) {
                    long[] longs = (long[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.BigInt64Array, longs.length);
                    v8ValueTypedArray.fromLongs(longs);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof short[]) {
                    short[] shorts = (short[]) object;
                    V8ValueTypedArray v8ValueTypedArray = v8Scope.createV8ValueTypedArray(
                            V8ValueReferenceType.Int16Array, shorts.length);
                    v8ValueTypedArray.fromShorts(shorts);
                    v8Value = v8ValueTypedArray;
                } else if (object instanceof String[]) {
                    V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                    for (String item : (String[]) object) {
                        v8ValueArray.push(v8Runtime.createV8ValueString(item));
                    }
                    v8Value = v8ValueArray;
                } else {
                    V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                    for (Object item : (Object[]) object) {
                        try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                            v8ValueArray.push(childV8Value);
                        }
                    }
                    v8Value = v8ValueArray;
                }
                v8Scope.setEscapable();
            }
        } else if (!customObjectMap.isEmpty()) {
            String customObjectClassName = object.getClass().getName();
            Lock readLock = customObjectLock.readLock();
            Method methodToMap = null;
            try {
                readLock.lock();
                AccessibleObject[] executables = customObjectMap.get(customObjectClassName);
                if (executables != null) {
                    methodToMap = (Method) executables[EXECUTABLE_INDEX_TO_MAP];
                }
            } finally {
                readLock.unlock();
            }
            if (methodToMap != null) {
                try {
                    Map map = (Map) methodToMap.invoke(object);
                    v8Value = toV8Value(v8Runtime, map);
                    ((V8ValueObject) v8Value).setPrivateProperty(
                            PRIVATE_PROPERTY_CUSTOM_OBJECT_CLASS_NAME, customObjectClassName);
                } catch (Throwable t) {
                }
            }
        }
        return (T) v8Value;
    }

    /**
     * Unregister custom object.
     *
     * @param customObjectClass the custom object class
     * @return true : success, false: failure
     * @since 0.9.12
     */
    public boolean unregisterCustomObject(Class<?> customObjectClass) {
        if (customObjectClass == null) {
            return false;
        }
        Lock writeLock = customObjectLock.writeLock();
        try {
            writeLock.lock();
            return customObjectMap.remove(customObjectClass.getName()) != null;
        } finally {
            writeLock.unlock();
        }
    }
}
