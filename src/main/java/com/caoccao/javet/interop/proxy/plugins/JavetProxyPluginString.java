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
import com.caoccao.javet.utils.SimpleSet;
import com.caoccao.javet.values.V8Value;

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
public class JavetProxyPluginString extends BaseJavetProxyPluginSingle<String> {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = String.class.getName();
    protected static final String CHAR_AT = "charAt";
    protected static final String CODE_POINT_AT = "codePointAt";
    protected static final String ENDS_WITH = "endsWith";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING =
            "Target object must be an instance of String.";
    protected static final String INDEX_OF = "indexOf";
    protected static final String LAST_INDEX_OF = "lastIndexOf";
    protected static final String LENGTH = "length";
    protected static final String REPEAT = "repeat";
    protected static final String REPLACE = "replace";
    protected static final String REPLACE_ALL = "replaceAll";
    protected static final String SPLIT = "split";
    protected static final String STARTS_WITH = "startsWith";
    protected static final String SUBSTRING = "substring";
    protected static final String TRIM = "trim";
    /**
     * The constant DEFAULT_PROXYABLE_METHODS.
     *
     * @since 3.0.4
     */
    protected static final String[] DEFAULT_PROXYABLE_METHODS = new String[]{
            CHAR_AT, CODE_POINT_AT, ENDS_WITH, INDEX_OF, LAST_INDEX_OF,
            LENGTH, REPEAT, REPLACE, REPLACE_ALL, SPLIT,
            STARTS_WITH, SUBSTRING, TRIM, VALUE_OF};
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
        proxyGetByStringMap.put(TO_JSON, this::valueOf);
        proxyGetByStringMap.put(VALUE_OF, this::valueOf);
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
    protected V8Value createTargetObject(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final String string = validateTargetObject(targetObject);
        return v8Runtime.createV8ValueStringObject(string);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object[] getProxyOwnKeys(Object targetObject) {
        final String string = validateTargetObject(targetObject);
        List<Object> keys = new ArrayList<>();
        IntStream.range(0, string.length()).boxed().forEach(keys::add);
        keys.add(LENGTH);
        return keys.toArray();
    }

    @Override
    public <T> IJavetEntityPropertyDescriptor<T> getProxyOwnPropertyDescriptor(Object targetObject, Object propertyName) {
        return new JavetEntityPropertyDescriptor<>(false, !LENGTH.equals(propertyName), false);
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
    public boolean isMethodProxyable(String methodName, Class<?> targetClass) {
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
        final String string = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(string.length());
    }

    @Override
    protected String validateTargetObject(Object targetObject) {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        return (String) targetObject;
    }

    /**
     * Polyfill String.prototype.valueOf().
     * The valueOf() method of String values returns this string value.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value valueOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        final String string = validateTargetObject(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueString(string)));
    }
}
