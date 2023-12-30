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

package com.caoccao.javet.interop;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * The interface V8 cloneable.
 *
 * @since 0.7.0
 */
public interface IV8Cloneable {
    /**
     * Get a clone of the current V8 value.
     *
     * @param <T> the type parameter
     * @return the cloned V8 value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    default <T extends V8Value> T toClone() throws JavetException {
        return toClone(true);
    }

    /**
     * Get a clone of the current V8 value.
     *
     * @param <T>           the type parameter
     * @param referenceCopy the reference copy
     * @return the cloned V8 value
     * @throws JavetException the javet exception
     * @since 2.0.2
     */
    @CheckReturnValue
    <T extends V8Value> T toClone(boolean referenceCopy) throws JavetException;
}
