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
 * Console interceptors that redirect JavaScript {@code console.log()}, {@code console.warn()},
 * {@code console.error()}, etc. to Java output streams.
 * <ul>
 *   <li>{@link com.caoccao.javet.interception.logging.BaseJavetConsoleInterceptor} - Abstract base providing
 *       console.log, debug, error, info, warn, and trace methods.</li>
 *   <li>{@link com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor} - Routes console output to
 *       configurable {@code PrintStream} instances ({@code System.out}/{@code System.err} by default).</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * JavetStandardConsoleInterceptor interceptor = new JavetStandardConsoleInterceptor(v8Runtime);
 * interceptor.register(v8Runtime.getGlobalObject());
 * // ... run JavaScript that uses console.log() ...
 * interceptor.unregister(v8Runtime.getGlobalObject());
 * }</pre>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interception.logging;