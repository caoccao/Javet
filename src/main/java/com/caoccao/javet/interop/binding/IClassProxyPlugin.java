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

package com.caoccao.javet.interop.binding;

import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;

import java.util.Set;

/**
 * The interface Class proxy plugin.
 *
 * @since 3.0.4
 */
public interface IClassProxyPlugin {
    /**
     * Delete by object property.
     *
     * @param targetObject the target object
     * @param propertyKey  the property key
     * @return true : deleted, false : not deleted
     * @since 3.0.4
     */
    boolean deleteByObject(Object targetObject, Object propertyKey);

    /**
     * Gets by index.
     *
     * @param targetObject the target object
     * @param index        the index
     * @return the by index
     */
    Object getByIndex(Object targetObject, int index);

    /**
     * Gets the plugin name.
     *
     * @return the plugin name
     * @since 3.0.4
     */
    String getName();

    /**
     * Gets proxy get function by string property.
     *
     * @param <E>          the type parameter
     * @param targetClass  the target class
     * @param propertyName the property name
     * @return the proxy get by string
     * @since 3.0.4
     */
    <E extends Exception> IClassProxyPluginFunction<E> getProxyGetByString(
            Class<?> targetClass, String propertyName);

    /**
     * Gets proxy get function by symbol property.
     *
     * @param <E>         the type parameter
     * @param targetClass the target class
     * @param symbolName  the symbol name
     * @return the proxy get by symbol
     * @since 3.0.4
     */
    <E extends Exception> IClassProxyPluginFunction<E> getProxyGetBySymbol(
            Class<?> targetClass, String symbolName);

    /**
     * Get proxy own keys for Object.getOwnPropertyNames().
     *
     * @param targetObject the target object
     * @return the own keys
     * @since 3.0.4
     */
    Object[] getProxyOwnKeys(Object targetObject);

    /**
     * Gets proxy own property descriptor.
     *
     * @param <T>          the type parameter
     * @param targetObject the target object
     * @param propertyName the property name
     * @return the proxy own property descriptor
     * @since 3.0.4
     */
    <T> IJavetEntityPropertyDescriptor<T> getProxyOwnPropertyDescriptor(Object targetObject, Object propertyName);

    /**
     * Gets proxy symbol to primitive function.
     *
     * @param <E> the type parameter
     * @return the proxy symbol to primitive function
     * @since 3.0.4
     */
    <E extends Exception> IClassProxyPluginFunction<E> getProxySymbolToPrimitive();

    /**
     * Gets target object constructor.
     *
     * @param <E>         the type parameter
     * @param targetClass the target class
     * @return the target object constructor
     * @since 3.0.4
     */
    <E extends Exception> IClassProxyPluginFunction<E> getTargetObjectConstructor(Class<?> targetClass);

    /**
     * Has by object property.
     *
     * @param targetObject the target object
     * @param propertyKey  the property key
     * @return true : has, false : not has
     */
    boolean hasByObject(Object targetObject, Object propertyKey);

    /**
     * Is keyword delete supported.
     *
     * @param targetClass the target class
     * @return true : supported, false : not supported
     * @since 3.0.4
     */
    boolean isDeleteSupported(Class<?> targetClass);

    /**
     * Is has() supported.
     *
     * @param targetClass the target class
     * @return true : supported, false : not supported
     * @since 3.0.4
     */
    boolean isHasSupported(Class<?> targetClass);

    /**
     * Is index supported.
     *
     * @param targetClass the target class
     * @return true : supported, false : not supported
     * @since 3.0.4
     */
    boolean isIndexSupported(Class<?> targetClass);

    /**
     * Is method proxyable.
     *
     * @param methodName  the method name
     * @param targetClass the target class
     * @return true : yes, false : no
     * @since 3.0.4
     */
    boolean isMethodProxyable(String methodName, Class<?> targetClass);

    /**
     * Is ownKeys() supported.
     *
     * @param targetClass the target class
     * @return true : supported, false : not supported
     * @since 3.0.4
     */
    boolean isOwnKeysSupported(Class<?> targetClass);

    /**
     * Is the target class proxyable.
     *
     * @param targetClass the target class
     * @return true : proxyable, false : not proxyable
     * @since 3.0.4
     */
    boolean isProxyable(Class<?> targetClass);

    /**
     * Is unique key supported.
     *
     * @param targetClass the target class
     * @return true : supported, false : not supported
     * @since 3.0.4
     */
    boolean isUniqueKeySupported(Class<?> targetClass);

    /**
     * Populate unique keys.
     *
     * @param uniqueKeySet the unique key set
     * @param targetObject the target object
     * @since 3.0.4
     */
    void populateUniqueKeys(Set<String> uniqueKeySet, Object targetObject);

    /**
     * Sets by index.
     *
     * @param targetObject the target object
     * @param index        the index
     * @param value        the value
     * @return true : set, false : not set
     * @since 3.0.4
     */
    boolean setByIndex(Object targetObject, int index, Object value);
}
