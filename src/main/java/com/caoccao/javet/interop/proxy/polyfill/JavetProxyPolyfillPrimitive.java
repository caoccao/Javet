/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy.polyfill;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The type Javet proxy polyfill primitive.
 *
 * @since 3.0.4
 */
public final class JavetProxyPolyfillPrimitive {
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BIG_INTEGER =
            "Target object must be an instance of BigInteger.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BOOLEAN =
            "Target object must be an instance of Boolean.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BYTE =
            "Target object must be an instance of Byte.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_CHARACTER =
            "Target object must be an instance of Character.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_DOUBLE =
            "Target object must be an instance of Double.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_FLOAT =
            "Target object must be an instance of Float.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_INTEGER =
            "Target object must be an instance of Integer.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LONG =
            "Target object must be an instance of Long.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SHORT =
            "Target object must be an instance of Short.";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING =
            "Target object must be an instance of String.";
    private static final String TO_JSON = "toJSON";
    private static final Map<Class<?>, Map<String, IJavetProxyPolyfillFunction<?, ?>>> primitiveFunctionMap;

    static {
        primitiveFunctionMap = new HashMap<>();
        {
            // java.math.BigInteger
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::bigIntegerToJSON);
            primitiveFunctionMap.put(BigInteger.class, polyfillFunctionMap);
        }
        {
            // java.lang.Boolean
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::booleanToJSON);
            primitiveFunctionMap.put(Boolean.class, polyfillFunctionMap);
        }
        {
            // java.lang.Byte
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::byteToJSON);
            primitiveFunctionMap.put(Byte.class, polyfillFunctionMap);
        }
        {
            // java.lang.Character
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::characterToJSON);
            primitiveFunctionMap.put(Character.class, polyfillFunctionMap);
        }
        {
            // java.lang.Double
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::doubleToJSON);
            primitiveFunctionMap.put(Double.class, polyfillFunctionMap);
        }
        {
            // java.lang.Float
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::floatToJSON);
            primitiveFunctionMap.put(Float.class, polyfillFunctionMap);
        }
        {
            // java.lang.Integer
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::integerToJSON);
            primitiveFunctionMap.put(Integer.class, polyfillFunctionMap);
        }
        {
            // java.lang.Long
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::longToJSON);
            primitiveFunctionMap.put(Long.class, polyfillFunctionMap);
        }
        {
            // java.lang.Short
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::shortToJSON);
            primitiveFunctionMap.put(Short.class, polyfillFunctionMap);
        }
        {
            // java.lang.String
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, JavetProxyPolyfillPrimitive::stringToJSON);
            primitiveFunctionMap.put(String.class, polyfillFunctionMap);
        }
    }

    private JavetProxyPolyfillPrimitive() {
    }

    /**
     * Polyfill BigInteger.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value bigIntegerToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof BigInteger : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BIG_INTEGER;
        final BigInteger value = (BigInteger) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueBigInteger(value)));
    }

    /**
     * Polyfill Boolean.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value booleanToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Boolean : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BOOLEAN;
        final Boolean value = (Boolean) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueBoolean(value)));
    }

    /**
     * Polyfill Byte.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value byteToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Byte : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BYTE;
        final Byte value = (Byte) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill Character.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value characterToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Character : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_CHARACTER;
        final Character value = (Character) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueString(value.toString())));
    }

    /**
     * Polyfill Double.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value doubleToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Double : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_DOUBLE;
        final Double value = (Double) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueDouble(value)));
    }

    /**
     * Polyfill Float.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value floatToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Float : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_FLOAT;
        final Float value = (Float) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueDouble(value.doubleValue())));
    }

    /**
     * Gets function.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the function
     * @since 3.0.4
     */
    public static IJavetProxyPolyfillFunction<?, ?> getFunction(Class<?> clazz, String name) {
        return Optional.ofNullable(primitiveFunctionMap.get(clazz))
                .map(map -> map.get(name))
                .orElse(null);
    }

    /**
     * Polyfill Integer.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value integerToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Integer : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_INTEGER;
        final Integer value = (Integer) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueInteger(value)));
    }

    /**
     * Polyfill Long.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value longToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Long : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LONG;
        final Long value = (Long) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueLong(value)));
    }

    /**
     * Polyfill Short.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value shortToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Short : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SHORT;
        final Short value = (Short) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill String.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value stringToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String value = (String) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueString(value)));
    }
}
