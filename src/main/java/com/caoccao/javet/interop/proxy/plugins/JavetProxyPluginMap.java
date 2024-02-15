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

import com.caoccao.javet.entities.JavetEntityPropertyDescriptor;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.caoccao.javet.values.virtual.V8VirtualIterator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Javet proxy plugin map.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public class JavetProxyPluginMap extends BaseJavetProxyPluginSingle<Map<Object, Object>> {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = Map.class.getName();
    protected static final String CLEAR = "clear";
    protected static final String DELETE = "delete";
    protected static final String ENTRIES = "entries";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP =
            "Target object must be an instance of Map.";
    protected static final String FOR_EACH = "forEach";
    protected static final String GET = "get";
    protected static final String HAS = "has";
    protected static final String KEYS = "keys";
    protected static final String OBJECT_MAP = "[object Map]";
    protected static final String SET = "set";
    protected static final String SIZE = "size";
    /**
     * The constant DEFAULT_PROXYABLE_METHODS.
     *
     * @since 3.0.4
     */
    protected static final String[] DEFAULT_PROXYABLE_METHODS = new String[]{
            CLEAR, FOR_EACH, GET, SIZE, TO_STRING};
    protected static final String VALUES = "values";
    private static final JavetProxyPluginMap instance = new JavetProxyPluginMap();
    /**
     * The proxyable methods.
     *
     * @since 3.0.4
     */
    protected final Set<String> proxyableMethods;

    public JavetProxyPluginMap() {
        super();
        proxyableMethods = SimpleSet.of(DEFAULT_PROXYABLE_METHODS);
        proxyGetByStringMap.put(CLEAR, this::clear);
        proxyGetByStringMap.put(DELETE, this::delete);
        proxyGetByStringMap.put(ENTRIES, this::entries);
        proxyGetByStringMap.put(FOR_EACH, this::forEach);
        proxyGetByStringMap.put(GET, this::get);
        proxyGetByStringMap.put(HAS, this::has);
        proxyGetByStringMap.put(KEYS, this::keys);
        proxyGetByStringMap.put(SET, this::set);
        proxyGetByStringMap.put(SIZE, this::size);
        proxyGetByStringMap.put(TO_JSON, this::toJSON);
        proxyGetByStringMap.put(TO_STRING, this::toString);
        proxyGetByStringMap.put(VALUE_OF, this::valueOf);
        proxyGetByStringMap.put(VALUES, this::values);
        proxyGetBySymbolMap.put(V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR, this::entries);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginMap getInstance() {
        return instance;
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
    public V8Value clear(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                CLEAR, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    map.clear();
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    @Override
    protected V8Value createTargetObject(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        validateTargetObject(targetObject);
        return null;
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
    public V8Value delete(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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

    @Override
    public boolean deleteByObject(Object targetObject, Object propertyKey) {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return propertyKey != null && map.remove(propertyKey) != null;
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
    public V8Value entries(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        List<List<Object>> entries = map.entrySet().stream()
                .map(entry -> SimpleList.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                ENTRIES, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        PROXY_CONVERTER.toV8Value(v8Runtime, new V8VirtualIterator<>(entries.iterator()))));
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
    public V8Value forEach(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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
    public V8Value get(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] getProxyOwnKeys(Object targetObject) {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return map.keySet().toArray();
    }

    @Override
    public <T> IJavetEntityPropertyDescriptor<T> getProxyOwnPropertyDescriptor(Object targetObject, Object propertyName) {
        return new JavetEntityPropertyDescriptor<>(true, true, true);
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
    public V8Value has(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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

    @Override
    public boolean hasByObject(Object targetObject, Object propertyKey) {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return map.containsKey(propertyKey);
    }

    @Override
    public boolean isDeleteSupported(Class<?> targetClass) {
        return true;
    }

    @Override
    public boolean isHasSupported(Class<?> targetClass) {
        return true;
    }

    @Override
    public boolean isMethodProxyable(String methodName, Class<?> targetClass) {
        return proxyableMethods.contains(methodName);
    }

    @Override
    public boolean isOwnKeysSupported(Class<?> targetClass) {
        return true;
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null && Map.class.isAssignableFrom(targetClass);
    }

    @Override
    public boolean isUniqueKeySupported(Class<?> targetClass) {
        return true;
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
    public V8Value keys(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                KEYS, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        PROXY_CONVERTER.toV8Value(v8Runtime, new V8VirtualIterator<>(map.keySet().iterator()))));
    }

    @Override
    public void populateUniqueKeys(Set<String> uniqueKeySet, Object targetObject) {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        map.keySet().stream()
                .map(Object::toString)
                .filter(Objects::nonNull)
                .forEach(Objects.requireNonNull(uniqueKeySet)::add);
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
    public V8Value set(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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
    public V8Value size(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
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
    public V8Value toJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    Object[] objects = new Object[map.size() << 1];
                    int index = 0;
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        objects[index] = entry.getKey();
                        objects[index + 1] = entry.getValue();
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
    public V8Value toString(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueString(OBJECT_MAP)));
    }

    @Override
    protected Map<Object, Object> validateTargetObject(Object targetObject) {
        assert targetObject instanceof Map : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_MAP;
        return (Map<Object, Object>) targetObject;
    }

    /**
     * Polyfill Object.prototype.valueOf().
     * The valueOf() method of Object instances converts the this value to an object.
     * This method is meant to be overridden by derived objects for custom type conversion logic.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value valueOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                VALUES, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        V8ValueUtils.createV8ValueMap(v8Runtime, map)));
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
    public V8Value values(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final Map<Object, Object> map = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                VALUES, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        PROXY_CONVERTER.toV8Value(v8Runtime, new V8VirtualIterator<>(map.values().iterator()))));
    }
}
