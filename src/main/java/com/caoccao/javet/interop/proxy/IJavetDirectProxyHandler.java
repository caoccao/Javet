/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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

import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

/**
 * The interface Javet direct proxy handler.
 *
 * @param <E> the type parameter
 * @since 2.2.0
 */
public interface IJavetDirectProxyHandler<E extends Exception> {
    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @since 2.2.0
     */
    V8Runtime getV8Runtime();

    /**
     * Apply to object.
     *
     * @param target     the target
     * @param thisObject this object
     * @param arguments  the arguments
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value proxyApply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
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
     * @since 2.2.0
     */
    default V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, E {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            if (IJavetProxyHandler.FUNCTION_NAME_TO_V8_VALUE.equals(propertyName)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            }
        } else if (property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToPrimitive));
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)) {
                return getV8Runtime().createV8ValueFunction(
                        new JavetCallbackContext(
                                V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR,
                                V8ValueSymbolType.BuiltIn,
                                JavetCallbackType.DirectCallNoThisAndResult,
                                (IJavetDirectCallable.NoThisAndResult<?>) this::symbolToIterator));
            }

        }
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Has property
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value boolean
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueBoolean proxyHas(V8Value target, V8Value property) throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Own keys.
     *
     * @param target the target
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8ValueArray proxyOwnKeys(V8Value target) throws JavetException, E {
        return getV8Runtime().createV8ValueArray();
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
     * @since 2.2.0
     */
    default V8ValueBoolean proxySet(
            V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver)
            throws JavetException, E {
        return getV8Runtime().createV8ValueBoolean(false);
    }

    /**
     * Sets V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     * @since 2.2.0
     */
    void setV8Runtime(V8Runtime v8Runtime);

    /**
     * Symbol toIterator.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value symbolToIterator(V8Value... v8Values) throws JavetException, E {
        return getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Symbol toPrimitive.
     *
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @throws E              the custom exception
     * @since 2.2.0
     */
    default V8Value symbolToPrimitive(V8Value... v8Values) throws JavetException, E {
        return getV8Runtime().createV8ValueNull();
    }
}
