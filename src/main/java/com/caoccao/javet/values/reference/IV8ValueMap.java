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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * The interface V8 value map.
 *
 * @since 0.7.0
 */
public interface IV8ValueMap extends IV8ValueKeyContainer {
    /**
     * The constant FUNCTION_ENTRIES.
     *
     * @since 3.0.4
     */
    String FUNCTION_ENTRIES = "entries";
    /**
     * The constant FUNCTION_KEYS.
     *
     * @since 3.0.4
     */
    String FUNCTION_KEYS = "keys";
    /**
     * The constant FUNCTION_VALUES.
     *
     * @since 3.0.4
     */
    String FUNCTION_VALUES = "values";

    /**
     * As array.
     *
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    @CheckReturnValue
    IV8ValueArray asArray() throws JavetException;

    /**
     * Clear.
     *
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    void clear() throws JavetException;

    /**
     * Gets entries.
     *
     * @return the entries
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    default IV8ValueIterator<V8ValueArray> getEntries() throws JavetException {
        return invoke(FUNCTION_ENTRIES);
    }

    /**
     * Gets keys.
     *
     * @return the keys
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    default IV8ValueIterator<? extends V8Value> getKeys() throws JavetException {
        return invoke(FUNCTION_KEYS);
    }

    /**
     * Gets values.
     *
     * @return the values
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    default IV8ValueIterator<? extends V8Value> getValues() throws JavetException {
        return invoke(FUNCTION_VALUES);
    }
}
