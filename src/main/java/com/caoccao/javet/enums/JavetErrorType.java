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
 * The enum Javet error type is for categorizing the errors.
 *
 * @since 0.8.5
 */
public enum JavetErrorType {
    /**
     * System javet error type.
     *
     * @since 0.8.5
     */
    System,
    /**
     * Compilation javet error type.
     *
     * @since 0.8.5
     */
    Compilation,
    /**
     * Execution javet error type.
     *
     * @since 0.8.5
     */
    Execution,
    /**
     * Callback javet error type.
     *
     * @since 0.8.5
     */
    Callback,
    /**
     * Converter javet error type.
     *
     * @since 0.8.5
     */
    Converter,
    /**
     * Module javet error type.
     *
     * @since 0.8.5
     */
    Module,
    /**
     * Lock javet error type.
     *
     * @since 0.8.5
     */
    Lock,
    /**
     * Runtime javet error type.
     *
     * @since 0.8.5
     */
    Runtime,
    /**
     * Engine javet error type.
     *
     * @since 1.1.6
     */
    Engine,
}
