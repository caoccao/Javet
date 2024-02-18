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
import java.util.Optional;
import java.util.Set;

/**
 * The type Base javet proxy plugin for multiple classes.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public abstract class BaseJavetProxyPluginMultiple extends BaseJavetProxyPlugin {
    /**
     * The Proxy get by string map.
     *
     * @since 3.0.4
     */
    protected final Map<Class<?>, Map<String, IClassProxyPluginFunction<?>>> proxyGetByStringMap;
    /**
     * The Proxy get by symbol map.
     *
     * @since 3.0.4
     */
    protected final Map<Class<?>, Map<String, IClassProxyPluginFunction<?>>> proxyGetBySymbolMap;
    /**
     * The Proxyable methods map.
     *
     * @since 3.0.4
     */
    protected final Map<Class<?>, Set<String>> proxyableMethodsMap;
    /**
     * The Target object constructor map.
     *
     * @since 3.0.4
     */
    protected final Map<Class<?>, IClassProxyPluginFunction<?>> targetObjectConstructorMap;

    /**
     * Instantiates a new Base javet proxy plugin for multiple classes.
     *
     * @since 3.0.4
     */
    public BaseJavetProxyPluginMultiple() {
        super();
        proxyableMethodsMap = new HashMap<>();
        proxyGetByStringMap = new HashMap<>();
        proxyGetBySymbolMap = new HashMap<>();
        targetObjectConstructorMap = new HashMap<>();
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetByString(
            Class<?> targetClass, String propertyName) {
        return (IClassProxyPluginFunction<E>) Optional.ofNullable(proxyGetByStringMap.get(targetClass))
                .map(map -> map.get(propertyName))
                .orElse(null);
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetBySymbol(
            Class<?> targetClass, String symbolName) {
        return (IClassProxyPluginFunction<E>) Optional.ofNullable(proxyGetBySymbolMap.get(targetClass))
                .map(map -> map.get(symbolName))
                .orElse(super.getProxyGetBySymbol(targetClass, symbolName));
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getTargetObjectConstructor(Class<?> targetClass) {
        return (IClassProxyPluginFunction<E>) Optional.ofNullable(targetObjectConstructorMap.get(targetClass))
                .orElse(super.getTargetObjectConstructor(targetClass));
    }

    @Override
    public boolean isMethodProxyable(String methodName, Class<?> targetClass) {
        return Optional.ofNullable(proxyableMethodsMap.get(targetClass))
                .map(m -> m.contains(methodName))
                .orElse(false);
    }
}
