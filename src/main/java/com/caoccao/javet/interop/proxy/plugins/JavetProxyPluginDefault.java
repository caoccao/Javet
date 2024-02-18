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
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
    protected static final String BIG_INT_PROTOTYPE_TO_LOCALE_STRING = "BigInt.prototype.toLocaleString";
    protected static final String CHAR_AT = "charAt";
    protected static final String CODE_POINT_AT = "codePointAt";
    protected static final String DATE_PROTOTYPE_GET_DATE = "Date.prototype.getDate";
    protected static final String DATE_PROTOTYPE_GET_DAY = "Date.prototype.getDay";
    protected static final String DATE_PROTOTYPE_GET_FULL_YEAR = "Date.prototype.getFullYear";
    protected static final String DATE_PROTOTYPE_GET_HOURS = "Date.prototype.getHours";
    protected static final String DATE_PROTOTYPE_GET_MILLISECONDS = "Date.prototype.getMilliseconds";
    protected static final String DATE_PROTOTYPE_GET_MINUTES = "Date.prototype.getMinutes";
    protected static final String DATE_PROTOTYPE_GET_MONTH = "Date.prototype.getMonth";
    protected static final String DATE_PROTOTYPE_GET_SECONDS = "Date.prototype.getSeconds";
    protected static final String DATE_PROTOTYPE_GET_TIME = "Date.prototype.getTime";
    protected static final String DATE_PROTOTYPE_GET_TIMEZONE_OFFSET = "Date.prototype.getTimezoneOffset";
    protected static final String DATE_PROTOTYPE_GET_UTC_DATE = "Date.prototype.getUTCDate";
    protected static final String DATE_PROTOTYPE_GET_UTC_DAY = "Date.prototype.getUTCDay";
    protected static final String DATE_PROTOTYPE_GET_UTC_FULL_YEAR = "Date.prototype.getUTCFullYear";
    protected static final String DATE_PROTOTYPE_GET_UTC_HOURS = "Date.prototype.getUTCHours";
    protected static final String DATE_PROTOTYPE_GET_UTC_MILLISECONDS = "Date.prototype.getUTCMilliseconds";
    protected static final String DATE_PROTOTYPE_GET_UTC_MINUTES = "Date.prototype.getUTCMinutes";
    protected static final String DATE_PROTOTYPE_GET_UTC_MONTH = "Date.prototype.getUTCMonth";
    protected static final String DATE_PROTOTYPE_GET_UTC_SECONDS = "Date.prototype.getUTCSeconds";
    protected static final String DATE_PROTOTYPE_GET_YEAR = "Date.prototype.getYear";
    protected static final String DATE_PROTOTYPE_SET_DATE = "Date.prototype.setDate";
    protected static final String DATE_PROTOTYPE_SET_FULL_YEAR = "Date.prototype.setFullYear";
    protected static final String DATE_PROTOTYPE_SET_HOURS = "Date.prototype.setHours";
    protected static final String DATE_PROTOTYPE_SET_MILLISECONDS = "Date.prototype.setMilliseconds";
    protected static final String DATE_PROTOTYPE_SET_MINUTES = "Date.prototype.setMinutes";
    protected static final String DATE_PROTOTYPE_SET_MONTH = "Date.prototype.setMonth";
    protected static final String DATE_PROTOTYPE_SET_SECONDS = "Date.prototype.setSeconds";
    protected static final String DATE_PROTOTYPE_SET_TIME = "Date.prototype.setTime";
    protected static final String DATE_PROTOTYPE_SET_UTC_DATE = "Date.prototype.setUTCDate";
    protected static final String DATE_PROTOTYPE_SET_UTC_FULL_YEAR = "Date.prototype.setUTCFullYear";
    protected static final String DATE_PROTOTYPE_SET_UTC_HOURS = "Date.prototype.setUTCHours";
    protected static final String DATE_PROTOTYPE_SET_UTC_MILLISECONDS = "Date.prototype.setUTCMilliseconds";
    protected static final String DATE_PROTOTYPE_SET_UTC_MINUTES = "Date.prototype.setUTCMinutes";
    protected static final String DATE_PROTOTYPE_SET_UTC_MONTH = "Date.prototype.setUTCMonth";
    protected static final String DATE_PROTOTYPE_SET_UTC_SECONDS = "Date.prototype.setUTCSeconds";
    protected static final String DATE_PROTOTYPE_SET_YEAR = "Date.prototype.setYear";
    protected static final String DATE_PROTOTYPE_SYMBOL_TO_PRIMITIVE = "Date.prototype[Symbol.toPrimitive]";
    protected static final String DATE_PROTOTYPE_TO_DATE_STRING = "Date.prototype.toDateString";
    protected static final String DATE_PROTOTYPE_TO_ISOSTRING = "Date.prototype.toISOString";
    protected static final String DATE_PROTOTYPE_TO_JSON = "Date.prototype.toJSON";
    protected static final String DATE_PROTOTYPE_TO_LOCALE_DATE_STRING = "Date.prototype.toLocaleDateString";
    protected static final String DATE_PROTOTYPE_TO_LOCALE_STRING = "Date.prototype.toLocaleString";
    protected static final String DATE_PROTOTYPE_TO_LOCALE_TIME_STRING = "Date.prototype.toLocaleTimeString";
    protected static final String DATE_PROTOTYPE_TO_STRING = "Date.prototype.toString";
    protected static final String DATE_PROTOTYPE_TO_TIME_STRING = "Date.prototype.toTimeString";
    protected static final String DATE_PROTOTYPE_TO_UTC_STRING = "Date.prototype.toUTCString";
    protected static final String DATE_PROTOTYPE_VALUE_OF = "Date.prototype.valueOf";
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
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_ZONED_DATE_TIME =
            "Target object must be an instance of ZonedDateTime.";
    protected static final String GET_DATE = "getDate";
    protected static final String GET_DAY = "getDay";
    protected static final String GET_FULL_YEAR = "getFullYear";
    protected static final String GET_HOURS = "getHours";
    protected static final String GET_MILLISECONDS = "getMilliseconds";
    protected static final String GET_MINUTES = "getMinutes";
    protected static final String GET_MONTH = "getMonth";
    protected static final String GET_SECONDS = "getSeconds";
    protected static final String GET_TIME = "getTime";
    protected static final String GET_TIMEZONE_OFFSET = "getTimezoneOffset";
    protected static final String GET_UTC_DATE = "getUTCDate";
    protected static final String GET_UTC_DAY = "getUTCDay";
    protected static final String GET_UTC_FULL_YEAR = "getUTCFullYear";
    protected static final String GET_UTC_HOURS = "getUTCHours";
    protected static final String GET_UTC_MILLISECONDS = "getUTCMilliseconds";
    protected static final String GET_UTC_MINUTES = "getUTCMinutes";
    protected static final String GET_UTC_MONTH = "getUTCMonth";
    protected static final String GET_UTC_SECONDS = "getUTCSeconds";
    protected static final String GET_YEAR = "getYear";
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
    protected static final String SET_DATE = "setDate";
    protected static final String SET_FULL_YEAR = "setFullYear";
    protected static final String SET_HOURS = "setHours";
    protected static final String SET_MILLISECONDS = "setMilliseconds";
    protected static final String SET_MINUTES = "setMinutes";
    protected static final String SET_MONTH = "setMonth";
    protected static final String SET_SECONDS = "setSeconds";
    protected static final String SET_TIME = "setTime";
    protected static final String SET_UTC_DATE = "setUTCDate";
    protected static final String SET_UTC_FULL_YEAR = "setUTCFullYear";
    protected static final String SET_UTC_HOURS = "setUTCHours";
    protected static final String SET_UTC_MILLISECONDS = "setUTCMilliseconds";
    protected static final String SET_UTC_MINUTES = "setUTCMinutes";
    protected static final String SET_UTC_MONTH = "setUTCMonth";
    protected static final String SET_UTC_SECONDS = "setUTCSeconds";
    protected static final String SET_YEAR = "setYear";
    protected static final String SPLIT = "split";
    protected static final String STARTS_WITH = "startsWith";
    protected static final String SUBSTRING = "substring";
    protected static final Set<Class<?>> SUPPORTED_CLASSES = SimpleSet.of(
            BigInteger.class, Boolean.class, Byte.class, Character.class, Double.class,
            Float.class, Integer.class, Long.class, Short.class, String.class, ZonedDateTime.class);
    protected static final String TO_DATE_STRING = "toDateString";
    protected static final String TO_EXPONENTIAL = "toExponential";
    protected static final String TO_FIXED = "toFixed";
    protected static final String TO_ISO_STRING = "toISOString";
    protected static final String TO_LOCALE_DATE_STRING = "toLocaleDateString";
    protected static final String TO_LOCALE_STRING = "toLocaleString";
    protected static final String TO_LOCALE_TIME_STRING = "toLocaleTimeString";
    protected static final String TO_PRECISION = "toPrecision";
    protected static final String TO_TIME_STRING = "toTimeString";
    protected static final String TO_UTC_STRING = "toUTCString";
    protected static final String TRIM = "trim";
    private static final JavetProxyPluginDefault instance = new JavetProxyPluginDefault();

    public JavetProxyPluginDefault() {
        super();
        {
            // java.math.BigInteger
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(BIG_INT_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(BigInteger.class, SimpleSet.of(VALUE_OF));
            proxyGetByStringMap.put(BigInteger.class, polyfillFunctionMap);
            polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE, this::symbolToPrimitive);
            proxyGetBySymbolMap.put(BigInteger.class, polyfillFunctionMap);
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
            polyfillFunctionMap.put(TO_EXPONENTIAL, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_EXPONENTIAL, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_FIXED, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_FIXED, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_PRECISION, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_PRECISION, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(TO_EXPONENTIAL, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_EXPONENTIAL, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_FIXED, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_FIXED, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_PRECISION, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_PRECISION, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(TO_EXPONENTIAL, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_EXPONENTIAL, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_FIXED, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_FIXED, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_PRECISION, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_PRECISION, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(TO_EXPONENTIAL, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_EXPONENTIAL, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_FIXED, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_FIXED, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_PRECISION, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_PRECISION, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(BIG_INT_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(TO_EXPONENTIAL, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_EXPONENTIAL, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_FIXED, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_FIXED, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_PRECISION, (v8Runtime, targetObject) ->
                    callWithObjectConverter(NUMBER_PROTOTYPE_TO_PRECISION, v8Runtime, targetObject));
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
            polyfillFunctionMap.put(LENGTH, (v8Runtime, targetObject) ->
                    v8Runtime.createV8ValueInteger(((String) targetObject).length()));
            polyfillFunctionMap.put(TO_JSON, this::valueOf);
            polyfillFunctionMap.put(TO_STRING, this::valueOf);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyableMethodsMap.put(String.class, SimpleSet.of(
                    CHAR_AT, CODE_POINT_AT, ENDS_WITH, INDEX_OF, LAST_INDEX_OF,
                    LENGTH, REPEAT, REPLACE, REPLACE_ALL, SPLIT,
                    STARTS_WITH, SUBSTRING, TRIM, VALUE_OF, TO_STRING));
            proxyGetByStringMap.put(String.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    String.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueStringObject((String) targetObject));
        }
        {
            // java.time.ZonedDateTime
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(GET_DATE, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_DATE, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_DAY, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_DAY, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_FULL_YEAR, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_FULL_YEAR, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_HOURS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_HOURS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_MILLISECONDS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_MILLISECONDS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_MINUTES, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_MINUTES, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_MONTH, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_MONTH, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_SECONDS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_SECONDS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_TIME, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_TIME, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_TIMEZONE_OFFSET, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_TIMEZONE_OFFSET, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_DATE, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_DATE, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_DAY, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_DAY, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_FULL_YEAR, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_FULL_YEAR, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_HOURS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_HOURS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_MILLISECONDS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_MILLISECONDS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_MINUTES, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_MINUTES, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_MONTH, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_MONTH, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_UTC_SECONDS, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_UTC_SECONDS, v8Runtime, targetObject));
            polyfillFunctionMap.put(GET_YEAR, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_GET_YEAR, v8Runtime, targetObject));
            polyfillFunctionMap.put(SET_DATE, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_DATE, v8Runtime));
            polyfillFunctionMap.put(SET_FULL_YEAR, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_FULL_YEAR, v8Runtime));
            polyfillFunctionMap.put(SET_HOURS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_HOURS, v8Runtime));
            polyfillFunctionMap.put(SET_MILLISECONDS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_MILLISECONDS, v8Runtime));
            polyfillFunctionMap.put(SET_MINUTES, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_MINUTES, v8Runtime));
            polyfillFunctionMap.put(SET_MONTH, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_MONTH, v8Runtime));
            polyfillFunctionMap.put(SET_SECONDS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_SECONDS, v8Runtime));
            polyfillFunctionMap.put(SET_TIME, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_TIME, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_DATE, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_DATE, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_FULL_YEAR, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_FULL_YEAR, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_HOURS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_HOURS, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_MILLISECONDS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_MILLISECONDS, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_MINUTES, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_MINUTES, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_MONTH, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_MONTH, v8Runtime));
            polyfillFunctionMap.put(SET_UTC_SECONDS, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_UTC_SECONDS, v8Runtime));
            polyfillFunctionMap.put(SET_YEAR, (v8Runtime, targetObject) ->
                    typeErrorFunctionIsNotSupported(DATE_PROTOTYPE_SET_YEAR, v8Runtime));
            polyfillFunctionMap.put(TO_DATE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_DATE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_ISO_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_ISOSTRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_JSON, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_JSON, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_LOCALE_DATE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_LOCALE_DATE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_LOCALE_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_LOCALE_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_LOCALE_TIME_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_LOCALE_TIME_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_TIME_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_TIME_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(TO_UTC_STRING, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_TO_UTC_STRING, v8Runtime, targetObject));
            polyfillFunctionMap.put(VALUE_OF, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_VALUE_OF, v8Runtime, targetObject));
            proxyableMethodsMap.put(ZonedDateTime.class, SimpleSet.of(
                    VALUE_OF, TO_STRING));
            proxyGetByStringMap.put(ZonedDateTime.class, polyfillFunctionMap);
            polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE, (v8Runtime, targetObject) ->
                    callWithObjectConverter(DATE_PROTOTYPE_SYMBOL_TO_PRIMITIVE, v8Runtime, targetObject));
            proxyGetBySymbolMap.put(ZonedDateTime.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    ZonedDateTime.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueZonedDateTime((ZonedDateTime) targetObject));
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
    public boolean isOwnKeysSupported(Class<?> targetClass) {
        return !SUPPORTED_CLASSES.contains(targetClass);
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null;
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
