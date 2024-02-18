/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.interop.binding.IClassProxyPlugin;
import com.caoccao.javet.interop.proxy.IJavetReflectionObjectFactory;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginArray;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginList;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginMap;
import com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginSet;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Javet converter config.
 *
 * @param <T> the type parameter
 * @since 0.9.4
 */
public class JavetConverterConfig<T extends JavetConverterConfig<T>> {
    /**
     * The constant DEFAULT_MAX_DEPTH.
     *
     * @since 0.9.4
     */
    public static final int DEFAULT_MAX_DEPTH = 20;
    /**
     * The Proxy plugins.
     *
     * @since 3.0.4
     */
    protected final List<IClassProxyPlugin> proxyPlugins;
    /**
     * The Default boolean.
     *
     * @since 0.9.4
     */
    protected boolean defaultBoolean;
    /**
     * The Default byte.
     *
     * @since 0.9.4
     */
    protected byte defaultByte;
    /**
     * The Default char.
     *
     * @since 0.9.4
     */
    protected char defaultChar;
    /**
     * The Default double.
     *
     * @since 0.9.4
     */
    protected double defaultDouble;
    /**
     * The Default float.
     *
     * @since 0.9.4
     */
    protected float defaultFloat;
    /**
     * The Default int.
     *
     * @since 0.9.4
     */
    protected int defaultInt;
    /**
     * The Default long.
     *
     * @since 0.9.4
     */
    protected long defaultLong;
    /**
     * The Default short.
     *
     * @since 0.9.4
     */
    protected short defaultShort;
    /**
     * The Extract function source code.
     *
     * @since 0.9.4
     */
    protected boolean extractFunctionSourceCode;
    /**
     * The Max depth.
     *
     * @since 0.9.3
     */
    protected int maxDepth;
    /**
     * The Reflection object factory.
     *
     * @since 2.0.1
     */
    protected IJavetReflectionObjectFactory reflectionObjectFactory;
    /**
     * This flag determines whether function should be skipped in object or not.
     *
     * @since 0.9.4
     */
    protected boolean skipFunctionInObject;

    /**
     * Instantiates a new Javet converter config.
     *
     * @since 0.9.4
     */
    public JavetConverterConfig() {
        defaultBoolean = false;
        defaultByte = (byte) 0;
        defaultChar = '\0';
        defaultDouble = 0D;
        defaultFloat = 0F;
        defaultInt = 0;
        defaultLong = 0L;
        defaultShort = 0;
        extractFunctionSourceCode = false;
        maxDepth = DEFAULT_MAX_DEPTH;
        proxyPlugins = new ArrayList<>();
        reflectionObjectFactory = null;
        skipFunctionInObject = true;
    }

    /**
     * Gets default boolean boolean.
     *
     * @return the boolean
     * @since 0.9.4
     */
    public boolean getDefaultBoolean() {
        return defaultBoolean;
    }

    /**
     * Gets default byte.
     *
     * @return the default byte
     * @since 0.9.4
     */
    public byte getDefaultByte() {
        return defaultByte;
    }

    /**
     * Gets default char.
     *
     * @return the default char
     * @since 0.9.4
     */
    public char getDefaultChar() {
        return defaultChar;
    }

    /**
     * Gets default double.
     *
     * @return the default double
     * @since 0.9.4
     */
    public double getDefaultDouble() {
        return defaultDouble;
    }

    /**
     * Gets default float.
     *
     * @return the default float
     * @since 0.9.4
     */
    public float getDefaultFloat() {
        return defaultFloat;
    }

    /**
     * Gets default int.
     *
     * @return the default int
     * @since 0.9.4
     */
    public int getDefaultInt() {
        return defaultInt;
    }

    /**
     * Gets default long.
     *
     * @return the default long
     * @since 0.9.4
     */
    public long getDefaultLong() {
        return defaultLong;
    }

    /**
     * Gets default short.
     *
     * @return the default short
     * @since 0.9.4
     */
    public short getDefaultShort() {
        return defaultShort;
    }

    /**
     * Gets max depth.
     *
     * @return the max depth
     * @since 0.9.3
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Gets proxy plugins.
     *
     * @return the proxy plugins
     * @since 3.0.4
     */
    public List<IClassProxyPlugin> getProxyPlugins() {
        return proxyPlugins;
    }

    /**
     * Gets reflection object factory.
     *
     * @return the reflection object factory
     * @since 2.0.1
     */
    public IJavetReflectionObjectFactory getReflectionObjectFactory() {
        return reflectionObjectFactory;
    }

    /**
     * Is extract function source code.
     *
     * @return true : extract, false : skip
     * @since 0.9.4
     */
    public boolean isExtractFunctionSourceCode() {
        return extractFunctionSourceCode;
    }

    /**
     * Is proxy array enabled.
     *
     * @return true : enabled, false : disabled
     * @since 3.0.4
     */
    public boolean isProxyArrayEnabled() {
        return getProxyPlugins().stream().anyMatch(p -> p instanceof JavetProxyPluginArray);
    }

    /**
     * Is proxy list enabled.
     *
     * @return true : enabled, false : disabled
     * @since 3.0.3
     */
    public boolean isProxyListEnabled() {
        return getProxyPlugins().stream().anyMatch(p -> p instanceof JavetProxyPluginList);
    }

    /**
     * Is proxy map enabled.
     *
     * @return true : enabled, false : disabled
     * @since 0.9.6
     */
    public boolean isProxyMapEnabled() {
        return getProxyPlugins().stream().anyMatch(p -> p instanceof JavetProxyPluginMap);
    }

    /**
     * Is proxy set enabled.
     *
     * @return true : enabled, false : disabled
     * @since 0.9.8
     */
    public boolean isProxySetEnabled() {
        return getProxyPlugins().stream().anyMatch(p -> p instanceof JavetProxyPluginSet);
    }

    /**
     * Is skip functions.
     *
     * @return true : skip, false : not skip
     * @since 0.9.4
     */
    public boolean isSkipFunctionInObject() {
        return skipFunctionInObject;
    }

    /**
     * Sets default boolean.
     *
     * @param defaultBoolean the default boolean
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultBoolean(boolean defaultBoolean) {
        this.defaultBoolean = defaultBoolean;
        return this;
    }

    /**
     * Sets default byte.
     *
     * @param defaultByte the default byte
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultByte(byte defaultByte) {
        this.defaultByte = defaultByte;
        return this;
    }

    /**
     * Sets default char.
     *
     * @param defaultChar the default char
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultChar(char defaultChar) {
        this.defaultChar = defaultChar;
        return this;
    }

    /**
     * Sets default double.
     *
     * @param defaultDouble the default double
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultDouble(double defaultDouble) {
        this.defaultDouble = defaultDouble;
        return this;
    }

    /**
     * Sets default float.
     *
     * @param defaultFloat the default float
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultFloat(float defaultFloat) {
        this.defaultFloat = defaultFloat;
        return this;
    }

    /**
     * Sets default int.
     *
     * @param defaultInt the default int
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultInt(int defaultInt) {
        this.defaultInt = defaultInt;
        return this;
    }

    /**
     * Sets default long.
     *
     * @param defaultLong the default long
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultLong(long defaultLong) {
        this.defaultLong = defaultLong;
        return this;
    }

    /**
     * Sets default short.
     *
     * @param defaultShort the default short
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setDefaultShort(short defaultShort) {
        this.defaultShort = defaultShort;
        return this;
    }

    /**
     * Sets extract function source code.
     *
     * @param extractFunctionSourceCode the extract function source code
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setExtractFunctionSourceCode(boolean extractFunctionSourceCode) {
        this.extractFunctionSourceCode = extractFunctionSourceCode;
        return this;
    }

    /**
     * Sets max depth.
     *
     * @param maxDepth the max depth
     * @return the self
     * @since 0.9.3
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    /**
     * Sets proxy array enabled.
     *
     * @param proxyArrayEnabled the proxy array enabled
     * @return the self
     * @since 3.0.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setProxyArrayEnabled(boolean proxyArrayEnabled) {
        if (proxyArrayEnabled) {
            if (!isProxyArrayEnabled()) {
                getProxyPlugins().add(0, JavetProxyPluginArray.getInstance());
            }
        } else {
            getProxyPlugins().removeIf(p -> p instanceof JavetProxyPluginArray);
        }
        return this;
    }

    /**
     * Sets proxy list enabled.
     *
     * @param proxyListEnabled the proxy list enabled
     * @return the self
     * @since 3.0.3
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setProxyListEnabled(boolean proxyListEnabled) {
        if (proxyListEnabled) {
            if (!isProxyListEnabled()) {
                getProxyPlugins().add(0, JavetProxyPluginList.getInstance());
            }
        } else {
            getProxyPlugins().removeIf(p -> p instanceof JavetProxyPluginList);
        }
        return this;
    }

    /**
     * Sets proxy map enabled.
     *
     * @param proxyMapEnabled the proxy map enabled
     * @return the self
     * @since 0.9.6
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setProxyMapEnabled(boolean proxyMapEnabled) {
        if (proxyMapEnabled) {
            if (!isProxyMapEnabled()) {
                getProxyPlugins().add(0, JavetProxyPluginMap.getInstance());
            }
        } else {
            getProxyPlugins().removeIf(p -> p instanceof JavetProxyPluginMap);
        }
        return this;
    }

    /**
     * Sets proxy set enabled.
     *
     * @param proxySetEnabled the proxy set enabled
     * @return the self
     * @since 0.9.8
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setProxySetEnabled(boolean proxySetEnabled) {
        if (proxySetEnabled) {
            if (!isProxySetEnabled()) {
                getProxyPlugins().add(0, JavetProxyPluginSet.getInstance());
            }
        } else {
            getProxyPlugins().removeIf(p -> p instanceof JavetProxyPluginSet);
        }
        return this;
    }

    /**
     * Sets reflection object factory.
     *
     * @param reflectionObjectFactory the reflection object factory
     * @return the self
     * @since 2.0.1
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setReflectionObjectFactory(IJavetReflectionObjectFactory reflectionObjectFactory) {
        this.reflectionObjectFactory = reflectionObjectFactory;
        return this;
    }

    /**
     * Sets skip functions.
     *
     * @param skipFunctionInObject the skip functions
     * @return the self
     * @since 0.9.4
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetConverterConfig<T> setSkipFunctionInObject(boolean skipFunctionInObject) {
        this.skipFunctionInObject = skipFunctionInObject;
        return this;
    }
}
