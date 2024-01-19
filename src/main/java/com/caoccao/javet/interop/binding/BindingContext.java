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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Binding context.
 *
 * @since 0.9.2
 */
public class BindingContext {
    /**
     * The Function map.
     *
     * @since 0.9.2
     */
    protected Map<String, MethodDescriptor> functionMap;
    /**
     * The Property getter map.
     *
     * @since 0.9.2
     */
    protected Map<String, MethodDescriptor> propertyGetterMap;
    /**
     * The Property setter map.
     *
     * @since 0.9.2
     */
    protected Map<String, MethodDescriptor> propertySetterMap;
    /**
     * The V8 binding enabler.
     *
     * @since 0.9.3
     */
    protected Method v8BindingEnabler;
    /**
     * The V8 runtime setter.
     *
     * @since 0.9.2
     */
    protected Method v8RuntimeSetter;

    /**
     * Instantiates a new Binding context.
     *
     * @since 0.9.2
     */
    public BindingContext() {
        functionMap = new HashMap<>();
        propertyGetterMap = new HashMap<>();
        propertySetterMap = new HashMap<>();
        v8BindingEnabler = null;
        v8RuntimeSetter = null;
    }

    /**
     * Gets function map.
     *
     * @return the function map
     * @since 0.9.2
     */
    public Map<String, MethodDescriptor> getFunctionMap() {
        return functionMap;
    }

    /**
     * Gets property getter map.
     *
     * @return the property getter map
     * @since 0.9.2
     */
    public Map<String, MethodDescriptor> getPropertyGetterMap() {
        return propertyGetterMap;
    }

    /**
     * Gets property setter map.
     *
     * @return the property setter map
     * @since 0.9.2
     */
    public Map<String, MethodDescriptor> getPropertySetterMap() {
        return propertySetterMap;
    }

    /**
     * Gets V8 bind enabler.
     *
     * @return the V8 bind enabler
     * @since 0.9.3
     */
    public Method getV8BindingEnabler() {
        return v8BindingEnabler;
    }

    /**
     * Gets V8 runtime setter.
     *
     * @return the V8 runtime setter
     * @since 0.9.2
     */
    public Method getV8RuntimeSetter() {
        return v8RuntimeSetter;
    }

    /**
     * Sets V8 bind enabler.
     *
     * @param v8BindingEnabler the V8 bind enabler
     * @since 0.9.3
     */
    public void setV8BindingEnabler(Method v8BindingEnabler) {
        this.v8BindingEnabler = v8BindingEnabler;
    }

    /**
     * Sets V8 runtime setter.
     *
     * @param v8RuntimeSetter the V8 runtime setter
     * @since 0.9.2
     */
    public void setV8RuntimeSetter(Method v8RuntimeSetter) {
        this.v8RuntimeSetter = v8RuntimeSetter;
    }
}
