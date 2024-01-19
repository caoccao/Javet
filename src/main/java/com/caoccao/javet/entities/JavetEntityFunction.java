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

package com.caoccao.javet.entities;

import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.interfaces.IJavetEntityFunction;

import java.util.Objects;

/**
 * The type Javet entity function is for converting JS function
 * to or from Java object.
 *
 * @since 0.9.4
 */
public class JavetEntityFunction implements IJavetEntityFunction {
    /**
     * The JS function type.
     *
     * @since 0.9.4
     */
    protected JSFunctionType jsFunctionType;
    /**
     * The Source code.
     *
     * @since 0.9.4
     */
    protected String sourceCode;

    /**
     * Instantiates a new Javet entity function.
     *
     * @since 0.9.4
     */
    public JavetEntityFunction() {
        this(null);
    }

    /**
     * Instantiates a new Javet entity function.
     *
     * @param sourceCode     the source code
     * @param jsFunctionType the js function type
     * @since 0.9.13
     */
    public JavetEntityFunction(String sourceCode, JSFunctionType jsFunctionType) {
        setJSFunctionType(jsFunctionType);
        setSourceCode(sourceCode);
    }

    /**
     * Instantiates a new Javet entity function.
     *
     * @param sourceCode the source code
     * @since 0.9.4
     */
    public JavetEntityFunction(String sourceCode) {
        this(sourceCode, JSFunctionType.Unknown);
    }

    @Override
    public JSFunctionType getJSFunctionType() {
        return jsFunctionType;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setJSFunctionType(JSFunctionType jsFunctionType) {
        this.jsFunctionType = Objects.requireNonNull(jsFunctionType);
    }

    @Override
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
