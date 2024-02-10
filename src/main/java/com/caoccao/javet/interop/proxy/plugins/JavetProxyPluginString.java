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

import com.caoccao.javet.entities.JavetEntityObject;
import com.caoccao.javet.entities.JavetEntityPropertyDescriptor;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;
import com.caoccao.javet.values.virtual.V8VirtualIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The type Javet proxy plugin string.
 *
 * @since 3.0.4
 */
public class JavetProxyPluginString extends BaseJavetProxyPluginSingle {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = String.class.getName();
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING =
            "Target object must be an instance of String.";
    protected static final String LENGTH = "length";
    /**
     * The constant DEFAULT_PROXYABLE_METHODS.
     *
     * @since 3.0.4
     */
    protected static final String[] DEFAULT_PROXYABLE_METHODS = new String[]{
            LENGTH, TO_STRING};
    private static final JavetProxyPluginString instance = new JavetProxyPluginString();
    /**
     * The proxyable methods.
     *
     * @since 3.0.4
     */
    protected final Set<String> proxyableMethods;

    public JavetProxyPluginString() {
        super();
        proxyableMethods = SimpleSet.of(DEFAULT_PROXYABLE_METHODS);
        proxyGetByStringMap.put(LENGTH, this::length);
        proxyGetByStringMap.put(TO_STRING, this::toString);
        proxyGetBySymbolMap.put(V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR, this::symbolIterator);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginString getInstance() {
        return instance;
    }

    @Override
    public Object getByIndex(Object targetObject, int index) {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        if (index >= 0 && index < string.length()) {
            return new JavetEntityObject<>(String.valueOf(string.charAt(index)));
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] getProxyOwnKeys(Object targetObject) {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        List<Object> keys = new ArrayList<>();
        IntStream.range(0, string.length()).boxed().forEach(keys::add);
        keys.add(LENGTH);
        return keys.toArray();
    }

    @Override
    public <T> IJavetEntityPropertyDescriptor<T> getProxyOwnPropertyDescriptor(Object targetObject, Object propertyName) {
        if (propertyName instanceof String) {
            return new JavetEntityPropertyDescriptor<>(true, !LENGTH.equals(propertyName), false);
        }
        return new JavetEntityPropertyDescriptor<>(false, false, false);
    }

    /**
     * Gets proxyable methods.
     *
     * @return the proxyable methods
     * @since 3.0.4
     */
    public Set<String> getProxyableMethods() {
        return proxyableMethods;
    }

    @Override
    public boolean isMethodProxyable(String methodName) {
        return proxyableMethods.contains(methodName);
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null && String.class.isAssignableFrom(targetClass);
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
    public V8Value length(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(string.length());
    }

    /**
     * Polyfill String.prototype[@@iterator]().
     * The [@@iterator]() method of String values implements the iterable protocol and allows strings
     * to be consumed by most syntaxes expecting iterables, such as the spread syntax and for...of loops.
     * It returns a string iterator object that yields the Unicode code points of the string value as individual strings.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value symbolIterator(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        List<V8Value> list = new ArrayList<>();
        try {
            for (char c : string.toCharArray()) {
                list.add(v8Runtime.createV8ValueString(String.valueOf(c)));
            }
            return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                    V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                            PROXY_CONVERTER.toV8Value(v8Runtime, new V8VirtualIterator<>(list.iterator()))));
        } finally {
            JavetResourceUtils.safeClose(list);
        }
    }

    /**
     * Polyfill String.prototype.toString().
     * The toString() method of String values returns this string value.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toString(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String string = (String) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueString(string)));
    }
}
