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
 * Core interfaces used throughout the Javet API.
 * <p>
 * Key interfaces:
 * <ul>
 *   <li>{@link com.caoccao.javet.interfaces.IJavetClosable} - Resource lifecycle management (extends {@code AutoCloseable}). All V8 values and runtimes implement this.</li>
 *   <li>{@link com.caoccao.javet.interfaces.IJavetAnonymous} - Marker for ad-hoc annotation-based callback receivers.</li>
 *   <li>{@link com.caoccao.javet.interfaces.IJavetLogger} - Logging abstraction with debug, info, warn, and error levels.</li>
 *   <li>{@link com.caoccao.javet.interfaces.IJavetEntityError}, {@link com.caoccao.javet.interfaces.IJavetEntityFunction},
 *       {@link com.caoccao.javet.interfaces.IJavetEntitySymbol}, etc. - Contracts for entity types used in conversion.</li>
 *   <li>{@link com.caoccao.javet.interfaces.IJavetBiConsumer}, {@link com.caoccao.javet.interfaces.IJavetBiFunction},
 *       {@link com.caoccao.javet.interfaces.IJavetUniConsumer}, {@link com.caoccao.javet.interfaces.IJavetUniFunction} - Functional interfaces for V8 value callbacks with exception support.</li>
 *   <li>{@link com.caoccao.javet.interfaces.IJavaFunction}, {@link com.caoccao.javet.interfaces.IJavaSupplier} - Android-compatible polyfills for {@code java.util.function}.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interfaces;