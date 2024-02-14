/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy.plugins;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The type Javet proxy plugin primitive.
 *
 * @since 3.0.4
 */
public class JavetProxyPluginDefault extends BaseJavetProxyPluginMultiple {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = Object.class.getName();
    protected static final String CHAR_AT = "charAt";
    protected static final String CODE_POINT_AT = "codePointAt";
    protected static final String ENDS_WITH = "endsWith";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BIG_INTEGER =
            "Target object must be an instance of BigInteger.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BOOLEAN =
            "Target object must be an instance of Boolean.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BYTE =
            "Target object must be an instance of Byte.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_CHARACTER =
            "Target object must be an instance of Character.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_DOUBLE =
            "Target object must be an instance of Double.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_FLOAT =
            "Target object must be an instance of Float.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_INTEGER =
            "Target object must be an instance of Integer.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LONG =
            "Target object must be an instance of Long.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SHORT =
            "Target object must be an instance of Short.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING =
            "Target object must be an instance of String.";
    protected static final String INDEX_OF = "indexOf";
    protected static final String LAST_INDEX_OF = "lastIndexOf";
    protected static final String LENGTH = "length";
    protected static final String NUMBER_PROTOTYPE_TO_EXPONENTIAL = "Number.prototype.toExponential";
    protected static final String NUMBER_PROTOTYPE_TO_FIXED = "Number.prototype.toFixed";
    protected static final String NUMBER_PROTOTYPE_TO_LOCALE_STRING = "Number.prototype.toLocaleString";
    protected static final String NUMBER_PROTOTYPE_TO_PRECISION = "Number.prototype.toPrecision";
    protected static final String REPEAT = "repeat";
    protected static final String REPLACE = "replace";
    protected static final String REPLACE_ALL = "replaceAll";
    protected static final String SPLIT = "split";
    protected static final String STARTS_WITH = "startsWith";
    protected static final String SUBSTRING = "substring";
    protected static final String TO_EXPONENTIAL = "toExponential";
    protected static final String TO_FIXED = "toFixed";
    protected static final String TO_LOCALE_STRING = "toLocaleString";
    protected static final String TO_PRECISION = "toPrecision";
    protected static final String TRIM = "trim";
    private static final JavetProxyPluginDefault instance = new JavetProxyPluginDefault();

    public JavetProxyPluginDefault() {
        super();
        {
            // java.math.BigInteger
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(BigInteger.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(BigInteger.class, polyfillFunctionMap);
        }
        {
            // java.lang.Boolean
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Boolean.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Boolean.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Boolean.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueBooleanObject((Boolean) targetObject));
        }
        {
            // java.lang.Byte
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_EXPONENTIAL, this::toExponential);
            polyfillFunctionMap.put(TO_FIXED, this::toFixed);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, this::toLocaleString);
            polyfillFunctionMap.put(TO_PRECISION, this::toPrecision);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Byte.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Byte.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Byte.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Byte) targetObject));
        }
        {
            // java.lang.Character
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Character.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Character.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Character.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueStringObject(String.valueOf(targetObject)));
        }
        {
            // java.lang.Double
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_EXPONENTIAL, this::toExponential);
            polyfillFunctionMap.put(TO_FIXED, this::toFixed);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, this::toLocaleString);
            polyfillFunctionMap.put(TO_PRECISION, this::toPrecision);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Double.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Double.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Double.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueDoubleObject((Double) targetObject));
        }
        {
            // java.lang.Float
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_EXPONENTIAL, this::toExponential);
            polyfillFunctionMap.put(TO_FIXED, this::toFixed);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, this::toLocaleString);
            polyfillFunctionMap.put(TO_PRECISION, this::toPrecision);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Float.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Float.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Float.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueDoubleObject((Float) targetObject));
        }
        {
            // java.lang.Integer
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_EXPONENTIAL, this::toExponential);
            polyfillFunctionMap.put(TO_FIXED, this::toFixed);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, this::toLocaleString);
            polyfillFunctionMap.put(TO_PRECISION, this::toPrecision);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Integer.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Integer.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Integer.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Integer) targetObject));
        }
        {
            // java.lang.Long
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Long.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Long.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Long.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueLongObject((Long) targetObject));
        }
        {
            // java.lang.Short
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_EXPONENTIAL, this::toExponential);
            polyfillFunctionMap.put(TO_FIXED, this::toFixed);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, this::toLocaleString);
            polyfillFunctionMap.put(TO_PRECISION, this::toPrecision);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(Short.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(Short.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Short.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Short) targetObject));
        }
        {
            // java.lang.String
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(LENGTH, this::stringLength);
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(String.class, SimpleSet.of(
                    CHAR_AT, CODE_POINT_AT, ENDS_WITH, INDEX_OF, LAST_INDEX_OF,
                    LENGTH, REPEAT, REPLACE, REPLACE_ALL, SPLIT,
                    STARTS_WITH, SUBSTRING, TRIM, VALUE_OF));
            proxyGetByStringMap.put(String.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    String.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueStringObject((String) targetObject));
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginDefault getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null;
    }

    /**
     * Polyfill String: length.
     * The length data property of a String value contains the length of the string in UTF-16 code units.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value stringLength(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(string.length());
    }

    /**
     * Polyfill Number.prototype.toExponential().
     * The toExponential() method of Number values returns a string representing this number in exponential notation.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toExponential(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_EXPONENTIAL, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                            NUMBER_PROTOTYPE_TO_EXPONENTIAL).execute()) {
                        return v8ValueFunction.call(OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject), v8Values);
                    }
                }));
    }

    /**
     * Polyfill Number.prototype.toFixed().
     * The toFixed() method of Number values formats this number using fixed-point notation.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toFixed(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_FIXED, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                            NUMBER_PROTOTYPE_TO_FIXED).execute()) {
                        return v8ValueFunction.call(OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject), v8Values);
                    }
                }));
    }

    /**
     * Polyfill Number.prototype.toLocaleString().
     * The toLocaleString() method of Number values returns a string with a language-sensitive representation of
     * this number. In implementations with Intl.NumberFormat API support, this method simply calls Intl.NumberFormat.
     * <p>
     * Every time toLocaleString is called, it has to perform a search in a big database of localization strings,
     * which is potentially inefficient. When the method is called many times with the same arguments,
     * it is better to create a Intl.NumberFormat object and use its format() method, because a NumberFormat object
     * remembers the arguments passed to it and may decide to cache a slice of the database,
     * so future format calls can search for localization strings within a more constrained context.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toLocaleString(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_LOCALE_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                            NUMBER_PROTOTYPE_TO_LOCALE_STRING).execute()) {
                        return v8ValueFunction.call(OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject), v8Values);
                    }
                }));
    }

    /**
     * Polyfill Number.prototype.toPrecision().
     * The toPrecision() method of Number values returns a string representing this number to the specified precision.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toPrecision(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_PRECISION, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8ValueFunction v8ValueFunction = v8Runtime.getExecutor(
                            NUMBER_PROTOTYPE_TO_PRECISION).execute()) {
                        return v8ValueFunction.call(OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject), v8Values);
                    }
                }));
    }

    /**
     * Polyfill valueOf().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value valueOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                VALUE_OF, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject)));
    }
}
