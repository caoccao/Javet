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

package com.caoccao.javet.interop.callback;

/**
 * The enum Javet callback type.
 *
 * @since 2.2.0
 */
public enum JavetCallbackType {
    /**
     * The callback is via Java direct getter without this object.
     *
     * @since 2.2.0
     */
    DirectCallGetterAndNoThis(IJavetDirectCallable.GetterAndNoThis.class, false, true),
    /**
     * The callback is via Java direct getter with this object.
     *
     * @since 2.2.0
     */
    DirectCallGetterAndThis(IJavetDirectCallable.GetterAndThis.class, true, true),
    /**
     * The callback is via Java direct setter without this object.
     *
     * @since 2.2.0
     */
    DirectCallSetterAndNoThis(IJavetDirectCallable.SetterAndNoThis.class, false, true),
    /**
     * The callback is via Java direct setter with this object.
     *
     * @since 2.2.0
     */
    DirectCallSetterAndThis(IJavetDirectCallable.SetterAndThis.class, true, true),
    /**
     * The callback is via Java direct call without this object and without result.
     *
     * @since 2.2.0
     */
    DirectCallNoThisAndNoResult(IJavetDirectCallable.NoThisAndNoResult.class, false, false),
    /**
     * The callback is via Java direct call without this object and with result.
     *
     * @since 2.2.0
     */
    DirectCallNoThisAndResult(IJavetDirectCallable.NoThisAndResult.class, false, true),
    /**
     * The callback is via Java direct call with this object and without result.
     *
     * @since 2.2.0
     */
    DirectCallThisAndNoResult(IJavetDirectCallable.ThisAndNoResult.class, true, false),
    /**
     * The callback is via Java direct call with this object and with result.
     *
     * @since 2.2.0
     */
    DirectCallThisAndResult(IJavetDirectCallable.ThisAndResult.class, true, true),
    /**
     * The callback is via Java reflection.
     * <p>
     * PROS: It's non-intrusive and supports all kinds of Java classes.
     * CONS: Performance is relatively low.
     *
     * @since 2.2.0
     */
    Reflection(null, null, null);
    private final Class<? extends IJavetDirectCallable.DirectCall> directCallClass;
    private final Boolean returnResult;
    private final Boolean thisObjectRequired;

    JavetCallbackType(
            Class<? extends IJavetDirectCallable.DirectCall> directCallClass,
            Boolean thisObjectRequired,
            Boolean returnResult) {
        this.directCallClass = directCallClass;
        this.returnResult = returnResult;
        this.thisObjectRequired = thisObjectRequired;
    }

    /**
     * Gets direct call class.
     *
     * @return the direct call class
     * @since 2.2.0
     */
    public Class<? extends IJavetDirectCallable.DirectCall> getDirectCallClass() {
        return directCallClass;
    }

    /**
     * Gets return result.
     *
     * @return the return result
     * @since 2.2.0
     */
    public Boolean getReturnResult() {
        return returnResult;
    }

    /**
     * Gets this object required.
     *
     * @return the this object required
     * @since 2.2.0
     */
    public Boolean getThisObjectRequired() {
        return thisObjectRequired;
    }
}
