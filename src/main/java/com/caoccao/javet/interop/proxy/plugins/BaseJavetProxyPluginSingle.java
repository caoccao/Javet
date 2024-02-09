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

import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Base javet proxy plugin for single class.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public abstract class BaseJavetProxyPluginSingle extends BaseJavetProxyPlugin {
    /**
     * The Proxy get by string map.
     *
     * @since 3.0.4
     */
    protected final Map<String, IClassProxyPluginFunction<?>> proxyGetByStringMap;
    /**
     * The Proxy get by symbol map.
     *
     * @since 3.0.4
     */
    protected final Map<String, IClassProxyPluginFunction<?>> proxyGetBySymbolMap;

    /**
     * Instantiates a new Base javet proxy plugin for single class.
     *
     * @since 3.0.4
     */
    public BaseJavetProxyPluginSingle() {
        super();
        proxyGetByStringMap = new HashMap<>();
        proxyGetBySymbolMap = new HashMap<>();
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetByString(
            Class<?> targetClass, String propertyName) {
        return (IClassProxyPluginFunction<E>) proxyGetByStringMap.get(propertyName);
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetBySymbol(
            Class<?> targetClass, String symbolName) {
        IClassProxyPluginFunction<E> classProxyPluginFunction = super.getProxyGetBySymbol(targetClass, symbolName);
        if (classProxyPluginFunction != null) {
            return classProxyPluginFunction;
        }
        return (IClassProxyPluginFunction<E>) proxyGetBySymbolMap.get(symbolName);
    }
}
