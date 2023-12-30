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

package com.caoccao.javet.enums;

/**
 * The enum V8 conversion mode.
 *
 * @since 1.0.6
 */
public enum V8ConversionMode {
    /**
     * Transparent mode maps the Java objects directly to V8
     * and ignores any annotations.
     *
     * @since 1.0.6
     */
    Transparent,
    /**
     * AllowOnly mode only maps the API with @V8Allow.
     *
     * @since 1.0.6
     */
    AllowOnly,
    /**
     * BlockOnly mode ignored the API with @V8Block.
     *
     * @since 1.0.6
     */
    BlockOnly,
}
