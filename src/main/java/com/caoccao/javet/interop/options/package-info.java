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
 * Configuration options for creating V8 and Node.js runtimes.
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.options.NodeRuntimeOptions} - Node.js runtime configuration: console arguments, built-in module resolution, and snapshot support.</li>
 *   <li>{@link com.caoccao.javet.interop.options.V8RuntimeOptions} - V8 runtime configuration: global name and snapshot support.</li>
 *   <li>{@link com.caoccao.javet.interop.options.V8Flags} - V8 engine command-line flags (e.g., max heap size, expose GC, use strict).</li>
 *   <li>{@link com.caoccao.javet.interop.options.NodeFlags} - Node.js command-line flags (e.g., ICU data directory).</li>
 * </ul>
 * <p>
 * Flags are set globally before creating the first runtime and are sealed after V8 initialization.
 * Runtime options are per-runtime and passed to {@code V8Host.createV8Runtime(runtimeOptions)}.
 *
 * @since 1.0.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.options;