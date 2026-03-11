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
 * Core interop layer between Java and V8/Node.js engines.
 * <p>
 * This is the primary package for creating and managing JavaScript runtimes:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.V8Host} - Main entry point. Obtain an instance via {@code V8Host.getInstance(JSRuntimeType)} to create runtimes.</li>
 *   <li>{@link com.caoccao.javet.interop.V8Runtime} - Represents a V8 isolate with a single context. Compile and execute JavaScript, manage modules, and convert values.</li>
 *   <li>{@link com.caoccao.javet.interop.NodeRuntime} - Extends V8Runtime with Node.js APIs including {@code require()}, built-in modules, and the event loop.</li>
 *   <li>{@link com.caoccao.javet.interop.V8Scope} - RAII scope for automatic V8 value cleanup (use with try-with-resources).</li>
 *   <li>{@link com.caoccao.javet.interop.V8Guard} - Timeout guard that terminates long-running scripts.</li>
 *   <li>{@link com.caoccao.javet.interop.V8Inspector} - Chrome DevTools debugger session.</li>
 *   <li>{@link com.caoccao.javet.interop.V8ScriptOrigin} - Script source metadata (resource name, line/column offsets).</li>
 *   <li>{@link com.caoccao.javet.interop.V8Locker} - Explicit V8 isolate lock for multi-threaded access.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop;