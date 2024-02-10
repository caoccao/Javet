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

package com.caoccao.javet.entities;

import com.caoccao.javet.interfaces.IJavetEntityPropertyDescriptor;

/**
 * The type Javet entity property descriptor.
 *
 * @param <T> the type parameter
 * @since 3.0.4
 */
public class JavetEntityPropertyDescriptor<T> implements IJavetEntityPropertyDescriptor<T> {
    /**
     * The Configurable.
     *
     * @since 3.0.4
     */
    protected boolean configurable;
    /**
     * The Enumerable.
     *
     * @since 3.0.4
     */
    protected boolean enumerable;
    /**
     * The Value.
     *
     * @since 3.0.4
     */
    protected T value;
    /**
     * The Writable.
     *
     * @since 3.0.4
     */
    protected boolean writable;

    /**
     * Instantiates a new Javet entity property descriptor.
     *
     * @param configurable the configurable
     * @param enumerable   the enumerable
     * @param writable     the writable
     * @since 3.0.4
     */
    public JavetEntityPropertyDescriptor(boolean configurable, boolean enumerable, boolean writable) {
        this(configurable, enumerable, writable, null);
    }

    /**
     * Instantiates a new Javet entity property descriptor.
     *
     * @param configurable the configurable
     * @param enumerable   the enumerable
     * @param writable     the writable
     * @param value        the value
     * @since 3.0.4
     */
    public JavetEntityPropertyDescriptor(boolean configurable, boolean enumerable, boolean writable, T value) {
        this.configurable = configurable;
        this.enumerable = enumerable;
        this.value = value;
        this.writable = writable;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean isConfigurable() {
        return configurable;
    }

    @Override
    public boolean isEnumerable() {
        return enumerable;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public void setConfigurable(boolean configurable) {
        this.configurable = configurable;
    }

    @Override
    public void setEnumerable(boolean enumerable) {
        this.enumerable = enumerable;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
