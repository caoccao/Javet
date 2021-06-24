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
import java.util.HashMap;
import java.util.Map;

/**
 * The type Binding context.
 */
public class BindingContext {
    /**
     * The Function map.
     */
    protected Map<String, MethodDescriptor> functionMap;
    /**
     * The Property getter map.
     */
    protected Map<String, MethodDescriptor> propertyGetterMap;
    /**
     * The Property setter map.
     */
    protected Map<String, MethodDescriptor> propertySetterMap;
    /**
     * The V8 runtime setter.
     */
    protected Method v8RuntimeSetter;

    /**
     * Instantiates a new Binding context.
     */
    public BindingContext() {
        functionMap = new HashMap<>();
        propertyGetterMap = new HashMap<>();
        propertySetterMap = new HashMap<>();
        v8RuntimeSetter = null;
    }

    /**
     * Gets function map.
     *
     * @return the function map
     */
    public Map<String, MethodDescriptor> getFunctionMap() {
        return functionMap;
    }

    /**
     * Gets property getter map.
     *
     * @return the property getter map
     */
    public Map<String, MethodDescriptor> getPropertyGetterMap() {
        return propertyGetterMap;
    }

    /**
     * Gets property setter map.
     *
     * @return the property setter map
     */
    public Map<String, MethodDescriptor> getPropertySetterMap() {
        return propertySetterMap;
    }

    /**
     * Gets V8 runtime setter.
     *
     * @return the V8 runtime setter
     */
    public Method getV8RuntimeSetter() {
        return v8RuntimeSetter;
    }

    /**
     * Sets V8 runtime setter.
     *
     * @param v8RuntimeSetter the V8 runtime setter
     */
    public void setV8RuntimeSetter(Method v8RuntimeSetter) {
        this.v8RuntimeSetter = v8RuntimeSetter;
    }
}
