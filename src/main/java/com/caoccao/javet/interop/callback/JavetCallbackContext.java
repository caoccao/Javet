/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.callback;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * The type Javet callback context.
 *
 * @since 0.7.1
 */
public final class JavetCallbackContext {
    /**
     * The constant INVALID_HANDLE.
     */
    public static final long INVALID_HANDLE = 0L;
    private static final String ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID =
            "Callback receiver or callback method is invalid";
    private static final String ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID =
            "Javet callback context handle is invalid";
    private final Method callbackMethod;
    private final Object callbackReceiver;
    private final boolean returnResult;
    private final boolean thisObjectRequired;
    private long handle;

    /**
     * Instantiates a new Javet callback context.
     *
     * @param callbackReceiver the callback receiver
     * @param callbackMethod   the callback method
     * @since 0.7.1
     */
    public JavetCallbackContext(Object callbackReceiver, Method callbackMethod) {
        this(callbackReceiver, callbackMethod, false);
    }

    /**
     * Instantiates a new Javet callback context.
     *
     * @param callbackReceiver   the callback receiver
     * @param callbackMethod     the callback method
     * @param thisObjectRequired the this object required
     * @since 0.7.1
     */
    public JavetCallbackContext(Object callbackReceiver, Method callbackMethod, boolean thisObjectRequired) {
        Objects.requireNonNull(callbackMethod);
        assert (callbackReceiver != null && !Modifier.isStatic(callbackMethod.getModifiers()))
                || (callbackReceiver == null && Modifier.isStatic(callbackMethod.getModifiers()))
                : ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID;
        this.callbackMethod = callbackMethod;
        this.callbackReceiver = callbackReceiver;
        handle = INVALID_HANDLE;
        this.returnResult = !callbackMethod.getReturnType().equals(Void.TYPE);
        this.thisObjectRequired = thisObjectRequired;
    }

    /**
     * Gets callback method.
     *
     * @return the callback method
     * @since 0.9.1
     */
    public Method getCallbackMethod() {
        return callbackMethod;
    }

    /**
     * Gets callback receiver.
     *
     * @return the callback receiver
     * @since 0.7.1
     */
    public Object getCallbackReceiver() {
        return callbackReceiver;
    }

    /**
     * Gets handle.
     *
     * @return the handle
     * @since 0.7.1
     */
    public long getHandle() {
        return handle;
    }

    /**
     * Is return result.
     *
     * @return the boolean
     * @since 0.9.1
     */
    public boolean isReturnResult() {
        return returnResult;
    }

    /**
     * Is this object required.
     *
     * @return the boolean
     * @since 0.9.1
     */
    public boolean isThisObjectRequired() {
        return thisObjectRequired;
    }

    /**
     * Is valid.
     *
     * @return the boolean
     * @since 0.9.11
     */
    public boolean isValid() {
        return handle != INVALID_HANDLE;
    }

    /**
     * Sets handle.
     *
     * @param handle the handle
     * @since 0.7.1
     */
    public void setHandle(long handle) {
        assert !isValid() : ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID;
        this.handle = handle;
    }
}
