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

/**
 * The type Javet proxy plugin class.
 *
 * @since 3.0.4
 */
public class JavetProxyPluginClass extends BaseJavetProxyPlugin {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = Class.class.getName();
    private static final JavetProxyPluginClass instance = new JavetProxyPluginClass();

    /**
     * Instantiates a new Javet proxy plugin class.
     *
     * @since 3.0.4
     */
    public JavetProxyPluginClass() {
        super();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginClass getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetByString(
            Class<?> targetClass, String propertyName) {
        return null;
    }

    @Override
    public <E extends Exception> IClassProxyPluginFunction<E> getProxyGetBySymbol(
            Class<?> targetClass, String symbolName) {
        return null;
    }

    @Override
    public boolean isMethodProxyable(String methodName, Class<?> targetClass) {
        return false;
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null && Class.class.isAssignableFrom(targetClass);
    }
}
