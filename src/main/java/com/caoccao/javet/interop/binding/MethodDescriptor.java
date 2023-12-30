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

package com.caoccao.javet.interop.binding;

import com.caoccao.javet.enums.V8ValueSymbolType;

import java.lang.reflect.Method;

/**
 * The type Method descriptor.
 *
 * @since 0.9.2
 */
public class MethodDescriptor {
    /**
     * The Method.
     *
     * @since 0.9.2
     */
    protected Method method;
    /**
     * The Symbol type.
     *
     * @since 0.9.11
     */
    protected V8ValueSymbolType symbolType;
    /**
     * This object required.
     *
     * @since 0.9.2
     */
    protected boolean thisObjectRequired;

    /**
     * Instantiates a new Method descriptor.
     *
     * @param method             the method
     * @param thisObjectRequired this object required
     * @since 0.9.2
     */
    public MethodDescriptor(Method method, boolean thisObjectRequired) {
        this(method, thisObjectRequired, V8ValueSymbolType.None);
    }

    /**
     * Instantiates a new Method descriptor.
     *
     * @param method             the method
     * @param thisObjectRequired this object required
     * @param symbolType         the symbol type
     * @since 0.9.11
     */
    public MethodDescriptor(Method method, boolean thisObjectRequired, V8ValueSymbolType symbolType) {
        this.method = method;
        this.symbolType = symbolType;
        this.thisObjectRequired = thisObjectRequired;
    }

    /**
     * Gets method.
     *
     * @return the method
     * @since 0.9.2
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Gets symbol type.
     *
     * @return the symbol type
     * @since 0.9.11
     */
    public V8ValueSymbolType getSymbolType() {
        return symbolType;
    }

    /**
     * Is this object required boolean.
     *
     * @return the boolean
     * @since 0.9.2
     */
    public boolean isThisObjectRequired() {
        return thisObjectRequired;
    }
}
