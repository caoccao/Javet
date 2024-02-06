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

import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.converters.JavetConverterConfig;

import java.util.Set;

/**
 * The type Base javet proxy plugin.
 *
 * @since 3.0.4
 */
public abstract class BaseJavetProxyPlugin implements IClassProxyPlugin {
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
}
