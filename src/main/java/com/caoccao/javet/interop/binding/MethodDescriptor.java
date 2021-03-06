/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.binding;

import java.lang.reflect.Method;

/**
 * The type Method descriptor.
 */
public class MethodDescriptor {
    /**
     * The Method.
     */
    protected Method method;
    /**
     * The This object required.
     */
    protected boolean thisObjectRequired;

    /**
     * Instantiates a new Method descriptor.
     *
     * @param method             the method
     * @param thisObjectRequired the this object required
     */
    public MethodDescriptor(Method method, boolean thisObjectRequired) {
        this.method = method;
        this.thisObjectRequired = thisObjectRequired;
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Is this object required boolean.
     *
     * @return the boolean
     */
    public boolean isThisObjectRequired() {
        return thisObjectRequired;
    }
}
