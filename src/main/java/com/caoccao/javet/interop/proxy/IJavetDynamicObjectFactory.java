/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueProxy;

/**
 * The interface Javet dynamic object factory.
 *
 * @since 2.0.1
 */
public interface IJavetDynamicObjectFactory {
    /**
     * Is dynamic object conversion supported.
     *
     * @param type    the type to be converted
     * @param v8Value the V8 value
     * @return true: supported, false: not supported
     * @since 2.0.1
     */
    default boolean isSupported(Class<?> type, V8Value v8Value) {
        if (type.isInterface() || type.isSynthetic() || type.isPrimitive() || type.isArray()
                || type.isEnum() || type.isAnnotation()) {
            return false;
        }
        return v8Value instanceof V8ValueObject && (!(v8Value instanceof V8ValueProxy));
    }

    /**
     * Convert from V8 value to a dynamic object.
     *
     * @param type    the type to be converted
     * @param v8Value the V8 value
     * @return the object
     * @since 2.0.1
     */
    Object toObject(Class<?> type, V8Value v8Value);
}
