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
 * Callback receiver base classes for handling JavaScript-to-Java function calls.
 * <p>
 * When JavaScript calls a Java-backed function, the receiver object is the target of the invocation.
 * <ul>
 *   <li>{@link com.caoccao.javet.utils.receivers.IJavetCallbackReceiver} - Interface for callback receivers.</li>
 *   <li>{@link com.caoccao.javet.utils.receivers.JavetCallbackReceiver} - Base class holding a V8 runtime reference,
 *       providing common ground for custom callback implementations.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.utils.receivers;