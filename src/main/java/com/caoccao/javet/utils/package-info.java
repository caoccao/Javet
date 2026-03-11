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
 * Utility classes for common operations across the Javet API.
 * <ul>
 *   <li>{@link com.caoccao.javet.utils.JavetResourceUtils} - Safe resource cleanup for V8 values and closables.</li>
 *   <li>{@link com.caoccao.javet.utils.JavetOSUtils} - OS and architecture detection (platform, working directory).</li>
 *   <li>{@link com.caoccao.javet.utils.JavetDateTimeUtils} - Date/time conversion between V8 timestamps and {@code ZonedDateTime}.</li>
 *   <li>{@link com.caoccao.javet.utils.V8ValueUtils} - V8 value array and type manipulation helpers.</li>
 *   <li>{@link com.caoccao.javet.utils.JavetDefaultLogger} - Default logging implementation via {@code java.util.logging}.</li>
 *   <li>{@link com.caoccao.javet.utils.StringUtils}, {@link com.caoccao.javet.utils.ArrayUtils},
 *       {@link com.caoccao.javet.utils.ListUtils} - Common string, array, and list operations.</li>
 *   <li>{@link com.caoccao.javet.utils.SimpleMap}, {@link com.caoccao.javet.utils.SimpleList},
 *       {@link com.caoccao.javet.utils.SimpleSet} - JDK 8 polyfills for {@code Map.of()}, {@code List.of()}, {@code Set.of()}.</li>
 *   <li>{@link com.caoccao.javet.utils.ThreadSafeMap} - Thread-safe map with permanent and weak reference support.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.utils;