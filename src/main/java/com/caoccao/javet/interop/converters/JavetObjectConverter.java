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
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetEntityFunction;
import com.caoccao.javet.interfaces.IJavetEntityMap;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.proxy.IJavetProxyHandler;
import com.caoccao.javet.interop.proxy.JavetUniversalProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;

import java.util.*;

/**
 * The type Javet object converter.
 *
 * @since 0.7.2
 */
@SuppressWarnings("unchecked")
public class JavetObjectConverter extends JavetPrimitiveConverter {

    /**
     * The constant PROPERTY_CONSTRUCTOR.
     *
     * @since 0.7.2
     */
    protected static final String PROPERTY_CONSTRUCTOR = "constructor";
    /**
     * The constant PROPERTY_NAME.
     *
     * @since 0.7.2
     */
    protected static final String PROPERTY_NAME = "name";

    /**
     * The constant PROXY_TARGET.
     *
     * @since 0.9.6
     */
    protected static final String PROXY_TARGET = "target";

    /**
     * Instantiates a new Javet object converter.
     *
     * @since 0.7.1
     */
    public JavetObjectConverter() {
        super();
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
     * Create entity map map.
     *
     * @return the map
     * @since 0.7.2
     */
    protected Map<String, Object> createEntityMap() {
        return new JavetEntityMap();
    }

    @Override
    protected Object toObject(V8Value v8Value, final int depth) throws JavetException {
        Object returnObject = super.toObject(v8Value, depth);
        if (!(returnObject instanceof V8Value)) {
            return returnObject;
        }
        if (v8Value instanceof V8ValueArray) {
            V8ValueArray v8ValueArray = (V8ValueArray) v8Value;
            List<Object> list = new ArrayList<>();
            v8ValueArray.forEach(value -> list.add(toObject(value, depth + 1)));
            return list;
        } else if (v8Value instanceof V8ValueSet) {
            V8ValueSet v8ValueSet = (V8ValueSet) v8Value;
            HashSet<Object> set = new HashSet<>();
            v8ValueSet.forEach(key -> set.add(toObject(key, depth + 1)));
            return set;
        } else if (v8Value instanceof V8ValueMap) {
            V8ValueMap v8ValueMap = (V8ValueMap) v8Value;
            Map<String, Object> map = createEntityMap();
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
            IJavetEntityFunction javetEntityFunction = createEntityFunction();
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
        } else if (v8Value instanceof V8ValueObject) {
            if (v8Value instanceof V8ValueProxy) {
                V8ValueProxy v8ValueProxy = (V8ValueProxy) v8Value;
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    Long handle = iV8ValueObjectHandler.getLong(PROXY_TARGET);
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
            Map<String, Object> map = new HashMap<>();
            v8ValueObject.forEach((V8Value key, V8Value value) -> {
                String keyString = key.toString();
                if (PROPERTY_CONSTRUCTOR.equals(keyString)) {
                    map.put(PROPERTY_CONSTRUCTOR, ((V8ValueObject) value).getString(PROPERTY_NAME));
                } else {
                    Object object = toObject(value, depth + 1);
                    if (!(config.isSkipFunctionInObject() && object instanceof JavetEntityFunction)) {
                        map.put(keyString, object);
                    }
                }
            });
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
            V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap();
            Map<?, ?> mapObject = (Map<?, ?>) object;
            for (Object key : mapObject.keySet()) {
                try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key), depth + 1)) {
                    String childStringKey = key instanceof String ? (String) key : key.toString();
                    v8ValueMap.set(childStringKey, childV8Value);
                }
            }
            v8Value = v8ValueMap;
        } else if (object instanceof Map) {
            if (config.isProxyMapEnabled()) {
                V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy();
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    JavetUniversalProxyHandler<Map> javetUniversalProxyHandler =
                            new JavetUniversalProxyHandler<>(v8Runtime, (Map) object);
                    List<JavetCallbackContext> javetCallbackContexts =
                            iV8ValueObjectHandler.bind(javetUniversalProxyHandler);
                    iV8ValueObjectHandler.set(PROXY_TARGET, javetCallbackContexts.get(0).getHandle());
                }
                v8Value = v8ValueProxy;
            } else {
                V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
                Map<?, ?> mapObject = (Map<?, ?>) object;
                for (Object key : mapObject.keySet()) {
                    try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key), depth + 1)) {
                        String childStringKey = key instanceof String ? (String) key : key.toString();
                        v8ValueObject.set(childStringKey, childV8Value);
                    }
                }
                v8Value = v8ValueObject;
            }
        } else if (object instanceof Set) {
            if (config.isProxySetEnabled()) {
                V8ValueProxy v8ValueProxy = v8Runtime.createV8ValueProxy();
                try (IV8ValueObject iV8ValueObjectHandler = v8ValueProxy.getHandler()) {
                    JavetUniversalProxyHandler<Set> javetUniversalProxyHandler =
                            new JavetUniversalProxyHandler<>(v8Runtime, (Set) object);
                    List<JavetCallbackContext> javetCallbackContexts =
                            iV8ValueObjectHandler.bind(javetUniversalProxyHandler);
                    iV8ValueObjectHandler.set(PROXY_TARGET, javetCallbackContexts.get(0).getHandle());
                }
                v8Value = v8ValueProxy;
            } else {
                V8ValueSet v8ValueSet = v8Runtime.createV8ValueSet();
                Set<?> setObject = (Set<?>) object;
                for (Object item : setObject) {
                    try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                        v8ValueSet.add(childV8Value);
                    }
                }
                v8Value = v8ValueSet;
            }
        } else if (object instanceof Collection) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (Object item : (Collection<?>) object) {
                try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                    v8ValueArray.push(childV8Value);
                }
            }
            v8Value = v8ValueArray;
        } else if (object instanceof IJavetEntityFunction) {
            IJavetEntityFunction javetEntityFunction = (IJavetEntityFunction) object;
            String sourceCode = javetEntityFunction.getJSFunctionType().isUserDefined() ?
                    javetEntityFunction.getSourceCode() : null;
            if (sourceCode == null || sourceCode.length() == 0) {
                v8Value = v8Runtime.createV8ValueNull();
            } else {
                v8Value = v8Runtime.getExecutor(sourceCode).execute();
            }
        } else if (object instanceof boolean[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (boolean item : (boolean[]) object) {
                v8ValueArray.push(v8Runtime.createV8ValueBoolean(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof byte[]) {
            byte[] bytes = (byte[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.Int8Array, bytes.length);
            v8ValueTypedArray.fromBytes(bytes);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof double[]) {
            double[] doubles = (double[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.Float64Array, doubles.length);
            v8ValueTypedArray.fromDoubles(doubles);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof float[]) {
            float[] floats = (float[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.Float32Array, floats.length);
            v8ValueTypedArray.fromFloats(floats);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof int[]) {
            int[] integers = (int[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.Int32Array, integers.length);
            v8ValueTypedArray.fromIntegers(integers);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof long[]) {
            long[] longs = (long[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.BigInt64Array, longs.length);
            v8ValueTypedArray.fromLongs(longs);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof short[]) {
            short[] shorts = (short[]) object;
            V8ValueTypedArray v8ValueTypedArray = v8Runtime.createV8ValueTypedArray(
                    V8ValueReferenceType.Int16Array, shorts.length);
            v8ValueTypedArray.fromShorts(shorts);
            v8Value = v8ValueTypedArray;
        } else if (object instanceof String[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (String item : (String[]) object) {
                v8ValueArray.push(v8Runtime.createV8ValueString(item));
            }
            v8Value = v8ValueArray;
        } else if (object.getClass().isArray()) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (Object item : (Object[]) object) {
                try (V8Value childV8Value = toV8Value(v8Runtime, item, depth + 1)) {
                    v8ValueArray.push(childV8Value);
                }
            }
            v8Value = v8ValueArray;
        }
        return (T) v8Runtime.decorateV8Value(v8Value);
    }
}
