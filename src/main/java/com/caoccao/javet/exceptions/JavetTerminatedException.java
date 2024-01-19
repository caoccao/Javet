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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.SimpleMap;

/**
 * The type Javet terminated exception.
 *
 * @since 0.7.2
 */
public class JavetTerminatedException extends JavetException {
    /**
     * The Continuable.
     *
     * @since 0.7.2
     */
    protected boolean continuable;

    /**
     * Instantiates a new Javet terminated exception.
     *
     * @param continuable the continuable
     * @since 0.7.2
     */
    public JavetTerminatedException(boolean continuable) {
        super(JavetError.ExecutionTerminated, SimpleMap.of(JavetError.PARAMETER_CONTINUABLE, continuable));
        this.continuable = continuable;
    }

    /**
     * Is continuable.
     *
     * @return the boolean
     * @since 0.7.2
     */
    public boolean isContinuable() {
        return continuable;
    }
}
