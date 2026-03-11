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
 * Proxy handlers that expose Java objects as JavaScript objects with full property and method access.
 * <p>
 * Javet provides two proxy strategies:
 * <ul>
 *   <li><b>Reflection-based</b> - Uses Java reflection to dynamically dispatch property access, method calls,
 *       and iteration to the underlying Java object. Flexible but has reflection overhead.
 *       See {@link com.caoccao.javet.interop.proxy.JavetReflectionProxyObjectHandler},
 *       {@link com.caoccao.javet.interop.proxy.JavetReflectionProxyClassHandler},
 *       {@link com.caoccao.javet.interop.proxy.JavetReflectionProxyFunctionHandler}.</li>
 *   <li><b>Direct</b> - Implements proxy handlers without reflection for maximum performance.
 *       See {@link com.caoccao.javet.interop.proxy.JavetDirectProxyObjectHandler},
 *       {@link com.caoccao.javet.interop.proxy.JavetDirectProxyFunctionHandler}.</li>
 * </ul>
 * <p>
 * {@link com.caoccao.javet.interop.proxy.IJavetNonProxy} is a marker interface for types that should
 * not be automatically proxied by the converter.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.proxy;