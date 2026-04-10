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
 * Root package for all V8 value types.
 * <p>
 * All V8 values implement {@link com.caoccao.javet.values.IV8Value} and extend {@link com.caoccao.javet.values.V8Value}.
 * The type hierarchy is:
 * <ul>
 *   <li>{@code com.caoccao.javet.values.primitive} - Primitives: string, number, boolean, null, undefined, bigint, symbol.</li>
 *   <li>{@code com.caoccao.javet.values.reference} - Reference types: object, array, function, promise, map, set, module, etc.</li>
 *   <li>{@code com.caoccao.javet.values.reference.builtin} - Built-in objects: JSON, Object, Promise, Reflect, Symbol.</li>
 *   <li>{@code com.caoccao.javet.values.virtual} - Virtual values for iterator proxying and value list management.</li>
 * </ul>
 * <p>
 * <b>Important:</b> Reference values hold native V8 handles and must be closed after use
 * (via try-with-resources or {@link com.caoccao.javet.utils.JavetResourceUtils#safeClose}) to prevent memory leaks.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.values;