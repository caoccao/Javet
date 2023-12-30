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

import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;

import java.lang.annotation.*;

/**
 * The interface V8 convert.
 *
 * @since 1.0.6
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface V8Convert {
    /**
     * V8 conversion mode.
     *
     * @return the V8 conversion mode
     * @since 1.0.6
     */
    V8ConversionMode mode() default V8ConversionMode.Transparent;

    /**
     * Proxy mode.
     *
     * @return the proxy mode
     * @since 1.1.7
     */
    V8ProxyMode proxyMode() default V8ProxyMode.Object;
}
