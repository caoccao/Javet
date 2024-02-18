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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.interop.proxy.IJavetNonProxy;

/**
 * The interface Javet entity property descriptor.
 *
 * @param <T> the type parameter
 * @since 3.0.4
 */
public interface IJavetEntityPropertyDescriptor<T> extends IJavetNonProxy {
    /**
     * The constant PROXY_PROPERTY_CONFIGURABLE.
     *
     * @since 3.0.4
     */
    String PROXY_PROPERTY_CONFIGURABLE = "configurable";
    /**
     * The constant PROXY_PROPERTY_WRITABLE.
     *
     * @since 3.0.4
     */
    String PROXY_PROPERTY_WRITABLE = "writable";
    /**
     * The constant PROXY_PROPERTY_ENUMERABLE.
     *
     * @since 3.0.4
     */
    String PROXY_PROPERTY_ENUMERABLE = "enumerable";
    /**
     * The constant PROXY_PROPERTY_VALUE.
     *
     * @since 3.0.4
     */
    String PROXY_PROPERTY_VALUE = "value";

    /**
     * Gets value.
     *
     * @return the value
     * @since 3.0.4
     */
    T getValue();

    /**
     * Is configurable.
     *
     * @return true : configurable, false : not configurable
     * @since 3.0.4
     */
    boolean isConfigurable();

    /**
     * Is enumerable.
     *
     * @return true : enumerable, false : enumerable
     * @since 3.0.4
     */
    boolean isEnumerable();

    /**
     * Is writable.
     *
     * @return true : writable, false : not writable
     * @since 3.0.4
     */
    boolean isWritable();

    /**
     * Sets configurable.
     *
     * @param configurable the configurable
     * @since 3.0.4
     */
    void setConfigurable(boolean configurable);

    /**
     * Sets enumerable.
     *
     * @param enumerable the enumerable
     * @since 3.0.4
     */
    void setEnumerable(boolean enumerable);

    /**
     * Sets value.
     *
     * @param value the value
     * @since 3.0.4
     */
    void setValue(T value);

    /**
     * Sets writable.
     *
     * @param writable the writable
     * @since 3.0.4
     */
    void setWritable(boolean writable);
}
