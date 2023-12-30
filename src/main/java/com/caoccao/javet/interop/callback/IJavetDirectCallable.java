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

package com.caoccao.javet.interop.callback;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.io.Serializable;

/**
 * The interface Javet direct callable is for converting the calls
 * from reflection calls to direct calls.
 *
 * @since 2.2.0
 */
public interface IJavetDirectCallable {
    /**
     * Get supported callback contexts.
     *
     * @return the supported callback contexts
     * @since 2.2.0
     */
    JavetCallbackContext[] getCallbackContexts();

    /**
     * Sets V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     * @since 2.2.0
     */
    default void setV8Runtime(V8Runtime v8Runtime) {
        // Pass.
    }

    /**
     * The interface Direct call.
     *
     * @since 2.2.0
     */
    interface DirectCall extends Serializable {
    }

    /**
     * The interface GetterAndNoThis gets the property value by a property key
     * without this object passed in.
     *
     * @since 2.2.0
     */
    interface GetterAndNoThis<E extends Exception> extends DirectCall {
        /**
         * Get the property value by a property key
         * without this object passed in.
         *
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value get() throws JavetException, E;
    }

    /**
     * The interface GetterAndThis gets the property value by a property key
     * with this object passed in.
     *
     * @since 2.2.0
     */
    interface GetterAndThis<E extends Exception> extends DirectCall {
        /**
         * Get the property value by a property key
         * with this object passed in.
         *
         * @param thisObject the this object
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value get(V8Value thisObject) throws JavetException, E;
    }

    /**
     * The interface NoThisAndResult does not return the result and accept this object passed in.
     *
     * @since 2.2.0
     */
    interface NoThisAndNoResult<E extends Exception> extends DirectCall {
        /**
         * Call.
         *
         * @param v8Values the V8 values
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        void call(V8Value... v8Values) throws JavetException, E;
    }

    /**
     * The interface NoThisAndResult returns the result
     * without this object passed in.
     *
     * @since 2.2.0
     */
    interface NoThisAndResult<E extends Exception> extends DirectCall {
        /**
         * Call and return the result.
         *
         * @param v8Values the V8 values
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value call(V8Value... v8Values) throws JavetException, E;
    }

    /**
     * The interface SetterAndNoThis gets the property value by a property key
     * without this object passed in.
     *
     * @since 2.2.0
     */
    interface SetterAndNoThis<E extends Exception> extends DirectCall {
        /**
         * Set the property value by a property key
         * without this object passed in.
         *
         * @param v8ValueValue the V8 value value
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value set(V8Value v8ValueValue) throws JavetException, E;
    }

    /**
     * The interface SetterAndThis gets the property value by a property key
     * with this object passed in.
     *
     * @since 2.2.0
     */
    interface SetterAndThis<E extends Exception> extends DirectCall {
        /**
         * Set the property value by a property key
         * with this object passed in.
         *
         * @param thisObject   the this object
         * @param v8ValueValue the V8 value value
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value set(V8Value thisObject, V8Value v8ValueValue) throws JavetException, E;
    }

    /**
     * The interface ThisAndNoResult does not return the result, but accepts this object.
     *
     * @since 2.2.0
     */
    interface ThisAndNoResult<E extends Exception> extends DirectCall {
        /**
         * Call by this object.
         *
         * @param thisObject the this object
         * @param v8Values   the V8 values
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        void call(V8Value thisObject, V8Value... v8Values) throws JavetException, E;
    }

    /**
     * The interface ThisAndResult accepts this object and returns the result.
     *
     * @param <E> the custom exception
     * @since 2.2.0
     */
    interface ThisAndResult<E extends Exception> extends DirectCall {
        /**
         * Call by this object and return the result.
         *
         * @param thisObject the this object
         * @param v8Values   the V8 values
         * @return the V8 value
         * @throws JavetException the javet exception
         * @throws E              the custom exception
         * @since 2.2.0
         */
        V8Value call(V8Value thisObject, V8Value... v8Values) throws JavetException, E;
    }
}
