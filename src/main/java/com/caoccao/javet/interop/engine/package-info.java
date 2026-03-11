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
 * Engine pool for managing reusable V8/Node.js runtime instances in production.
 * <p>
 * Creating V8 runtimes is expensive. Use the engine pool to amortize that cost:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.engine.JavetEnginePool} - Thread-safe pool of reusable runtimes with automatic lifecycle management.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.JavetEngineConfig} - Pool configuration: pool size, runtime type, timeout, auto-reset, etc.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.JavetEngine} - A pooled runtime wrapper with usage tracking. Obtain via {@code pool.getEngine()} and use with try-with-resources.</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * try (JavetEnginePool<V8Runtime> pool = new JavetEnginePool<>()) {
 *     try (IJavetEngine<V8Runtime> engine = pool.getEngine()) {
 *         V8Runtime runtime = engine.getV8Runtime();
 *         runtime.getExecutor("1 + 1").executeInteger();
 *     }
 * }
 * }</pre>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.engine;