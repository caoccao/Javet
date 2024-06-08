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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueArray;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Objects;

/**
 * The type V8 value built in object.
 *
 * @since 0.9.2
 */
@SuppressWarnings("unchecked")
public class V8ValueBuiltInObject extends V8ValueObject {
    /**
     * The constant FUNCTION_ASSIGN.
     *
     * @since 0.9.2
     */
    public static final String FUNCTION_ASSIGN = "assign";
    /**
     * The constant FUNCTION_CREATE.
     *
     * @since 3.1.3
     */
    public static final String FUNCTION_CREATE = "create";
    /**
     * The constant FUNCTION_FREEZE.
     *
     * @since 3.0.1
     */
    public static final String FUNCTION_FREEZE = "freeze";
    /**
     * The constant FUNCTION_GET_OWN_PROPERTY_SYMBOLS.
     *
     * @since 0.9.11
     */
    public static final String FUNCTION_GET_OWN_PROPERTY_SYMBOLS = "getOwnPropertySymbols";
    /**
     * The constant FUNCTION_GET_PROTOTYPE_OF.
     *
     * @since 3.1.3
     */
    public static final String FUNCTION_GET_PROTOTYPE_OF = "getPrototypeOf";
    /**
     * The constant FUNCTION_SEAL.
     *
     * @since 3.1.3
     */
    public static final String FUNCTION_SEAL = "seal";
    /**
     * The constant FUNCTION_SET_PROTOTYPE_OF.
     *
     * @since 3.1.3
     */
    public static final String FUNCTION_SET_PROTOTYPE_OF = "setPrototypeOf";
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = "Object";

    /**
     * Instantiates a new V8 value built in object.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    public V8ValueBuiltInObject(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
    }

    /**
     * Object.assign()
     * The Object.assign() static method copies all enumerable own properties from one or more source objects
     * to a target object. It returns the modified target object.
     *
     * @param iV8ValueObjectTarget The target object — what to apply the sources' properties to, which is returned after it is modified.
     * @param iV8ValueObjectSource The source object(s) — objects containing the properties you want to apply.
     * @return The target object.
     * @throws JavetException the javet exception
     * @since 0.9.2
     */
    @CheckReturnValue
    public V8ValueObject assign(
            IV8ValueObject iV8ValueObjectTarget,
            IV8ValueObject iV8ValueObjectSource)
            throws JavetException {
        return invoke(
                FUNCTION_ASSIGN,
                Objects.requireNonNull(iV8ValueObjectTarget),
                Objects.requireNonNull(iV8ValueObjectSource));
    }

    /**
     * Object.create()
     * The Object.create() static method creates a new object, using an existing object as the prototype
     * of the newly created object.
     *
     * @param iV8ValueObject The object which should be the prototype of the newly-created object.
     * @return A new object with the specified prototype object and properties.
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    public V8ValueObject create(IV8ValueObject iV8ValueObject) throws JavetException {
        return invoke(FUNCTION_CREATE, Objects.requireNonNull(iV8ValueObject));
    }

    /**
     * Object.create()
     * The Object.create() static method creates a new object, using an existing object as the prototype
     * of the newly created object.
     *
     * @param <T>                      the type parameter
     * @param iV8ValueObject           The object which should be the prototype of the newly-created object.
     * @param iV8ValueObjectProperties If specified and not undefined, an object whose enumerable own properties specify property descriptors to be added to the newly-created object, with the corresponding property names. These properties correspond to the second argument of Object.defineProperties().
     * @return A new object with the specified prototype object and properties.
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    public <T extends IV8ValueObject> T create(
            IV8ValueObject iV8ValueObject,
            IV8ValueObject iV8ValueObjectProperties)
            throws JavetException {
        return (T) invoke(
                FUNCTION_CREATE,
                Objects.requireNonNull(iV8ValueObject),
                Objects.requireNonNull(iV8ValueObjectProperties));
    }

    /**
     * Object.freeze()
     * The Object.freeze() static method freezes an object. Freezing an object prevents extensions
     * and makes existing properties non-writable and non-configurable.
     * A frozen object can no longer be changed: new properties cannot be added, existing properties cannot be removed,
     * their enumerability, configurability, writability, or value cannot be changed, and the object's prototype
     * cannot be re-assigned. freeze() returns the same object that was passed in.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject The object to freeze.
     * @return The object that was passed to the function.
     * @throws JavetException the javet exception
     * @since 3.0.1
     */
    @CheckReturnValue
    public <T extends IV8ValueObject> T freeze(T iV8ValueObject) throws JavetException {
        return (T) invoke(FUNCTION_FREEZE, Objects.requireNonNull(iV8ValueObject));
    }

    /**
     * Object.getOwnPropertySymbols()
     * The Object.getOwnPropertySymbols() static method returns an array of all symbol properties
     * found directly upon a given object.
     *
     * @param iV8ValueObject The object whose symbol properties are to be returned.
     * @return An array of all symbol properties found directly upon the given object.
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    @CheckReturnValue
    public IV8ValueArray getOwnPropertySymbols(IV8ValueObject iV8ValueObject) throws JavetException {
        return invoke(FUNCTION_GET_OWN_PROPERTY_SYMBOLS, iV8ValueObject);
    }

    /**
     * Object.getPrototypeOf()
     * The Object.getPrototypeOf() static method returns the prototype (i.e. the value of the internal
     * [[Prototype]] property) of the specified object.
     *
     * @param v8Value The object whose prototype is to be returned.
     * @return The prototype of the given object, which may be null.
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    public V8Value getPrototypeOf(V8Value v8Value) throws JavetException {
        return invoke(FUNCTION_GET_PROTOTYPE_OF, Objects.requireNonNull(v8Value));
    }

    /**
     * Object.seal()
     * The Object.seal() static method seals an object. Sealing an object prevents extensions and makes existing
     * properties non-configurable. A sealed object has a fixed set of properties: new properties cannot be added,
     * existing properties cannot be removed, their enumerability and configurability cannot be changed,
     * and its prototype cannot be re-assigned. Values of existing properties can still be changed as long as
     * they are writable. seal() returns the same object that was passed in.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject The object to seal.
     * @return The object that was passed to the function.
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    public <T extends IV8ValueObject> T seal(T iV8ValueObject) throws JavetException {
        return (T) invoke(FUNCTION_SEAL, Objects.requireNonNull(iV8ValueObject));
    }

    /**
     * Object.setPrototypeOf()
     * The Object.setPrototypeOf() static method sets the prototype (i.e., the internal [[Prototype]] property)
     * of a specified object to another object or null.
     *
     * @param v8Value          The object which is to have its prototype set.
     * @param v8ValuePrototype The object's new prototype (an object or null).
     * @return The specified object.
     * @throws JavetException the javet exception
     * @since 3.1.3
     */
    @CheckReturnValue
    public V8Value setPrototypeOf(V8Value v8Value, V8Value v8ValuePrototype) throws JavetException {
        return invoke(
                FUNCTION_SET_PROTOTYPE_OF,
                Objects.requireNonNull(v8Value),
                Objects.requireNonNull(v8ValuePrototype));
    }

    @Override
    public V8ValueBuiltInObject toClone() throws JavetException {
        return this;
    }
}
