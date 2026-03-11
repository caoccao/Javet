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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * Interface for V8 objects that contain keyed entries, such as Map and Set.
 */
public interface IV8ValueKeyContainer extends IV8ValueObject {
    /**
     * Returns an iterator over the keys in this container.
     *
     * @return an iterator of V8 value keys
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    IV8ValueIterator<? extends V8Value> getKeys() throws JavetException;

    /**
     * Returns the number of entries in this container.
     *
     * @return the size of this container
     * @throws JavetException the javet exception
     */
    int getSize() throws JavetException;
}
