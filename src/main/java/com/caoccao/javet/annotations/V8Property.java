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

package com.caoccao.javet.annotations;

import com.caoccao.javet.enums.V8ValueSymbolType;

import java.lang.annotation.*;

/**
 * The interface V8 property is for auto-registering JS property interception.
 *
 * @since 0.9.0
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface V8Property {
    /**
     * JS property name.
     *
     * @return the name of the JS property to be injected
     * @since 0.9.0
     */
    String name() default "";

    /**
     * Symbol V8 value symbol type. Default: None.
     *
     * @return the V8 value symbol type
     * @since 0.9.12
     */
    V8ValueSymbolType symbolType() default V8ValueSymbolType.None;

    /**
     * This object required.
     *
     * @return true : this object is required, false: this object is ignored
     * @since 0.9.0
     */
    boolean thisObjectRequired() default false;
}
