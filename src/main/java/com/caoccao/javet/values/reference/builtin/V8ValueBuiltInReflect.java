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
     * The constant FUNCTION_GET.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET = "get";
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
     * Reflect.get()
     * The Reflect.get() static method is like the property accessor syntax, but as a function.
     *
     * @param target   the target
     * @param property the property
     * @return the v 8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value get(V8Value target, V8Value property) throws JavetException {
        return invoke(FUNCTION_GET, Objects.requireNonNull(target), Objects.requireNonNull(property));
    }

    @Override
    public V8ValueBuiltInReflect toClone() throws JavetException {
        return this;
    }
}
