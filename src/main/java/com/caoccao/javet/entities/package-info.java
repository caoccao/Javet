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
 * Java entity classes that mirror JavaScript value types for converter round-trips.
 * <p>
 * These plain Java objects carry JavaScript data across the JNI boundary and are used
 * by the built-in converters ({@link com.caoccao.javet.interop.converters.JavetObjectConverter}, etc.)
 * when converting between V8 values and Java objects:
 * <ul>
 *   <li>{@link com.caoccao.javet.entities.JavetEntityError} - V8 error with type, message, and stack trace.</li>
 *   <li>{@link com.caoccao.javet.entities.JavetEntityFunction} - JavaScript function with type and source code.</li>
 *   <li>{@link com.caoccao.javet.entities.JavetEntityMap} - HashMap-based container for JavaScript Map objects.</li>
 *   <li>{@link com.caoccao.javet.entities.JavetEntitySymbol} - JavaScript Symbol with description.</li>
 *   <li>{@link com.caoccao.javet.entities.JavetEntityPropertyDescriptor} - JavaScript property descriptor (configurable, enumerable, writable, value).</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.entities;