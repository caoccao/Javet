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

package com.caoccao.javet.node.modules;

import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The interface Node module.
 *
 * @since 0.8.0
 */
public interface INodeModule extends IJavetClosable {
    /**
     * Gets the module object.
     *
     * @return the module object
     * @since 0.8.0
     */
    V8ValueObject getModuleObject();

    /**
     * Gets the module name.
     *
     * @return the module name
     * @since 0.8.0
     */
    String getName();
}
