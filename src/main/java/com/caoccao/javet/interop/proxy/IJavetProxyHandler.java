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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The interface Javet proxy handler.
 * <p>
 * Please refer to this <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Proxy">doc</a>
 * for more details.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 0.9.6
 */
public interface IJavetProxyHandler<T, E extends Exception> {
    /**
     * The constant FUNCTION_NAME_TO_V8_VALUE.
     *
     * @since 1.0.4
     */
    String FUNCTION_NAME_TO_V8_VALUE = "toV8Value";
    /**
     * The constant PROXY_FUNCTION_NAME_APPLY.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_APPLY = "apply";
    /**
     * The constant PROXY_FUNCTION_NAME_CONSTRUCT.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_CONSTRUCT = "construct";
    /**
     * The constant PROXY_FUNCTION_NAME_GET.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_GET = "get";
    /**
     * The constant PROXY_FUNCTION_NAME_HAS.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_HAS = "has";
    /**
     * The constant PROXY_FUNCTION_NAME_OWN_KEYS.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_OWN_KEYS = "ownKeys";
    /**
     * The constant PROXY_FUNCTION_NAME_SET.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_SET = "set";

    /**
     * Apply to object.
     *
     * @param target     the target
     * @param thisObject this object
     * @param arguments  the arguments
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Construct.
     *
     * @param target    the target
     * @param arguments the arguments
     * @param newTarget the new target
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Define property.
     *
     * @param target     the target
     * @param property   the property
     * @param descriptor the descriptor
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean defineProperty(
            V8Value target, V8Value property, V8ValueObject descriptor)
            throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Delete property.
     *
     * @param target     the target
     * @param property   the property
     * @param descriptor the descriptor
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean deleteProperty(
            V8Value target, V8Value property, V8ValueObject descriptor)
            throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Get by property.
     *
     * @param target   the target
     * @param property the property
     * @param receiver the receiver
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Gets own property descriptor.
     *
     * @param target   the target
     * @param property the property
     * @return the own property descriptor
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueObject getOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException, E {
        return getV8Runtime().createV8ValueObject();
    }

    /**
     * Gets prototype of.
     *
     * @param target the target
     * @return the prototype of
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8Value getPrototypeOf(V8Value target) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Gets target object.
     *
     * @return the target object
     * @since 0.9.6
     */
    T getTargetObject();

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 2.2.0
     */
    V8Runtime getV8Runtime();

    /**
     * Has property
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean has(V8Value target, V8Value property) throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Is extensible.
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean isExtensible(V8Value target) throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Own keys.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueArray ownKeys(V8Value target) throws JavetException, E {
        return getV8Runtime().createV8ValueArray();
    }

    /**
     * Prevent extensions.
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean preventExtensions(V8Value target) throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
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
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean set(
            V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Sets prototype of.
     *
     * @param target    the target
     * @param prototype the prototype
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean setPrototypeOf(V8Value target, V8Value prototype) throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }
}
