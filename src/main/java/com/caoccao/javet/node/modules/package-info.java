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
 * Java wrappers for Node.js built-in modules (Node.js mode only).
 * <p>
 * These classes provide typed Java access to Node.js module objects:
 * <ul>
 *   <li>{@link com.caoccao.javet.node.modules.NodeModuleModule} - The {@code module} module, providing
 *       {@code createRequire()} for setting the require root directory.</li>
 *   <li>{@link com.caoccao.javet.node.modules.NodeModuleProcess} - The {@code process} module, providing
 *       access to the process object, environment, and methods.</li>
 *   <li>{@link com.caoccao.javet.node.modules.NodeModuleAny} - Generic wrapper for any Node.js module not covered by a specialized class.</li>
 * </ul>
 * <p>
 * Obtain a module via {@code nodeRuntime.getNodeModule(NodeModuleModule.class)}.
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.node.modules;