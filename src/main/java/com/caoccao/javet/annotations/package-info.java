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
 * Annotations for declarative Java-to-JavaScript binding.
 * <p>
 * Use these annotations to control how Java methods, fields, and constructors are exposed to JavaScript:
 * <ul>
 *   <li>{@link com.caoccao.javet.annotations.V8Function} - Register a Java method as a JavaScript function.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8Property} - Register a Java method or field as a JavaScript property.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8Getter} / {@link com.caoccao.javet.annotations.V8Setter} - Mark methods as JavaScript property getters/setters.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8Allow} / {@link com.caoccao.javet.annotations.V8Block} - Allowlist or blocklist members for V8 access.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8Convert} - Specify conversion mode and proxy mode for a class.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8BindingEnabler} - Conditionally enable or disable bindings at runtime.</li>
 *   <li>{@link com.caoccao.javet.annotations.V8RuntimeSetter} - Inject the V8 runtime instance into a method.</li>
 *   <li>{@link com.caoccao.javet.annotations.NodeModule} - Mark a class as a Node.js module with a configurable name.</li>
 *   <li>{@link com.caoccao.javet.annotations.CheckReturnValue} - Warn when return values are not consumed (prevents memory leaks).</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.annotations;