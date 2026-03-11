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
 * Typed wrappers for JavaScript built-in global objects, providing direct Java access to their methods.
 * <ul>
 *   <li>{@link com.caoccao.javet.values.reference.builtin.V8ValueBuiltInObject} - {@code Object.assign()}, {@code Object.keys()}, {@code Object.freeze()}, etc.</li>
 *   <li>{@link com.caoccao.javet.values.reference.builtin.V8ValueBuiltInJson} - {@code JSON.stringify()} and {@code JSON.parse()}.</li>
 *   <li>{@link com.caoccao.javet.values.reference.builtin.V8ValueBuiltInPromise} - {@code Promise.all()}, {@code Promise.race()}, {@code Promise.resolve()}, {@code Promise.reject()}.</li>
 *   <li>{@link com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol} - Well-known symbols ({@code Symbol.iterator}, {@code Symbol.toPrimitive}, etc.).</li>
 *   <li>{@link com.caoccao.javet.values.reference.builtin.V8ValueBuiltInReflect} - {@code Reflect} API for meta-operations.</li>
 * </ul>
 * <p>
 * Obtain via {@code v8Runtime.getGlobalObject().getBuiltInObject()}, etc.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.values.reference.builtin;