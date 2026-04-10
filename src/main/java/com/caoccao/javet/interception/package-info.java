/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

/**
 * Interceptors for injecting Java functionality into the JavaScript global scope.
 * <p>
 * Interceptors register and unregister themselves on V8 value objects (typically the global object)
 * to expose Java capabilities to JavaScript code:
 * <ul>
 *   <li>{@link com.caoccao.javet.interception.BaseJavetInterceptor} - Abstract base implementing register/unregister lifecycle.</li>
 *   <li>{@link com.caoccao.javet.interception.jvm.JavetJVMInterceptor} - Exposes the JVM as a {@code javet} object in JavaScript,
 *       allowing access to Java packages, classes, and the V8 garbage collector.</li>
 * </ul>
 * <p>
 * See the {@code com.caoccao.javet.interception.logging} sub-package for console interceptors.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interception;