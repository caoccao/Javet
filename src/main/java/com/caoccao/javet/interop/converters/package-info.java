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
 * Type converters for automatic conversion between Java objects and V8 values.
 * <p>
 * Javet provides a converter hierarchy with increasing capability:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.converters.JavetPrimitiveConverter} - Converts primitives only (numbers, strings, booleans). Lightweight and fast.</li>
 *   <li>{@link com.caoccao.javet.interop.converters.JavetObjectConverter} - Default converter. Handles primitives plus objects, arrays, maps, sets, and entity types.</li>
 *   <li>{@link com.caoccao.javet.interop.converters.JavetProxyConverter} - Creates JavaScript proxy objects backed by live Java objects (bidirectional access).</li>
 *   <li>{@link com.caoccao.javet.interop.converters.JavetBridgeConverter} - Full bridge converting all Java objects with automatic type mapping.</li>
 * </ul>
 * <p>
 * Implement {@link com.caoccao.javet.interop.converters.IJavetConverter} or extend
 * {@link com.caoccao.javet.interop.converters.BaseJavetConverter} to create custom converters.
 * Use {@link com.caoccao.javet.interop.converters.JavetConverterConfig} to configure converter plugins.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.converters;