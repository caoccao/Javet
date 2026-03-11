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
 * Callback mechanisms for communication between JavaScript and Java.
 * <p>
 * This package handles all callback scenarios where V8 needs to invoke Java code:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.callback.JavetCallbackContext} - Context object carrying method reference and metadata for a V8-to-Java callback.</li>
 *   <li>{@link com.caoccao.javet.interop.callback.V8FunctionCallback} - Dispatches V8 function calls to Java methods.</li>
 *   <li>{@link com.caoccao.javet.interop.callback.IV8ModuleResolver} - Resolve ES module {@code import} statements to compiled modules on the Java side.</li>
 *   <li>{@link com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver} - Built-in resolver for Node.js {@code node:*} modules.</li>
 *   <li>{@link com.caoccao.javet.interop.callback.IJavetDirectCallable} - Convert annotation-based callbacks to direct Java method calls (avoids reflection).</li>
 *   <li>{@link com.caoccao.javet.interop.callback.IJavetPromiseRejectCallback} - Handle unhandled promise rejections.</li>
 *   <li>{@link com.caoccao.javet.interop.callback.IJavetGCCallback} - Receive V8 garbage collection events.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.callback;