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
    DirectCallGetterAndNoThis,
    /**
     * The callback is via Java direct getter with this object.
     *
     * @since 2.2.0
     */
    DirectCallGetterAndThis,
    /**
     * The callback is via Java direct generic getter without this object.
     *
     * @since 2.2.0
     */
    DirectCallGenericGetterAndNoThis,
    /**
     * The callback is via Java direct generic getter with this object.
     *
     * @since 2.2.0
     */
    DirectCallGenericGetterAndThis,
    /**
     * The callback is via Java direct generic setter without this object.
     *
     * @since 2.2.0
     */
    DirectCallGenericSetterAndNoThis,
    /**
     * The callback is via Java direct generic setter with this object.
     *
     * @since 2.2.0
     */
    DirectCallGenericSetterAndThis,
    /**
     * The callback is via Java direct setter without this object.
     *
     * @since 2.2.0
     */
    DirectCallSetterAndNoThis,
    /**
     * The callback is via Java direct setter with this object.
     *
     * @since 2.2.0
     */
    DirectCallSetterAndThis,
    /**
     * The callback is via Java direct call without this object and without result.
     *
     * @since 2.2.0
     */
    DirectCallNoThisAndNoResult,
    /**
     * The callback is via Java direct call without this object and with result.
     *
     * @since 2.2.0
     */
    DirectCallNoThisAndResult,
    /**
     * The callback is via Java direct call with this object and without result.
     *
     * @since 2.2.0
     */
    DirectCallThisAndNoResult,
    /**
     * The callback is via Java direct call with this object and with result.
     *
     * @since 2.2.0
     */
    DirectCallThisAndResult,
    /**
     * The callback is via Java reflection.
     * <p>
     * PROS: It's non-intrusive and supports all kinds of Java classes.
     * CONS: Performance is relatively low.
     *
     * @since 2.2.0
     */
    Reflection,
}
