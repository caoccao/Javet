/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.enums.JSFunctionType;

/**
 * The interface Javet entity function.
 *
 * @since 0.9.4
 */
public interface IJavetEntityFunction {
    /**
     * Gets JS function type.
     *
     * @return the JS function type
     * @since 0.9.4
     */
    JSFunctionType getJSFunctionType();

    /**
     * Gets source code.
     *
     * @return the source code
     * @since 0.9.4
     */
    String getSourceCode();

    /**
     * Sets JS function type.
     *
     * @param jsFunctionType the JS function type
     * @since 0.9.4
     */
    void setJSFunctionType(JSFunctionType jsFunctionType);

    /**
     * Sets source code.
     *
     * @param sourceCode the source code
     * @since 0.9.4
     */
    void setSourceCode(String sourceCode);
}
