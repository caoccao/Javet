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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

/**
 * The type V8 value built in reflect.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public class V8ValueBuiltInReflect extends V8ValueObject {
    /**
     * The constant FUNCTION_APPLY.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_APPLY = "apply";
    /**
     * The constant FUNCTION_GET.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET = "get";
    /**
     * The constant FUNCTION_GET_OWN_PROPERTY_DESCRIPTOR.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET_OWN_PROPERTY_DESCRIPTOR = "getOwnPropertyDescriptor";
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = "Reflect";

    /**
     * Instantiates a new V8 value built in reflect.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBuiltInReflect(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Reflect.apply().
     * The Reflect.apply() static method calls a target function with arguments as specified.
     *
     * @param target     the target
     * @param thisObject this object
     * @param arguments  the arguments
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException {
        return invoke(
                FUNCTION_APPLY,
                Objects.requireNonNull(target),
                Objects.requireNonNull(thisObject),
                Objects.requireNonNull(arguments));
    }

    /**
     * Reflect.get().
     * The Reflect.get() static method is like the property accessor syntax, but as a function.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value get(V8Value target, V8Value property) throws JavetException {
        return invoke(
                FUNCTION_GET,
                Objects.requireNonNull(target),
                Objects.requireNonNull(property));
    }

    /**
     * Reflect.getOwnPropertyDescriptor().
     * The Reflect.getOwnPropertyDescriptor() static method is like Object.getOwnPropertyDescriptor().
     * It returns a property descriptor of the given property if it exists on the object, undefined otherwise.
     *
     * @param target   the target
     * @param property the property
     * @return the own property descriptor
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value getOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException {
        return invoke(
                FUNCTION_GET_OWN_PROPERTY_DESCRIPTOR,
                Objects.requireNonNull(target),
                Objects.requireNonNull(property));
    }

    @Override
    public V8ValueBuiltInReflect toClone() throws JavetException {
        return this;
    }
}
