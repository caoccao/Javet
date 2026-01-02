/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInReflect;

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
     * The constant FUNCTION_NAME_TO_JSON.
     *
     * @since 3.0.4
     */
    String FUNCTION_NAME_TO_JSON = "toJSON";
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
     * The constant PROXY_FUNCTION_NAME_DELETE_PROPERTY.
     *
     * @since 3.0.3
     */
    String PROXY_FUNCTION_NAME_DELETE_PROPERTY = "deleteProperty";
    /**
     * The constant PROXY_FUNCTION_NAME_GET.
     *
     * @since 2.2.0
     */
    String PROXY_FUNCTION_NAME_GET = "get";
    /**
     * The constant PROXY_FUNCTION_NAME_GET_OWN_PROPERTY_DESCRIPTOR.
     *
     * @since 3.0.4
     */
    String PROXY_FUNCTION_NAME_GET_OWN_PROPERTY_DESCRIPTOR = "getOwnPropertyDescriptor";
    /**
     * The constant PROXY_FUNCTION_NAME_GET_PROTOTYPE_OF.
     *
     * @since 3.1.3
     */
    String PROXY_FUNCTION_NAME_GET_PROTOTYPE_OF = "getPrototypeOf";
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
     * handler.apply()
     * The handler.apply() method is a trap for the [[Call]] object internal method,
     * which is used by operations such as function calls.
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
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.apply(target, thisObject, arguments);
        }
    }

    /**
     * handler.construct()
     * The handler.construct() method is a trap for the [[Construct]] object internal method,
     * which is used by operations such as the new operator.
     * In order for the new operation to be valid on the resulting Proxy object,
     * the target used to initialize the proxy must itself be a valid constructor.
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
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.construct(target, arguments, newTarget);
        }
    }

    /**
     * handler.defineProperty()
     * The handler.defineProperty() method is a trap for the [[DefineOwnProperty]] object internal method,
     * which is used by operations such as Object.defineProperty().
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
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.defineProperty(target, property, descriptor);
        }
    }

    /**
     * handler.deleteProperty()
     * The handler.deleteProperty() method is a trap for the [[Delete]] object internal method,
     * which is used by operations such as the delete operator.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean deleteProperty(V8Value target, V8Value property) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.deleteProperty(target, property);
        }
    }

    /**
     * handler.get()
     * The handler.get() method is a trap for the [[Get]] object internal method,
     * which is used by operations such as property accessors.
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
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect._get(target, property);
        }
    }

    /**
     * handler.getOwnPropertyDescriptor()
     * The handler.getOwnPropertyDescriptor() method is a trap for the [[GetOwnProperty]] object internal method,
     * which is used by operations such as Object.getOwnPropertyDescriptor().
     *
     * @param target   the target
     * @param property the property
     * @return the own property descriptor
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value getOwnPropertyDescriptor(V8Value target, V8Value property) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.getOwnPropertyDescriptor(target, property);
        }
    }

    /**
     * handler.getPrototypeOf()
     * The handler.getPrototypeOf() method is a trap for the [[GetPrototypeOf]] object internal method,
     * which is used by operations such as Object.getPrototypeOf().
     *
     * @param target the target
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8Value getPrototypeOf(V8Value target) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.getPrototypeOf(target);
        }
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
     * handler.has()
     * The handler.has() method is a trap for the [[HasProperty]] object internal method,
     * which is used by operations such as the in operator.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean has(V8Value target, V8Value property) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect._has(target, property);
        }
    }

    /**
     * handler.isExtensible()
     * The handler.isExtensible() method is a trap for the [[IsExtensible]] object internal method,
     * which is used by operations such as Object.isExtensible().
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean isExtensible(V8Value target) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.isExtensible(target);
        }
    }

    /**
     * handler.ownKeys()
     * The handler.ownKeys() method is a trap for the [[OwnPropertyKeys]] object internal method,
     * which is used by operations such as Object.keys(), Reflect.ownKeys(), etc.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueArray ownKeys(V8Value target) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.ownKeys(target);
        }
    }

    /**
     * handler.preventExtensions()
     * The handler.preventExtensions() method is a trap for the [[PreventExtensions]] object internal method,
     * which is used by operations such as Object.preventExtensions().
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean preventExtensions(V8Value target) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.preventExtensions(target);
        }
    }

    /**
     * handler.set()
     * The handler.set() method is a trap for the [[Set]] object internal method,
     * which is used by operations such as using property accessors to set a property's value.
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
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect._set(target, propertyKey, propertyValue, receiver);
        }
    }

    /**
     * handler.setPrototypeOf()
     * The handler.setPrototypeOf() method is a trap for the [[SetPrototypeOf]] object internal method,
     * which is used by operations such as Object.setPrototypeOf().
     *
     * @param target    the target
     * @param prototype the prototype
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 0.9.6
     */
    default V8ValueBoolean setPrototypeOf(V8Value target, V8Value prototype) throws JavetException, E {
        try (V8ValueBuiltInReflect v8ValueBuiltInReflect = getV8Runtime().getGlobalObject().getBuiltInReflect()) {
            return v8ValueBuiltInReflect.setPrototypeOf(target, prototype);
        }
    }
}
