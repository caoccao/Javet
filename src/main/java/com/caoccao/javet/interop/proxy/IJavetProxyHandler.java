/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;

/**
 * The interface Javet proxy handler.
 * <p>
 * Please refer to this <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy">doc</a>
 * for more details.
 *
 * @param <T> the type parameter
 * @since 0.9.6
 */
public interface IJavetProxyHandler<T> {
    /**
     * Apply to object
     *
     * @param target     the target
     * @param thisObject this object
     * @param arguments  the arguments
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Construct.
     *
     * @param target    the target
     * @param arguments the arguments
     * @param newTarget the new target
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

//    TODO:
//    default V8ValueBoolean defineProperty(V8Value target, V8Value property, V8PropertyDescriptor descriptor) throws JavetException {
//        throw new RuntimeException("Not implemented");
//    }

//    TODO:
//    default V8ValueBoolean deleteProperty(V8Value target, V8Value property, V8PropertyDescriptor descriptor) throws JavetException {
//        throw new RuntimeException("Not implemented");
//    }

    /**
     * Get by property.
     *
     * @param target   the target
     * @param property the property
     * @param receiver the receiver
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

//    TODO:
//    default V8PropertyDescriptor getOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException {
//        throw new RuntimeException("Not implemented");
//    }

    /**
     * Gets prototype of.
     *
     * @param target the target
     * @return the prototype of
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8Value getPrototypeOf(V8Value target) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets target object.
     *
     * @return the target object
     * @since 0.9.6
     */
    T getTargetObject();

    /**
     * Has property
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Is extensible.
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8ValueBoolean isExtensible(V8Value target) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Own keys V8 value.
     *
     * @param target the target
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8Value ownKeys(V8Value target) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Prevent extensions.
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8ValueBoolean preventExtensions(V8Value target) throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Set value by property.
     *
     * @param target        the target
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @param receiver      the receiver
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8ValueBoolean set(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets prototype of.
     *
     * @param target    the target
     * @param prototype the prototype
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    default V8ValueBoolean setPrototypeOf(V8Value target, V8Value prototype) throws JavetException {
        throw new RuntimeException("Not implemented");
    }
}
