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
import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.converters.JavetConverterConfig;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.values.V8Value;

import java.util.Set;

/**
 * The type Base javet proxy plugin.
 *
 * @since 3.0.4
 */
public abstract class BaseJavetProxyPlugin implements IClassProxyPlugin {
    /**
     * The constant HINT_BOOLEAN.
     *
     * @since 3.0.4
     */
    protected static final String HINT_BOOLEAN = "boolean";
    /**
     * The constant HINT_DEFAULT.
     *
     * @since 3.0.4
     */
    protected static final String HINT_DEFAULT = "default";
    /**
     * The constant HINT_NUMBER.
     *
     * @since 3.0.4
     */
    protected static final String HINT_NUMBER = "number";
    /**
     * The constant HINT_STRING.
     *
     * @since 3.0.4
     */
    protected static final String HINT_STRING = "string";
    /**
     * The constant OBJECT_CONVERTER.
     *
     * @since 3.0.4
     */
    protected static final JavetObjectConverter OBJECT_CONVERTER = new JavetObjectConverter();
    /**
     * The constant TO_JSON.
     *
     * @since 3.0.4
     */
    protected static final String TO_JSON = "toJSON";
    /**
     * The constant TO_STRING.
     *
     * @since 3.0.4
     */
    protected static final String TO_STRING = "toString";

    /**
     * Instantiates a new Base javet proxy plugin.
     *
     * @since 3.0.4
     */
    protected BaseJavetProxyPlugin() {
    }

    @Override
    public boolean deleteByObject(Object targetObject, Object propertyKey) {
        return false;
    }

    @Override
    public Object getByIndex(Object targetObject, int index) {
        return null;
    }

    @Override
    public Set<String> getOverrideMethods(JavetConverterConfig<?> config) {
        return null;
    }

    @Override
    public Object[] getOwnKeys(Object targetObject) {
        return new Object[0];
    }

    @Override
    public boolean hasByObject(Object targetObject, Object propertyKey) {
        return false;
    }

    @Override
    public boolean isDeleteSupported() {
        return false;
    }

    @Override
    public boolean isHasSupported() {
        return false;
    }

    @Override
    public boolean isIndexedPropertySupported() {
        return false;
    }

    @Override
    public boolean isSymbolToPrimitiveSupported() {
        return false;
    }

    @Override
    public boolean isUniqueKeySupported() {
        return false;
    }

    @Override
    public void populateUniqueKeys(Set<String> uniqueKeySet, Object targetObject) {
    }

    @Override
    public boolean setByIndex(Object targetObject, int index, Object value) {
        return false;
    }

    @Override
    public V8Value toPrimitive(V8Runtime v8Runtime, Object targetObject, String hintString) throws JavetException {
        return OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject);
    }
}
