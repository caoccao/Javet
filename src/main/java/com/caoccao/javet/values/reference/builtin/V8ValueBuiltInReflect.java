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
import com.caoccao.javet.values.primitive.V8ValueBoolean;
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
     * The constant FUNCTION_CONSTRUCT.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_CONSTRUCT = "construct";
    /**
     * The constant FUNCTION_DEFINE_PROPERTY.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_DEFINE_PROPERTY = "defineProperty";
    /**
     * The constant FUNCTION_DELETE_PROPERTY.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_DELETE_PROPERTY = "deleteProperty";
    /**
     * The constant FUNCTION_GET.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET = "get";
    /**
     * The constant FUNCTION_GET_PROTOTYPE_OF.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET_PROTOTYPE_OF = "getPrototypeOf";
    /**
     * The constant FUNCTION_GET_OWN_PROPERTY_DESCRIPTOR.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_GET_OWN_PROPERTY_DESCRIPTOR = "getOwnPropertyDescriptor";
    /**
     * The constant FUNCTION_HAS.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_HAS = "has";
    /**
     * The constant FUNCTION_IS_EXTENSIBLE.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_IS_EXTENSIBLE = "isExtensible";
    /**
     * The constant FUNCTION_OWN_KEYS.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_OWN_KEYS = "ownKeys";
    /**
     * The constant FUNCTION_PREVENT_EXTENSIONS.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_PREVENT_EXTENSIONS = "preventExtensions";
    /**
     * The constant FUNCTION_SET.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_SET = "set";
    /**
     * The constant FUNCTION_SET_PROTOTYPE_OF.
     *
     * @since 3.0.4
     */
    public static final String FUNCTION_SET_PROTOTYPE_OF = "setPrototypeOf";
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
     * Reflect.get().
     * The Reflect.get() static method is like the property accessor syntax, but as a function.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value _get(V8Value target, V8Value property) throws JavetException {
        return invoke(FUNCTION_GET, Objects.requireNonNull(target), Objects.requireNonNull(property));
    }

    /**
     * Reflect.has().
     * The Reflect.has() static method is like the in operator, but as a function.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean _has(V8Value target, V8Value property) throws JavetException {
        return invoke(FUNCTION_HAS, Objects.requireNonNull(target), Objects.requireNonNull(property));
    }

    /**
     * Reflect.set().
     * The Reflect.set() static method is like the property accessor and assignment syntax, but as a function.
     *
     * @param target        the target
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @param receiver      the receiver
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean _set(
            V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException {
        return invoke(
                FUNCTION_SET,
                Objects.requireNonNull(target),
                Objects.requireNonNull(propertyKey),
                Objects.requireNonNull(propertyValue),
                Objects.requireNonNull(receiver));
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
     * Reflect.construct().
     * The Reflect.construct() static method is like the new operator, but as a function.
     * It is equivalent to calling new target(...args).
     * It gives also the added option to specify a different new.target value.
     *
     * @param target    the target
     * @param arguments the arguments
     * @param newTarget the new target
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        return invoke(
                FUNCTION_CONSTRUCT,
                Objects.requireNonNull(target),
                Objects.requireNonNull(arguments),
                Objects.requireNonNull(newTarget));
    }

    /**
     * Reflect.defineProperty().
     * The Reflect.defineProperty() static method is like Object.defineProperty() but returns a Boolean.
     *
     * @param target     the target
     * @param property   the property
     * @param descriptor the descriptor
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean defineProperty(
            V8Value target, V8Value property, V8ValueObject descriptor)
            throws JavetException {
        return invoke(
                FUNCTION_DEFINE_PROPERTY,
                Objects.requireNonNull(target),
                Objects.requireNonNull(property),
                Objects.requireNonNull(descriptor));
    }

    /**
     * Reflect.deleteProperty().
     * The Reflect.deleteProperty() static method is like the delete operator,
     * but as a function. It deletes a property from an object.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean deleteProperty(V8Value target, V8Value property) throws JavetException {
        return invoke(
                FUNCTION_DELETE_PROPERTY,
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

    /**
     * Reflect.getPrototypeOf().
     * The Reflect.getPrototypeOf() static method is like Object.getPrototypeOf().
     * It returns the prototype of the specified object.
     *
     * @param target the target
     * @return the prototype of
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value getPrototypeOf(V8Value target) throws JavetException {
        return invoke(FUNCTION_GET_PROTOTYPE_OF, Objects.requireNonNull(target));
    }

    /**
     * Reflect.isExtensible().
     * The Reflect.isExtensible() static method is like Object.isExtensible().
     * It determines if an object is extensible (whether it can have new properties added to it).
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean isExtensible(V8Value target) throws JavetException {
        return invoke(FUNCTION_IS_EXTENSIBLE, Objects.requireNonNull(target));
    }

    /**
     * Reflect.ownKeys().
     * The Reflect.ownKeys() static method returns an array of the target object's own property keys.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueArray ownKeys(V8Value target) throws JavetException {
        return invoke(FUNCTION_OWN_KEYS, Objects.requireNonNull(target));
    }

    /**
     * Reflect.preventExtensions().
     * The Reflect.preventExtensions() static method is like Object.preventExtensions().
     * It prevents new properties from ever being added to an object (i.e., prevents future extensions to the object).
     *
     * @param target the target
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean preventExtensions(V8Value target) throws JavetException {
        return invoke(FUNCTION_PREVENT_EXTENSIONS, Objects.requireNonNull(target));
    }

    /**
     * Reflect.setPrototypeOf().
     * The Reflect.setPrototypeOf() static method is like Object.setPrototypeOf() but returns a Boolean.
     * It sets the prototype (i.e., the internal [[Prototype]] property) of a specified object.
     *
     * @param target    the target
     * @param prototype the prototype
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8ValueBoolean setPrototypeOf(V8Value target, V8Value prototype) throws JavetException {
        return invoke(
                FUNCTION_SET_PROTOTYPE_OF,
                Objects.requireNonNull(target),
                Objects.requireNonNull(prototype));
    }

    @Override
    public V8ValueBuiltInReflect toClone() throws JavetException {
        return this;
    }
}
