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
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.values.V8Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Javet proxy polyfill set.
 *
 * @since 3.0.4
 */
public final class JavetProxyPolyfillSet {
    private static final String DELETE = "delete";
    private static final String ENTRIES = "entries";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET =
            "Target object must be an instance of Set.";
    private static final String HAS = "has";
    private static final String KEYS = "keys";
    private static final String VALUES = "values";
    private static final Map<String, IJavetProxyPolyfillFunction<?, ?>> functionMap;

    static {
        functionMap = new HashMap<>();
        functionMap.put(DELETE, JavetProxyPolyfillSet::delete);
        functionMap.put(ENTRIES, JavetProxyPolyfillSet::entries);
        functionMap.put(HAS, JavetProxyPolyfillSet::has);
        functionMap.put(KEYS, JavetProxyPolyfillSet::values);
        functionMap.put(VALUES, JavetProxyPolyfillSet::values);
    }

    private JavetProxyPolyfillSet() {
    }

    /**
     * Polyfill Set.prototype.delete().
     * The delete() method of Set instances removes a specified value from this set, if it is in the set.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value delete(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                DELETE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean result = false;
                    if (v8Values != null && v8Values.length > 0) {
                        result = set.remove(v8Runtime.toObject(v8Values[0]));
                    }
                    return v8Runtime.createV8ValueBoolean(result);
                }));
    }

    /**
     * Polyfill Set.prototype.entries().
     * The entries() method of Set instances returns a new set iterator object that contains an array of
     * [value, value] for each element in this set, in insertion order.
     * For Set objects there is no key like in Map objects. However, to keep the API similar to the Map object,
     * each entry has the same value for its key and value here, so that an array [value, value] is returned.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value entries(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) Objects.requireNonNull(targetObject);
        final List<List<?>> entries = set.stream().map(o -> SimpleList.of(o, o)).collect(Collectors.toList());
        return new JavetProxySymbolIterableConverter<>(v8Runtime, entries).getV8ValueFunction();
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
     * Polyfill Set.prototype.has().
     * The has() method of Set instances returns a boolean indicating whether an element
     * with the specified value exists in this set or not.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value has(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) Objects.requireNonNull(targetObject);
        return v8Runtime.createV8ValueFunction(new JavetCallbackContext(
                HAS, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean result = false;
                    if (v8Values != null && v8Values.length > 0) {
                        result = set.contains(v8Runtime.toObject(v8Values[0]));
                    }
                    return v8Runtime.createV8ValueBoolean(result);
                }));
    }

    /**
     * Polyfill Set.prototype.values().
     * The values() method of Set instances returns a new set iterator object that contains the values
     * for each element in this set in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value values(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return new JavetProxySymbolIterableConverter<>(
                v8Runtime, targetObject).getV8ValueFunction();
    }
}
