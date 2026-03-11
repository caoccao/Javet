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
 * Exception hierarchy and error definitions for Javet.
 * <p>
 * All Javet exceptions extend {@link com.caoccao.javet.exceptions.JavetException}. The main exception types are:
 * <ul>
 *   <li>{@link com.caoccao.javet.exceptions.JavetCompilationException} - JavaScript syntax errors during compilation, with source location details.</li>
 *   <li>{@link com.caoccao.javet.exceptions.JavetExecutionException} - JavaScript runtime errors during execution, with stack trace and source context.</li>
 *   <li>{@link com.caoccao.javet.exceptions.JavetConverterException} - Type conversion failures between Java and JavaScript.</li>
 *   <li>{@link com.caoccao.javet.exceptions.JavetTerminatedException} - V8 execution was terminated (e.g., by a timeout guard).</li>
 *   <li>{@link com.caoccao.javet.exceptions.JavetOutOfMemoryException} - V8 heap limit exceeded, with heap statistics.</li>
 * </ul>
 * <p>
 * {@link com.caoccao.javet.exceptions.JavetError} defines all predefined error codes and message templates.
 * {@link com.caoccao.javet.exceptions.JavetScriptingError} carries detailed scripting error information
 * including line number, column, source code snippet, and resource name.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.exceptions;