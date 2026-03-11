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
 * Native library loading for platform-specific JNI binaries.
 * <p>
 * Javet ships separate native libraries per OS and architecture (Linux x86_64/arm64, macOS x86_64/arm64, Windows x86_64, Android).
 * This package handles locating and loading the correct binary:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.loader.JavetLibLoader} - Loads the platform-specific native library at startup.</li>
 *   <li>{@link com.caoccao.javet.interop.loader.IJavetLibLoadingListener} - Customize library loading behavior (e.g., load from a custom path).</li>
 *   <li>{@link com.caoccao.javet.interop.loader.JavetLibLoadingListener} - Default listener that locates libraries from the classpath resources.</li>
 * </ul>
 *
 * @since 1.0.1
 * @author Sam Cao
 */
package com.caoccao.javet.interop.loader;