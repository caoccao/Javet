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

import com.caoccao.javet.enums.V8ValueSymbolType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * The type Javet callback context.
 *
 * @since 0.7.1
 */
@SuppressWarnings("unchecked")
public final class JavetCallbackContext {
    /**
     * The constant INVALID_HANDLE.
     *
     * @since 0.9.11
     */
    public static final long INVALID_HANDLE = 0L;
    private static final String ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID =
            "Callback receiver or callback method is invalid";
    private static final String ERROR_CALLBACK_TYPE_MISMATCHES = "Callback type mismatches.";
    private static final String ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID =
            "Javet callback context handle is invalid";
    private final Object callbackMethod;
    private final Object callbackReceiver;
    private final JavetCallbackType callbackType;
    private final String name;
    private final V8ValueSymbolType symbolType;
    private final boolean thisObjectRequired;
    private long handle;
    private boolean returnResult;

    /**
     * Instantiates a new Javet callback context.
     *
     * @param name         the name
     * @param callbackType the callback type
     * @param directCall   the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            JavetCallbackType callbackType,
            IJavetDirectCallable.DirectCall directCall) {
        this(name, V8ValueSymbolType.None, callbackType, directCall);
    }

    /**
     * Instantiates a new Javet callback context.
     *
     * @param name             the name
     * @param callbackReceiver the callback receiver
     * @param callbackType     the callback type
     * @param directCall       the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            Object callbackReceiver,
            JavetCallbackType callbackType,
            IJavetDirectCallable.DirectCall directCall) {
        this(Objects.requireNonNull(name), V8ValueSymbolType.None, callbackReceiver, callbackType, directCall);
    }

    /**
     * Instantiates a new Javet callback context.
     *
     * @param name         the name
     * @param symbolType   the symbol type
     * @param callbackType the callback type
     * @param directCall   the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            JavetCallbackType callbackType,
            IJavetDirectCallable.DirectCall directCall) {
        this(Objects.requireNonNull(name), symbolType, null, callbackType, directCall);
    }

    /**
     * Instantiates a new Javet callback context that takes a Java method for making further reflection calls.
     *
     * @param name             the name
     * @param callbackReceiver the callback receiver
     * @param callbackMethod   the callback method
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            Object callbackReceiver,
            Method callbackMethod) {
        this(name, V8ValueSymbolType.None, callbackReceiver, callbackMethod);
    }

    /**
     * Instantiates a new Javet callback context that takes a Java method for making further reflection calls.
     *
     * @param name             the name
     * @param symbolType       the symbol type
     * @param callbackReceiver the callback receiver
     * @param callbackMethod   the callback method
     * @since 0.7.1
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            Object callbackReceiver,
            Method callbackMethod) {
        this(name, symbolType, callbackReceiver, callbackMethod, false);
    }

    /**
     * Instantiates a new Javet callback context that takes a Java method for making further reflection calls.
     *
     * @param name               the name
     * @param callbackReceiver   the callback receiver
     * @param callbackMethod     the callback method
     * @param thisObjectRequired the this object required
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            Object callbackReceiver,
            Method callbackMethod,
            boolean thisObjectRequired) {
        this(name, V8ValueSymbolType.None, callbackReceiver, callbackMethod, thisObjectRequired);
    }

    /**
     * Instantiates a new Javet callback context.
     *
     * @param name             the name
     * @param symbolType       the symbol type
     * @param callbackReceiver the callback receiver
     * @param callbackType     the callback type
     * @param directCall       the direct call
     * @since 3.0.1
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            Object callbackReceiver,
            JavetCallbackType callbackType,
            IJavetDirectCallable.DirectCall directCall) {
        this(
                name,
                symbolType,
                callbackReceiver,
                Objects.requireNonNull(callbackType),
                directCall,
                callbackType.getThisObjectRequired());
        assert callbackType.getDirectCallClass().isAssignableFrom(directCall.getClass()) : ERROR_CALLBACK_TYPE_MISMATCHES;
        this.returnResult = callbackType.getReturnResult();
    }

    /**
     * Instantiates a new Javet callback context that takes a Java method for making further reflection calls.
     *
     * @param name               the name
     * @param symbolType         the symbol type
     * @param callbackReceiver   the callback receiver
     * @param callbackMethod     the callback method
     * @param thisObjectRequired the this object required
     * @since 0.7.1
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            Object callbackReceiver,
            Method callbackMethod,
            boolean thisObjectRequired) {
        this(Objects.requireNonNull(name),
                symbolType,
                callbackReceiver,
                JavetCallbackType.Reflection,
                callbackMethod,
                thisObjectRequired);
        assert (callbackReceiver != null && !Modifier.isStatic(callbackMethod.getModifiers()))
                || (callbackReceiver == null && Modifier.isStatic(callbackMethod.getModifiers()))
                : ERROR_CALLBACK_RECEIVER_OR_CALLBACK_METHOD_IS_INVALID;
        this.returnResult = !callbackMethod.getReturnType().equals(Void.TYPE);
    }

    private JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            Object callbackReceiver,
            JavetCallbackType callbackType,
            Object callbackMethod,
            boolean thisObjectRequired) {
        this.callbackMethod = Objects.requireNonNull(callbackMethod);
        this.callbackReceiver = callbackReceiver;
        this.callbackType = Objects.requireNonNull(callbackType);
        handle = INVALID_HANDLE;
        this.name = name;
        this.thisObjectRequired = thisObjectRequired;
        this.symbolType = Objects.requireNonNull(symbolType);
    }

    /**
     * Gets callback method.
     *
     * @param <T> the type parameter
     * @return the callback method
     * @since 0.9.1
     */
    public <T> T getCallbackMethod() {
        return (T) callbackMethod;
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
     * Gets callback type.
     *
     * @return the callback type
     * @since 2.2.0
     */
    public JavetCallbackType getCallbackType() {
        return callbackType;
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
     * Gets name.
     *
     * @return the name
     * @since 2.2.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets symbol type.
     *
     * @return the symbol type
     * @since 2.2.0
     */
    public V8ValueSymbolType getSymbolType() {
        return symbolType;
    }

    /**
     * Is return result.
     *
     * @return true : yes, false : no
     * @since 0.9.1
     */
    public boolean isReturnResult() {
        return returnResult;
    }

    /**
     * Is this object required.
     *
     * @return true : yes, false : no
     * @since 0.9.1
     */
    public boolean isThisObjectRequired() {
        return thisObjectRequired;
    }

    /**
     * Is valid.
     *
     * @return true : yes, false : no
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
    void setHandle(long handle) {
        assert !isValid() : ERROR_JAVET_CALLBACK_CONTEXT_HANDLE_IS_INVALID;
        this.handle = handle;
    }
}
