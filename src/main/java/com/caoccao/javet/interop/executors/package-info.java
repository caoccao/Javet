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
 * Script executors for compiling and running JavaScript code from various sources.
 * <p>
 * Obtain an executor via {@code v8Runtime.getExecutor(...)}:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.executors.V8StringExecutor} - Execute JavaScript from a {@code String}.</li>
 *   <li>{@link com.caoccao.javet.interop.executors.V8FileExecutor} - Execute JavaScript from a {@code File}.</li>
 *   <li>{@link com.caoccao.javet.interop.executors.V8PathExecutor} - Execute JavaScript from a {@code Path}.</li>
 * </ul>
 * <p>
 * All executors implement {@link com.caoccao.javet.interop.executors.IV8Executor} which provides
 * methods for script execution, module compilation, resource naming, and cached data support.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.executors;