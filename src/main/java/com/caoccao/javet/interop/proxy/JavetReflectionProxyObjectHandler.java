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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Javet reflection proxy object handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetReflectionProxyObjectHandler<T, E extends Exception>
        extends BaseJavetReflectionProxyHandler<T, E> {
    /**
     * The constant POLYFILL_LIST_AT.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_AT = "at";
    /**
     * The constant POLYFILL_LIST_CONCAT.
     *
     * @since 3.0.4
     */
    protected static final String POLYFILL_LIST_CONCAT = "concat";
    /**
     * The constant POLYFILL_LIST_EVERY.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_EVERY = "every";
    /**
     * The constant POLYFILL_LIST_FILL.
     *
     * @since 3.0.4
     */
    protected static final String POLYFILL_LIST_FILL = "fill";
    /**
     * The constant POLYFILL_LIST_FILTER.
     *
     * @since 3.0.4
     */
    protected static final String POLYFILL_LIST_FILTER = "filter";
    /**
     * The constant POLYFILL_LIST_INCLUDES.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_INCLUDES = "includes";
    /**
     * The constant POLYFILL_LIST_MAP.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_MAP = "map";
    /**
     * The constant POLYFILL_LIST_POP.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_POP = "pop";
    /**
     * The constant POLYFILL_LIST_PUSH.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_PUSH = "push";
    /**
     * The constant POLYFILL_LIST_REVERSE.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_REVERSE = "reverse";
    /**
     * The constant POLYFILL_LIST_SHIFT.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_SHIFT = "shift";
    /**
     * The constant POLYFILL_LIST_SOME.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_SOME = "some";
    /**
     * The constant POLYFILL_LIST_TO_REVERSED.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_TO_REVERSED = "toReversed";
    /**
     * The constant POLYFILL_LIST_UNSHIFT.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_UNSHIFT = "unshift";
    /**
     * The constant POLYFILL_LIST_WITH.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_LIST_WITH = "with";
    /**
     * The constant POLYFILL_SET_DELETE.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_SET_DELETE = "delete";
    /**
     * The constant POLYFILL_SET_HAS.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_SET_HAS = "has";
    /**
     * The constant POLYFILL_SHARED_ENTRIES.
     *
     * @since 3.0.4
     */
    protected static final String POLYFILL_SHARED_ENTRIES = "entries";
    /**
     * The constant POLYFILL_SHARED_KEYS.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_SHARED_KEYS = "keys";
    /**
     * The constant POLYFILL_SHARED_LENGTH.
     *
     * @since 1.0.6
     */
    protected static final String POLYFILL_SHARED_LENGTH = "length";
    /**
     * The constant POLYFILL_SHARED_TO_JSON.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_SHARED_TO_JSON = "toJSON";
    /**
     * The constant POLYFILL_SHARED_VALUES.
     *
     * @since 3.0.3
     */
    protected static final String POLYFILL_SHARED_VALUES = "values";
    /**
     * The constant classDescriptorMap.
     *
     * @since 1.1.7
     */
    protected static final ThreadSafeMap<Class<?>, ClassDescriptor> classDescriptorMap = new ThreadSafeMap<>();
    /**
     * The constant polyfillClassFunctionMap.
     *
     * @since 3.0.4
     */
    protected static final Map<Class<?>, Map<String, IJavetProxyPolyfillFunction<?, ?>>> polyfillClassFunctionMap;
    /**
     * The constant polyfillListFunctionMap.
     *
     * @since 3.0.3
     */
    protected static final Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillListFunctionMap;
    /**
     * The constant polyfillMapFunctionMap.
     *
     * @since 3.0.3
     */
    protected static final Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillMapFunctionMap;
    /**
     * The constant polyfillSetFunctionMap.
     *
     * @since 3.0.3
     */
    protected static final Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillSetFunctionMap;

    static {
        polyfillListFunctionMap = new HashMap<>();
        polyfillListFunctionMap.put(POLYFILL_LIST_AT, JavetReflectionProxyObjectHandler::polyfillListAt);
        polyfillListFunctionMap.put(POLYFILL_LIST_CONCAT, JavetReflectionProxyObjectHandler::polyfillListConcat);
        polyfillListFunctionMap.put(POLYFILL_SHARED_ENTRIES, JavetReflectionProxyObjectHandler::polyfillListEntries);
        polyfillListFunctionMap.put(POLYFILL_LIST_EVERY, JavetReflectionProxyObjectHandler::polyfillListEvery);
        polyfillListFunctionMap.put(POLYFILL_LIST_FILL, JavetReflectionProxyObjectHandler::polyfillListFill);
        polyfillListFunctionMap.put(POLYFILL_LIST_FILTER, JavetReflectionProxyObjectHandler::polyfillListFilter);
        polyfillListFunctionMap.put(POLYFILL_LIST_INCLUDES, JavetReflectionProxyObjectHandler::polyfillListIncludes);
        polyfillListFunctionMap.put(POLYFILL_SHARED_KEYS, JavetReflectionProxyObjectHandler::polyfillListKeys);
        polyfillListFunctionMap.put(POLYFILL_SHARED_LENGTH, JavetReflectionProxyObjectHandler::polyfillListLength);
        polyfillListFunctionMap.put(POLYFILL_LIST_MAP, JavetReflectionProxyObjectHandler::polyfillListMap);
        polyfillListFunctionMap.put(POLYFILL_LIST_POP, JavetReflectionProxyObjectHandler::polyfillListPop);
        polyfillListFunctionMap.put(POLYFILL_LIST_PUSH, JavetReflectionProxyObjectHandler::polyfillListPush);
        polyfillListFunctionMap.put(POLYFILL_LIST_REVERSE, JavetReflectionProxyObjectHandler::polyfillListReverse);
        polyfillListFunctionMap.put(POLYFILL_LIST_SHIFT, JavetReflectionProxyObjectHandler::polyfillListShift);
        polyfillListFunctionMap.put(POLYFILL_LIST_SOME, JavetReflectionProxyObjectHandler::polyfillListSome);
        polyfillListFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillListToJSON);
        polyfillListFunctionMap.put(POLYFILL_LIST_TO_REVERSED, JavetReflectionProxyObjectHandler::polyfillListToReversed);
        polyfillListFunctionMap.put(POLYFILL_LIST_UNSHIFT, JavetReflectionProxyObjectHandler::polyfillListUnshift);
        polyfillListFunctionMap.put(POLYFILL_SHARED_VALUES, JavetReflectionProxyObjectHandler::polyfillSharedValues);
        polyfillListFunctionMap.put(POLYFILL_LIST_WITH, JavetReflectionProxyObjectHandler::polyfillListWith);
        polyfillMapFunctionMap = new HashMap<>();
        polyfillMapFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillMapToJSON);
        polyfillSetFunctionMap = new HashMap<>();
        polyfillSetFunctionMap.put(POLYFILL_SET_DELETE, JavetReflectionProxyObjectHandler::polyfillSetDelete);
        polyfillSetFunctionMap.put(POLYFILL_SHARED_ENTRIES, JavetReflectionProxyObjectHandler::polyfillSetEntries);
        polyfillSetFunctionMap.put(POLYFILL_SET_HAS, JavetReflectionProxyObjectHandler::polyfillSetHas);
        polyfillSetFunctionMap.put(POLYFILL_SHARED_KEYS, JavetReflectionProxyObjectHandler::polyfillSharedValues);
        polyfillSetFunctionMap.put(POLYFILL_SHARED_VALUES, JavetReflectionProxyObjectHandler::polyfillSharedValues);
        polyfillClassFunctionMap = new HashMap<>();
        {
            // java.lang.Boolean
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillBooleanToJSON);
            polyfillClassFunctionMap.put(Boolean.class, polyfillFunctionMap);
        }
        {
            // java.lang.Byte
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillByteToJSON);
            polyfillClassFunctionMap.put(Byte.class, polyfillFunctionMap);
        }
        {
            // java.lang.Character
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillCharacterToJSON);
            polyfillClassFunctionMap.put(Character.class, polyfillFunctionMap);
        }
        {
            // java.lang.Double
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillDoubleToJSON);
            polyfillClassFunctionMap.put(Double.class, polyfillFunctionMap);
        }
        {
            // java.lang.Float
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillFloatToJSON);
            polyfillClassFunctionMap.put(Float.class, polyfillFunctionMap);
        }
        {
            // java.lang.Integer
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillIntegerToJSON);
            polyfillClassFunctionMap.put(Integer.class, polyfillFunctionMap);
        }
        {
            // java.lang.Long
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillLongToJSON);
            polyfillClassFunctionMap.put(Long.class, polyfillFunctionMap);
        }
        {
            // java.lang.Short
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillShortToJSON);
            polyfillClassFunctionMap.put(Short.class, polyfillFunctionMap);
        }
        {
            // java.lang.String
            Map<String, IJavetProxyPolyfillFunction<?, ?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(POLYFILL_SHARED_TO_JSON, JavetReflectionProxyObjectHandler::polyfillStringToJSON);
            polyfillClassFunctionMap.put(String.class, polyfillFunctionMap);
        }
    }

    /**
     * Instantiates a new Javet reflection proxy object handler.
     *
     * @param v8Runtime               the V8 runtime
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @since 0.9.6
     */
    public JavetReflectionProxyObjectHandler(
            V8Runtime v8Runtime,
            IJavetReflectionObjectFactory reflectionObjectFactory,
            T targetObject) {
        super(v8Runtime, reflectionObjectFactory, Objects.requireNonNull(targetObject));
    }

    /**
     * Polyfill Boolean.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillBooleanToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Boolean value = (Boolean) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueBoolean(value)));
    }

    /**
     * Polyfill Byte.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillByteToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Byte value = (Byte) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill Character.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillCharacterToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Character value = (Character) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueString(value.toString())));
    }

    /**
     * Polyfill Double.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillDoubleToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Double value = (Double) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueDouble(value)));
    }

    /**
     * Polyfill Float.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillFloatToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Float value = (Float) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueDouble(value.doubleValue())));
    }

    /**
     * Polyfill Integer.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillIntegerToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Integer value = (Integer) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueInteger(value)));
    }

    /**
     * Polyfill Array.prototype.at().
     * The at() method of Array instances takes an integer value and returns the item at that index,
     * allowing for positive and negative integers. Negative integers count back from the last item in the array.
     * <p>
     * Parameters
     * index
     * Zero-based index of the array element to be returned, converted to an integer.
     * Negative index counts back from the end of the array — if index &lt; 0, index + array.length is accessed.
     * <p>
     * Return value
     * The element in the array matching the given index.
     * Always returns undefined if index &lt; -array.length or index &gt;= array.length
     * without attempting to access the corresponding property.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListAt(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_AT, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (ArrayUtils.isNotEmpty(v8Values) && v8Values[0] instanceof V8ValueInteger) {
                        final int size = list.size();
                        int index = ((V8ValueInteger) v8Values[0]).getValue();
                        if (index < 0) {
                            index += size;
                        }
                        if (index >= 0 && index < size) {
                            return handler.getV8Runtime().toV8Value(list.get(index));
                        }
                    }
                    return handler.getV8Runtime().createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.concat().
     * The concat() method of Array instances is used to merge two or more arrays.
     * This method does not change the existing arrays, but instead returns a new array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListConcat(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_CONCAT, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        v8ValueArray.push(list.toArray());
                        if (ArrayUtils.isNotEmpty(v8Values)) {
                            List<Object> objects = new ArrayList<>(v8Values.length);
                            for (V8Value v8Value : v8Values) {
                                if (v8Value instanceof IV8ValueArray) {
                                    IV8ValueArray iV8ValueArray = (IV8ValueArray) v8Value;
                                    V8Value[] items = new V8Value[iV8ValueArray.getLength()];
                                    if (ArrayUtils.isNotEmpty(items)) {
                                        iV8ValueArray.batchGet(items, 0, items.length);
                                        Collections.addAll(objects, items);
                                    }
                                } else {
                                    Object object = handler.getV8Runtime().toObject(v8Value);
                                    if (object instanceof Collection) {
                                        Collections.addAll(objects, (Collection<Object>) object);
                                    } else {
                                        objects.add(v8Value);
                                    }
                                }
                            }
                            if (!objects.isEmpty()) {
                                v8ValueArray.push(objects.toArray());
                            }
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.entries().
     * The entries() method of Array instances returns a new array iterator object
     * that contains the key/value pairs for each index in the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    protected static V8Value polyfillListEntries(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        final int length = list.size();
        List<List<Object>> entries = new ArrayList<>(length);
        int index = 0;
        for (Object object : list) {
            entries.add(SimpleList.of(index, object));
            ++index;
        }
        return new JavetProxySymbolIterableConverter<>(handler.getV8Runtime(), entries).getV8ValueFunction();
    }

    /**
     * Polyfill Array.prototype.every().
     * The every() method of Array instances tests whether all elements in the array pass the test implemented
     * by the provided function. It returns a Boolean value.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListEvery(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_EVERY, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    boolean valid = false;
                    if (ArrayUtils.isNotEmpty(v8Values) && v8Values[0] instanceof V8ValueFunction) {
                        V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Values[0];
                        IV8ValueObject thisArg = v8Values.length > 1 && v8Values[1] instanceof IV8ValueObject
                                ? (IV8ValueObject) v8Values[1] : null;
                        int index = 0;
                        valid = true;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(thisArg, object, index, thisObject)) {
                                if (!(result instanceof V8ValueBoolean) || !((V8ValueBoolean) result).getValue()) {
                                    valid = false;
                                    break;
                                }
                            }
                            ++index;
                        }
                    }
                    return handler.getV8Runtime().createV8ValueBoolean(valid);
                }));
    }

    /**
     * Polyfill Array.prototype.fill()
     * The fill() method of Array instances changes all elements within a range of indices
     * in an array to a static value. It returns the modified array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListFill(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_FILL, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        final int length = v8Values.length;
                        V8Value v8Value = v8Values[0];
                        int startIndex = 0;
                        if (length > 1 && v8Values[1] instanceof V8ValueInteger) {
                            startIndex = ((V8ValueInteger) v8Values[1]).getValue();
                            if (startIndex < 0) {
                                startIndex += length;
                                if (startIndex < 0) {
                                    startIndex = 0;
                                }
                            }
                        }
                        int endIndex = 0;
                        if (length > 2 && v8Values[2] instanceof V8ValueInteger) {
                            endIndex = ((V8ValueInteger) v8Values[2]).getValue();
                            if (endIndex < 0) {
                                endIndex += length;
                                if (endIndex < 0) {
                                    endIndex = 0;
                                }
                            }
                        }
                        if (startIndex < length && endIndex > startIndex) {
                            for (int i = startIndex; i < endIndex; ++i) {
                                list.set(i, v8Value);
                            }
                        }
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.filter().
     * The filter() method of Array instances creates a shallow copy of a portion of a given array,
     * filtered down to just the elements from the given array that pass the test implemented by the provided function.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListFilter(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_FILTER, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (ArrayUtils.isNotEmpty(v8Values) && v8Values[0] instanceof V8ValueFunction) {
                            V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Values[0];
                            IV8ValueObject thisArg = v8Values.length > 1 && v8Values[1] instanceof IV8ValueObject
                                    ? (IV8ValueObject) v8Values[1] : null;
                            List<Object> results = new ArrayList<>(list.size());
                            int index = 0;
                            for (Object object : list) {
                                try (V8Value v8ValueResult = v8ValueFunction.call(thisArg, object, index, thisObject)) {
                                    if (v8ValueResult.isPositive()) {
                                        results.add(object);
                                    }
                                }
                                ++index;
                            }
                            if (!results.isEmpty()) {
                                v8ValueArray.push(results.toArray());
                            }
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.includes().
     * The includes() method of Array instances determines whether an array includes a certain value among its entries,
     * returning true or false as appropriate.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListIncludes(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_INCLUDES, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean included = false;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        Object object = handler.getV8Runtime().toObject(v8Values[0]);
                        int fromIndex = 0;
                        if (v8Values.length > 1 && v8Values[1] instanceof V8ValueInteger) {
                            fromIndex = ((V8ValueInteger) v8Values[1]).getValue();
                        }
                        included = ListUtils.includes(list, object, fromIndex);
                    }
                    return handler.getV8Runtime().createV8ValueBoolean(included);
                }));
    }

    /**
     * Polyfill Array.prototype.keys()
     * The keys() method of Array instances returns a new array iterator object
     * that contains the keys for each index in the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListKeys(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_KEYS, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    final int size = list.size();
                    Object[] indexes = new Object[size];
                    for (int i = 0; i < size; ++i) {
                        indexes[i] = handler.getV8Runtime().createV8ValueInteger(i);
                    }
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (size > 0) {
                            v8ValueArray.push(indexes);
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array: length.
     * The length data property of an Array instance represents the number of elements in that array.
     * The value is an unsigned, 32-bit integer that is always numerically greater than the highest index in the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListLength(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueInteger(list.size());
    }

    /**
     * Polyfill Array.prototype.map().
     * The map() method of Array instances creates a new array populated with the results of
     * calling a provided function on every element in the calling array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListMap(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_MAP, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (ArrayUtils.isNotEmpty(v8Values) && v8Values[0] instanceof V8ValueFunction) {
                            V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Values[0];
                            IV8ValueObject thisArg = v8Values.length > 1 && v8Values[1] instanceof IV8ValueObject
                                    ? (IV8ValueObject) v8Values[1] : null;
                            List<V8Value> results = new ArrayList<>(list.size());
                            try {
                                int index = 0;
                                for (Object object : list) {
                                    results.add(v8ValueFunction.call(thisArg, object, index, thisObject));
                                    ++index;
                                }
                                v8ValueArray.push(results.toArray());
                            } finally {
                                JavetResourceUtils.safeClose(results);
                            }
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.pop().
     * The pop() method of Array instances removes the last element from an array and returns that element.
     * This method changes the length of the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListPop(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_POP, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (list.isEmpty()) {
                        return handler.getV8Runtime().createV8ValueUndefined();
                    }
                    return handler.getV8Runtime().toV8Value(ListUtils.pop(list));
                }));
    }

    /**
     * Polyfill Array.prototype.push().
     * The push() method of Array instances adds the specified elements to the end of an array
     * and returns the new length of the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListPush(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_PUSH, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        handler.getV8Runtime().createV8ValueInteger(
                                ListUtils.push(list, V8ValueUtils.toArray(handler.getV8Runtime(), v8Values)))));
    }

    /**
     * Polyfill Array.prototype.reverse().
     * The reverse() method of Array instances reverses an array in place and returns the reference to the same array,
     * the first array element now becoming the last, and the last array element becoming the first. In other words,
     * elements order in the array will be turned towards the direction opposite to that previously stated.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListReverse(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_REVERSE, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    if (!list.isEmpty()) {
                        Collections.reverse(list);
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.shift().
     * The shift() method of Array instances removes the first element from an array and returns that removed element.
     * This method changes the length of the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListShift(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_SHIFT, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (list.isEmpty()) {
                        return handler.getV8Runtime().createV8ValueUndefined();
                    }
                    return handler.getV8Runtime().toV8Value(ListUtils.shift(list));
                }));
    }

    /**
     * Polyfill Array.prototype.some().
     * The some() method of Array instances tests whether at least one element in the array passes
     * the test implemented by the provided function. It returns true if, in the array,
     * it finds an element for which the provided function returns true; otherwise it returns false.
     * It doesn't modify the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListSome(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_SOME, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    boolean valid = false;
                    if (ArrayUtils.isNotEmpty(v8Values) && v8Values[0] instanceof V8ValueFunction) {
                        V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Values[0];
                        IV8ValueObject thisArg = v8Values.length > 1 && v8Values[1] instanceof IV8ValueObject
                                ? (IV8ValueObject) v8Values[1] : null;
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(thisArg, object, index, thisObject)) {
                                if (result instanceof V8ValueBoolean && ((V8ValueBoolean) result).getValue()) {
                                    valid = true;
                                    break;
                                }
                            }
                            ++index;
                        }
                    }
                    return handler.getV8Runtime().createV8ValueBoolean(valid);
                }));
    }

    /**
     * Polyfill Array.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    Object[] objects = list.toArray();
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        v8ValueArray.push(objects);
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.toReversed().
     * The toReversed() method of Array instances is the copying counterpart of the reverse() method.
     * It returns a new array with the elements in reversed order.
     *
     * @param handler the handler
     * @return the v 8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListToReversed(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_TO_REVERSED, handler, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (!list.isEmpty()) {
                            List<Object> reversedList = new ArrayList<>(list);
                            Collections.reverse(reversedList);
                            v8ValueArray.push(reversedList.toArray());
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.unshift()
     * The unshift() method of Array instances adds the specified elements to the beginning of an array
     * and returns the new length of the array.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListUnshift(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_UNSHIFT, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        handler.getV8Runtime().createV8ValueInteger(
                                ListUtils.unshift(list, V8ValueUtils.toArray(handler.getV8Runtime(), v8Values)))));
    }

    /**
     * Polyfill Array.prototype.with().
     * The with() method of Array instances is the copying version of using the bracket notation to change the value
     * of a given index. It returns a new array with the element at the given index replaced with the given value.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillListWith(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final List<Object> list = (List<Object>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_LIST_WITH, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        Object[] objects = list.toArray();
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (v8Values != null && v8Values.length > 1 && v8Values[0] instanceof V8ValueInteger) {
                            int toBeReplacedIndex = ((V8ValueInteger) v8Values[0]).getValue();
                            if (toBeReplacedIndex >= 0 && toBeReplacedIndex < objects.length) {
                                objects[toBeReplacedIndex] = handler.getV8Runtime().toObject(v8Values[1]);
                            }
                        }
                        v8ValueArray.push(objects);
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Long.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillLongToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Long value = (Long) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueLong(value)));
    }

    /**
     * Polyfill Map.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillMapToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Map<?, ?> map = (Map<?, ?>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    Object[] objects = new Object[map.size() << 1];
                    int index = 0;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        objects[index] = entry.getKey();
                        objects[index + 1] = entry.getKey();
                        index += 2;
                    }
                    try (V8Scope v8Scope = handler.getV8Runtime().getV8Scope()) {
                        V8ValueObject v8ValueObject = v8Scope.createV8ValueObject();
                        v8ValueObject.set(objects);
                        v8Scope.setEscapable();
                        return v8ValueObject;
                    }
                }));
    }

    /**
     * Polyfill Set.prototype.delete().
     * The delete() method of Set instances removes a specified value from this set, if it is in the set.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillSetDelete(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Set<?> set = (Set<?>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SET_DELETE, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean result = false;
                    if (v8Values != null && v8Values.length > 0) {
                        result = set.remove(handler.getV8Runtime().toObject(v8Values[0]));
                    }
                    return handler.getV8Runtime().createV8ValueBoolean(result);
                }));
    }

    /**
     * Polyfill Set.prototype.entries().
     * The entries() method of Set instances returns a new set iterator object that contains an array of
     * [value, value] for each element in this set, in insertion order.
     * For Set objects there is no key like in Map objects. However, to keep the API similar to the Map object,
     * each entry has the same value for its key and value here, so that an array [value, value] is returned.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    protected static V8Value polyfillSetEntries(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Set<Object> set = (Set<Object>) handler.getTargetObject();
        final List<List<Object>> entries = set.stream().map(o -> SimpleList.of(o, o)).collect(Collectors.toList());
        return new JavetProxySymbolIterableConverter<>(handler.getV8Runtime(), entries).getV8ValueFunction();
    }

    /**
     * Polyfill Set.prototype.has().
     * The has() method of Set instances returns a boolean indicating whether an element
     * with the specified value exists in this set or not.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillSetHas(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Set<?> set = (Set<?>) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SET_HAS, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean result = false;
                    if (v8Values != null && v8Values.length > 0) {
                        result = set.contains(handler.getV8Runtime().toObject(v8Values[0]));
                    }
                    return handler.getV8Runtime().createV8ValueBoolean(result);
                }));
    }

    /**
     * Polyfill Array.prototype.values().
     * The values() method of Array instances returns a new array iterator object that iterates the value
     * of each item in the array.
     * <p>
     * Polyfill Set.prototype.values().
     * The values() method of Set instances returns a new set iterator object that contains the values
     * for each element in this set in insertion order.
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillSharedValues(IJavetProxyHandler<?, ?> handler) throws JavetException {
        return new JavetProxySymbolIterableConverter<>(
                handler.getV8Runtime(), handler.getTargetObject()).getV8ValueFunction();
    }

    /**
     * Polyfill Short.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillShortToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final Short value = (Short) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill String.toJSON().
     *
     * @param handler the handler
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    protected static V8Value polyfillStringToJSON(IJavetProxyHandler<?, ?> handler) throws JavetException {
        final String value = (String) handler.getTargetObject();
        return handler.getV8Runtime().createV8ValueFunction(new JavetCallbackContext(
                POLYFILL_SHARED_TO_JSON, handler, JavetCallbackType.DirectCallNoThisAndResult,
                (NoThisAndResult<Exception>) (v8Values) -> handler.getV8Runtime().createV8ValueString(value)));
    }

    /**
     * Delete from collection.
     *
     * @param property the property
     * @return true : deleted, false : not deleted
     * @throws JavetException the javet exception
     */
    protected boolean deleteFromCollection(V8Value property) throws JavetException {
        if (property instanceof V8ValueString) {
            try {
                String propertyString = ((V8ValueString) property).getValue();
                if (StringUtils.isDigital(propertyString)) {
                    final int index = Integer.parseInt(propertyString);
                    if (index >= 0) {
                        if (classDescriptor.getTargetClass().isArray()) {
                            if (index < Array.getLength(targetObject)) {
                                // Only non-primitive array supports delete.
                                if (!classDescriptor.getTargetClass().getComponentType().isPrimitive()) {
                                    Array.set(targetObject, index, null);
                                    return true;
                                }
                            }
                        } else if (classDescriptor.isTargetTypeList()) {
                            List<?> list = (List<?>) targetObject;
                            if (index < list.size()) {
                                list.remove(index);
                                return true;
                            }
                        }
                    }
                }
                if (classDescriptor.isTargetTypeMap()) {
                    Map<?, ?> map = (Map<?, ?>) targetObject;
                    return map.remove(propertyString) != null;
                }
            } catch (Throwable ignored) {
            }
        }
        return false;
    }

    @Override
    public V8ValueBoolean deleteProperty(V8Value target, V8Value property) throws JavetException, E {
        boolean result = deleteFromCollection(property);
        return super.deleteProperty(target, property);
    }

    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException, E {
        V8Value result = getFromCollection(property);
        result = result == null ? getFromField(property) : result;
        result = result == null ? getFromMethod(target, property) : result;
        result = result == null ? getFromSymbol(property) : result;
        result = result == null ? getFromGetter(property) : result;
        result = result == null ? getFromPolyfill(property) : result;
        return result == null ? v8Runtime.createV8ValueUndefined() : result;
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        if (callbackContexts == null) {
            callbackContexts = new JavetCallbackContext[]{
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_GET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> get(v8Values[0], v8Values[1], v8Values[2])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_DELETE_PROPERTY, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> deleteProperty(v8Values[0], v8Values[1])),
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
    public ThreadSafeMap<Class<?>, ClassDescriptor> getClassDescriptorCache() {
        return classDescriptorMap;
    }

    /**
     * Gets from collection.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromCollection(V8Value property) throws JavetException {
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).getValue();
            if (StringUtils.isDigital(propertyString)) {
                final int index = Integer.parseInt(propertyString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            return v8Runtime.toV8Value(Array.get(targetObject, index));
                        }
                    } else if (classDescriptor.isTargetTypeList()) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            return v8Runtime.toV8Value(list.get(index));
                        }
                    }
                }
            } else if (classDescriptor.getTargetClass().isArray() && POLYFILL_SHARED_LENGTH.equals(propertyString)) {
                return v8Runtime.toV8Value(Array.getLength(targetObject));
            }
        }
        return null;
    }

    /**
     * Gets from polyfill.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 3.0.3
     */
    protected V8Value getFromPolyfill(V8Value property) throws JavetException, E {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).getValue();
            IJavetProxyPolyfillFunction<T, E> iJavetProxyPolyfillFunction = null;
            if (classDescriptor.isTargetTypeList()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        polyfillListFunctionMap.get(propertyName);
            } else if (classDescriptor.isTargetTypeMap()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        polyfillMapFunctionMap.get(propertyName);
            } else if (classDescriptor.isTargetTypeSet()) {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        polyfillSetFunctionMap.get(propertyName);
            } else {
                iJavetProxyPolyfillFunction = (IJavetProxyPolyfillFunction<T, E>)
                        Optional.ofNullable(polyfillClassFunctionMap.get(classDescriptor.getTargetClass()))
                                .map(map -> map.get(propertyName)).orElse(null);
            }
            if (iJavetProxyPolyfillFunction != null) {
                return iJavetProxyPolyfillFunction.apply(this);
            }
        }
        return null;
    }

    /**
     * Gets from symbol.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromSymbol(V8Value property) throws JavetException {
        if (property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                return new JavetProxySymbolToPrimitiveConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)
                    && (targetObject instanceof Iterable<?>
                    || targetObject instanceof Map<?, ?>
                    || targetObject instanceof String
                    || classDescriptor.getTargetClass().isArray())) {
                return new JavetProxySymbolIterableConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            }
        }
        return null;
    }

    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = hasFromCollection(property);
        isFound = isFound || hasFromRegular(property);
        isFound = isFound || hasFromGeneric(property);
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    /**
     * Has from collection.
     *
     * @param property the property
     * @return true : has, false: not has
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean hasFromCollection(V8Value property) throws JavetException {
        if (classDescriptor.isTargetTypeMap()) {
            return ((Map<?, ?>) targetObject).containsKey(v8Runtime.toObject(property));
        } else if (classDescriptor.isTargetTypeList()) {
            return ((List<?>) targetObject).contains(v8Runtime.toObject(property));
        } else if (classDescriptor.isTargetTypeSet()) {
            return ((Set<?>) targetObject).contains(v8Runtime.toObject(property));
        } else if (property instanceof V8ValueString) {
            String indexString = ((V8ValueString) property).getValue();
            if (StringUtils.isDigital(indexString)) {
                final int index = Integer.parseInt(indexString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        return index < Array.getLength(targetObject);
                    } else if (List.class.isAssignableFrom(classDescriptor.getTargetClass())) {
                        return index < ((List<?>) targetObject).size();
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void initialize() {
        Class<T> targetClass = (Class<T>) targetObject.getClass();
        classDescriptor = classDescriptorMap.get(targetClass);
        if (classDescriptor == null) {
            classDescriptor = new ClassDescriptor(V8ProxyMode.Object, targetClass);
            if (targetObject instanceof Class) {
                initializeFieldsAndMethods((Class<?>) targetObject, true);
            }
            initializeCollection();
            initializeFieldsAndMethods(targetClass, false);
            classDescriptorMap.put(targetClass, classDescriptor);
        }
    }

    /**
     * Initialize collection.
     *
     * @since 1.1.7
     */
    protected void initializeCollection() {
        if (classDescriptor.isTargetTypeMap()) {
            ((Map<Object, ?>) targetObject).keySet().stream()
                    .map(Object::toString)
                    .filter(Objects::nonNull)
                    .forEach(classDescriptor.getUniqueKeySet()::add);
        } else if (classDescriptor.isTargetTypeSet()) {
            ((Set<Object>) targetObject).stream()
                    .map(Object::toString)
                    .filter(Objects::nonNull)
                    .forEach(classDescriptor.getUniqueKeySet()::add);
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
    public V8ValueArray ownKeys(V8Value target) throws JavetException {
        Object[] keys = null;
        if (classDescriptor.isTargetTypeMap()) {
            keys = ((Map<?, ?>) targetObject).keySet().toArray();
        } else if (classDescriptor.isTargetTypeSet()) {
            keys = ((Set<?>) targetObject).toArray();
        } else if (classDescriptor.getTargetClass().isArray()
                || Collection.class.isAssignableFrom(classDescriptor.getTargetClass())) {
            final int length = classDescriptor.getTargetClass().isArray()
                    ? Array.getLength(targetObject)
                    : ((List<?>) targetObject).size();
            keys = new Object[length];
            for (int i = 0; i < length; ++i) {
                keys[i] = i;
            }
        }
        if (ArrayUtils.isEmpty(keys)) {
            keys = classDescriptor.getUniqueKeySet().toArray();
        }
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            if (key instanceof String) {
                keys[i] = v8Runtime.createV8ValueString((String) key);
            } else if (!(key instanceof V8ValueString) && !(key instanceof V8ValueSymbol)) {
                keys[i] = v8Runtime.createV8ValueString(key.toString());
            }
        }
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
            v8ValueArray.push(keys);
            v8Scope.setEscapable();
            return v8ValueArray;
        }
    }

    @Override
    public V8ValueBoolean set(
            V8Value target,
            V8Value propertyKey,
            V8Value propertyValue,
            V8Value receiver) throws JavetException {
        boolean isSet = setToCollection(propertyKey, propertyValue);
        isSet = isSet || setToField(propertyKey, propertyValue);
        isSet = isSet || setToSetter(target, propertyKey, propertyValue);
        return v8Runtime.createV8ValueBoolean(isSet);
    }

    /**
     * Sets to collection.
     *
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean setToCollection(V8Value propertyKey, V8Value propertyValue) throws JavetException {
        if (propertyKey instanceof V8ValueString) {
            String propertyKeyString = ((V8ValueString) propertyKey).getValue();
            if (StringUtils.isDigital(propertyKeyString)) {
                final int index = Integer.parseInt(propertyKeyString);
                if (index >= 0) {
                    if (classDescriptor.getTargetClass().isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            Array.set(targetObject, index, v8Runtime.toObject(propertyValue));
                            return true;
                        }
                    } else if (classDescriptor.isTargetTypeList()) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            list.set(index, v8Runtime.toObject(propertyValue));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
