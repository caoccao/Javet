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
     * Instantiates a new Javet callback context that takes a direct call as the getter.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.GetterAndNoThis directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallGetterAndNoThis, false, true);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call as the getter.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.GetterAndThis directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallGetterAndThis, true, true);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call as the setter.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.SetterAndNoThis directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallSetterAndNoThis, false, true);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call as the setter.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.SetterAndThis directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallSetterAndThis, true, true);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.NoThisAndNoResult directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallNoThisAndNoResult, false, false);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.NoThisAndResult directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallNoThisAndResult, false, true);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.ThisAndNoResult directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallThisAndNoResult, true, false);
    }

    /**
     * Instantiates a new Javet callback context that takes a direct call.
     *
     * @param name       the name
     * @param symbolType the symbol type
     * @param directCall the direct call
     * @since 2.2.0
     */
    public JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.ThisAndResult directCall) {
        this(name, symbolType, directCall, JavetCallbackType.DirectCallThisAndResult, true, true);
    }

    private JavetCallbackContext(
            String name,
            V8ValueSymbolType symbolType,
            IJavetDirectCallable.DirectCall directCall,
            JavetCallbackType callbackType,
            boolean thisObjectRequired,
            boolean returnResult) {
        this(name, symbolType, null, callbackType, directCall, thisObjectRequired);
        this.returnResult = returnResult;
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
        this(name, symbolType, callbackReceiver, JavetCallbackType.Reflection, callbackMethod, thisObjectRequired);
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
        this.name = Objects.requireNonNull(name);
        this.thisObjectRequired = thisObjectRequired;
        this.symbolType = Objects.requireNonNull(symbolType);
    }

    /**
     * Gets callback method.
     *
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
