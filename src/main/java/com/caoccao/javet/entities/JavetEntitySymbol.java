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

import com.caoccao.javet.interfaces.IJavetEntitySymbol;

import java.util.Objects;

/**
 * The type Javet entity symbol is for converting JS symbol
 * to or from Java symbol.
 *
 * @since 0.9.11
 */
public class JavetEntitySymbol implements IJavetEntitySymbol {
    /**
     * The Description.
     *
     * @since 0.9.11
     */
    protected String description;

    /**
     * Instantiates a new Javet entity symbol.
     *
     * @param description the description
     * @since 0.9.11
     */
    public JavetEntitySymbol(String description) {
        this.description = Objects.requireNonNull(description);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
