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
 * Enumerations covering JavaScript runtime types, V8 value types, and engine configuration constants.
 * <p>
 * Key enums:
 * <ul>
 *   <li>{@link com.caoccao.javet.enums.JSRuntimeType} - Choose between Node.js and V8 runtime modes.</li>
 *   <li>{@link com.caoccao.javet.enums.V8ValueReferenceType} - V8 reference types (Object, Function, Array, Promise, etc.).</li>
 *   <li>{@link com.caoccao.javet.enums.V8ValueInternalType} - Comprehensive V8 internal value type enumeration.</li>
 *   <li>{@link com.caoccao.javet.enums.V8ConversionMode} - Converter strategies: Transparent, AllowOnly, BlockOnly.</li>
 *   <li>{@link com.caoccao.javet.enums.V8ProxyMode} - Proxy handler modes: Class, Function, Object.</li>
 *   <li>{@link com.caoccao.javet.enums.V8AwaitMode} - Event loop execution modes: RunNoWait, RunOnce, RunTillNoMoreTasks.</li>
 *   <li>{@link com.caoccao.javet.enums.V8GCType} / {@link com.caoccao.javet.enums.V8GCCallbackFlags} - Garbage collection event types and flags.</li>
 *   <li>{@link com.caoccao.javet.enums.V8ValueErrorType} - V8 error types: Error, RangeError, TypeError, SyntaxError, etc.</li>
 *   <li>{@link com.caoccao.javet.enums.JavetErrorType} - Javet error categories: System, Compilation, Execution, Converter, etc.</li>
 *   <li>{@link com.caoccao.javet.enums.JSFunctionType} - Function types: Native, API, UserDefined.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.enums;