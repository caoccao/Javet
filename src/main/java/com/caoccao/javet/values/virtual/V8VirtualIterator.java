/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.values.virtual;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * The type V8 virtual iterator.
 *
 * @param <T> the type parameter for internal iterable
 * @param <E> the type parameter for external exception
 * @since 2.2.1
 */
public class V8VirtualIterator<T, E extends Exception>
        implements IJavetDirectProxyHandler<E> {
    /**
     * The constant FUNCTION_NEXT.
     *
     * @since 2.2.1
     */
    protected static final String FUNCTION_NEXT = "next";
    /**
     * The constant PROPERTY_DONE.
     *
     * @since 2.2.1
     */
    protected static final String PROPERTY_DONE = "done";
    /**
     * The constant PROPERTY_VALUE.
     *
     * @since 2.2.1
     */
    protected static final String PROPERTY_VALUE = "value";
    /**
     * The Iterable.
     *
     * @since 2.2.1
     */
    protected Iterator<T> iterator;
    /**
     * The V8 runtime.
     *
     * @since 2.2.1
     */
    protected V8Runtime v8Runtime;
    /**
     * The Value.
     *
     * @since 2.2.1
     */
    protected Optional<T> value;

    /**
     * Instantiates a new V8 virtual iterator.
     *
     * @param iterator the iterable
     * @since 2.2.1
     */
    public V8VirtualIterator(Iterator<T> iterator) {
        this.iterator = Objects.requireNonNull(iterator);
        value = null;
        v8Runtime = null;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    /**
     * Next.
     *
     * @param thisObject the this object
     * @param v8Values   the V8 values
     * @return the V8 value
     * @since 2.2.1
     */
    protected V8Value next(V8Value thisObject, V8Value... v8Values) {
        if (iterator != null) {
            if (iterator.hasNext()) {
                value = Optional.ofNullable(iterator.next());
            } else {
                iterator = null;
                value = null;
            }
        }
        return thisObject;
    }

    @Override
    public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, E {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).getValue();
            if (FUNCTION_NEXT.equals(propertyName)) {
                return v8Runtime.createV8ValueFunction(
                        new JavetCallbackContext(
                                FUNCTION_NEXT,
                                JavetCallbackType.DirectCallThisAndResult,
                                (IJavetDirectCallable.ThisAndResult<?>) this::next));
            }
            if (PROPERTY_DONE.equals(propertyName)) {
                return v8Runtime.createV8ValueBoolean(iterator == null || !iterator.hasNext());
            }
            if (PROPERTY_VALUE.equals(propertyName)) {
                if (value == null) {
                    return v8Runtime.createV8ValueUndefined();
                }
                if (value.isPresent()) {
                    return v8Runtime.toV8Value(value.get());
                }
                return v8Runtime.createV8ValueNull();
            }
        }
        return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
    }

    @Override
    public V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, E {
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
            v8ValueArray.push(
                    v8Runtime.createV8ValueString(PROPERTY_DONE),
                    v8Runtime.createV8ValueString(PROPERTY_VALUE));
            v8Scope.setEscapable();
            return v8ValueArray;
        }
    }

    @Override
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    @Override
    public V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, E {
        return v8Runtime.createV8ValueString(
                Optional.ofNullable(iterator)
                        .map(Object::toString)
                        .orElse(String.valueOf((Object) null)));
    }
}
