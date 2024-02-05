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
import com.caoccao.javet.interop.proxy.JavetProxySymbolIterableConverter;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Javet proxy polyfill map.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public final class JavetProxyPolyfillMap {
    private static final String CLEAR = "clear";
    private static final String DELETE = "delete";
    private static final String ENTRIES = "entries";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP =
            "Target object must be an instance of Map.";
    private static final String FOR_EACH = "forEach";
    private static final String GET = "get";
    private static final String HAS = "has";
    private static final String KEYS = "keys";
    private static final String OBJECT_MAP = "[object Map]";
    private static final String SET = "set";
    private static final String SIZE = "size";
    private static final String TO_JSON = "toJSON";
    private static final String TO_STRING = "toString";
    private static final String VALUES = "values";
    private static final Map<String, IJavetProxyPolyfillFunction<?, ?>> functionMap;

    static {
        functionMap = new HashMap<>();
        functionMap.put(CLEAR, JavetProxyPolyfillMap::clear);
        functionMap.put(DELETE, JavetProxyPolyfillMap::delete);
        functionMap.put(ENTRIES, JavetProxyPolyfillMap::entries);
        functionMap.put(FOR_EACH, JavetProxyPolyfillMap::forEach);
        functionMap.put(GET, JavetProxyPolyfillMap::get);
        functionMap.put(HAS, JavetProxyPolyfillMap::has);
        functionMap.put(KEYS, JavetProxyPolyfillMap::keys);
        functionMap.put(SET, JavetProxyPolyfillMap::set);
        functionMap.put(SIZE, JavetProxyPolyfillMap::size);
        functionMap.put(TO_JSON, JavetProxyPolyfillMap::toJSON);
        functionMap.put(TO_STRING, JavetProxyPolyfillMap::toString);
        functionMap.put(VALUES, JavetProxyPolyfillMap::values);
    }

    private JavetProxyPolyfillMap() {
    }

    /**
     * Polyfill Map.prototype.clear().
     * The clear() method of Map instances removes all elements from this map.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value clear(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                CLEAR, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    map.clear();
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Map.prototype.delete()
     * The delete() method of Map instances removes the specified element from this map by key.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value delete(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                DELETE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean deleted = false;
                    if (!map.isEmpty() && ArrayUtils.isNotEmpty(v8Values)) {
                        deleted = map.remove(v8Runtime.toObject(v8Values[0])) != null;
                    }
                    return v8Runtime.createV8ValueBoolean(deleted);
                }));
    }

    /**
     * Polyfill Map.prototype.entries()
     * The entries() method of Map instances returns a new map iterator object that contains the [key, value]
     * pairs for each element in this map in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value entries(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        List<List<Object>> entries = map.entrySet().stream()
                .map(entry -> SimpleList.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new JavetProxySymbolIterableConverter<>(v8Runtime, entries).getV8ValueFunction();
    }

    /**
     * Polyfill Map.prototype.forEach().
     * The forEach() method of Map instances executes a provided function once per each key/value pair in this map,
     * in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value forEach(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FOR_EACH, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            try (V8Value v8ValueResult = v8ValueFunction.call(
                                    v8ValueObject, entry.getValue(), entry.getKey(), thisObject)) {
                            }
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Map.prototype.get().
     * The get() method of Map instances returns a specified element from this map.
     * If the value that is associated to the provided key is an object,
     * then you will get a reference to that object and any change made to
     * that object will effectively modify it inside the Map object.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value get(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                GET, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (!map.isEmpty() && ArrayUtils.isNotEmpty(v8Values)) {
                        Object key = v8Runtime.toObject(v8Values[0]);
                        if (map.containsKey(key)) {
                            return v8Runtime.toV8Value(map.get(key));
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Gets function.
     *
     * @param name the name
     * @return the function
     * @since 3.0.4
     */
    public static IJavetProxyPolyfillFunction<?, ?> getFunction(String name) {
        return functionMap.get(name);
    }

    /**
     * Polyfill Map.prototype.has()
     * The has() method of Map instances returns a boolean indicating whether an element
     * with the specified key exists in this map or not.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value has(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                HAS, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean found = false;
                    if (!map.isEmpty() && ArrayUtils.isNotEmpty(v8Values)) {
                        found = map.containsKey(v8Runtime.toObject(v8Values[0]));
                    }
                    return v8Runtime.createV8ValueBoolean(found);
                }));
    }

    /**
     * Polyfill Map.prototype.keys()
     * The keys() method of Map instances returns a new map iterator object that contains the keys for each element
     * in this map in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value keys(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return new JavetProxySymbolIterableConverter<>(v8Runtime, map.keySet()).getV8ValueFunction();
    }

    /**
     * Polyfill Map.prototype.set().
     * The set() method of Map instances adds or updates an entry in this map with a specified key and a value.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value set(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<Object, Object> map = (Map<Object, Object>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SET, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    Object key = v8Runtime.toObject(V8ValueUtils.asV8Value(v8Values, 0));
                    Object value = v8Runtime.toObject(V8ValueUtils.asV8Value(v8Values, 1));
                    if (key != null) {
                        map.put(key, value);
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Map.prototype.size.
     * The size accessor property of Map instances returns the number of elements in this map.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value size(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(map.size());
    }

    /**
     * Polyfill Map.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    Object[] objects = new Object[map.size() << 1];
                    int index = 0;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        objects[index] = entry.getKey();
                        objects[index + 1] = entry.getKey();
                        index += 2;
                    }
                    return V8ValueUtils.createV8ValueObject(v8Runtime, objects);
                }));
    }

    /**
     * Polyfill Object.prototype.toString()
     * The toString() method of Map instances always returns [object Map].
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toString(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueString(OBJECT_MAP)));
    }

    /**
     * Polyfill Map.prototype.values()
     * The values() method of Map instances returns a new map iterator object that contains the values
     * for each element in this map in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value values(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        final Map<?, ?> map = (Map<?, ?>) Objects.requireNonNull(targetObject);
        return new JavetProxySymbolIterableConverter<>(v8Runtime, map.values()).getV8ValueFunction();
    }
}
