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

package com.caoccao.javet.utils;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.V8ErrorTemplate;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.*;

import java.util.Map;
import java.util.StringJoiner;

/**
 * The type V8 value utils.
 *
 * @since 0.7.1
 */
public final class V8ValueUtils {

    private V8ValueUtils() {
    }

    /**
     * Call asInt() by V8 value array and index.
     *
     * @param v8Values the V8 values
     * @param index    the index
     * @return the int value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static int asInt(V8Value[] v8Values, int index) throws JavetException {
        return asInt(v8Values, index, 0);
    }

    /**
     * Call asInt() by V8 value array and index.
     *
     * @param v8Values     the V8 values
     * @param index        the index
     * @param defaultValue the default value
     * @return the int value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static int asInt(V8Value[] v8Values, int index, int defaultValue) throws JavetException {
        if (v8Values != null && index >= 0 && index < v8Values.length && v8Values[index] != null) {
            return v8Values[index].asInt();
        }
        return defaultValue;
    }

    /**
     * Call toString() by V8 value array and index.
     *
     * @param v8Values the V8 values
     * @param index    the index
     * @return the string
     * @since 3.0.4
     */
    public static String asString(V8Value[] v8Values, int index) {
        return asString(v8Values, index, V8ValueUndefined.UNDEFINED);
    }

    /**
     * Call toString() by V8 value array and index.
     *
     * @param v8Values     the V8 values
     * @param index        the index
     * @param defaultValue the default value
     * @return the string
     * @since 3.0.4
     */
    public static String asString(V8Value[] v8Values, int index, String defaultValue) {
        if (v8Values != null && index >= 0 && index < v8Values.length && v8Values[index] != null) {
            return v8Values[index].toString();
        }
        return defaultValue;
    }

    /**
     * Call toString() by V8 value.
     *
     * @param v8Value the V8 value
     * @return the string
     * @since 3.0.4
     */
    public static String asString(V8Value v8Value) {
        return asString(v8Value, V8ValueUndefined.UNDEFINED);
    }

    /**
     * Call toString() by V8 value.
     *
     * @param v8Value      the V8 value
     * @param defaultValue the default value
     * @return the string
     * @since 3.0.4
     */
    public static String asString(V8Value v8Value, String defaultValue) {
        return v8Value == null ? defaultValue : v8Value.toString();
    }

    /**
     * As V8Value by V8 value array and index.
     *
     * @param v8Values the V8 values
     * @param index    the index
     * @return the V8 value
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8Value asV8Value(V8Value[] v8Values, int index) {
        if (v8Values != null && index >= 0 && index < v8Values.length) {
            return v8Values[index];
        }
        return null;
    }

    /**
     * Get V8ValueFunction by V8 value array and index.
     *
     * @param v8Values the V8 values
     * @param index    the index
     * @return the V8 value function
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueFunction asV8ValueFunction(V8Value[] v8Values, int index) {
        if (ArrayUtils.isNotEmpty(v8Values) && index >= 0 && index < v8Values.length) {
            V8Value v8Value = v8Values[index];
            if (v8Value instanceof V8ValueFunction) {
                return (V8ValueFunction) v8Value;
            }
        }
        return null;
    }

    /**
     * Get V8ValueFunction by V8 value array and index.
     * If the V8ValueFunction is invalid, throw a TypeError.
     *
     * @param v8Runtime the V8 runtime
     * @param v8Values  the V8 values
     * @param index     the index
     * @return the V8 value function
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueFunction asV8ValueFunctionWithError(V8Runtime v8Runtime, V8Value[] v8Values, int index) {
        V8Value v8Value = null;
        if (ArrayUtils.isNotEmpty(v8Values) && index >= 0 && index < v8Values.length) {
            v8Value = v8Values[index];
            if (v8Value instanceof V8ValueFunction) {
                return (V8ValueFunction) v8Value;
            }
        }
        v8Runtime.throwError(V8ValueErrorType.TypeError, V8ErrorTemplate.typeErrorValueIsNotAFunction(v8Value));
        return null;
    }

    /**
     * As V8ValueObject by V8 value array and index.
     *
     * @param v8Values the V8 values
     * @param index    the index
     * @return the V8 value object
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueObject asV8ValueObject(V8Value[] v8Values, int index) {
        if (v8Values != null && index >= 0 && index < v8Values.length
                && v8Values[index] instanceof V8ValueObject) {
            return (V8ValueObject) v8Values[index];
        }
        return null;
    }

    /**
     * Concat string.
     *
     * @param delimiter the delimiter
     * @param v8Values  the V8 values
     * @return the string
     * @since 0.7.1
     */
    public static String concat(String delimiter, V8Value... v8Values) {
        if (ArrayUtils.isEmpty(v8Values)) {
            return StringUtils.EMPTY;
        }
        if (delimiter == null) {
            delimiter = StringUtils.EMPTY;
        }
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        for (V8Value v8Value : v8Values) {
            stringJoiner.add(asString(v8Value));
        }
        return stringJoiner.toString();
    }

    /**
     * Convert to virtual objects.
     *
     * @param v8Values the V8 values
     * @return the javet virtual objects
     * @since 0.9.10
     */
    public static JavetVirtualObject[] convertToVirtualObjects(V8Value... v8Values) {
        final int length = v8Values == null ? 0 : v8Values.length;
        JavetVirtualObject[] javetVirtualObjects = new JavetVirtualObject[length];
        for (int i = 0; i < length; ++i) {
            javetVirtualObjects[i] = new JavetVirtualObject(v8Values[i]);
        }
        return javetVirtualObjects;
    }

    /**
     * Create V8 value array from object array.
     *
     * @param v8Runtime the V8 runtime
     * @param objects   the objects
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueArray createV8ValueArray(V8Runtime v8Runtime, Object... objects) throws JavetException {
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
            if (ArrayUtils.isNotEmpty(objects)) {
                v8ValueArray.push(objects);
            }
            v8Scope.setEscapable();
            return v8ValueArray;
        }
    }

    /**
     * Create V8 value map from map.
     *
     * @param v8Runtime the V8 runtime
     * @param map       the map
     * @return the V8 value map
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueMap createV8ValueMap(V8Runtime v8Runtime, Map<?, ?> map) throws JavetException {
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueMap v8ValueMap = v8Scope.createV8ValueMap();
            if (map != null && !map.isEmpty()) {
                Object[] keyAndValuePairs = new Object[map.size() << 1];
                int index = 0;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    keyAndValuePairs[index++] = entry.getKey();
                    keyAndValuePairs[index++] = entry.getValue();
                }
                v8ValueMap.set(keyAndValuePairs);
            }
            v8Scope.setEscapable();
            return v8ValueMap;
        }
    }

    /**
     * Create V8 value object from object array.
     *
     * @param v8Runtime the V8 runtime
     * @param objects   the objects
     * @return the V8 value object
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueObject createV8ValueObject(V8Runtime v8Runtime, Object... objects) throws JavetException {
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueObject v8ValueObject = v8Scope.createV8ValueObject();
            if (ArrayUtils.isNotEmpty(objects)) {
                v8ValueObject.set(objects);
            }
            v8Scope.setEscapable();
            return v8ValueObject;
        }
    }

    /**
     * Create V8 value set from object array.
     *
     * @param v8Runtime the V8 runtime
     * @param objects   the objects
     * @return the V8 value set
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    public static V8ValueSet createV8ValueSet(V8Runtime v8Runtime, Object... objects) throws JavetException {
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueSet v8ValueSet = v8Scope.createV8ValueSet();
            if (ArrayUtils.isNotEmpty(objects)) {
                for (Object object : objects) {
                    v8ValueSet.add(object);
                }
            }
            v8Scope.setEscapable();
            return v8ValueSet;
        }
    }

    /**
     * Convert V8 values to objects.
     *
     * @param v8Runtime the V8 runtime
     * @param v8Values  the V8 values
     * @return the array
     * @throws JavetException the javet exception
     * @since 3.0.3
     */
    public static Object[] toArray(V8Runtime v8Runtime, V8Value... v8Values) throws JavetException {
        final int length = v8Values == null ? 0 : v8Values.length;
        Object[] objects = new Object[length];
        for (int i = 0; i < length; ++i) {
            objects[i] = v8Runtime.toObject(v8Values[i]);
        }
        return objects;
    }

    /**
     * Trim anonymous function source code.
     *
     * @param sourceCode the source code
     * @return the trimmed source code
     * @since 1.0.0
     */
    public static String trimAnonymousFunction(String sourceCode) {
        if (sourceCode != null) {
            final int length = sourceCode.length();
            if (length > 0) {
                int endPosition = length;
                boolean completed = false;
                while (!completed && endPosition > 0) {
                    switch (sourceCode.charAt(endPosition - 1)) {
                        case ' ':
                        case '\n':
                        case '\r':
                        case '\t':
                        case ';':
                            endPosition--;
                            break;
                        default:
                            completed = true;
                            break;
                    }
                }
                if (endPosition == length) {
                    return sourceCode;
                } else {
                    return sourceCode.substring(0, endPosition);
                }
            }
        }
        return null;
    }
}
