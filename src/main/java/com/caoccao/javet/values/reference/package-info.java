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
 * V8 reference value types that hold native V8 handles and <b>must be closed</b> after use.
 * <p>
 * Always use try-with-resources or {@link com.caoccao.javet.utils.JavetResourceUtils#safeClose}
 * to prevent native memory leaks. Key types:
 * <ul>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueObject} - General JavaScript object with property access.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueArray} - JavaScript Array.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueFunction} - Callable JavaScript function.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValuePromise} - JavaScript Promise with resolve/reject/then/catch.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueMap} / {@link com.caoccao.javet.values.reference.V8ValueSet} - Map and Set.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8Module} - ES module with instantiation, evaluation, and namespace access.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueArrayBuffer} / {@link com.caoccao.javet.values.reference.V8ValueTypedArray} - Binary data buffers.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueProxy} - JavaScript Proxy with target and handler.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueError} - JavaScript Error with message and stack.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueSymbol} - JavaScript Symbol.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8ValueRegExp} - JavaScript RegExp.</li>
 *   <li>{@link com.caoccao.javet.values.reference.V8Script} - Compiled script for repeated execution.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.values.reference;